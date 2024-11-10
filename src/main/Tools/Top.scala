package Tools

import chisel3._
import chisel3.util._

import Core._
import DPIC._

import Core.EXU.module.DMemBundle

class Top extends Module {
  val core = Module(new Core)

  val dpicIMem = Module(new DpicIMem)

  core.ioIMem <> dpicIMem.io.ioIMem
  dpicIMem.io.clock := clock
  dpicIMem.io.reset := reset

  val ioDMem = IO(Flipped(new DMemBundle))
  core.ioDMem <> ioDMem
}
