package Core.IDU.module

import chisel3._
import chisel3.util._

object RV32M {
  object INST {
    val MUL    = BitPat("b0000001_?????_?????_000_?????_01100_11")
    val MULH   = BitPat("b0000001_?????_?????_001_?????_01100_11")
    val MULHU  = BitPat("b0000001_?????_?????_011_?????_01100_11")
    val MULHSU = BitPat("b0000001_?????_?????_010_?????_01100_11")
    val DIV    = BitPat("b0000001_?????_?????_100_?????_01100_11")
    val DIVU   = BitPat("b0000001_?????_?????_101_?????_01100_11")
    val REM    = BitPat("b0000001_?????_?????_110_?????_01100_11")
    val REMU   = BitPat("b0000001_?????_?????_111_?????_01100_11")
  }
  val table = Array(
    INST.MUL    -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.MUL, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.MULH   -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.MULH, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.MULHU  -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.MULHU, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.MULHSU -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.MULHSU, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.DIV    -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.NOP, div.DIV, mem.NOP, csr.NOP, exc.NOP),
    INST.DIVU   -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.NOP, div.DIVU, mem.NOP, csr.NOP, exc.NOP),
    INST.REM    -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.NOP, div.REM, mem.NOP, csr.NOP, exc.NOP),
    INST.REMU   -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.NOP, imm.R, branch.NOP, mul.NOP, div.REMU, mem.NOP, csr.NOP, exc.NOP)
  )
}
