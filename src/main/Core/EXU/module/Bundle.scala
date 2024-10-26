package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.Pipe.module._
import Core.Config.Config

class EXUCtrlBundle extends Bundle {
  val flushReq = Output(Bool())                    // 冲刷请求
  val flushPC  = Output(UInt(Config.Addr.Width.W)) // 冲刷地址
  val stallReq = Output(Bool())                    // 停止请求
  val pipe     = new PipeCtrlBundle()              // pipe控制信号
}

class EXUStageBundle extends StageBundle {
  val pc         = Output(UInt(Config.Addr.Width.W))    // PC值
  val rdEn       = Output(Bool())                       // 目的寄存器使能
  val rdAddr     = Output(UInt(Config.Reg.NumWidth.W))  // 目的寄存器地址
  val csrResult  = Output(UInt(Config.Csr.DataWidth.W)) // csr结果
  val csrAddr    = Output(UInt(Config.Csr.AddrWidth.W)) // csr地址
  val csrWriteEn = Output(Bool())                       // csr写使能
  val rs1Data    = Output(UInt(Config.Reg.Width.W))     // rs1寄存器数据
  val rs2Data    = Output(UInt(Config.Reg.Width.W))     // rs2寄存器数据
  val EXUResult  = Output(UInt(Config.Data.XLEN.W))     // EXU结果
}
