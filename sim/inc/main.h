#ifndef __MAIN_H__
#define __MAIN_H__

#include <VCore.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

#define HELLO "YoriCore Running..."

extern VCore *vtop;
extern VerilatedContext *contextp;
extern VerilatedVcdC *tfp;

inline void eval_verilator(void) {
  vtop->eval();
  contextp->timeInc(1);
  tfp->dump(contextp->time());
}

#endif