package test.Core.PCU

import chisel3._
import chisel3.simulator.EphemeralSimulator._
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import Core.PCU._
import Tools.Config.Config

class PCUTest extends AnyFreeSpec with Matchers {
  "PCU test" in {
    simulate(new PCU) { dut =>
      // 初始化
      dut.ioCtrl.flushPC.poke(0.U)
      dut.ioCtrl.pipe.flush.poke(false.B)
      dut.ioCtrl.pipe.stall.poke(false.B)
      // reset
      dut.reset.poke(true.B)
      dut.clock.step()
      dut.ioPCU.pc.expect(Config.Addr.Base.U)
      // 正常步进
      dut.reset.poke(false.B)
      dut.clock.step()
      dut.ioPCU.pc.expect((Config.Addr.Base + 4).U)
      // pc冲刷
      dut.ioCtrl.flushPC.poke((Config.Addr.Base + 0x1000_0000).U)
      dut.ioCtrl.pipe.flush.poke(true.B)
      dut.clock.step()
      dut.ioPCU.pc.expect((Config.Addr.Base + 0x1000_0000).U)
      // 暂停
      dut.ioCtrl.flushPC.poke(0.U)
      dut.ioCtrl.pipe.flush.poke(false.B)
      dut.ioCtrl.pipe.stall.poke(true.B)
      dut.clock.step()
      dut.ioPCU.pc.expect((Config.Addr.Base + 0x1000_0000).U)
    }
  }
}
