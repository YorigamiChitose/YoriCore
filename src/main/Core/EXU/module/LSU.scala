package Core.EXU.module

import chisel3._
import chisel3.util._

import Core.Config.Config
import Core.IDU.module._

object LSUCtrl {
  object len {
    val WIDTH = log2Ceil(4)
    val NOP   = 0.U(WIDTH.W)
    val B     = 1.U(WIDTH.W)
    val H     = 2.U(WIDTH.W)
    val W     = 3.U(WIDTH.W)
  }
  //                     readEn   writeEn  isSigned len
  val defaultList = List(false.B, false.B, false.B, len.NOP)
  val table = Array(
    BitPat(mem.NOP) -> List(false.B, false.B, false.B, len.NOP),
    BitPat(mem.LB)  -> List(true.B, false.B, true.B, len.B),
    BitPat(mem.LH)  -> List(true.B, false.B, true.B, len.H),
    BitPat(mem.LBU) -> List(true.B, false.B, false.B, len.B),
    BitPat(mem.LHU) -> List(true.B, false.B, false.B, len.H),
    BitPat(mem.LW)  -> List(true.B, false.B, true.B, len.W),
    BitPat(mem.SB)  -> List(false.B, true.B, false.B, len.B),
    BitPat(mem.SH)  -> List(false.B, true.B, false.B, len.H),
    BitPat(mem.SW)  -> List(false.B, true.B, false.B, len.W)
  )
}

class LSUBundle extends Bundle {
  val memCtrl = Input(UInt(mem.WIDTH.W))         // LSU控制信号
  val addr    = Input(UInt(Config.Addr.Width.W)) // 地址计算结果
  val dataIn  = Input(UInt(Config.Data.XLEN.W))  // 保存数据
  val dataOut = Output(UInt(Config.Data.XLEN.W)) // LSU结果
  val ready   = Output(Bool())                   // LSU准备完成
}

class LSU extends Module {
  val ioLSU  = IO(new LSUBundle)           // LSU IO
  val ioDMem = IO(Flipped(new DMemBundle)) // DMem IO

  // 解析操作
  val readEn :: writeEn :: isSigned :: dataLen :: Nil = ListLookup(ioLSU.memCtrl, LSUCtrl.defaultList, LSUCtrl.table)

  // DMem IO
  ioDMem.readEn   := readEn       // 读使能
  ioDMem.writeEn  := writeEn      // 写使能
  ioDMem.isSigned := isSigned     // 符号标志
  ioDMem.dataLen  := dataLen      // 操作长度
  ioDMem.dataIn   := ioLSU.dataIn // 待存储数据
  ioDMem.addr     := ioLSU.addr   // 访问地址

  // LSU IO
  ioLSU.dataOut := ioDMem.dataOut // LSU结果
  ioLSU.ready   := ioDMem.ready   // LSU准备完成
}
