/* verilator lint_off NULLPORT */
module SimInfo (
  input        clock,
  input        reset,
  input        SI_PC_IF_ioValid,
  input [31:0] SI_PC_IF_pc,
  input        SI_IF_ID_ioValid,
  input [31:0] SI_IF_ID_pc,
  input [31:0] SI_IF_ID_inst,
  input        SI_ID_EX_ioValid,
  input [31:0] SI_ID_EX_pc,
  input [31:0] SI_ID_EX_inst,
  input        SI_EX_WB_ioValid,
  input [31:0] SI_EX_WB_pc,
  input [31:0] SI_EX_WB_inst,
  input [ 3:0] SI_EX_WB_excType
);
endmodule
/* verilator lint_on NULLPORT */