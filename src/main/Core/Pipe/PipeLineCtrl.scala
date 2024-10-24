package Core.Pipe

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Pipe.module._
import Core.PCU.module._
import Core.IFU.module._
import Core.IDU.module._

// pipe控制器
class PipeLineCtrl extends Module {
  val ioPCUCtrl       = IO(Flipped(new PCUCtrlBundle))       // PCU流水控制信号
  val ioPC_IFPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // PC_IF级间寄存器控制信号
  val ioIFUCtrl       = IO(Flipped(new IFUCtrlBundle))       // IFU流水控制信号
  val ioIF_IDPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // IF_ID级间寄存器控制信号
  val ioIDUCtrl       = IO(Flipped(new IDUCtrlBundle))       // IDU流水控制信号
  val ioID_EXPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // ID_EX级间寄存器控制信号

  val stallCode = WireDefault("b00000".U(5.W)) // 暂停码
  val flushCode = WireDefault("b00000".U(5.W)) // 冲刷码

  val flushPC = WireDefault(0.U(Config.Addr.Width.W)) // 跳转PC

  ioPCUCtrl.flushPC := flushPC // PC值

  ioPCUCtrl.pipe.flush := DontCare
  ioIFUCtrl.pipe.flush := DontCare
  ioIDUCtrl.pipe.flush := DontCare

  ioPCUCtrl.pipe.stall := DontCare
  ioIFUCtrl.pipe.stall := DontCare
  ioIDUCtrl.pipe.stall := DontCare

  ioPC_IFPipeCtrl.flush := DontCare
  ioIF_IDPipeCtrl.flush := DontCare
  ioID_EXPipeCtrl.flush := DontCare

  ioIF_IDPipeCtrl.stallNext := DontCare
  ioPC_IFPipeCtrl.stallNext := DontCare
  ioID_EXPipeCtrl.stallNext := DontCare

  ioIF_IDPipeCtrl.stallPrev := DontCare
  ioPC_IFPipeCtrl.stallPrev := DontCare
  ioID_EXPipeCtrl.stallPrev := DontCare

  ioPC_IFPipeCtrl.valid := ioPCUCtrl.pipe.valid // PCU数据有效信号
  ioIF_IDPipeCtrl.valid := ioIFUCtrl.pipe.valid // IFU数据有效信号
  ioID_EXPipeCtrl.valid := ioIDUCtrl.pipe.valid // IDU数据有效信号
}
