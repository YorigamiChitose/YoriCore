package Core.PCU

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.PCU.module._

// PCU 决定PC值
class PCU extends Module {
  val ioCtrl = IO(new PCUCtrlBundle())  // 控制信号
  val ioPCU  = IO(new PCUStageBundle()) // 下级流水信号

  val pc     = WireDefault(0.U(Config.Addr.Width.W))                    // pc线
  val nextPc = WireDefault(0.U(Config.Addr.Width.W))                    // 下一pc值
  val pcReg  = RegNext(nextPc, Config.Addr.Base.U(Config.Addr.Width.W)) // pc寄存器

  // 冲刷时: 使用flushPC 默认时: 确定是否被停止, 不是则步进
  nextPc := Mux(ioCtrl.pipe.flush, ioCtrl.flushPC, Mux(ioCtrl.pipe.stall, pc, pcReg + 4.U))
  pc     := pcReg

  ioPCU.pc          := pc     // 向后级传递pc
  ioCtrl.pipe.valid := true.B // PCU准备信号默认一直为有效
}
