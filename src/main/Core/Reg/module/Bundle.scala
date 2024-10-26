package Core.Reg.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Core.Config.Config
import Core.IDU.module._

class RegIDUBundle extends Bundle {
  val rs1 = new Bundle {
    val en   = Input(Bool())
    val addr = Input(UInt(Config.Reg.NumWidth.W))
    val data = Output(UInt(Config.Reg.Width.W))
  }
  val rs2 = new Bundle {
    val en   = Input(Bool())
    val addr = Input(UInt(Config.Reg.NumWidth.W))
    val data = Output(UInt(Config.Reg.Width.W))
  }
}

class RegWBUBundle extends Bundle {
  val rd = new Bundle {
    val en   = Input(Bool())
    val addr = Input(UInt(Config.Reg.NumWidth.W))
    val data = Input(UInt(Config.Reg.Width.W))
  }
}

class CSRIDUBundle extends Bundle {
  val csr        = new Bundle {
    val en   = Input(Bool())
    val addr = Input(UInt(Config.Csr.AddrWidth.W))
    val data = Output(UInt(Config.Csr.DataWidth.W))
  }
  val preFlushPC = Output(UInt(Config.Csr.DataWidth.W))
  val excType    = Input(UInt(exc.WIDTH.W))
}

class CSRWBUBundle extends Bundle {
  val csr     = new Bundle {
    val en   = Input(Bool())
    val addr = Input(UInt(Config.Csr.AddrWidth.W))
    val data = Input(UInt(Config.Csr.DataWidth.W))
  }
  val flushPC = Output(UInt(Config.Csr.DataWidth.W))
  val pc      = Input(UInt(Config.Addr.Width.W))
  val excType = Input(UInt(exc.WIDTH.W))
}
