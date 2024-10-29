package Core.Pipe

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Pipe.module._
import Core.PCU.module._
import Core.IFU.module._
import Core.IDU.module._
import Core.EXU.module._
import Core.WBU.module._

// pipe控制器
class PipeLineCtrl extends Module {
  val ioPCUCtrl       = IO(Flipped(new PCUCtrlBundle))       // PCU流水控制信号
  val ioPC_IFPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // PC_IF级间寄存器控制信号
  val ioIFUCtrl       = IO(Flipped(new IFUCtrlBundle))       // IFU流水控制信号
  val ioIF_IDPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // IF_ID级间寄存器控制信号
  val ioIDUCtrl       = IO(Flipped(new IDUCtrlBundle))       // IDU流水控制信号
  val ioID_EXPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // ID_EX级间寄存器控制信号
  val ioEXUCtrl       = IO(Flipped(new EXUCtrlBundle))       // EXU流水控制信号
  val ioEX_WBPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // EX_WB级间寄存器控制信号
  val ioWBUCtrl       = IO(Flipped(new WBUCtrlBundle))       // WBU流水控制信号

  val stallCode = WireDefault("b00000".U(5.W)) // 暂停码
  val flushCode = WireDefault("b00000".U(5.W)) // 冲刷码

  val keepWBUFlushReq = RegInit(false.B)                  // WBU冲刷请求保留
  val keepEXUFlushReq = RegInit(false.B)                  // EXU冲刷请求保留
  val keepWBUFlushPC  = RegInit(0.U(Config.Addr.Width.W)) // WBU冲刷地址保留
  val keepEXUFlushPC  = RegInit(0.U(Config.Addr.Width.W)) // EXU冲刷地址保留

  val busyIFU = ioIFUCtrl.busy // IFU忙信号
  val busyEXU = ioEXUCtrl.busy // EXU忙信号

  val flushPC = MuxCase(
    0.U(Config.Addr.Width.W),
    Seq(
      keepWBUFlushReq    -> keepWBUFlushPC,
      ioWBUCtrl.flushReq -> ioWBUCtrl.flushPC,
      keepEXUFlushReq    -> keepEXUFlushPC,
      ioEXUCtrl.flushReq -> ioEXUCtrl.flushPC
    )
  ) // 跳转PC

  when((busyEXU || busyIFU) && ioWBUCtrl.flushReq && !keepWBUFlushReq) { // IFU EXU busy - WBU flushReq
    keepWBUFlushReq := ioWBUCtrl.flushReq
    keepWBUFlushPC  := ioWBUCtrl.flushPC
    keepEXUFlushReq := false.B
    keepEXUFlushPC  := 0.U
  }.elsewhen(busyIFU && ioEXUCtrl.flushReq && !keepEXUFlushReq) { // IFU busy - EXU flushReq
    keepEXUFlushReq := ioEXUCtrl.flushReq
    keepEXUFlushPC  := ioEXUCtrl.flushPC
  }.elsewhen(!(busyEXU || busyIFU) && keepWBUFlushReq) { // !IFU && !EXU busy - WBU keep FlushReq
    keepWBUFlushReq := false.B
    keepWBUFlushPC  := 0.U
  }.elsewhen(!busyIFU && keepEXUFlushReq) { // !IFU busy - EXU keep FlushReq
    keepEXUFlushReq := false.B
    keepEXUFlushPC  := 0.U
  }

  // 暂停码
  stallCode := MuxCase(
    "b00000".U(5.W),
    Seq(
      keepWBUFlushReq    -> "b00000".U(5.W),
      ioWBUCtrl.flushReq -> "b00000".U(5.W),
      keepEXUFlushReq    -> "b00000".U(5.W),
      ioEXUCtrl.flushReq -> "b00000".U(5.W),
      ioEXUCtrl.stallReq -> "b11110".U(5.W),
      ioIDUCtrl.stallReq -> "b11100".U(5.W),
      ioIFUCtrl.stallReq -> "b11000".U(5.W)
    )
  )

  // 冲刷码
  flushCode := MuxCase(
    "b00000".U(5.W),
    Seq(
      keepWBUFlushReq    -> "b11110".U(5.W),
      ioWBUCtrl.flushReq -> "b11110".U(5.W),
      keepEXUFlushReq    -> "b11100".U(5.W),
      ioEXUCtrl.flushReq -> "b11100".U(5.W),
      ioEXUCtrl.stallReq -> "b00000".U(5.W),
      ioIDUCtrl.stallReq -> "b00000".U(5.W),
      ioIFUCtrl.stallReq -> "b00000".U(5.W)
    )
  )

  ioPCUCtrl.flushPC := flushPC // PC值

  ioPCUCtrl.pipe.flush := flushCode(4) // 流水级冲刷信号
  ioIFUCtrl.pipe.flush := flushCode(3) // 流水级冲刷信号
  ioIDUCtrl.pipe.flush := flushCode(2) // 流水级冲刷信号
  ioEXUCtrl.pipe.flush := flushCode(1) // 流水级冲刷信号
  ioWBUCtrl.pipe.flush := flushCode(0) // 流水级冲刷信号

  ioPCUCtrl.pipe.stall := stallCode(4) // 流水级暂停信号
  ioIFUCtrl.pipe.stall := stallCode(3) // 流水级暂停信号
  ioIDUCtrl.pipe.stall := stallCode(2) // 流水级暂停信号
  ioEXUCtrl.pipe.stall := stallCode(1) // 流水级暂停信号
  ioWBUCtrl.pipe.stall := stallCode(0) // 流水级暂停信号

  ioPC_IFPipeCtrl.flush := flushCode(4) // 级间冲刷信号
  ioIF_IDPipeCtrl.flush := flushCode(3) // 级间冲刷信号
  ioID_EXPipeCtrl.flush := flushCode(2) // 级间冲刷信号
  ioEX_WBPipeCtrl.flush := flushCode(1) // 级间冲刷信号

  ioIF_IDPipeCtrl.stallPrev := stallCode(4) // 前级暂停信号
  ioPC_IFPipeCtrl.stallPrev := stallCode(3) // 前级暂停信号
  ioID_EXPipeCtrl.stallPrev := stallCode(2) // 前级暂停信号
  ioEX_WBPipeCtrl.stallPrev := stallCode(1) // 前级暂停信号

  ioIF_IDPipeCtrl.stallNext := stallCode(3) // 后级暂停信号
  ioPC_IFPipeCtrl.stallNext := stallCode(2) // 后级暂停信号
  ioID_EXPipeCtrl.stallNext := stallCode(1) // 后级暂停信号
  ioEX_WBPipeCtrl.stallNext := stallCode(0) // 后级暂停信号

  ioPC_IFPipeCtrl.valid := ioPCUCtrl.pipe.valid // PCU数据有效信号
  ioIF_IDPipeCtrl.valid := ioIFUCtrl.pipe.valid // IFU数据有效信号
  ioID_EXPipeCtrl.valid := ioIDUCtrl.pipe.valid // IDU数据有效信号
  ioEX_WBPipeCtrl.valid := ioEXUCtrl.pipe.valid // EXU数据有效信号
}
