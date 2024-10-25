package Core.IDU.module

import chisel3._
import chisel3.util._

object RV32I {
  object INST {
    // 移位
    val SLL     = BitPat("b0000000_?????_?????_001_?????_01100_11")
    val SLLI    = BitPat("b000000?_?????_?????_001_?????_00100_11")
    val SRL     = BitPat("b0000000_?????_?????_101_?????_01100_11")
    val SRLI    = BitPat("b000000?_?????_?????_101_?????_00100_11")
    val SRA     = BitPat("b0100000_?????_?????_101_?????_01100_11")
    val SRAI    = BitPat("b010000?_?????_?????_101_?????_00100_11")
    // 算数
    val ADD     = BitPat("b0000000_?????_?????_000_?????_01100_11")
    val ADDI    = BitPat("b???????_?????_?????_000_?????_00100_11")
    val SUB     = BitPat("b0100000_?????_?????_000_?????_01100_11")
    val LUI     = BitPat("b???????_?????_?????_???_?????_01101_11")
    val AUIPC   = BitPat("b???????_?????_?????_???_?????_00101_11")
    // 逻辑
    val XOR     = BitPat("b0000000_?????_?????_100_?????_01100_11")
    val XORI    = BitPat("b???????_?????_?????_100_?????_00100_11")
    val OR      = BitPat("b0000000_?????_?????_110_?????_01100_11")
    val ORI     = BitPat("b???????_?????_?????_110_?????_00100_11")
    val AND     = BitPat("b0000000_?????_?????_111_?????_01100_11")
    val ANDI    = BitPat("b???????_?????_?????_111_?????_00100_11")
    // 比较置位
    val SLT     = BitPat("b0000000_?????_?????_010_?????_01100_11")
    val SLTI    = BitPat("b???????_?????_?????_010_?????_00100_11")
    val SLTU    = BitPat("b0000000_?????_?????_011_?????_01100_11")
    val SLTIU   = BitPat("b???????_?????_?????_011_?????_00100_11")
    // 分支
    val BEQ     = BitPat("b???????_?????_?????_000_?????_11000_11")
    val BNE     = BitPat("b???????_?????_?????_001_?????_11000_11")
    val BLT     = BitPat("b???????_?????_?????_100_?????_11000_11")
    val BGE     = BitPat("b???????_?????_?????_101_?????_11000_11")
    val BLTU    = BitPat("b???????_?????_?????_110_?????_11000_11")
    val BGEU    = BitPat("b???????_?????_?????_111_?????_11000_11")
    // 跳转链接
    val JAL     = BitPat("b???????_?????_?????_???_?????_11011_11")
    val JALR    = BitPat("b???????_?????_?????_000_?????_11001_11")
    // 同步
    val FENCE   = BitPat("b0000???_?????_00000_000_00000_0001111")
    val FENCE_I = BitPat("b0000000_00000_00000_001_00000_0001111")
    // 环境
    val ECALL   = BitPat("b0000000_00000_00000_000_00000_11100_11")
    val EBREAK  = BitPat("b0000000_00001_00000_000_00000_11100_11")
    // CSR
    val CSRRW   = BitPat("b???????_?????_?????_001_?????_11100_11")
    val CSRRS   = BitPat("b???????_?????_?????_010_?????_11100_11")
    val CSRRC   = BitPat("b???????_?????_?????_011_?????_11100_11")
    val CSRRWI  = BitPat("b???????_?????_?????_101_?????_11100_11")
    val CSRRSI  = BitPat("b???????_?????_?????_110_?????_11100_11")
    val CSRRCI  = BitPat("b???????_?????_?????_111_?????_11100_11")
    // 存取
    val LB      = BitPat("b???????_?????_?????_000_?????_00000_11")
    val LH      = BitPat("b???????_?????_?????_001_?????_00000_11")
    val LBU     = BitPat("b???????_?????_?????_100_?????_00000_11")
    val LHU     = BitPat("b???????_?????_?????_101_?????_00000_11")
    val LW      = BitPat("b???????_?????_?????_010_?????_00000_11")
    val SB      = BitPat("b???????_?????_?????_000_?????_01000_11")
    val SH      = BitPat("b???????_?????_?????_001_?????_01000_11")
    val SW      = BitPat("b???????_?????_?????_010_?????_01000_11")
  }
  val table = Array(
    // 移位
    INST.SLL     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SLL, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SLLI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.SLL, imm.SHAMT, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SRL     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SRL, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SRLI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.SRL, imm.SHAMT, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SRA     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SRA, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SRAI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.SRA, imm.SHAMT, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 算数
    INST.ADD     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.ADD, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.ADDI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SUB     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SUB, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.LUI     -> List(rs1.NOP, rs2.NOP, rd.EN, op1.ZERO, op2.IMM, alu.ADD, imm.U, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.AUIPC   -> List(rs1.NOP, rs2.NOP, rd.EN, op1.PC, op2.IMM, alu.ADD, imm.U, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 逻辑
    INST.XOR     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.XOR, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.XORI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.XOR, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.OR      -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.OR, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.ORI     -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.OR, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.AND     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.AND, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.ANDI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.AND, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 比较置位
    INST.SLT     -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SLT, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SLTI    -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.SLT, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SLTU    -> List(rs1.EN, rs2.EN, rd.EN, op1.RS1, op2.RS2, alu.SLTU, imm.R, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.SLTIU   -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.SLTU, imm.I, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 分支
    INST.BEQ     -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BEQ, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.BNE     -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BNE, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.BLT     -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BLT, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.BGE     -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BGE, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.BLTU    -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BLTU, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.BGEU    -> List(rs1.EN, rs2.EN, rd.NOP, op1.PC, op2.IMM, alu.ADD, imm.B, branch.BGEU, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 跳转链接
    INST.JAL     -> List(rs1.NOP, rs2.NOP, rd.EN, op1.PC, op2.FORE, alu.ADD, imm.J, branch.JAL, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    INST.JALR    -> List(rs1.EN, rs2.NOP, rd.EN, op1.PC, op2.FORE, alu.ADD, imm.I, branch.JALR, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.NOP),
    // 同步
    INST.FENCE   -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.FENCE), // TODO: fence decode
    INST.FENCE_I -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.FENCE_I),
    // 环境
    INST.ECALL   -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.ECALL),
    INST.EBREAK  -> List(rs1.NOP, rs2.NOP, rd.NOP, op1.NOP, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.NOP, exc.EBREAK),
    // CSR
    INST.CSRRW   -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RW, exc.NOP),
    INST.CSRRS   -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RS, exc.NOP),
    INST.CSRRC   -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.NOP, alu.NOP, imm.N, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RC, exc.NOP),
    INST.CSRRWI  -> List(rs1.NOP, rs2.NOP, rd.EN, op1.NOP, op2.IMM, alu.NOP, imm.ZIMM, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RWI, exc.NOP),
    INST.CSRRSI  -> List(rs1.NOP, rs2.NOP, rd.EN, op1.NOP, op2.IMM, alu.NOP, imm.ZIMM, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RSI, exc.NOP),
    INST.CSRRCI  -> List(rs1.NOP, rs2.NOP, rd.EN, op1.NOP, op2.IMM, alu.NOP, imm.ZIMM, branch.NOP, mul.NOP, div.NOP, mem.NOP, csr.RCI, exc.NOP),
    // 存取
    INST.LB      -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.LB, csr.NOP, exc.NOP),
    INST.LH      -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.LH, csr.NOP, exc.NOP),
    INST.LBU     -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.LBU, csr.NOP, exc.NOP),
    INST.LHU     -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.LHU, csr.NOP, exc.NOP),
    INST.LW      -> List(rs1.EN, rs2.NOP, rd.EN, op1.RS1, op2.IMM, alu.ADD, imm.I, branch.NOP, mul.NOP, div.NOP, mem.LW, csr.NOP, exc.NOP),
    INST.SB      -> List(rs1.EN, rs2.EN, rd.NOP, op1.RS1, op2.IMM, alu.ADD, imm.S, branch.NOP, mul.NOP, div.NOP, mem.SB, csr.NOP, exc.NOP),
    INST.SH      -> List(rs1.EN, rs2.EN, rd.NOP, op1.RS1, op2.IMM, alu.ADD, imm.S, branch.NOP, mul.NOP, div.NOP, mem.SH, csr.NOP, exc.NOP),
    INST.SW      -> List(rs1.EN, rs2.EN, rd.NOP, op1.RS1, op2.IMM, alu.ADD, imm.S, branch.NOP, mul.NOP, div.NOP, mem.SW, csr.NOP, exc.NOP)
  )
}
