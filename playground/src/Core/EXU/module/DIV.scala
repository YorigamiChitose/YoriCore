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

  val sIdle :: sStart :: sFinish :: Nil = Enum(3)
  val stateReg                          = RegInit(sIdle)

  val divisorReg = RegInit(0.U((2 * Config.Data.XLEN).W)) // 除数寄存器

  val quotientReg  = RegInit(0.U(Config.Data.XLEN.W)) // 商寄存器
  val remainderReg = RegInit(0.U(Config.Data.XLEN.W)) // 余数寄存器

  val countReg = RegInit(0.U(log2Ceil(Config.Data.XLEN + 1).W)) // 计数器寄存器

  val isNegative = RegInit("b00".U.asTypeOf(new Bundle {
    val quo = Bool()
    val rem = Bool()
  })) // 符号标志寄存器

  switch(stateReg) {
    is(sIdle) {
      when(ioDIV.flush) {
        stateReg := sIdle
      }.elsewhen(ioDIV.divCtrl =/= div.NOP) {
        stateReg       := sStart
        remainderReg   := MuxCase(
          0.U(Config.Data.XLEN.W),
          Seq(
            (ioDIV.divCtrl === div.DIV)  -> Mux(ioDIV.op1(Config.Data.XLEN - 1), -ioDIV.op1, ioDIV.op1),
            (ioDIV.divCtrl === div.REM)  -> Mux(ioDIV.op1(Config.Data.XLEN - 1), -ioDIV.op1, ioDIV.op1),
            (ioDIV.divCtrl === div.DIVU) -> ioDIV.op1,
            (ioDIV.divCtrl === div.REMU) -> ioDIV.op1
          )
        )
        divisorReg     := MuxCase(
          0.U((2 * Config.Data.XLEN).W),
          Seq(
            (ioDIV.divCtrl === div.DIV)  -> Cat(Mux(ioDIV.op2(Config.Data.XLEN - 1), -ioDIV.op2, ioDIV.op2), Fill(Config.Data.XLEN, 0.U(1.W))),
            (ioDIV.divCtrl === div.REM)  -> Cat(Mux(ioDIV.op2(Config.Data.XLEN - 1), -ioDIV.op2, ioDIV.op2), Fill(Config.Data.XLEN, 0.U(1.W))),
            (ioDIV.divCtrl === div.DIVU) -> Cat(ioDIV.op2, Fill(Config.Data.XLEN, 0.U(1.W))),
            (ioDIV.divCtrl === div.REMU) -> Cat(ioDIV.op2, Fill(Config.Data.XLEN, 0.U(1.W)))
          )
        )
        countReg       := MuxCase(
          0.U(log2Ceil(Config.Data.XLEN + 1).W),
          Seq(
            (ioDIV.divCtrl === div.DIV)  -> (Config.Data.XLEN + 1).U,
            (ioDIV.divCtrl === div.REM)  -> (Config.Data.XLEN + 1).U,
            (ioDIV.divCtrl === div.DIVU) -> (Config.Data.XLEN + 1).U,
            (ioDIV.divCtrl === div.REMU) -> (Config.Data.XLEN + 1).U
          )
        )
        quotientReg    := 0.U
        isNegative.quo := MuxCase(
          false.B,
          Seq(
            (ioDIV.divCtrl === div.DIV)  -> (ioDIV.op1(Config.Data.XLEN - 1) ^ ioDIV.op2(Config.Data.XLEN - 1)),
            (ioDIV.divCtrl === div.REM)  -> (ioDIV.op1(Config.Data.XLEN - 1) ^ ioDIV.op2(Config.Data.XLEN - 1)),
            (ioDIV.divCtrl === div.DIVU) -> false.B,
            (ioDIV.divCtrl === div.REMU) -> false.B
          )
        )
        isNegative.rem := MuxCase(
          false.B,
          Seq(
            (ioDIV.divCtrl === div.DIV)  -> ioDIV.op1(Config.Data.XLEN - 1).asBool,
            (ioDIV.divCtrl === div.REM)  -> ioDIV.op1(Config.Data.XLEN - 1).asBool,
            (ioDIV.divCtrl === div.DIVU) -> false.B,
            (ioDIV.divCtrl === div.REMU) -> false.B
          )
        )
      }
    }
    is(sStart) {
      when(countReg === 0.U) {
        stateReg     := sFinish
        quotientReg  := Mux(isNegative.quo, -quotientReg, quotientReg)
        remainderReg := Mux(isNegative.rem, -remainderReg, remainderReg)
      }.otherwise {
        countReg     := countReg - 1.U
        divisorReg   := divisorReg >> 1.U
        quotientReg  := Cat(quotientReg(Config.Data.XLEN - 2, 0), (remainderReg >= divisorReg).asBool)
        remainderReg := remainderReg - Mux(remainderReg >= divisorReg, divisorReg, 0.U)
      }
    }
    is(sFinish) {
      stateReg := sIdle
    }
  }

  ioDIV.result := MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioDIV.divCtrl === div.DIV)  -> quotientReg,
      (ioDIV.divCtrl === div.REM)  -> remainderReg,
      (ioDIV.divCtrl === div.DIVU) -> quotientReg,
      (ioDIV.divCtrl === div.REMU) -> remainderReg
    )
  )
  ioDIV.ready  := (stateReg === sFinish)
}
