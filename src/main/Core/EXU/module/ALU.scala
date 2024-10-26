package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.IDU.module._

class ALUBundle extends Bundle {
  val aluCtrl = Input(UInt(alu.WIDTH.W))         // ALU控制信号
  val op1     = Input(UInt(Config.Data.XLEN.W))  // 操作数1
  val op2     = Input(UInt(Config.Data.XLEN.W))  // 操作数2
  val result  = Output(UInt(Config.Data.XLEN.W)) // 结果
}

class ALU extends Module {
  val ioALU = IO(new ALUBundle()) // EXU - ALU 通路

  val result = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioALU.aluCtrl === alu.NOP)  -> 0.U,                                                                       // 不计算
      (ioALU.aluCtrl === alu.ADD)  -> (ioALU.op1 + ioALU.op2).asUInt,                                            // 加法
      (ioALU.aluCtrl === alu.SUB)  -> (ioALU.op1 - ioALU.op2).asUInt,                                            // 减法
      (ioALU.aluCtrl === alu.SLT)  -> (ioALU.op1.asSInt < ioALU.op2.asSInt).asUInt,                              // 有符号比较
      (ioALU.aluCtrl === alu.SLTU) -> (ioALU.op1.asUInt < ioALU.op2.asUInt).asUInt,                              // 无符号比较
      (ioALU.aluCtrl === alu.XOR)  -> (ioALU.op1 ^ ioALU.op2).asUInt,                                            // 抑或
      (ioALU.aluCtrl === alu.OR)   -> (ioALU.op1 | ioALU.op2).asUInt,                                            // 或
      (ioALU.aluCtrl === alu.AND)  -> (ioALU.op1 & ioALU.op2).asUInt,                                            // 与
      (ioALU.aluCtrl === alu.SLL)  -> (ioALU.op1.asUInt << ioALU.op2(log2Ceil(Config.Data.XLEN) - 1, 0)).asUInt, // 逻辑左移
      (ioALU.aluCtrl === alu.SRL)  -> (ioALU.op1.asUInt >> ioALU.op2(log2Ceil(Config.Data.XLEN) - 1, 0)).asUInt, // 逻辑右移
      (ioALU.aluCtrl === alu.SRA)  -> (ioALU.op1.asSInt >> ioALU.op2(log2Ceil(Config.Data.XLEN) - 1, 0)).asUInt  // 算数右移
    )
  )

  ioALU.result := result // ALU计算结果
}
