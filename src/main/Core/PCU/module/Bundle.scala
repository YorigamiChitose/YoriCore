package Core.PCU.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Core.Config.Config

class PCUCtrlBundle extends Bundle {
  val flushPC = Input(UInt(Config.ADDR.WIDTH.W))
  val pipe    = new pipeCtrlBundle()
}

class PCUStageBundle extends StageBundle {
  val valid = Output(Bool())
  val pc    = Output(UInt(Config.ADDR.WIDTH.W))
}
