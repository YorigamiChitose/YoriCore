package Sim

import chisel3._
import chisel3.util._

import Core.EXU.module._

class SimDMem extends BlackBox with HasBlackBoxResource {
  val io = IO(new DMemBundle)
  addResource("/SimDMem.sv")
}
