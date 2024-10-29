package Core.WBU

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.IDU.module._
import Core.EXU.module._
import Core.WBU.module._
import Core.Pipe.module._
import Core.Reg.module._

class WBU extends Module {
  val ioCtrl          = IO(new WBUCtrlBundle)                // 控制信号
  val ioWBU           = IO(new WBUStageBundle)               // 下级流水信号
  val ioEXU           = IO(Flipped(new EXUStageBundle))      // 上级流水信号
  val ioRegWBU        = IO(Flipped(new RegWBUBundle))        // EXU - Reg寄存器 接口
  val ioCSRWBU        = IO(Flipped(new CSRWBUBundle))        // EXU - CSR寄存器 接口
  val ioWBUForwarding = IO(Flipped(new WBUForwardingBundle)) // EXU - 前递 接口
  val ioValid         = IO(Input(Bool()))                    // 上级数据有效信号

  ioCtrl.flushReq := (ioEXU.excType === exc.ECALL) || (ioEXU.excType === exc.MRET) || (ioEXU.excType === exc.SRET) // 冲刷请求
  ioCtrl.flushPC  := ioCSRWBU.flushPC                                                                              // 冲刷地址

  ioRegWBU.rd.en   := ioEXU.rdEn && ioValid // rd写入使能
  ioRegWBU.rd.addr := ioEXU.rdAddr          // rd写入地址
  ioRegWBU.rd.data := ioEXU.EXUResult       // rd写入数据

  ioWBUForwarding.rd.en    := ioEXU.rdEn && ioValid       // 前递rd写入使能
  ioWBUForwarding.rd.addr  := ioEXU.rdAddr                // 前递rd写入地址
  ioWBUForwarding.rd.data  := ioEXU.EXUResult             // 前递rd写入数据
  ioWBUForwarding.csr.en   := ioEXU.csrWriteEn && ioValid // 前递csr写入使能
  ioWBUForwarding.csr.addr := ioEXU.csrAddr               // 前递csr写入地址
  ioWBUForwarding.csr.data := ioEXU.csrResult             // 前递csr写入数据

  ioCSRWBU.csr.en   := ioEXU.csrWriteEn // 前递csr写入使能
  ioCSRWBU.csr.addr := ioEXU.csrAddr    // 前递csr写入地址
  ioCSRWBU.csr.data := ioEXU.csrResult  // 前递csr写入数据
  ioCSRWBU.excType  := ioEXU.excType    // 异常类型
  ioCSRWBU.pc       := ioEXU.pc         // 异常地址

  ioCtrl.pipe.valid := DontCare // 无下一阶段
}
