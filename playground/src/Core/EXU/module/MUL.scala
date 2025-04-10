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
  val ioMUL = IO(new MULBundle())

  val sIdle :: sStart :: sFinish :: Nil = Enum(3)

  val state = RegInit(sIdle)

  val multiplierReg   = RegInit(0.U((Config.Data.XLEN + 3).W))
  val multiplicandReg = RegInit(0.U((Config.Data.XLEN * 2).W))
  val resultReg       = RegInit(0.U((Config.Data.XLEN * 2).W))
  val countReg        = RegInit(0.U(log2Ceil((Config.Data.XLEN + 3) / 2).W))

  switch(state) {
    is(sIdle) {
      when(ioMUL.flush) {
        state := sIdle
      }.elsewhen(ioMUL.mulCtrl =/= mul.NOP) {
        state         := sStart
        resultReg     := 0.U
        multiplierReg := MuxCase(
          0.U,
          Seq(
            (ioMUL.mulCtrl === mul.MUL)    -> Cat(Fill(2, ioMUL.op1(Config.Data.XLEN - 1)), ioMUL.op1, 0.U),
            (ioMUL.mulCtrl === mul.MULH)   -> Cat(Fill(2, ioMUL.op1(Config.Data.XLEN - 1)), ioMUL.op1, 0.U),
            (ioMUL.mulCtrl === mul.MULHU)  -> Cat(0.U(2.W), ioMUL.op1, 0.U),
            (ioMUL.mulCtrl === mul.MULHSU) -> Cat(Fill(2, ioMUL.op1(Config.Data.XLEN - 1)), ioMUL.op1, 0.U)
          )
        )

        multiplicandReg := MuxCase(
          0.U,
          Seq(
            (ioMUL.mulCtrl === mul.MUL)    -> Cat(Fill(Config.Data.XLEN, ioMUL.op2(Config.Data.XLEN - 1)), ioMUL.op2),
            (ioMUL.mulCtrl === mul.MULH)   -> Cat(Fill(Config.Data.XLEN, ioMUL.op2(Config.Data.XLEN - 1)), ioMUL.op2),
            (ioMUL.mulCtrl === mul.MULHU)  -> Cat(0.U(Config.Data.XLEN.W), ioMUL.op2),
            (ioMUL.mulCtrl === mul.MULHSU) -> Cat(0.U(Config.Data.XLEN.W), ioMUL.op2)
          )
        )

        countReg := ((Config.Data.XLEN + 3) / 2).U
      }
    }
    is(sStart) {
      when(countReg === 0.U) {
        state := sFinish
      }.otherwise {
        countReg  := countReg - 1.U
        resultReg := resultReg + MuxLookup(multiplierReg(2, 0), 0.U)(
          Seq(
            "b000".U -> 0.U,
            "b001".U -> multiplicandReg,
            "b010".U -> multiplicandReg,
            "b011".U -> (multiplicandReg << 1.U),
            "b100".U -> ((-multiplicandReg) << 1.U),
            "b101".U -> (-multiplicandReg),
            "b110".U -> (-multiplicandReg),
            "b111".U -> 0.U
          )
        )
      }
      multiplicandReg := multiplicandReg << 2.U
      multiplierReg   := multiplierReg >> 2.U
    }
    is(sFinish) {
      state := sIdle
    }
  }
  ioMUL.result := MuxCase(
    0.U,
    Seq(
      (ioMUL.mulCtrl === mul.MUL)    -> resultReg(Config.Data.XLEN - 1, 0),
      (ioMUL.mulCtrl === mul.MULH)   -> resultReg(Config.Data.XLEN * 2 - 1, Config.Data.XLEN),
      (ioMUL.mulCtrl === mul.MULHU)  -> resultReg(Config.Data.XLEN * 2 - 1, Config.Data.XLEN),
      (ioMUL.mulCtrl === mul.MULHSU) -> resultReg(Config.Data.XLEN * 2 - 1, Config.Data.XLEN)
    )
  )
  ioMUL.ready  := (state === sFinish)
}
