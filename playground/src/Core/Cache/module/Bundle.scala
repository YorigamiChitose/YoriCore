package Core.Cache.module

import chisel3._
import chisel3.util._

import Tools.Config.Config

class ICacheBundle extends Bundle {}
class DCacheBundle extends Bundle {}

class SRAMBundle extends Bundle {
  val readEn  = Input(Bool())
  val writeEn = Input(Bool())
  val mask    = Input(Vec(Config.Cache.SRAM.Width / 8, Bool()))
  val addr    = Input(UInt(Config.Cache.SRAM.AddrWidth.W))
  val dataIn  = Input(Vec(Config.Cache.SRAM.Width / 8, UInt(8.W)))
  val dataOut = Output(Vec(Config.Cache.SRAM.Width / 8, UInt(8.W)))
}
