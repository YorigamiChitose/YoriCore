package Core.PCU.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Core.Config.Config

// PCU 控制信号
class PCUCtrlBundle extends Bundle {
  val flushPC = Input(UInt(Config.Addr.Width.W)) // 冲刷pc值
  val pipe    = new PipeCtrlBundle()             // pipe控制信号
}

// PCU 流水信号
class PCUStageBundle extends StageBundle {
  val pc = Output(UInt(Config.Addr.Width.W)) // PC值
}
