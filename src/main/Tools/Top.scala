package Tools

import chisel3._
import chisel3.util._

import Core._

import Core.EXU.module.DMemBundle
import Sim.SimIMem

class Top extends Module {
  val core = Module(new Core)

  val simIMem = Module(new SimIMem)

  core.ioIMem <> simIMem.io.ioIMem
  simIMem.io.clock := clock
  simIMem.io.reset := reset

  val ioDMem = IO(Flipped(new DMemBundle))
  core.ioDMem <> ioDMem
}
