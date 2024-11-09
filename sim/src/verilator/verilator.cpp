#include "verilator/verilator.h"
#include <VCore.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

VCore *vtop = NULL;
#ifdef CONFIG_WAVE
VerilatedContext *contextp = NULL;
VerilatedVcdC *tfp = NULL;
#endif

void init_verilator(void) {
#ifdef CONFIG_WAVE
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
#endif
  vtop = new VCore;
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

void exit_verilator(void) {
#ifdef CONFIG_WAVE
  tfp->close();
#endif
}