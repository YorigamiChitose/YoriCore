package Core

import chisel3._
import chisel3.util._

import Core.Pipe._
import Core.PCU._
import Core.PCU.module._
import Core.IFU._
import Core.IFU.module._

class Core extends Module {
  val ioIMem = IO(Flipped(new IMemBundle))
  val PCU    = Module(new PCU)
  val PC_IF  = Module(new PipeStage(new PCUStageBundle))
  val IFU    = Module(new IFU)
  val IF_ID  = Module(new PipeStage(new IFUStageBundle))

  val pipeLineCtrl = Module(new PipeLineCtrl)

  pipeLineCtrl.ioPCUCtrl <> PCU.ioCtrl // PCU - 流水控制器
  pipeLineCtrl.ioIFUCtrl <> IFU.ioCtrl // IFU - 流水控制器

  pipeLineCtrl.ioPC_IFPipeCtrl <> PC_IF.ioPipeCtrl // PC_IF级间寄存器 - 流水控制器
  pipeLineCtrl.ioIF_IDPipeCtrl <> IF_ID.ioPipeCtrl // IF_ID级间寄存器 - 流水控制器

  PCU.ioPCU <> PC_IF.ioPrevStage // PCU - PC_IF 数据通路
  IFU.ioIFU <> IF_ID.ioPrevStage // IFU - IF_ID 数据通路

  PC_IF.ioNextStage <> IFU.ioPCU // PC_IF - IFU 数据通路
  IFU.ioValid <> PC_IF.ioValid   // PC_IF 数据有效

  // test code
  IFU.ioIMem <> ioIMem
}
