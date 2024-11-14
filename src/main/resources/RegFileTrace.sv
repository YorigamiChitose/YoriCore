module RegFileTrace # (
  parameter integer REG_NUM_WIDTH  = 5,
  parameter integer REG_NUM        = 32,
  parameter integer REG_DATA_WIDTH = 32
)(
  input                         clock,
  input                         reset,
  input                         en,
  input  [ REG_NUM_WIDTH - 1:0] addr,
  input  [REG_DATA_WIDTH - 1:0] data
);
reg [REG_DATA_WIDTH - 1:0] RegFile [0:REG_NUM - 1];

import "DPI-C" function void set_gpr_ptr(input logic [REG_DATA_WIDTH - 1:0] a []);
initial set_gpr_ptr(RegFile);

integer i;
always @(posedge clock) begin
  if (reset) begin
    for (i = 0; i < REG_NUM; i = i + 1) begin
      RegFile[i] <= 0;
    end
  end
  else if (en && addr != 5'b00000) begin
    RegFile[addr] <= data;
  end
end
endmodule
