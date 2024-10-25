package Core.Reg

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Reg.module._
import Core.IDU.module._

class BundleMEPC extends Bundle {
  val pc      = UInt(Config.Csr.DataWidth.W)
  def initVal = 0.U.asTypeOf(this)
}

class BundleMCAUSE extends Bundle {
  val causeId = UInt(Config.Csr.DataWidth.W)
  def initVal = 0.U.asTypeOf(this)
}

class BundleMSTATUS extends Bundle {
  val SD          = UInt(1.W)
  val XLEN_SUB_24 = UInt((Config.Csr.DataWidth - 24).W)
  val TSR         = UInt(1.W)
  val TW          = UInt(1.W)
  val TVM         = UInt(1.W)
  val MXR         = UInt(1.W)
  val SUM         = UInt(1.W)
  val MPRV        = UInt(1.W)
  val XS          = UInt(2.W)
  val FS          = UInt(2.W)
  val MPP         = UInt(2.W)
  val ZERO_2      = UInt(2.W)
  val SPP         = UInt(1.W)
  val MPIE        = UInt(1.W)
  val ZERO_1      = UInt(1.W)
  val SPIE        = UInt(1.W)
  val UPIE        = UInt(1.W)
  val MIE         = UInt(1.W)
  val ZERO_0      = UInt(1.W)
  val SIE         = UInt(1.W)
  val UIE         = UInt(1.W)
  def initVal     = "h1800".U.asTypeOf(this)
}

class BundleMTVEC extends Bundle {
  val causeId = UInt(Config.Csr.DataWidth.W)
  def initVal = 0.U.asTypeOf(this)
}

object CSR_MAP {
  val MEPC    = "h341".U(Config.Csr.AddrWidth.W)
  val MCAUSE  = "h342".U(Config.Csr.AddrWidth.W)
  val MSTATUS = "h300".U(Config.Csr.AddrWidth.W)
  val MTVEC   = "h305".U(Config.Csr.AddrWidth.W)
}

class CsrFile extends Module {
  val ioCSRIDU = IO(new CSRIDUBundle())
  val ioCSRWBU = IO(new CSRWBUBundle())

  val dataOut = WireDefault(0.U(Config.Csr.DataWidth.W))
  val dataIn  = WireDefault(0.U(Config.Csr.DataWidth.W))
  val flushPC = WireDefault(0.U(Config.Csr.DataWidth.W))

  val mod     = RegInit("b11".U(2.W))
  val MEPC    = RegInit((new BundleMEPC).initVal)
  val MCAUSE  = RegInit((new BundleMCAUSE).initVal)
  val MSTATUS = RegInit((new BundleMSTATUS).initVal)
  val MTVEC   = RegInit((new BundleMTVEC).initVal)

  dataOut := MuxCase(
    0.U(Config.Csr.DataWidth.W),
    Seq(
      (ioCSRIDU.csr.en && ioCSRIDU.csr.addr === CSR_MAP.MEPC)    -> MEPC.asUInt,
      (ioCSRIDU.csr.en && ioCSRIDU.csr.addr === CSR_MAP.MCAUSE)  -> MCAUSE.asUInt,
      (ioCSRIDU.csr.en && ioCSRIDU.csr.addr === CSR_MAP.MSTATUS) -> MSTATUS.asUInt,
      (ioCSRIDU.csr.en && ioCSRIDU.csr.addr === CSR_MAP.MTVEC)   -> MTVEC.asUInt
    )
  )

  dataIn := ioCSRWBU.csr.data

  when(ioCSRWBU.csr.en) {
    switch(ioCSRWBU.csr.addr) {
      is(CSR_MAP.MEPC) {
        MEPC := dataIn.asTypeOf(MEPC)
      }
      is(CSR_MAP.MCAUSE) {
        MCAUSE := dataIn.asTypeOf(MCAUSE)
      }
      is(CSR_MAP.MSTATUS) {
        MSTATUS := dataIn.asTypeOf(MSTATUS)
      }
      is(CSR_MAP.MTVEC) {
        MTVEC := dataIn.asTypeOf(MTVEC)
      }
    }
  }

  switch(ioCSRWBU.excType) {
    is(exc.ECALL) {
      MEPC         := ioCSRWBU.pc.asTypeOf(MEPC)
      MCAUSE       := MuxCase(
        0.U(Config.Csr.DataWidth.W),
        Seq(((mod === "b00".U) -> 0x8.U), ((mod === "b01".U) -> 0x9.U), ((mod === "b11".U) -> 0xb.U))
      ).asTypeOf(MCAUSE)
      MSTATUS.MPIE := MSTATUS.MIE
      MSTATUS.MIE  := 0.U
      MSTATUS.MPP  := mod
      mod          := "b11".U
    }
    is(exc.MRET) {
      mod          := MSTATUS.MPP
      MSTATUS.MIE  := MSTATUS.MPIE
      MSTATUS.MPIE := 1.U
      MSTATUS.MPP  := 0.U
    }
    is(exc.FENCE_I) {
      // TODO: FENCE_I
    }
    is(exc.SRET) {
      // TODO: SRET
    }
    is(exc.FENCE) {
      // TODO: FENCE
    }
    is(exc.SFENCE_VMA) {
      // TODO: SFENCE_VMA
    }
    is(exc.WFI) {
      // TODO: WFI
    }
  }

  flushPC := MuxCase(
    0.U(Config.Addr.Width.W),
    Seq(
      (ioCSRWBU.excType === exc.MRET)  -> MEPC.asUInt,
      (ioCSRWBU.excType === exc.ECALL) -> MTVEC.asUInt
    )
  )

  ioCSRWBU.flushPC  := flushPC
  ioCSRIDU.csr.data := dataOut
}
