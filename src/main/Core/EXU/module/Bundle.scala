package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.IDU.module._
import Core.EXU.module._
import Core.Pipe.module._

class EXUCtrlBundle extends Bundle {
  val flushReq = Output(Bool())                    // 冲刷请求
  val flushPC  = Output(UInt(Config.Addr.Width.W)) // 冲刷地址
  val stallReq = Output(Bool())                    // 停止请求
  val busy     = Output(Bool())                    // 忙信号
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
  val excType    = Output(UInt(exc.WIDTH.W))            // 异常类型
  val EXUResult  = Output(UInt(Config.Data.XLEN.W))     // EXU结果
}

class DMemBundle extends Bundle {
  val ready    = Output(Bool())                   // 准备完成
  val writeEn  = Input(Bool())                    // 写使能
  val readEn   = Input(Bool())                    // 读使能
  val isSigned = Input(Bool())                    // 符号标志
  val dataLen  = Input(UInt(LSUCtrl.len.WIDTH.W)) // 操作长度
  val addr     = Input(UInt(Config.Addr.Width.W)) // 操作地址
  val dataIn   = Input(UInt(Config.Data.XLEN.W))  // 写入数据
  val dataOut  = Output(UInt(Config.Data.XLEN.W)) // 读出数据
}
