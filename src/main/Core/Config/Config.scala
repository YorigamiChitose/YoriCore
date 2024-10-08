package Core.Config

import chisel3._
import chisel3.util._

object Config {
object ADDR {
    val BASE  = 0x8000_0000L
    val WIDTH = 32
  }
  object REG {
    val NUM       = 16
    val WIDTH     = 32
    val NUM_WIDTH = chisel3.util.log2Ceil(NUM)
  }
  object CSR {
    val DATA_WIDTH = DATA.XLEN
    val ADDR_WIDTH = 12
  }
  object DATA {
    val XLEN = 32
    val B    = 8
    val H    = 2 * B
    val W    = 2 * H
    val D    = 2 * W
  }
}
