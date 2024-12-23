package Core.IFU

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.IFU.module._
import Core.PCU.module._
import Sim._

class IFU extends Module {
  val ioCtrl  = IO(new IFUCtrlBundle())           // 控制信号
  val ioIFU   = IO(new IFUStageBundle())          // 下级流水信号
  val ioPCU   = IO(Flipped(new PCUStageBundle())) // 上级流水信号
  val ioIMem  = IO(Flipped(new IMemBundle()))     // IFU-存储器
  val ioValid = IO(Input(Bool()))                 // 上级数据有效信号

  // IMem
  ioIMem.valid := Mux(ioValid, !ioCtrl.pipe.flush, false.B) // 上级数据有效 无冲刷 可读取指令
  ioIMem.pc    := ioPCU.pc                                  // PC值

  // Pipe控制
  ioCtrl.pipe.valid := ioIMem.ready && ioValid              // 读取成功 上级有效 向后传输
  ioCtrl.busy       := ioIMem.busy                          // 忙信号
  ioCtrl.stallReq   := Mux(ioValid, !ioIMem.ready, false.B) // 读取时暂停

  // IFU IO
  ioIFU.pc   := ioPCU.pc    // PC值
  ioIFU.inst := ioIMem.inst // 指令

  // Sim
  val ioSI = if (Config.Sim.enable) Some(IO(Flipped(new Sim.SI_PC_IF))) else None
  if (Config.Sim.enable) {
    ioSI.get.ioValid := ioValid
    ioSI.get.pc      := ioPCU.pc
  }
}
