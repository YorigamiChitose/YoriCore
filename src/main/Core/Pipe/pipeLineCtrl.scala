package Core.Pipe

import chisel3._
import chisel3.util._

import Core.PCU.module._
import Core.Pipe.module._
import Core.Config.Config


// PCU IFU ICU EXU WBU
class pipeLineCtrl extends Module {
  val ioPCUCtrl = IO(Flipped(new PCUCtrlBundle))
  val ioPCUPipeCtrl = IO(Flipped(new pipeRegCtrlBundle()))

}
