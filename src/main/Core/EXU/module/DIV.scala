package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.IDU.module._
import Tools.Config.Config

class DIVBundle extends Bundle {
  val divCtrl = Input(UInt(div.WIDTH.W))         // 除法器控制信号
  val op1     = Input(UInt(Config.Data.XLEN.W))  // 操作数1
  val op2     = Input(UInt(Config.Data.XLEN.W))  // 操作数2
  val flush   = Input(Bool())                    // 冲刷
  val result  = Output(UInt(Config.Data.XLEN.W)) // 计算结果
  val ready   = Output(Bool())                   // 准备完成
}

class DIV extends Module {
  val ioDIV = IO(new DIVBundle()) // 除法器IO

  // 边界情况
  val divZero  = ioDIV.op2 === 0.U                                                // 除数为0
  val overflow = (ioDIV.op1 === "h8000_0000".U) && (ioDIV.op2 === "hFFFF_FFFF".U) // 符号溢出

  // 原始计算结果
  val quoS = (ioDIV.op1.asSInt / ioDIV.op2.asSInt).asUInt // 符号除
  val remS = (ioDIV.op1.asSInt % ioDIV.op2.asSInt).asUInt // 符号取余
  val quoU = (ioDIV.op1.asUInt / ioDIV.op2.asUInt).asUInt // 无符号除
  val remU = (ioDIV.op1.asUInt % ioDIV.op2.asUInt).asUInt // 无符号取余

  // 计算结果
  val result = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioDIV.divCtrl === div.DIV)  -> Mux(divZero, "hFFFF_FFFF".U, Mux(overflow, "h8000_0000".U, quoS)),     // 符号除
      (ioDIV.divCtrl === div.REM)  -> Mux(divZero, ioDIV.op1, Mux(overflow, 0.U(Config.Data.XLEN.W), remS)), // 符号取余
      (ioDIV.divCtrl === div.DIVU) -> Mux(divZero, "hFFFF_FFFF".U, quoU),                                    // 无符号除
      (ioDIV.divCtrl === div.REMU) -> Mux(divZero, ioDIV.op1, remU)                                          // 无符号取余
    )
  )

  ioDIV.result := result // 计算结果
  ioDIV.ready  := true.B // 准备完成
}
