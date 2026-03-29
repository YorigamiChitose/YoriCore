module RegFileTrace # (
  parameter integer REG_NUM_WIDTH  = 5,
  parameter integer REG_NUM        = 32,
  parameter integer REG_DATA_WIDTH = 32
)(
  input                         io_clock,
  input                         io_reset,
  input                         io_en,
  input  [ REG_NUM_WIDTH - 1:0] io_addr,
  input  [REG_DATA_WIDTH - 1:0] io_data
);
reg [REG_DATA_WIDTH - 1:0] RegFile [0:REG_NUM - 1];

import "DPI-C" function void set_gpr_ptr(input logic [REG_DATA_WIDTH - 1:0] a []);
initial set_gpr_ptr(RegFile);

integer i;
always @(posedge io_clock) begin
  if (io_reset) begin
    for (i = 0; i < REG_NUM; i = i + 1) begin
      RegFile[i] <= 0;
    end
  end
  else if (io_en && io_addr != 5'b00000) begin
    RegFile[io_addr] <= io_data;
  end
end
endmodule
