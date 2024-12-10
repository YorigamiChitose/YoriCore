package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.IDU.module._
import Tools.Config.Config

class CSRBundle extends Bundle {
  val csrCtrl = Input(UInt(csr.WIDTH.W))         // CSR计算控制
  val rs1     = Input(UInt(Config.Data.XLEN.W))  // rs1数据
  val zimm    = Input(UInt(Config.Data.XLEN.W))  // 立即数
  val csrData = Input(UInt(Config.Data.XLEN.W))  // csr数据
  val result  = Output(UInt(Config.Data.XLEN.W)) // 计算结果
}

class CSR extends Module {
  val ioCSR = IO(new CSRBundle()) // CSR通路

  // 计算结果
  val result = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioCSR.csrCtrl === csr.NOP) -> 0.U,                                    // 默认
      (ioCSR.csrCtrl === csr.RC)  -> (ioCSR.csrData & (~ioCSR.rs1)).asUInt,  // rs1取反与
      (ioCSR.csrCtrl === csr.RW)  -> (ioCSR.rs1).asUInt,                     // rs1
      (ioCSR.csrCtrl === csr.RS)  -> (ioCSR.csrData | ioCSR.rs1).asUInt,     // rs1或
      (ioCSR.csrCtrl === csr.RCI) -> (ioCSR.csrData & (~ioCSR.zimm)).asUInt, // 立即数取反与
      (ioCSR.csrCtrl === csr.RWI) -> (ioCSR.zimm).asUInt,                    // 立即数
      (ioCSR.csrCtrl === csr.RSI) -> (ioCSR.csrData | ioCSR.zimm).asUInt     // 立即数或
    )
  )

  // io
  ioCSR.result := result // 计算结果
}
