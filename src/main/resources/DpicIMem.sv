module DpicIMem # (
  parameter integer INST_WIDTH = 32,
  parameter integer ADDR_WIDTH = 32
)(
  input                         clock,
  input                         reset,
  input                         ioIMem_valid,
  output                        ioIMem_ready,
  output                        ioIMem_busy,
  input      [ADDR_WIDTH - 1:0] ioIMem_pc,
  output reg [INST_WIDTH - 1:0] ioIMem_inst
);
  import "DPI-C" function int dpic_imem_read(input int pc);

  // always @(posedge clock) begin
  //   if (reset) begin
  //     ioIMem_inst <= 0;
  //   end else if (ioIMem_valid) begin
  //     ioIMem_inst <= dpic_imem_read(ioIMem_pc);
  //   end
  // end

  /* verilator lint_off LATCH */
  always @(*) begin
    if (reset) begin
      ioIMem_inst = 0;
    end else if (ioIMem_valid) begin
      ioIMem_inst = dpic_imem_read(ioIMem_pc);
    end
  end
  /* verilator lint_on LATCH */

  assign ioIMem_busy = 1'b1; 
  assign ioIMem_ready = 1'b1; 
endmodule