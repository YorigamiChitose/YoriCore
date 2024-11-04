package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.IDU.module._
import Tools.Config.Config

class MULBundle extends Bundle {
  val mulCtrl = Input(UInt(mul.WIDTH.W))         // 乘法器控制信号
  val op1     = Input(UInt(Config.Data.XLEN.W))  // 操作数1
  val op2     = Input(UInt(Config.Data.XLEN.W))  // 操作数2
  val flush   = Input(Bool())                    // 冲刷
  val result  = Output(UInt(Config.Data.XLEN.W)) // 计算结果
  val ready   = Output(Bool())                   // 准备完成
}

class MUL extends Module {
  val ioMUL = IO(new MULBundle()) // 乘法器IO

  // 计算结果
  val result = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioMUL.mulCtrl === mul.MUL)    -> (ioMUL.op1.asUInt * ioMUL.op2.asUInt).asUInt, // 无符号乘法
      (ioMUL.mulCtrl === mul.MULH)   -> (ioMUL.op1(Config.Data.XLEN - 1, Config.Data.XLEN / 2).asSInt * ioMUL
        .op2(Config.Data.XLEN - 1, Config.Data.XLEN / 2)
        .asSInt).asUInt, // 高位符号乘法
      (ioMUL.mulCtrl === mul.MULHU)  -> (ioMUL.op1(Config.Data.XLEN - 1, Config.Data.XLEN / 2).asUInt * ioMUL
        .op2(Config.Data.XLEN - 1, Config.Data.XLEN / 2)
        .asUInt).asUInt, // 高位无符号乘法
      (ioMUL.mulCtrl === mul.MULHSU) -> (ioMUL.op1(Config.Data.XLEN - 1, Config.Data.XLEN / 2).asSInt * ioMUL
        .op2(Config.Data.XLEN - 1, Config.Data.XLEN / 2)
        .asUInt).asUInt // 高位有符号乘无符号
    )
  )

  ioMUL.result := result // 计算结果
  ioMUL.ready  := true.B // 准备完成
}
