package Core.Pipe.module

import chisel3._
import chisel3.util._

import Core.Config.Config

// 级间流水寄存器控制信号IO (pipe控制器 <> 级间寄存器)
class PipeRegCtrlBundle extends Bundle {
  val valid     = Input(Bool()) // 前级完成
  val flush     = Input(Bool()) // 冲刷
  val stallPrev = Input(Bool()) // 停止前级
  val stallNext = Input(Bool()) // 停止后级
}

// 各级流水信号IO父类 (流水级 <> 级间寄存器)
class StageBundle extends Bundle {
  def initVal() = 0.U.asTypeOf(this)
}

// 流水线控制信号IO (pipe控制器 <> 流水级)
class PipeCtrlBundle extends Bundle {
  val valid = Output(Bool()) // 完成信号
  val flush = Input(Bool())  // 冲刷命令
  val stall = Input(Bool())  // 停止命令
}
