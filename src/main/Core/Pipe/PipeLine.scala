package Core.Pipe

import chisel3._
import chisel3.util._
import Core.Pipe.module._

// 级间寄存器
class PipeLine[T <: StageBundle](stageBundle: T) extends Module {
  val ioPipeCtrl  = IO(new PipeRegCtrlBundle()) // 控制信号
  val ioPrevStage = IO(Flipped(stageBundle)) // 前级传来的数据
  val ioNextStage = IO(stageBundle) // 发送到后级的数据

  // 流水级间寄存器
  val stageReg = RegInit(stageBundle, stageBundle.initVal())

  when(!ioPipeCtrl.valid || ioPipeCtrl.flush || (ioPipeCtrl.stallPrev && !ioPipeCtrl.stallNext)) {
    // 前级未完成 || 被命令冲刷 || 前级停止后级继续
    stageReg := stageBundle.initVal() // 发送默认数据
  }.elsewhen(!ioPipeCtrl.stallPrev) {
    // 前级不停止
    stageReg := ioPrevStage // 向后传输
  }
  ioNextStage := stageReg
}
