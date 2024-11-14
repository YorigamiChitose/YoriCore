package Core.EXU.module

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.IDU.module._

class BranchBundle extends Bundle {
  val branchCtrl      = Input(UInt(branch.WIDTH.W))       // 分支控制信号
  val rs1Data         = Input(UInt(Config.Data.XLEN.W))   // rs1数据
  val rs2Data         = Input(UInt(Config.Data.XLEN.W))   // rs2数据
  val pc              = Input(UInt(Config.Addr.Width.W))  // PC值
  val offset          = Input(UInt(Config.Data.XLEN.W))   // 跳转偏移
  val isBranchSuccess = Output(Bool())                    // 分支成功
  val branchPC        = Output(UInt(Config.Addr.Width.W)) // 跳转地址
}

class Branch extends Module {
  val ioBranch = IO(new BranchBundle()) // 分支接口

  // 分支结果
  val branchResult = MuxCase(
    false.B,
    Seq(
      (ioBranch.branchCtrl === branch.NOP)  -> false.B,                                                     // 不跳转
      (ioBranch.branchCtrl === branch.BEQ)  -> (ioBranch.rs1Data === ioBranch.rs2Data).asBool,              // 等于
      (ioBranch.branchCtrl === branch.BNE)  -> (ioBranch.rs1Data =/= ioBranch.rs2Data).asBool,              // 不等
      (ioBranch.branchCtrl === branch.BLT)  -> (ioBranch.rs1Data.asSInt < ioBranch.rs2Data.asSInt).asBool,  // 符号小于
      (ioBranch.branchCtrl === branch.BGE)  -> (ioBranch.rs1Data.asSInt >= ioBranch.rs2Data.asSInt).asBool, // 符号大于等于
      (ioBranch.branchCtrl === branch.BLTU) -> (ioBranch.rs1Data.asUInt < ioBranch.rs2Data.asUInt).asBool,  // 无符号小雨
      (ioBranch.branchCtrl === branch.BGEU) -> (ioBranch.rs1Data.asUInt >= ioBranch.rs2Data.asUInt).asBool, // 无符号大于等于
      (ioBranch.branchCtrl === branch.JAL)  -> true.B,                                                      // 跳转
      (ioBranch.branchCtrl === branch.JALR) -> true.B                                                       // 跳转
    )
  )

  // 分支跳转结果
  val branchPCResult = MuxCase(
    0.U(Config.Addr.Width.W),
    Seq(
      (ioBranch.branchCtrl === branch.NOP)  -> 0.U,                                                               // 不跳
      (ioBranch.branchCtrl === branch.BEQ)  -> (ioBranch.pc + ioBranch.offset),                                   // 等于
      (ioBranch.branchCtrl === branch.BNE)  -> (ioBranch.pc + ioBranch.offset),                                   // 不等
      (ioBranch.branchCtrl === branch.BLT)  -> (ioBranch.pc + ioBranch.offset),                                   // 符号
      (ioBranch.branchCtrl === branch.BGE)  -> (ioBranch.pc + ioBranch.offset),                                   // 符号
      (ioBranch.branchCtrl === branch.BLTU) -> (ioBranch.pc + ioBranch.offset),                                   // 无符
      (ioBranch.branchCtrl === branch.BGEU) -> (ioBranch.pc + ioBranch.offset),                                   // 无符
      (ioBranch.branchCtrl === branch.JAL)  -> (ioBranch.pc + ioBranch.offset),                                   // 跳转
      (ioBranch.branchCtrl === branch.JALR) -> ((ioBranch.rs1Data + ioBranch.offset) & ~1.U(Config.Addr.Width.W)) // 跳转
    )
  )

  ioBranch.isBranchSuccess := branchResult   // 分支结果
  ioBranch.branchPC        := branchPCResult // 分支跳转结果
}
