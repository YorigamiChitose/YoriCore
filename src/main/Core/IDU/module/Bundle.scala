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
  val op1Type    = Output(UInt(op1.WIDTH.W))            // 操作数1类型
  val op2Type    = Output(UInt(op2.WIDTH.W))            // 操作数2类型
  val immData    = Output(UInt(Config.Data.XLEN.W))     // 立即数
  val csrAddr    = Output(UInt(Config.Csr.AddrWidth.W)) // csr地址
  val csrData    = Output(UInt(Config.Csr.DataWidth.W)) // csr数据
  val csrWriteEn = Output(Bool())                       // csr写使能
  val aluCtrl    = Output(UInt(alu.WIDTH.W))            // alu控制信号
  val mulCtrl    = Output(UInt(mul.WIDTH.W))            // mul控制信号
  val divCtrl    = Output(UInt(div.WIDTH.W))            // div控制信号
  val branchCtrl = Output(UInt(branch.WIDTH.W))         // 分支控制信号
  val memCtrl    = Output(UInt(mem.WIDTH.W))            // 访存控制信号
  val csrCtrl    = Output(UInt(csr.WIDTH.W))            // csr控制信号
  val excType    = Output(UInt(exc.WIDTH.W))            // 异常连续
  val preFlushPC = Output(UInt(exc.WIDTH.W))            // 预冲刷地址
}
