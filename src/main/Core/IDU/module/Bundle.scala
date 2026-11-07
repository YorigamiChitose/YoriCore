package Core.IDU.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Tools.Config.Config

class IDUCtrlBundle extends Bundle {
  val stallReq = Output(Bool())       // 停止请求
  val pipe     = new PipeCtrlBundle() // pipe控制信号
}

class IDUStageBundle extends StageBundle {
  val pc         = Output(UInt(Config.Addr.Width.W))    // PC值
  val rdEn       = Output(Bool())                       // 目的寄存器使能
  val rdAddr     = Output(UInt(Config.Reg.NumWidth.W))  // 目的寄存器地址
  val rs1Data    = Output(UInt(Config.Reg.Width.W))     // 源寄存器1数据
  val rs2Data    = Output(UInt(Config.Reg.Width.W))     // 源寄存器2数据
  val op1Type    = Output(op1())                        // 操作数1类型
  val op2Type    = Output(op2())                        // 操作数2类型
  val immData    = Output(UInt(Config.Data.XLEN.W))     // 立即数
  val csrAddr    = Output(UInt(Config.Csr.AddrWidth.W)) // csr地址
  val csrData    = Output(UInt(Config.Csr.DataWidth.W)) // csr数据
  val csrWriteEn = Output(Bool())                       // csr写使能
  val aluCtrl    = Output(alu())                        // alu控制信号
  val mulCtrl    = Output(mul())                        // mul控制信号
  val divCtrl    = Output(div())                        // div控制信号
  val branchCtrl = Output(branch())                     // 分支控制信号
  val memCtrl    = Output(mem())                        // 访存控制信号
  val csrCtrl    = Output(csr())                        // csr控制信号
  val excType    = Output(exc())                        // 异常连续
  val preFlushPC = Output(UInt(Config.Addr.Width.W))    // 预冲刷地址

  val inst = if (Config.Sim.enable) Some(Output(UInt(Config.Addr.Width.W))) else None
}
