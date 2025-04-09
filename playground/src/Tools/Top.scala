package Tools

import chisel3._
import chisel3.util._

import Core._
import Tools.Config._

import Core.EXU.module.DMemBundle
import Sim._

class Top extends Module {
  val core = Module(new Core)

  val simIMem = Module(new SimIMem)

  core.ioIMem <> simIMem.io.ioIMem
  simIMem.io.clock := clock
  simIMem.io.reset := reset

  val simDMem = Module(new SimDMem)

  core.ioDMem <> simDMem.io.ioDMem
  simDMem.io.clock := clock
  simDMem.io.reset := reset

  val SimInfoIO = if (Config.Sim.enable) Some(IO(Flipped(new SimInfoBundle))) else None
  if (Config.Sim.enable) {
    SimInfoIO.get <> core.SimInfoIO.get
    SimInfoIO.get.clock := clock
    SimInfoIO.get.reset := reset
  }
}
