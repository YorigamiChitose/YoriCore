package Core.Pipe

import chisel3._
import chisel3.util._
import Core.Pipe.module._

class pipeLine[T <: StageBundle](stageBundle: T) extends Module {
  val ioPipeCtrl  = IO(new pipeRegCtrlBundle())
  val ioPrevStage = IO(Flipped(stageBundle))
  val ioNextStage = IO(stageBundle)

  val stageReg = RegInit(stageBundle, stageBundle.initVal())

  when(ioPipeCtrl.flush || (ioPipeCtrl.stallPrev && !ioPipeCtrl.stallNext)) {
    stageReg := stageBundle.initVal()
  }.elsewhen(!ioPipeCtrl.stallPrev) {
    stageReg := ioPrevStage
  }
  ioNextStage := stageReg
}
