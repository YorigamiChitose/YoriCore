#ifndef __VERILATOR_H__
#define __VERILATOR_H__

#include <VCore.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

#include "autoconf/common.h"

extern VCore *vtop;
#ifdef CONFIG_WAVE
extern VerilatedContext *contextp;
extern VerilatedVcdC *tfp;
#endif

inline void eval_verilator(void) {
  vtop->eval();
#ifdef CONFIG_WAVE
  contextp->timeInc(1);
  tfp->dump(contextp->time());
#endif
}

void init_verilator(void);
void exit_verilator(void);

#endif