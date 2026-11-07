package Core.IDU.module

import chisel3._
import chisel3.util._

import Tools.Config.Config

object rs1 {
  val NOP = false.B
  val EN  = true.B
}

object rs2 {
  val NOP = false.B
  val EN  = true.B
}

object rd {
  val NOP = false.B
  val EN  = true.B
}

object op1 extends ChiselEnum {
  val NOP, RS1, PC, ZERO = Value
}

object op2 extends ChiselEnum {
  val NOP, RS2, IMM, FORE = Value
}

object imm extends ChiselEnum {
  val N, I, S, B, U, J, R, SHAMT, ZIMM = Value
}

object alu extends ChiselEnum {
  val NOP, ADD, SUB, SLT, SLTU, XOR, OR, AND, SLL, SRL, SRA = Value
}

object mul extends ChiselEnum {
  val NOP, MUL, MULH, MULHU, MULHSU = Value
}

object div extends ChiselEnum {
  val NOP, DIV, REM, DIVU, REMU = Value
}

object branch extends ChiselEnum {
  val NOP, BEQ, BNE, BLT, BGE, BLTU, BGEU, JAL, JALR = Value
}

object mem extends ChiselEnum {
  val NOP, LB, LH, LBU, LHU, LW, SB, SH, SW,
      AMOSWAP, AMOADD, AMOXOR, AMOAND, AMOOR,
      AMOMIN, AMOMAX, AMOMINU, AMOMAXU, LR, SC = Value
}

object csr extends ChiselEnum {
  val NOP, RC, RW, RS, RCI, RWI, RSI = Value
}

object exc extends ChiselEnum {
  val NOP, EBREAK, ECALL, ILLEGAL_INST, MRET, SRET, WFI, SFENCE_VMA, FENCE, FENCE_I = Value
}

case class DecodeSig(
  rs1En:      Bool,
  rs2En:      Bool,
  rdEn:       Bool,
  op1Type:    op1.Type,
  op2Type:    op2.Type,
  aluCtrl:    alu.Type,
  immType:    imm.Type,
  branchCtrl: branch.Type,
  mulCtrl:    mul.Type,
  divCtrl:    div.Type,
  memCtrl:    mem.Type,
  csrCtrl:    csr.Type,
  excType:    exc.Type
)

object ALL {
  val defaultSig = DecodeSig(
    rs1.NOP, rs2.NOP, rd.NOP,
    op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.ILLEGAL_INST
  )
  val instList = RV32I.table ++ RV32M.table ++ RVMSU.table ++ RV32A.table
}
