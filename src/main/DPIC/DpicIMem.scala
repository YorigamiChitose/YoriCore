package DPIC

import chisel3._
import chisel3.util._

import Core.IFU.module._

class DpicIMemBundle extends Bundle {
  val clock  = Input(Clock())
  val reset  = Input(Reset())
  val ioIMem = new IMemBundle
}

class DpicIMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new DpicIMemBundle)
  addResource("/DpicIMem.sv")
}
