package Core.Util

import chisel3._
import chisel3.util._

object Util {
  def ignoreBundle(bun: Bundle): Unit = {
    require(bun.elements.nonEmpty)
    for ((name, elem) <- bun.elements) {
      elem := DontCare
    }
  }
}
