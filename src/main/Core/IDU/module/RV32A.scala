package Core.IDU.module

import chisel3._
import chisel3.util._

object RV32A {
  object INST {
    // aq/rl 位于 bits[26:25]，本次以 ?? 通配忽略
    // opcode = 0101111, funct3 = 010
    val LR      = BitPat("b00010??_00000_?????_010_?????_0101111")
    val SC      = BitPat("b00011??_?????_?????_010_?????_0101111")
    val AMOSWAP = BitPat("b00001??_?????_?????_010_?????_0101111")
    val AMOADD  = BitPat("b00000??_?????_?????_010_?????_0101111")
    val AMOXOR  = BitPat("b00100??_?????_?????_010_?????_0101111")
    val AMOOR   = BitPat("b01000??_?????_?????_010_?????_0101111")
    val AMOAND  = BitPat("b01100??_?????_?????_010_?????_0101111")
    val AMOMIN  = BitPat("b10000??_?????_?????_010_?????_0101111")
    val AMOMAX  = BitPat("b10100??_?????_?????_010_?????_0101111")
    val AMOMINU = BitPat("b11000??_?????_?????_010_?????_0101111")
    val AMOMAXU = BitPat("b11100??_?????_?????_010_?????_0101111")
  }
  val table = Array(
    INST.LR      -> DecodeSig(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.LR, csr.NOP, exc.NOP),
    INST.SC      -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.SC, csr.NOP, exc.NOP),
    INST.AMOSWAP -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOSWAP, csr.NOP, exc.NOP),
    INST.AMOADD  -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOADD, csr.NOP, exc.NOP),
    INST.AMOXOR  -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOXOR, csr.NOP, exc.NOP),
    INST.AMOAND  -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOAND, csr.NOP, exc.NOP),
    INST.AMOOR   -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOOR, csr.NOP, exc.NOP),
    INST.AMOMIN  -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOMIN, csr.NOP, exc.NOP),
    INST.AMOMAX  -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOMAX, csr.NOP, exc.NOP),
    INST.AMOMINU -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOMINU, csr.NOP, exc.NOP),
    INST.AMOMAXU -> DecodeSig(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.AMOMAXU, csr.NOP, exc.NOP)
  )
}
