package Sim

import chisel3._
import chisel3.util._

import Core.EXU.module._

class SimDMemBundle extends Bundle {
  val clock  = Input(Clock())
  val reset  = Input(Reset())
  val ioDMem = new DMemBundle
}

class SimDMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new SimDMemBundle)
  addResource("/SimDMem.sv")
}
