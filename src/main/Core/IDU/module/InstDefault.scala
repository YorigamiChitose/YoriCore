package Core.IDU.module

import chisel3._
import chisel3.util._

import Core.Config.Config

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

object op1 {
  val WIDTH = log2Ceil(4)
  val NOP   = 0.U(WIDTH.W)
  val RS1   = 1.U(WIDTH.W)
  val PC    = 2.U(WIDTH.W)
  val ZERO  = 3.U(WIDTH.W) // lui
}

object op2 {
  val WIDTH = log2Ceil(4)
  val NOP   = 0.U(WIDTH.W)
  val RS2   = 1.U(WIDTH.W)
  val IMM   = 2.U(WIDTH.W)
  val FORE  = 3.U(WIDTH.W)
}

object imm {
  val WIDTH = log2Ceil(9)
  val N     = 0.U(WIDTH.W)
  val I     = 1.U(WIDTH.W)
  val S     = 2.U(WIDTH.W)
  val B     = 3.U(WIDTH.W)
  val U     = 4.U(WIDTH.W)
  val J     = 5.U(WIDTH.W)
  val R     = 6.U(WIDTH.W)
  val SHAMT = 7.U(WIDTH.W)
  val ZIMM  = 8.U(WIDTH.W)
}

object alu {
  val WIDTH = log2Ceil(11)
  val NOP   = 0.U(WIDTH.W)
  val ADD   = 1.U(WIDTH.W)
  val SUB   = 2.U(WIDTH.W)
  val SLT   = 3.U(WIDTH.W)
  val SLTU  = 4.U(WIDTH.W)
  val XOR   = 5.U(WIDTH.W)
  val OR    = 6.U(WIDTH.W)
  val AND   = 7.U(WIDTH.W)
  val SLL   = 8.U(WIDTH.W)
  val SRL   = 9.U(WIDTH.W)
  val SRA   = 10.U(WIDTH.W)
}

object mul {
  val WIDTH  = log2Ceil(5)
  val NOP    = 0.U(WIDTH.W)
  val MUL    = 1.U(WIDTH.W)
  val MULH   = 2.U(WIDTH.W)
  val MULHU  = 3.U(WIDTH.W)
  val MULHSU = 4.U(WIDTH.W)
}
object div {
  val WIDTH = log2Ceil(5)
  val NOP   = 0.U(WIDTH.W)
  val DIV   = 1.U(WIDTH.W)
  val REM   = 2.U(WIDTH.W)
  val DIVU  = 3.U(WIDTH.W)
  val REMU  = 4.U(WIDTH.W)
}

object branch {
  val WIDTH = log2Ceil(9)
  val NOP   = 0.U(WIDTH.W)
  val BEQ   = 1.U(WIDTH.W)
  val BNE   = 2.U(WIDTH.W)
  val BLT   = 3.U(WIDTH.W)
  val BGE   = 4.U(WIDTH.W)
  val BLTU  = 5.U(WIDTH.W)
  val BGEU  = 6.U(WIDTH.W)
  val JAL   = 7.U(WIDTH.W)
  val JALR  = 8.U(WIDTH.W)
}

object mem {
  val WIDTH = log2Ceil(9)
  val NOP   = 0.U(WIDTH.W)
  val LB    = 1.U(WIDTH.W)
  val LH    = 2.U(WIDTH.W)
  val LBU   = 3.U(WIDTH.W)
  val LHU   = 4.U(WIDTH.W)
  val LW    = 5.U(WIDTH.W)
  val SB    = 6.U(WIDTH.W)
  val SH    = 7.U(WIDTH.W)
  val SW    = 8.U(WIDTH.W)
}

object csr {
  val WIDTH = log2Ceil(7)
  val NOP   = 0.U(WIDTH.W)
  val RC    = 1.U(WIDTH.W)
  val RW    = 2.U(WIDTH.W)
  val RS    = 3.U(WIDTH.W)
  val RCI   = 4.U(WIDTH.W)
  val RWI   = 5.U(WIDTH.W)
  val RSI   = 6.U(WIDTH.W)
}

object exc {
  val WIDTH        = log2Ceil(10)
  val NOP          = 0.U(WIDTH.W)
  val EBREAK       = 1.U(WIDTH.W)
  val ECALL        = 2.U(WIDTH.W)
  val ILLEGAL_INST = 3.U(WIDTH.W)
  val MRET         = 4.U(WIDTH.W)
  val SRET         = 5.U(WIDTH.W)
  val WFI          = 6.U(WIDTH.W)
  val SFENCE_VMA   = 7.U(WIDTH.W)
  val FENCE        = 8.U(WIDTH.W)
  val FENCE_I      = 9.U(WIDTH.W)
}

object ALL {
  val defaultList =
    rs1.NOP :: rs2.NOP :: rd.NOP :: op1.NOP :: op2.NOP :: imm.N :: alu.NOP :: branch.NOP :: mul.NOP :: div.NOP :: mem.NOP :: csr.NOP :: exc.ILLEGAL_INST :: Nil
  val instList    = RV32I.table ++ RVMSU.table
}
