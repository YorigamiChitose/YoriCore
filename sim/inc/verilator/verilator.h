#ifndef __VERILATOR_H__
#define __VERILATOR_H__

#include <VTop.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

#include "autoconf/common.h"

extern VTop *vtop;
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

struct CPU_STATUS {
  // PC - IF
  bool PC_IF_valid;
  // IF - ID
  bool IF_ID_valid;
  // ID - EX
  bool ID_EX_valid;
  // EX - WB
  bool EX_WB_valid;
  int EX_WB_excType;
};
extern struct CPU_STATUS cpu_status;

void init_verilator(void);
void step_verilator(void);
void exit_verilator(void);
void refresh_cpu_status(void);

#endif