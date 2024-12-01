#include "verilator/verilator.h"
#include "isa/isa.h"
#include <VTop.h>
#include <VTop_Core.h>
#include <VTop_SimInfo.h>
#include <VTop_Top.h>
#include <verilated.h>
#include <verilated_fst_c.h>

CPU_state cpu = {};
VTop *vtop = NULL;
#ifdef CONFIG_WAVE
VerilatedContext *contextp = NULL;
VerilatedFstC *tfp = NULL;
#endif

void init_verilator(void) {
#ifdef CONFIG_WAVE
  contextp = new VerilatedContext;
  tfp = new VerilatedFstC;
#endif
  vtop = new VTop;
#ifdef CONFIG_WAVE
  contextp->traceEverOn(true);
  vtop->trace(tfp, 3);
  tfp->open("wave.fst");
#endif
  vtop->reset = 1;
  vtop->clock = 0;
  eval_verilator();
  vtop->reset = 1;
  vtop->clock = 1;
  eval_verilator();
  vtop->reset = 0;
  vtop->clock = 0;
  eval_verilator();
}

void step_verilator(void) {
  vtop->clock = 1;
  eval_verilator();
  vtop->clock = 0;
  eval_verilator();
}

struct CPU_STATUS cpu_status;

void refresh_verilator_status(void) {
  cpu_status.SIM_valid = cpu_status.EX_WB_valid;
  cpu_status.SIM_pc = cpu_status.EX_WB_pc;
  cpu_status.SIM_inst = cpu_status.EX_WB_inst;
  cpu_status.SIM_excType = cpu_status.EX_WB_excType;
  cpu_status.PC_IF_valid = vtop->Top->core->SimInfo->SI_PC_IF_ioValid;
  cpu_status.PC_IF_pc = vtop->Top->core->SimInfo->SI_PC_IF_pc;
  cpu_status.IF_ID_valid = vtop->Top->core->SimInfo->SI_IF_ID_ioValid;
  cpu_status.IF_ID_pc = vtop->Top->core->SimInfo->SI_IF_ID_pc;
  cpu_status.IF_ID_inst = vtop->Top->core->SimInfo->SI_IF_ID_inst;
  cpu_status.ID_EX_valid = vtop->Top->core->SimInfo->SI_ID_EX_ioValid;
  cpu_status.ID_EX_pc = vtop->Top->core->SimInfo->SI_ID_EX_pc;
  cpu_status.ID_EX_inst = vtop->Top->core->SimInfo->SI_ID_EX_inst;
  cpu_status.EX_WB_valid = vtop->Top->core->SimInfo->SI_EX_WB_ioValid;
  cpu_status.EX_WB_pc = vtop->Top->core->SimInfo->SI_EX_WB_pc;
  cpu_status.EX_WB_inst = vtop->Top->core->SimInfo->SI_EX_WB_inst;
  cpu_status.EX_WB_excType = vtop->Top->core->SimInfo->SI_EX_WB_excType;
}

void exit_verilator(void) {
#ifdef CONFIG_WAVE
  tfp->close();
#endif
}