module SimIMem # (
  parameter integer INST_WIDTH = 32,
  parameter integer ADDR_WIDTH = 32
)(
  input                         clock,
  input                         reset,
  input                         ioIMem_valid,
  output                        ioIMem_ready,
  output reg                    ioIMem_busy,
  input      [ADDR_WIDTH - 1:0] ioIMem_pc,
  output reg [INST_WIDTH - 1:0] ioIMem_inst
);
  import "DPI-C" function int dpic_imem_read(input int pc);

  reg [1:0] state;
  reg [INST_WIDTH - 1:0] inst_reg;
  reg [ADDR_WIDTH - 1:0] pc_reg;
  /* verilator lint_off CASEINCOMPLETE */
  always @(posedge clock or posedge reset) begin
    if (reset) begin
      state <= 2'b00;
      ioIMem_busy <= 1'b0;
      ioIMem_inst <= 0;
      inst_reg <= 0;
      pc_reg <= 0;
    end else begin
      case (state)
        2'b00: begin
          if (ioIMem_valid) begin
            ioIMem_busy <= 1'b1;
            pc_reg <= ioIMem_pc;
            state <= 2'b01;
          end
        end
        2'b01: begin
          ioIMem_inst <= dpic_imem_read(pc_reg);
          state <= 2'b10;
        end
        2'b10: begin
          ioIMem_inst <= 0;
          ioIMem_busy <= 1'b0;
          state <= 2'b00;
        end
      endcase
    end
  end
  /* verilator lint_on CASEINCOMPLETE */
  assign ioIMem_ready = (state == 2'b10);

endmodule