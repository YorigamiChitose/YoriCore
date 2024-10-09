package Core.Pipe

import chisel3._
import chisel3.util._

import Core.PCU.module._
import Core.Pipe.module._
import Core.Config.Config

// pipe控制器
class PipeLineCtrl extends Module {
  val ioPCUCtrl     = IO(Flipped(new PCUCtrlBundle)) // PCU流水控制信号
  val ioPC_IFPipeCtrl = IO(Flipped(new PipeRegCtrlBundle())) // PC_IF级间寄存器控制信号

  ioPC_IFPipeCtrl.valid := ioPCUCtrl.pipe.valid
}
