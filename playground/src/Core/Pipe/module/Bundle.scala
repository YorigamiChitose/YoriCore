package Core.Pipe.module

import chisel3._
import chisel3.util._

import Tools.Config.Config

// 级间流水寄存器控制信号IO (pipe控制器 <> 级间寄存器)
class PipeRegCtrlBundle extends Bundle {
  val valid     = Input(Bool()) // 前级完成
  val flush     = Input(Bool()) // 冲刷
  val stallPrev = Input(Bool()) // 停止前级
  val stallNext = Input(Bool()) // 停止后级
}

// 各级流水信号IO父类 (流水级 <> 级间寄存器)
class StageBundle extends Bundle {
  def initVal() = 0.U.asTypeOf(this) // 默认值
}

// 流水线控制信号IO (pipe控制器 <> 流水级)
class PipeCtrlBundle extends Bundle {
  val valid = Output(Bool()) // 完成信号
  val flush = Input(Bool())  // 冲刷命令
  val stall = Input(Bool())  // 停止命令
}

// IDU - 前递模块IO
class IDUForwardingBundle extends Bundle {
  // rs1 数据
  val rs1       = new Bundle {
    val en       = Input(Bool())                      // 使能
    val addr     = Input(UInt(Config.Reg.NumWidth.W)) // 地址
    val needPass = Output(Bool())                     // 需要传递
    val data     = Output(UInt(Config.Reg.Width.W))   // 数据
  }
  // rs2 数据
  val rs2       = new Bundle {
    val en       = Input(Bool())                      // 使能
    val addr     = Input(UInt(Config.Reg.NumWidth.W)) // 地址
    val needPass = Output(Bool())                     // 需要传递
    val data     = Output(UInt(Config.Reg.Width.W))   // 数据
  }
  // csr 数据
  val csr       = new Bundle {
    val en       = Input(Bool())                        // 使能
    val addr     = Input(UInt(Config.Csr.AddrWidth.W))  // 地址
    val needPass = Output(Bool())                       // 需要传递
    val data     = Output(UInt(Config.Csr.DataWidth.W)) // 数据
  }
  // 其他控制信号
  val needStall = Output(Bool()) // 需要暂停等待
}

class EXUForwardingBundle extends Bundle {
  // rd数据
  val rd     = new Bundle {
    val en   = Input(Bool())                      // 使能
    val addr = Input(UInt(Config.Reg.NumWidth.W)) // 地址
    val data = Input(UInt(Config.Reg.Width.W))    // 数据
  }
  // csr数据
  val csr    = new Bundle {
    val en   = Input(Bool())                       // 使能
    val addr = Input(UInt(Config.Csr.AddrWidth.W)) // 地址
    val data = Input(UInt(Config.Csr.DataWidth.W)) // 数据
  }
  // 其他控制信号
  val isLoad = Input(Bool()) // 访存结果需要等待
  val isMD   = Input(Bool()) // 乘除结果需要等待
}

class WBUForwardingBundle extends Bundle {
  // rd数据
  val rd  = new Bundle {
    val en   = Input(Bool())                      // 使能
    val addr = Input(UInt(Config.Reg.NumWidth.W)) // 地址
    val data = Input(UInt(Config.Reg.Width.W))    // 数据
  }
  // csr数据
  val csr = new Bundle {
    val en   = Input(Bool())                       // 使能
    val addr = Input(UInt(Config.Csr.AddrWidth.W)) // 地址
    val data = Input(UInt(Config.Csr.DataWidth.W)) // 数据
  }
}
