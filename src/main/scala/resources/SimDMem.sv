module SimDMem # (
  parameter integer DATA_WIDTH = 32,
  parameter integer ADDR_WIDTH = 32
)(
  input                     clock,
  input                     reset,
  output                    ioDMem_ready,
  input                     ioDMem_writeEn,
  input                     ioDMem_readEn,
  input                     ioDMem_isSigned,
  input  [             1:0] ioDMem_dataLen,
  input  [ADDR_WIDTH - 1:0] ioDMem_addr,
  input  [DATA_WIDTH - 1:0] ioDMem_dataIn,
  output [DATA_WIDTH - 1:0] ioDMem_dataOut
);
  import "DPI-C" function int dpic_dmem_read(input int addr, input byte len);
  import "DPI-C" function void dpic_dmem_write(input int addr, input byte len, input int data);

  reg [DATA_WIDTH - 1:0] dataOutTemp;
  reg [DATA_WIDTH - 1:0] dataOut;
  reg [             7:0] dataLen;

  always @(*) begin
    case (ioDMem_dataLen)
      0: dataLen = 0;
      1: dataLen = 1;
      2: dataLen = 2;
      3: dataLen = 4;
      default: dataLen = 0;
    endcase
  end

  /* verilator lint_off LATCH */
  always @(negedge clock) begin
    if (reset) begin
      dataOutTemp = 0;
    end else if (ioDMem_readEn) begin
      dataOutTemp = dpic_dmem_read(ioDMem_addr, dataLen);
    end
  end
  /* verilator lint_on LATCH */

  always @(*) begin
    case (ioDMem_dataLen)
      0: dataOut = 0;
      1: dataOut = {ioDMem_isSigned ? (dataOutTemp[ 7] ? 24'hFFFF_FF : 24'h0) : 24'h0, dataOutTemp[ 7:0]};
      2: dataOut = {ioDMem_isSigned ? (dataOutTemp[15] ? 16'hFFFF    : 16'h0) : 16'h0, dataOutTemp[15:0]};
      3: dataOut = dataOutTemp;
      default: dataOut = 0;
    endcase
  end
  assign ioDMem_dataOut = dataOut;

  always @(*) begin
    if (ioDMem_writeEn) begin
      dpic_dmem_write(ioDMem_addr, dataLen, ioDMem_dataIn);
    end
  end

  assign ioDMem_ready = 1;
endmodule