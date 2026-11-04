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

class AdvancedDIV extends Module {
  val ioDIV = IO(new DIVBundle())

  val XLEN = Config.Data.XLEN

  val sIdle :: sBusy :: sFix :: sDone :: Nil = Enum(4)

  val state      = RegInit(sIdle)
  val rem        = RegInit(0.U(XLEN.W))
  val dividend   = RegInit(0.U(XLEN.W))
  val divisor    = RegInit(0.U(XLEN.W))
  val quo        = RegInit(0.U(XLEN.W))
  val aNeg       = RegInit(false.B)
  val bNeg       = RegInit(false.B)
  val isDivZero  = RegInit(false.B)
  val isOverflow = RegInit(false.B)
  val rawOp1     = RegInit(0.U(XLEN.W))
  val ctrl       = RegInit(div.NOP)
  val cnt        = RegInit(0.U(log2Ceil(XLEN).W))
  val result     = RegInit(0.U(XLEN.W))

  val aSigned = (ioDIV.divCtrl === div.DIV) || (ioDIV.divCtrl === div.REM)
  val aNegCur = aSigned && ioDIV.op1(XLEN - 1)
  val bNegCur = aSigned && ioDIV.op2(XLEN - 1)

  val start = (state === sIdle) && (ioDIV.divCtrl =/= div.NOP)
  val last  = cnt === (XLEN - 1).U

  val shifted = Cat(rem, dividend(XLEN - 1))
  val diff    = shifted - divisor
  val enough  = !diff(XLEN)

  val quoS = Mux(aNeg ^ bNeg, 0.U - quo, quo)
  val remS = Mux(aNeg, 0.U - rem, rem)

  ioDIV.result := result
  ioDIV.ready  := (state === sDone) || (state === sIdle && ioDIV.divCtrl === div.NOP)

  when(ioDIV.flush) {
    state := sIdle
  }.elsewhen(start) {
    dividend   := Mux(aNegCur, 0.U - ioDIV.op1, ioDIV.op1)
    divisor    := Mux(bNegCur, 0.U - ioDIV.op2, ioDIV.op2)
    aNeg       := aNegCur
    bNeg       := bNegCur
    isDivZero  := ioDIV.op2 === 0.U
    isOverflow := aSigned && (ioDIV.op1 === "h8000_0000".U) && (ioDIV.op2 === "hFFFF_FFFF".U)
    rawOp1     := ioDIV.op1
    ctrl       := ioDIV.divCtrl
    rem        := 0.U
    quo        := 0.U
    cnt        := 0.U
    state      := sBusy
  }.elsewhen(state === sBusy) {
    quo      := Cat(quo(XLEN - 2, 0), enough)
    rem      := Mux(enough, diff(XLEN - 1, 0), shifted(XLEN - 1, 0))
    dividend := (dividend << 1)(XLEN - 1, 0)
    when(last) {
      state := sFix
    }.otherwise {
      cnt := cnt + 1.U
    }
  }.elsewhen(state === sFix) {
    result := MuxCase(
      0.U(XLEN.W),
      Seq(
        (ctrl === div.DIV)  -> Mux(isDivZero, "hFFFF_FFFF".U, Mux(isOverflow, "h8000_0000".U, quoS)),
        (ctrl === div.REM)  -> Mux(isDivZero, rawOp1, Mux(isOverflow, 0.U(XLEN.W), remS)),
        (ctrl === div.DIVU) -> Mux(isDivZero, "hFFFF_FFFF".U, quo),
        (ctrl === div.REMU) -> Mux(isDivZero, rawOp1, rem)
      )
    )
    state  := sDone
  }.elsewhen(state === sDone) {
    state := sIdle
  }
}
