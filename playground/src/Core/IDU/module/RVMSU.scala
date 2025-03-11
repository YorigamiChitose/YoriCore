package Core.IDU.module

import chisel3._
import chisel3.util._

object RVMSU {
  object INST {
    val SRET       = BitPat("b0001000_00010_00000_000_00000_11100_11")
    val MRET       = BitPat("b0011000_00010_00000_000_00000_11100_11")
    val WFI        = BitPat("b0001000_00101_00000_000_00000_11100_11")
    val SFENCE_VMA = BitPat("b0001000_00101_00000_000_00000_11100_11")
  }
  val table = Array(
    INST.SRET       -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.SRET),
    INST.MRET       -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.MRET),
    INST.WFI        -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.WFI),
    INST.SFENCE_VMA -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.SFENCE_VMA)
  )
}
