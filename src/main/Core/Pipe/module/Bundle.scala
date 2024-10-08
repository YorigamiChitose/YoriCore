package Core.Pipe.module

import chisel3._
import chisel3.util._

import Core.Config.Config

// 流水线控制信号IO
class pipeRegCtrlBundle extends Bundle {
  val flush     = Input(Bool())
  val stallPrev = Input(Bool())
  val stallNext = Input(Bool())
}

// 各级流水信号IO父类
class StageBundle extends Bundle {
  def initVal() = 0.U.asTypeOf(this)
}

class pipeCtrlBundle extends Bundle {
  val flush = Input(Bool())
  val stall = Input(Bool())
}
