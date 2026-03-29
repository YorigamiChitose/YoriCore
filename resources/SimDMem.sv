module SimDMem # (
  parameter integer DATA_WIDTH = 32,
  parameter integer ADDR_WIDTH = 32
)(
  input                     io_clock,
  input                     io_reset,
  output                    io_ioDMem_ready,
  input                     io_ioDMem_writeEn,
  input                     io_ioDMem_readEn,
  input                     io_ioDMem_isSigned,
  input  [             1:0] io_ioDMem_dataLen,
  input  [ADDR_WIDTH - 1:0] io_ioDMem_addr,
  input  [DATA_WIDTH - 1:0] io_ioDMem_dataIn,
  output [DATA_WIDTH - 1:0] io_ioDMem_dataOut
);
  import "DPI-C" function int dpic_dmem_read(input int addr, input byte len);
  import "DPI-C" function void dpic_dmem_write(input int addr, input byte len, input int data);

  reg [DATA_WIDTH - 1:0] dataOutTemp;
  reg [DATA_WIDTH - 1:0] dataOut;
  reg [             7:0] dataLen;

  always @(*) begin
    case (io_ioDMem_dataLen)
      0: dataLen = 0;
      1: dataLen = 1;
      2: dataLen = 2;
      3: dataLen = 4;
      default: dataLen = 0;
    endcase
  end

  /* verilator lint_off LATCH */
  always @(negedge io_clock) begin
    if (io_reset) begin
      dataOutTemp = 0;
    end else if (io_ioDMem_readEn) begin
      dataOutTemp = dpic_dmem_read(io_ioDMem_addr, dataLen);
    end
  end
  /* verilator lint_on LATCH */

  always @(*) begin
    case (io_ioDMem_dataLen)
      0: dataOut = 0;
      1: dataOut = {io_ioDMem_isSigned ? (dataOutTemp[ 7] ? 24'hFFFF_FF : 24'h0) : 24'h0, dataOutTemp[ 7:0]};
      2: dataOut = {io_ioDMem_isSigned ? (dataOutTemp[15] ? 16'hFFFF    : 16'h0) : 16'h0, dataOutTemp[15:0]};
      3: dataOut = dataOutTemp;
      default: dataOut = 0;
    endcase
  end
  assign io_ioDMem_dataOut = dataOut;

  always @(*) begin
    if (io_ioDMem_writeEn) begin
      dpic_dmem_write(io_ioDMem_addr, dataLen, io_ioDMem_dataIn);
    end
  end

  assign io_ioDMem_ready = 1;
endmodule