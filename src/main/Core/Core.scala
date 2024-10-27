package Core

import chisel3._
import chisel3.util._

import Core.Pipe._
import Core.Pipe.module._
import Core.PCU._
import Core.PCU.module._
import Core.IFU._
import Core.IFU.module._
import Core.IDU._
import Core.IDU.module._
import Core.EXU._
import Core.EXU.module._
import Core.Reg._
import Core.Reg.module._

class Core extends Module {
  val PCU   = Module(new PCU)
  val PC_IF = Module(new PipeStage(new PCUStageBundle))
  val IFU   = Module(new IFU)
  val IF_ID = Module(new PipeStage(new IFUStageBundle))
  val IDU   = Module(new IDU)
  val ID_EX = Module(new PipeStage(new IDUStageBundle))
  val EXU   = Module(new EXU)
  val EX_WB = Module(new PipeStage(new EXUStageBundle))

  val regFile = Module(new RegFile)
  val csrFile = Module(new CsrFile)

  val pipeLineCtrl = Module(new PipeLineCtrl)
  val forwording   = Module(new Forwarding)

  pipeLineCtrl.ioPCUCtrl <> PCU.ioCtrl // PCU - 流水控制器
  pipeLineCtrl.ioIFUCtrl <> IFU.ioCtrl // IFU - 流水控制器
  pipeLineCtrl.ioIDUCtrl <> IDU.ioCtrl // IDU - 流水控制器
  pipeLineCtrl.ioEXUCtrl <> EXU.ioCtrl // EXU - 流水控制器

  pipeLineCtrl.ioPC_IFPipeCtrl <> PC_IF.ioPipeCtrl // PC_IF级间寄存器 - 流水控制器
  pipeLineCtrl.ioIF_IDPipeCtrl <> IF_ID.ioPipeCtrl // IF_ID级间寄存器 - 流水控制器
  pipeLineCtrl.ioID_EXPipeCtrl <> ID_EX.ioPipeCtrl // ID_EX级间寄存器 - 流水控制器
  pipeLineCtrl.ioEX_WBPipeCtrl <> EX_WB.ioPipeCtrl // EX_WB级间寄存器 - 流水控制器

  PCU.ioPCU <> PC_IF.ioPrevStage // PCU - PC_IF 数据通路
  IFU.ioIFU <> IF_ID.ioPrevStage // IFU - IF_ID 数据通路
  IDU.ioIDU <> ID_EX.ioPrevStage // IDU - ID_EX 数据通路
  EXU.ioEXU <> EX_WB.ioPrevStage // EXU - EX_WB 数据通路

  PC_IF.ioNextStage <> IFU.ioPCU // PC_IF - IFU 数据通路
  IF_ID.ioNextStage <> IDU.ioIFU // IF_ID - IDU 数据通路
  ID_EX.ioNextStage <> EXU.ioIDU // ID_EX - EXU 数据通路

  IFU.ioValid <> PC_IF.ioValid // PC_IF 数据有效
  IDU.ioValid <> IF_ID.ioValid // PC_IF 数据有效
  EXU.ioValid <> ID_EX.ioValid // ID_EX 数据有效

  IDU.ioRegIDU <> regFile.ioRegIDU // IDU - 通用寄存器 数据通路
  IDU.ioCSRIDU <> csrFile.ioCSRIDU // IDU - csr寄存器 数据通路

  forwording.ioIDU <> IDU.ioIDUForwarding // IDU - 前递 数据通路
  forwording.ioEXU <> EXU.ioEXUForwarding // EXU - 前递 数据通路

  // test code
  val ioIMem = IO(Flipped(new IMemBundle))
  IFU.ioIMem <> ioIMem

  val ioDMem = IO(Flipped(new DMemBundle))
  EXU.ioDMem <> ioDMem

  val ioRegWBU = IO(new RegWBUBundle)
  regFile.ioRegWBU <> ioRegWBU

  val ioCSRWBU = IO(new CSRWBUBundle)
  csrFile.ioCSRWBU <> ioCSRWBU

  val ioEXU = IO(new EXUStageBundle)
  EXU.ioEXU <> ioEXU

  val ioWBU = IO(new WBUForwardingBundle)
  forwording.ioWBU <> ioWBU
}
