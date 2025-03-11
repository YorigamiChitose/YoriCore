package Core.Cache

import chisel3._
import chisel3.util._

import Core.Cache.module._
import Tools.Config.Config

class SRAM extends Module {
  val ioSRAM = IO(new SRAMBundle)

  val syncRAM = SyncReadMem(Config.Cache.SRAM.Depth, Vec(Config.Cache.SRAM.Width / 8, UInt(8.W)))
  val dataOut = WireDefault(0.U.asTypeOf(ioSRAM.dataOut))

  when(ioSRAM.readEn) {
    dataOut := syncRAM.read(ioSRAM.addr)
  }.elsewhen(ioSRAM.writeEn) {
    syncRAM.write(ioSRAM.addr, ioSRAM.dataIn, ioSRAM.mask)
  }

  ioSRAM.dataOut := dataOut
}
