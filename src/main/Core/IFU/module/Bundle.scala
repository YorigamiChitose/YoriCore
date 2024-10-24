package Core.IFU.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Core.Config.Config

// IFU 控制信号
class IFUCtrlBundle extends Bundle {
  val stallReq = Output(Bool())       // 停止请求
  val busy     = Output(Bool())       // 忙信号
  val pipe     = new PipeCtrlBundle() // pipe控制信号
}

// IFU 流水信号
class IFUStageBundle extends StageBundle {
  val pc   = Output(UInt(Config.Addr.Width.W)) // PC值
  val inst = Output(UInt(Config.Inst.Width.W)) // 指令
}

// IFU - 存储器 接口
class IMemBundle extends Bundle {
  val valid = Input(Bool())  // IFU准备好地址
  val ready = Output(Bool()) // 存储器准备好指令
  val busy  = Output(Bool()) // 存储器正在读取

  val pc   = Input(UInt(Config.Addr.Width.W))  // PC值
  val inst = Output(UInt(Config.Inst.Width.W)) // 指令
}
