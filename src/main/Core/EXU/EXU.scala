package Core.EXU

import chisel3._
import chisel3.util._

import Tools.Config.Config
import Core.Reg.module._
import Core.EXU.module._
import Core.IDU.module._
import Core.Pipe.module._

class EXU extends Module {
  val ioCtrl          = IO(new EXUCtrlBundle())                // 控制信号
  val ioEXU           = IO(new EXUStageBundle())               // 下级流水信号
  val ioIDU           = IO(Flipped(new IDUStageBundle))        // 上级流水信号
  val ioEXUForwarding = IO(Flipped(new EXUForwardingBundle())) // EXU - 前递 接口
  val ioDMem          = IO(Flipped(new DMemBundle))            // DMem 接口
  val ioValid         = IO(Input(Bool()))                      // 上级数据有效信号

  // 操作数1
  val operator1 = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioIDU.op1Type === op1.NOP)  -> 0.U,           // 无
      (ioIDU.op1Type === op1.RS1)  -> ioIDU.rs1Data, // rs1寄存器
      (ioIDU.op1Type === op1.PC)   -> ioIDU.pc,      // PC值
      (ioIDU.op1Type === op1.ZERO) -> 0.U            // 0
    )
  )
  // 操作数2
  val operator2 = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioIDU.op2Type === op2.NOP)  -> 0.U,           // 无
      (ioIDU.op2Type === op2.RS2)  -> ioIDU.rs2Data, // rs2寄存器
      (ioIDU.op2Type === op2.IMM)  -> ioIDU.immData, // 立即数
      (ioIDU.op2Type === op2.FORE) -> 4.U            // +4
    )
  )

  // ALU
  val ALU       = Module(new ALU()) // ALU模块
  val ALUResult = ALU.ioALU.result  // ALU结果
  ALU.ioALU.aluCtrl := Mux(ioValid, ioIDU.aluCtrl, alu.NOP) // ALU控制信号
  ALU.ioALU.op1     := operator1                            // 操作数1
  ALU.ioALU.op2     := operator2                            // 操作数2

  // Branch
  val Branch          = Module(new Branch())            // 分支模块
  val isBranchSuccess = Branch.ioBranch.isBranchSuccess // 分支成功
  val branchPC        = Branch.ioBranch.branchPC        // 分支目的地址
  Branch.ioBranch.branchCtrl := Mux(ioValid, ioIDU.branchCtrl, alu.NOP) // 分支控制信号
  Branch.ioBranch.rs1Data    := ioEXU.rs1Data                           // rs1数据
  Branch.ioBranch.rs2Data    := ioEXU.rs2Data                           // rs2数据
  Branch.ioBranch.pc         := ioIDU.pc                                // PC值
  Branch.ioBranch.offset     := ioIDU.immData                           // 跳转偏移

  // CSR
  val CSR       = Module(new CSR()) // CSR模块
  val CSRResult = CSR.ioCSR.result  // CSR结果
  CSR.ioCSR.csrCtrl := Mux(ioValid, ioIDU.csrCtrl, alu.NOP) // CSR控制信号
  CSR.ioCSR.rs1     := operator1                            // rs1数据
  CSR.ioCSR.zimm    := operator2                            // 立即数
  CSR.ioCSR.csrData := ioIDU.csrData                        // CSR数据

  // DIV
  val DIV       = Module(new DIV()) // DIV模块
  val DIVResult = DIV.ioDIV.result  // DIV结果
  val DIVReady  = DIV.ioDIV.ready   // DIV准备完成
  DIV.ioDIV.divCtrl := Mux(ioValid, ioIDU.divCtrl, alu.NOP) // DIV控制信号
  DIV.ioDIV.op1     := operator1                            // 操作数1
  DIV.ioDIV.op2     := operator2                            // 操作数2
  DIV.ioDIV.flush   := ioCtrl.pipe.flush                    // 冲刷

  // MLU
  val MUL       = Module(new MUL()) // MUL模块
  val MULResult = MUL.ioMUL.result  // MUL结果
  val MULReady  = MUL.ioMUL.ready   // MUL准备完成
  MUL.ioMUL.mulCtrl := Mux(ioValid, ioIDU.mulCtrl, alu.NOP) // MUL控制信号
  MUL.ioMUL.op1     := operator1                            // 操作数1
  MUL.ioMUL.op2     := operator2                            // 操作数2
  MUL.ioMUL.flush   := ioCtrl.pipe.flush                    // 冲刷

  // LSU
  val LSU       = Module(new LSU)   // LSU模块
  val LSUResult = LSU.ioLSU.dataOut // LSU结果
  val LSUReady  = LSU.ioLSU.ready   // LSU准备完成
  LSU.ioLSU.memCtrl := ioIDU.memCtrl // LSU控制信号
  LSU.ioLSU.addr    := ALUResult     // 地址计算结果
  LSU.ioLSU.dataIn  := ioEXU.rs2Data // 保存数据

  // 运算结果
  val EXUResult = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (ioIDU.aluCtrl =/= alu.NOP) -> ALUResult,
      (ioIDU.mulCtrl =/= mul.NOP) -> MULResult,
      (ioIDU.divCtrl =/= div.NOP) -> DIVResult,
      (ioIDU.csrCtrl =/= csr.NOP) -> ioIDU.csrData, // 存入通用寄存器
      (ioIDU.memCtrl =/= mem.NOP) -> LSUResult
    )
  )

  // Pipe控制
  ioCtrl.stallReq := ((ioIDU.mulCtrl =/= mul.NOP) && !MULReady) ||
    ((ioIDU.divCtrl =/= div.NOP) && !DIVReady) ||
    (ioIDU.memCtrl =/= mem.NOP) // TODO: LSU
  ioCtrl.flushPC    := MuxCase(
    0.U(Config.Addr.Width.W),
    Seq(
      isBranchSuccess                                                                           -> Branch.ioBranch.branchPC,
      (ioIDU.excType === exc.ECALL || ioIDU.excType === exc.MRET || ioIDU.excType === exc.SRET) -> ioIDU.preFlushPC // TODO: CSR预冲刷逻辑待优化
    )
  )
  ioCtrl.flushReq   := isBranchSuccess || (ioIDU.excType === exc.ECALL || ioIDU.excType === exc.MRET || ioIDU.excType === exc.SRET)
  ioCtrl.pipe.valid := MuxCase(
    false.B,
    Seq(
      (ioIDU.aluCtrl =/= alu.NOP)       -> true.B,
      (ioIDU.branchCtrl =/= branch.NOP) -> true.B,
      (ioIDU.csrCtrl =/= csr.NOP)       -> true.B,
      (ioIDU.divCtrl =/= div.NOP)       -> DIVReady,
      (ioIDU.memCtrl =/= mem.NOP)       -> LSUReady,
      (ioIDU.mulCtrl =/= mul.NOP)       -> MULReady
    )
  )
  ioCtrl.busy       := (ioIDU.mulCtrl =/= mul.NOP) && !LSUReady

  // 前递IO
  ioEXUForwarding.isLoad   := (ioIDU.memCtrl === mem.LB) ||
    (ioIDU.memCtrl === mem.LBU) ||
    (ioIDU.memCtrl === mem.LH) ||
    (ioIDU.memCtrl === mem.LHU) ||
    (ioIDU.memCtrl === mem.LW)
  ioEXUForwarding.isMD     := false.B          // TODO: (ioIDU.divCtrl =/= div.NOP) || (ioIDU.mulCtrl =/= mul.NOP) // 当前为乘除任务
  ioEXUForwarding.rd.en    := ioIDU.rdEn       // 目的寄存器使能
  ioEXUForwarding.rd.addr  := ioIDU.rdAddr     // 目的寄存器地址
  ioEXUForwarding.rd.data  := EXUResult        // 目的寄存器数据
  ioEXUForwarding.csr.en   := ioIDU.csrWriteEn // csr使能
  ioEXUForwarding.csr.addr := ioIDU.csrAddr    // csr地址
  ioEXUForwarding.csr.data := CSRResult        // csr数据

  // DMem IO
  ioDMem <> LSU.ioDMem

  // EXU IO
  ioEXU.pc         := ioIDU.pc         // PC值
  ioEXU.rdEn       := ioIDU.rdEn       // 目的寄存器使能
  ioEXU.rdAddr     := ioIDU.rdAddr     // 目的寄存器地址
  ioEXU.csrResult  := CSRResult        // csr计算结果 存入CSR
  ioEXU.csrAddr    := ioIDU.csrAddr    // csr地址
  ioEXU.csrWriteEn := ioIDU.csrWriteEn // csr写使能
  ioEXU.rs1Data    := ioIDU.rs1Data    // rs1数据
  ioEXU.rs2Data    := ioIDU.rs2Data    // rs2数据
  ioEXU.EXUResult  := EXUResult        // EXU计算结果
  ioEXU.excType    := ioIDU.excType    // 异常类型
}
