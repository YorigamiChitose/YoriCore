module SimIMem # (
  parameter integer INST_WIDTH = 32,
  parameter integer ADDR_WIDTH = 32
)(
  input                         io_clock,
  input                         io_reset,
  input                         io_ioIMem_valid,
  output                        io_ioIMem_ready,
  output                        io_ioIMem_busy,
  input      [ADDR_WIDTH - 1:0] io_ioIMem_pc,
  output reg [INST_WIDTH - 1:0] io_ioIMem_inst
);
  import "DPI-C" function int dpic_imem_read(input int pc);

  /* verilator lint_off LATCH */
  always @(*) begin
    if (io_reset) begin
      io_ioIMem_inst = 0;
    end else if (io_ioIMem_valid) begin
      io_ioIMem_inst = dpic_imem_read(io_ioIMem_pc);
    end
  end
  /* verilator lint_on LATCH */

  assign io_ioIMem_busy = 1'b0; 
  assign io_ioIMem_ready = 1'b1; 
endmodule