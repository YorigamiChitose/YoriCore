package Core.IFU

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.IFU.module._
import Core.PCU.module._

class IFU extends Module {
  val ioCtrl  = IO(new IFUCtrlBundle())           // 控制信号
  val ioIFU   = IO(new IFUStageBundle())          // 下级流水信号
  val ioPCU   = IO(Flipped(new PCUStageBundle())) // 上级流水信号
  val ioIMem  = IO(Flipped(new IMemBundle()))     // IFU-存储器
  val ioValid = IO(Input(Bool()))                 // 上级数据有效信号

  ioCtrl.stallReq := Mux(ioValid, !ioIMem.ready, false.B) // 读取时暂停
  ioCtrl.busy     := ioIMem.busy                          // 忙信号
  ioIMem.valid    := ioValid                              // 上级数据有效 可读取指令
  ioIMem.pc       := ioPCU.pc                             // PC值

  ioCtrl.pipe.valid := ioIMem.ready // 读取成功 向后传输
  ioIFU.inst        := ioIMem.inst  // 指令
}
