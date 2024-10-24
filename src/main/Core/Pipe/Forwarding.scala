package Core.Pipe

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.Pipe.module._

class Forwarding extends Module {
  val ioIDU = IO(new IDUForwardingBundle()) // IDU - Forwarding 控制信号
  val ioEXU = IO(new EXUForwardingBundle()) // EXU - Forwarding 控制信号
  val ioWBU = IO(new WBUForwardingBundle()) // WBU - Forwarding 控制信号

  val rs1NeedPass = WireDefault(false.B) // rs1可前递
  val rs2NeedPass = WireDefault(false.B) // rs1可前递
  val csrNeedPass = WireDefault(false.B) // csr可前递

  val rs1ForwardingData = WireDefault(0.U(Config.Reg.Width.W))     // rs1前递数据
  val rs2ForwardingData = WireDefault(0.U(Config.Reg.Width.W))     // rs1前递数据
  val csrForwardingData = WireDefault(0.U(Config.Csr.DataWidth.W)) // csr前递数据

  val rs1NeedStall = WireDefault(false.B) // rs1等待前递信号
  val rs2NeedStall = WireDefault(false.B) // rs2等待前递信号

  // csr前递逻辑
  when(ioIDU.csr.en) {
    when(ioEXU.csr.en && (ioEXU.csr.addr === ioIDU.csr.addr)) {
      // EXU
      csrNeedPass       := true.B         // 可前递
      csrForwardingData := ioEXU.csr.data // 前递数据
    }.elsewhen(ioWBU.csr.en && (ioWBU.csr.addr === ioIDU.csr.addr)) {
      // WBU
      csrNeedPass       := true.B         // 可前递
      csrForwardingData := ioWBU.csr.data // 前递数据
    }.otherwise {
      // 默认
      csrNeedPass       := false.B // 不可前递
      csrForwardingData := 0.U     // 默认
    }
  }

  // rs1前递逻辑
  when(ioIDU.rs1.en && (ioIDU.rs1.addr =/= 0.U)) {
    when(ioEXU.rd.en && (ioIDU.rs1.addr === ioEXU.rd.addr)) {
      // EXU
      rs1NeedPass       := true.B                     // 可前递
      rs1ForwardingData := ioEXU.rd.data              // 前递数据
      rs1NeedStall      := ioEXU.isLoad || ioEXU.isMD // 需要等待访存/乘除结束
    }.elsewhen(ioWBU.rd.en && (ioIDU.rs1.addr === ioWBU.rd.addr)) {
      // WBU
      rs1NeedPass       := true.B        // 可前递
      rs1ForwardingData := ioWBU.rd.data // 前递数据
      rs1NeedStall      := false.B       // 无需等待
    }.otherwise {
      // 默认
      rs1NeedPass       := false.B // 不可前递
      rs1ForwardingData := 0.U     // 默认
      rs1NeedStall      := false.B // 无需等待
    }
  }

  // rs2前递逻辑
  when(ioIDU.rs2.en && (ioIDU.rs2.addr =/= 0.U)) {
    when(ioEXU.rd.en && (ioIDU.rs2.addr === ioEXU.rd.addr)) {
      // EXU
      rs2NeedPass       := true.B                     // 可前递
      rs2ForwardingData := ioEXU.rd.data              // 前递数据
      rs2NeedStall      := ioEXU.isLoad || ioEXU.isMD // 需要等待访存/乘除结
    }.elsewhen(ioWBU.rd.en && (ioIDU.rs2.addr === ioWBU.rd.addr)) {
      // WBU
      rs2NeedPass       := true.B        // 可前递
      rs2ForwardingData := ioWBU.rd.data // 前递数据
      rs2NeedStall      := false.B       // 无需等待
    }.otherwise {
      // 默认
      rs2NeedPass       := false.B // 不可前递
      rs2ForwardingData := 0.U     // 默认
      rs2NeedStall      := false.B // 无需等待
    }
  }

  ioIDU.rs1.needPass := rs1NeedPass                  // 可前递信号
  ioIDU.rs2.needPass := rs2NeedPass                  // 可前递信号
  ioIDU.csr.needPass := csrNeedPass                  // 可前递信号
  ioIDU.rs1.data     := rs1ForwardingData            // 前递数据
  ioIDU.rs2.data     := rs2ForwardingData            // 前递数据
  ioIDU.csr.data     := csrForwardingData            // 前递数据
  ioIDU.needStall    := rs1NeedStall || rs2NeedStall // 等待信号
}
