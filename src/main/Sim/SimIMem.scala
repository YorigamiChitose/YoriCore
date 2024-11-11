package Sim

import chisel3._
import chisel3.util._

import Core.IFU.module._

class SimIMemBundle extends Bundle {
  val clock  = Input(Clock())
  val reset  = Input(Reset())
  val ioIMem = new IMemBundle
}

class SimIMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new SimIMemBundle)
  addResource("/SimIMem.sv")
}
