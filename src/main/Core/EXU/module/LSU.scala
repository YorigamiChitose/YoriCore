package Core.EXU.module

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.IDU.module._

object LSUCtrl {
  object len {
    val WIDTH = log2Ceil(4)
    val NOP   = 0.U(WIDTH.W)
    val B     = 1.U(WIDTH.W)
    val H     = 2.U(WIDTH.W)
    val W     = 3.U(WIDTH.W)
  }
  //                     isSigned len
  val defaultList = List(false.B, len.NOP)
  val table = Array(
    BitPat(mem.NOP)     -> List(false.B, len.NOP),
    BitPat(mem.LB)      -> List(true.B, len.B),
    BitPat(mem.LH)      -> List(true.B, len.H),
    BitPat(mem.LBU)     -> List(false.B, len.B),
    BitPat(mem.LHU)     -> List(false.B, len.H),
    BitPat(mem.LW)      -> List(true.B, len.W),
    BitPat(mem.SB)      -> List(false.B, len.B),
    BitPat(mem.SH)      -> List(false.B, len.H),
    BitPat(mem.SW)      -> List(false.B, len.W),
    BitPat(mem.AMOSWAP) -> List(false.B, len.W),
    BitPat(mem.AMOADD)  -> List(false.B, len.W),
    BitPat(mem.AMOXOR)  -> List(false.B, len.W),
    BitPat(mem.AMOAND)  -> List(false.B, len.W),
    BitPat(mem.AMOOR)   -> List(false.B, len.W),
    BitPat(mem.AMOMIN)  -> List(false.B, len.W),
    BitPat(mem.AMOMAX)  -> List(false.B, len.W),
    BitPat(mem.AMOMINU) -> List(false.B, len.W),
    BitPat(mem.AMOMAXU) -> List(false.B, len.W),
    BitPat(mem.LR)      -> List(false.B, len.W),
    BitPat(mem.SC)      -> List(false.B, len.W)
  )
}

class LSUBundle extends Bundle {
  val memCtrl = Input(UInt(mem.WIDTH.W))         // LSU控制信号
  val addr    = Input(UInt(Config.Addr.Width.W)) // 地址计算结果
  val dataIn  = Input(UInt(Config.Data.XLEN.W))  // 保存数据（AMO = rs2 操作数）
  val flush   = Input(Bool())                    // 冲刷
  val dataOut = Output(UInt(Config.Data.XLEN.W)) // LSU结果（AMO/LR=旧值，SC=成败标志）
  val ready   = Output(Bool())                   // LSU准备完成
}

class LSU extends Module {
  val ioLSU  = IO(new LSUBundle)           // LSU IO
  val ioDMem = IO(Flipped(new DMemBundle)) // DMem IO

  val XLEN = Config.Data.XLEN
  val AW   = Config.Addr.Width

  // 普通 load/store 的长度与符号
  val isSigned :: dataLen :: Nil = ListLookup(ioLSU.memCtrl, LSUCtrl.defaultList, LSUCtrl.table)

  val sIdle :: sRead :: sWrite :: sDone :: Nil = Enum(4)
  val state                                    = RegInit(sIdle)

  // 锁存输入供多周期使用
  val ctrlReg = RegInit(mem.NOP)
  val addrReg = RegInit(0.U(AW.W))
  val dataReg = RegInit(0.U(XLEN.W)) // rs2 / 存数数据
  val oldReg  = RegInit(0.U(XLEN.W)) // 读回的旧值

  // reservation set
  val resValid = RegInit(false.B)
  val resAddr  = RegInit(0.U(AW.W))
  val scOk     = RegInit(false.B)

  val start = (state === sIdle) && (ioLSU.memCtrl =/= mem.NOP)

  // 当前操作类型：idle 期看 memCtrl，运行期看 ctrlReg
  val curCtrl = Mux(state === sIdle, ioLSU.memCtrl, ctrlReg)
  val curAddr = Mux(state === sIdle, ioLSU.addr, addrReg)

  val isLoad  = (curCtrl === mem.LB) || (curCtrl === mem.LH) || (curCtrl === mem.LBU) || (curCtrl === mem.LHU) || (curCtrl === mem.LW)
  val isStore = (curCtrl === mem.SB) || (curCtrl === mem.SH) || (curCtrl === mem.SW)
  val isAMO   = (curCtrl === mem.AMOSWAP) || (curCtrl === mem.AMOADD) || (curCtrl === mem.AMOXOR) || (curCtrl === mem.AMOAND) ||
    (curCtrl === mem.AMOOR) || (curCtrl === mem.AMOMIN) || (curCtrl === mem.AMOMAX) ||
    (curCtrl === mem.AMOMINU) || (curCtrl === mem.AMOMAXU)
  val isLR    = curCtrl === mem.LR
  val isSC    = curCtrl === mem.SC

  val scSuccess = resValid && (curAddr === resAddr)

  // AMO 新值计算
  val amoNew    = MuxCase(
    0.U(XLEN.W),
    Seq(
      (ctrlReg === mem.AMOSWAP) -> dataReg,
      (ctrlReg === mem.AMOADD)  -> (oldReg + dataReg),
      (ctrlReg === mem.AMOXOR)  -> (oldReg ^ dataReg),
      (ctrlReg === mem.AMOAND)  -> (oldReg & dataReg),
      (ctrlReg === mem.AMOOR)   -> (oldReg | dataReg),
      (ctrlReg === mem.AMOMIN)  -> Mux(oldReg.asSInt < dataReg.asSInt, oldReg, dataReg),
      (ctrlReg === mem.AMOMAX)  -> Mux(oldReg.asSInt > dataReg.asSInt, oldReg, dataReg),
      (ctrlReg === mem.AMOMINU) -> Mux(oldReg < dataReg, oldReg, dataReg),
      (ctrlReg === mem.AMOMAXU) -> Mux(oldReg > dataReg, oldReg, dataReg)
    )
  )
  val writeData = Mux(isAMO, amoNew, dataReg)

  // DMem IO
  ioDMem.readEn   := state === sRead
  ioDMem.writeEn  := state === sWrite
  ioDMem.isSigned := isSigned
  ioDMem.dataLen  := dataLen
  ioDMem.addr     := addrReg
  ioDMem.dataIn   := writeData

  // LSU IO
  ioLSU.dataOut := Mux(ctrlReg === mem.SC, Mux(scOk, 0.U(XLEN.W), 1.U(XLEN.W)), oldReg)
  ioLSU.ready   := (state === sIdle && ioLSU.memCtrl === mem.NOP) || (state === sDone)

  when(ioLSU.flush) {
    state := sIdle
  }.elsewhen(start) {
    ctrlReg := ioLSU.memCtrl
    addrReg := ioLSU.addr
    dataReg := ioLSU.dataIn
    when(isSC) {
      scOk := scSuccess
    }
    when(isStore || isAMO) {
      when(ioLSU.addr === resAddr) {
        resValid := false.B
      }
    }
    when(isLoad || isAMO || isLR) {
      state := sRead
    }.elsewhen(isStore || (isSC && scSuccess)) {
      state := sWrite
    }.elsewhen(isSC) {
      state := sDone
    }
  }.elsewhen(state === sRead) {
    when(ioDMem.ready) {
      oldReg := ioDMem.dataOut
      when(isLR) {
        resValid := true.B
        resAddr  := addrReg
      }
      when(isAMO) {
        state := sWrite
      }.otherwise {
        state := sDone
      }
    }
  }.elsewhen(state === sWrite) {
    when(ioDMem.ready) {
      state := sDone
    }
  }.elsewhen(state === sDone) {
    state := sIdle
  }
}
