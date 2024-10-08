package Core

import chisel3._
import chisel3.util._

import Core.PCU._
import Core.PCU.module._
import Core.Pipe._

class Core extends Module {
  val PCU   = Module(new PCU)
  val PC_IF = Module(new pipeLine(new PCUStageBundle))

  val pipeLineCtrl = Module(new pipeLineCtrl)

  pipeLineCtrl.ioPCUCtrl <> PCU.ioPCU
  pipeLineCtrl.ioPCUPipeCtrl <> PC_IF.ioPipeCtrl

}
