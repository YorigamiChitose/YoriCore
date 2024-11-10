package DPIC

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.Reg.module._

class RegFileTraceBundle extends Bundle {
  val clock = Input(Clock())
  val reset = Input(Reset())
  val en    = Input(Bool())
  val addr  = Input(UInt(Config.Reg.NumWidth.W))
  val data  = Input(UInt(Config.Reg.Width.W))
}

class RegFileTrace extends BlackBox with HasBlackBoxResource {
  val io = IO(new RegFileTraceBundle)
  addResource("/RegFileTrace.sv")
}
