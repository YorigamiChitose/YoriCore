/* verilator lint_off NULLPORT */
module SimInfo (
  input        io_clock,
  input        io_reset,
  input        io_SI_PC_IF_ioValid,
  input [31:0] io_SI_PC_IF_pc,
  input        io_SI_IF_ID_ioValid,
  input [31:0] io_SI_IF_ID_pc,
  input [31:0] io_SI_IF_ID_inst,
  input        io_SI_ID_EX_ioValid,
  input [31:0] io_SI_ID_EX_pc,
  input [31:0] io_SI_ID_EX_inst,
  input        io_SI_EX_WB_ioValid,
  input [31:0] io_SI_EX_WB_pc,
  input [31:0] io_SI_EX_WB_inst,
  input [ 3:0] io_SI_EX_WB_excType
);
endmodule
/* verilator lint_on NULLPORT */