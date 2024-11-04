package Core.Reg

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.Reg.module._

class RegFile extends Module {
  val ioRegIDU = IO(new RegIDUBundle())
  val ioRegWBU = IO(new RegWBUBundle())

  // reg init
  val reg = RegInit(VecInit(Seq.fill(Config.Reg.Num) { 0.U(Config.Reg.Width.W) }))
  // read
  ioRegIDU.rs1.data := Mux(ioRegIDU.rs1.en, reg(ioRegIDU.rs1.addr), 0.U)
  ioRegIDU.rs2.data := Mux(ioRegIDU.rs2.en, reg(ioRegIDU.rs2.addr), 0.U)
  // write
  when(ioRegWBU.rd.en && (ioRegWBU.rd.addr =/= 0.U)) {
    reg(ioRegWBU.rd.addr) := ioRegWBU.rd.data
  }
}
