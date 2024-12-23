package Sim

import chisel3._
import chisel3.util._

import Core.IDU.module._
import Tools.Config.Config

class SI_PC_IF extends Bundle {
  val ioValid = Input(Bool())
  val pc      = Input(UInt(Config.Addr.Width.W)) // PC值
}
class SI_IF_ID extends Bundle {
  val ioValid = Input(Bool())
  val pc      = Input(UInt(Config.Addr.Width.W)) // PC值
  val inst    = Input(UInt(Config.Inst.Width.W)) // 指令
}
class SI_ID_EX extends Bundle {
  val ioValid = Input(Bool())
  val pc      = Input(UInt(Config.Addr.Width.W)) // PC值
  val inst    = Input(UInt(Config.Inst.Width.W)) // 指令
}
class SI_EX_WB extends Bundle {
  val ioValid = Input(Bool())
  val pc      = Input(UInt(Config.Addr.Width.W)) // PC值
  val inst    = Input(UInt(Config.Inst.Width.W)) // 指令
  val excType = Input(UInt(exc.WIDTH.W))
}

class SimInfoBundle extends Bundle {
  val clock    = Input(Clock())
  val reset    = Input(Reset())
  val SI_PC_IF = new SI_PC_IF
  val SI_IF_ID = new SI_IF_ID
  val SI_ID_EX = new SI_ID_EX
  val SI_EX_WB = new SI_EX_WB
}

class SimInfo extends BlackBox with HasBlackBoxResource {
  val io = IO(new SimInfoBundle)
  addResource("/SimInfo.sv")
}
