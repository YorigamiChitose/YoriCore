package Core.WBU.module

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Pipe.module._

class WBUCtrlBundle extends Bundle {
  val flushReq = Output(Bool())                    // 冲刷请求
  val flushPC  = Output(UInt(Config.Addr.Width.W)) // 冲刷地址
  val pipe     = new PipeCtrlBundle()              // pipe控制信号
}

class WBUStageBundle extends StageBundle {
  // no next
}
