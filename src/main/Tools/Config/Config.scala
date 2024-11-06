package Tools.Config

import chisel3._
import chisel3.util._

object Config {
  object Inst {
    val Width = 32
  }

  object Addr {
    val Base  = 0x8000_0000L
    val Width = 32
  }

  object Reg {
    val Num      = 32
    val Width    = Data.XLEN
    val NumWidth = chisel3.util.log2Ceil(Num)
  }

  object Csr {
    val DataWidth = Data.XLEN
    val AddrWidth = 12
  }

  object Data {
    val XLEN = 32
    val B    = 8
    val H    = 2 * B
    val W    = 2 * H
    val D    = 2 * W
  }

  object NPC {
    val enable = true
  }
}
