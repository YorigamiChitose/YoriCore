package Core.PCU

import chisel3._
import chisel3.util._

import Core.PCU.module._
import Core.Config.Config

class PCU extends Module {
  val ioCtrl = IO(new PCUCtrlBundle())
  val ioPCU  = IO(new PCUStageBundle())

  val pc     = WireDefault(0.U(Config.ADDR.WIDTH.W))
  val nextPc = WireDefault(0.U(Config.ADDR.WIDTH.W))
  val pcReg  = RegNext(nextPc, Config.ADDR.BASE.U(Config.ADDR.WIDTH.W))

  nextPc := Mux(ioCtrl.pipe.flush, ioCtrl.flushPC, Mux(ioCtrl.pipe.stall, pc, pcReg + 4.U))
  pc     := pcReg

  ioPCU.pc    := pc
  ioPCU.valid := true.B
}
