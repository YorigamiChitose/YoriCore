package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.IDU.module._
import Tools.Config.Config

class MULBundle extends Bundle {
  val mulCtrl = Input(mul())                     // 乘法器控制信号
  val op1     = Input(UInt(Config.Data.XLEN.W))  // 操作数1
  val op2     = Input(UInt(Config.Data.XLEN.W))  // 操作数2
  val flush   = Input(Bool())                    // 冲刷
  val result  = Output(UInt(Config.Data.XLEN.W)) // 计算结果
  val ready   = Output(Bool())                   // 准备完成
}

class MUL extends Module {
  val ioMUL = IO(new MULBundle())

  val result = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioMUL.mulCtrl === mul.MUL)    -> (ioMUL.op1.asUInt * ioMUL.op2.asUInt).asUInt,
      (ioMUL.mulCtrl === mul.MULH)   -> ((ioMUL.op1.asSInt.pad(Config.Data.XLEN * 2) * ioMUL.op2.asSInt.pad(Config.Data.XLEN * 2)).asSInt >> Config.Data.XLEN.U).asUInt,
      (ioMUL.mulCtrl === mul.MULHU)  -> ((ioMUL.op1.asUInt.pad(Config.Data.XLEN * 2) * ioMUL.op2.asUInt.pad(Config.Data.XLEN * 2)).asUInt >> Config.Data.XLEN.U).asUInt,
      (ioMUL.mulCtrl === mul.MULHSU) -> ((ioMUL.op1.asSInt.pad(Config.Data.XLEN * 2) * ioMUL.op2.asUInt.pad(Config.Data.XLEN * 2)).asSInt >> Config.Data.XLEN.U).asUInt
    )
  )

  ioMUL.result := result
  ioMUL.ready  := true.B
}

class AdvancedMUL extends Module {
  val ioMUL = IO(new MULBundle())

  val XLEN = Config.Data.XLEN
  val accW = XLEN + 4

  val sIdle :: sBusy :: sFix :: sDone :: Nil = Enum(4)

  val state  = RegInit(sIdle)
  val acc    = RegInit(0.U(accW.W))
  val low    = RegInit(0.U(XLEN.W))
  val breg   = RegInit(0.U((XLEN + 1).W))
  val mulA   = RegInit(0.U((XLEN + 2).W))
  val corrEn = RegInit(false.B)
  val ctrl   = RegInit(mul.NOP)
  val cnt    = RegInit(0.U(log2Ceil(XLEN / 2).W))
  val result = RegInit(0.U(XLEN.W))

  val aSigned = (ioMUL.mulCtrl === mul.MULH) || (ioMUL.mulCtrl === mul.MULHSU)
  val bSigned = ioMUL.mulCtrl === mul.MULH
  val signA   = Mux(aSigned, ioMUL.op1(XLEN - 1), 0.U)

  val start = (state === sIdle) && (ioMUL.mulCtrl =/= mul.NOP)
  val last  = cnt === (XLEN / 2 - 1).U

  val win    = breg(2, 0)
  val addend = MuxCase(
    0.U((XLEN + 3).W),
    Seq(
      (win === "b000".U) -> 0.U((XLEN + 3).W),
      (win === "b001".U) -> Cat(0.U(1.W), mulA),
      (win === "b010".U) -> Cat(0.U(1.W), mulA),
      (win === "b011".U) -> Cat(mulA, 0.U(1.W)),
      (win === "b100".U) -> (0.U((XLEN + 3).W) - Cat(mulA, 0.U(1.W))),
      (win === "b101".U) -> (0.U((XLEN + 3).W) - Cat(0.U(1.W), mulA)),
      (win === "b110".U) -> (0.U((XLEN + 3).W) - Cat(0.U(1.W), mulA)),
      (win === "b111".U) -> 0.U((XLEN + 3).W)
    )
  )

  val t       = acc + addend
  val accNext = Cat(Fill(2, t(accW - 1)), t(accW - 1, 2))
  val highFix = acc + Mux(corrEn, Cat(0.U(2.W), mulA), 0.U)

  ioMUL.result := result
  ioMUL.ready  := (state === sDone) || (state === sIdle && ioMUL.mulCtrl === mul.NOP)

  when(ioMUL.flush) {
    state := sIdle
  }.elsewhen(start) {
    mulA   := Cat(signA, signA, ioMUL.op1)
    breg   := Cat(ioMUL.op2, 0.U(1.W))
    corrEn := !bSigned && ioMUL.op2(XLEN - 1)
    ctrl   := ioMUL.mulCtrl
    acc    := 0.U
    low    := 0.U
    cnt    := 0.U
    state  := sBusy
  }.elsewhen(state === sBusy) {
    acc  := accNext
    low  := Cat(t(1, 0), low(XLEN - 1, 2))
    breg := breg >> 2
    when(last) {
      state := sFix
    }.otherwise {
      cnt := cnt + 1.U
    }
  }.elsewhen(state === sFix) {
    result := Mux(ctrl === mul.MUL, low, highFix(XLEN - 1, 0))
    state  := sDone
  }.elsewhen(state === sDone) {
    state := sIdle
  }
}
