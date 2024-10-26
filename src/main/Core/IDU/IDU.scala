package Core.IDU

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Reg.module._
import Core.IDU.module._
import Core.IFU.module._
import Core.Pipe.module._

class IDU extends Module {
  val ioCtrl          = IO(new IDUCtrlBundle())                // 控制信号
  val ioIDU           = IO(new IDUStageBundle())               // 下级流水信号
  val ioIFU           = IO(Flipped(new IFUStageBundle))        // 上级流水信号
  val ioRegIDU        = IO(Flipped(new RegIDUBundle()))        // IDU - Reg寄存器 接口
  val ioCSRIDU        = IO(Flipped(new CSRIDUBundle()))        // IDU - CSR寄存器 接口
  val ioIDUForwarding = IO(Flipped(new IDUForwardingBundle())) // IDU - 前递 接口
  val ioValid         = IO(Input(Bool()))                      // 上级数据有效信号

  val inst = Mux(ioValid, ioIFU.inst, 0.U) // 指令

  // 指令ListLookup解析
  val rs1En :: rs2En :: rdEn :: op1Type :: op2Type :: aluCtrl :: immType :: branchCtrl :: mulCtrl :: divCtrl :: memCtrl :: csrCtrl :: excType :: Nil =
    ListLookup(inst, ALL.defaultList, ALL.instList)

  val rs1Data = Mux(ioIDUForwarding.rs1.needPass, ioIDUForwarding.rs1.data, ioRegIDU.rs1.data) // 通用寄存器数据
  val rs2Data = Mux(ioIDUForwarding.rs2.needPass, ioIDUForwarding.rs2.data, ioRegIDU.rs2.data) // 通用寄存器数据

  val csrData    = Mux(ioIDUForwarding.csr.needPass, ioIDUForwarding.csr.data, ioCSRIDU.csr.data) // csr寄存器数据
  val csrAddr    = inst(31, 20)                                                                   // csr寄存器地址
  val csrWriteEn = csrCtrl =/= csr.NOP                                                            // csr寄存器写使能
  val csrReadEn  = csrCtrl =/= csr.NOP                                                            // csr寄存器读使能

  val rdAddr  = inst(11, 7)  // 目的寄存器地址
  val rs1Addr = inst(19, 15) // 源寄存器地址
  val rs2Addr = inst(24, 20) // 源寄存器地址

  // 立即数扩展
  val immR     = 0.U(Config.Data.XLEN.W)
  val immN     = 0.U(Config.Data.XLEN.W)
  val immI     = Cat(Fill(Config.Data.XLEN - 12, inst(31)), inst(31, 20))
  val immS     = Cat(Fill(Config.Data.XLEN - 12, inst(31)), inst(31, 25), inst(11, 7))
  val immB     = Cat(Fill(Config.Data.XLEN - 12, inst(31)), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W))
  val immU     = Cat(Fill(32, inst(31)), Cat(inst(31, 12), Fill(12, 0.U)))
  val immJ     = Cat(Fill(Config.Data.XLEN - 20, inst(31)), inst(31), inst(19, 12), inst(20), inst(30, 21), Fill(1, 0.U))
  val immSHAMT = Cat(Fill(Config.Data.XLEN - 6, 0.U), inst(25, 20))
  val immZIMM  = Cat(Fill(Config.Data.XLEN - 5, 0.U), inst(19, 15))

  // 立即数
  val immData = MuxCase(
    0.U(Config.Data.XLEN.W),
    Seq(
      (immType === imm.I)     -> immI,
      (immType === imm.B)     -> immB,
      (immType === imm.J)     -> immJ,
      (immType === imm.U)     -> immU,
      (immType === imm.S)     -> immS,
      (immType === imm.R)     -> immR,
      (immType === imm.S)     -> immS,
      (immType === imm.N)     -> immN,
      (immType === imm.SHAMT) -> immSHAMT,
      (immType === imm.ZIMM)  -> immZIMM
    )
  )

  // 通用寄存器IO
  ioRegIDU.rs1.en   := rs1En   // 使能
  ioRegIDU.rs2.en   := rs2En   // 使能
  ioRegIDU.rs1.addr := rs1Addr // 地址
  ioRegIDU.rs2.addr := rs2Addr // 地址

  // csr寄存器IO
  ioCSRIDU.csr.en   := csrReadEn // CSR使能
  ioCSRIDU.csr.addr := csrAddr   // CSR地址
  ioCSRIDU.excType  := excType   // 异常类型

  // 前递IO
  ioIDUForwarding.rs1.en   := rs1En     // 使能
  ioIDUForwarding.rs2.en   := rs2En     // 使能
  ioIDUForwarding.csr.en   := csrReadEn // 使能
  ioIDUForwarding.rs1.addr := rs1Addr   // 地址
  ioIDUForwarding.rs2.addr := rs2Addr   // 地址
  ioIDUForwarding.csr.addr := csrAddr   // 地址

  // Pipe控制
  ioCtrl.pipe.valid := ioValid                   // 上级有效 向后传输
  ioCtrl.stallReq   := ioIDUForwarding.needStall // 前递需要等待

  // IDU IO
  ioIDU.pc         := ioIFU.pc            // PC值
  ioIDU.rdEn       := rdEn                // 目的寄存器使能
  ioIDU.rdAddr     := rdAddr              // 目的寄存器地址
  ioIDU.rs1Data    := rs1Data             // 源寄存器1数据
  ioIDU.rs2Data    := rs2Data             // 源寄存器2数据
  ioIDU.op1Type    := op1Type             // 操作数1类型
  ioIDU.op2Type    := op2Type             // 操作数2类型
  ioIDU.immData    := immData             // 立即数
  ioIDU.csrAddr    := csrAddr             // csr地址
  ioIDU.csrData    := csrData             // csr数据
  ioIDU.csrWriteEn := csrWriteEn          // csr写使能
  ioIDU.aluCtrl    := aluCtrl             // alu控制信号
  ioIDU.mulCtrl    := mulCtrl             // mul控制信号
  ioIDU.divCtrl    := divCtrl             // div控制信号
  ioIDU.branchCtrl := branchCtrl          // 分支控制信号
  ioIDU.memCtrl    := memCtrl             // 访存控制信号
  ioIDU.csrCtrl    := csrCtrl             // csr控制信号
  ioIDU.excType    := excType             // exc类型
  ioIDU.preFlushPC := ioCSRIDU.preFlushPC // 预冲刷地址
}
