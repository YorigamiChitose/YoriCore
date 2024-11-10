#include "verilator/verilator.h"
#include <VTop.h>
#include <VTop_Core.h>
#include <VTop_PipeStage.h>
#include <VTop_PipeStage_1.h>
#include <VTop_PipeStage_2.h>
#include <VTop_PipeStage_3.h>
#include <VTop_Top.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

VTop *vtop = NULL;
#ifdef CONFIG_WAVE
VerilatedContext *contextp = NULL;
VerilatedVcdC *tfp = NULL;
#endif

void init_verilator(void) {
#ifdef CONFIG_WAVE
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
#endif
  vtop = new VTop;
#ifdef CONFIG_WAVE
  contextp->traceEverOn(true);
  vtop->trace(tfp, 3);
  tfp->open("wave.vcd");
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

void refresh_cpu_status(void) {
  cpu_status.PC_IF_valid = vtop->Top->core->PC_IF->ioValid;
  cpu_status.IF_ID_valid = vtop->Top->core->IF_ID->ioValid;
  cpu_status.ID_EX_valid = vtop->Top->core->ID_EX->ioValid;
  cpu_status.EX_WB_valid = vtop->Top->core->EX_WB->ioValid;
  cpu_status.EX_WB_excType = vtop->Top->core->EX_WB->ioNextStage_excType;
}

void exit_verilator(void) {
#ifdef CONFIG_WAVE
  tfp->close();
#endif
}