#ifndef __VERILATOR_H__
#define __VERILATOR_H__

#include <VTop.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

#include "common.h"

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
  uint32_t PC_IF_pc;
  // IF - ID
  bool IF_ID_valid;
  uint32_t IF_ID_pc;
  uint32_t IF_ID_inst;
  // ID - EX
  bool ID_EX_valid;
  uint32_t ID_EX_pc;
  uint32_t ID_EX_inst;
  // EX - WB
  bool EX_WB_valid;
  uint32_t EX_WB_pc;
  uint32_t EX_WB_inst;
  int EX_WB_excType;
  // SIM
  bool SIM_valid;
  uint32_t SIM_pc;
  uint32_t SIM_inst;
  int SIM_excType;
};
extern struct CPU_STATUS cpu_status;

void init_verilator(void);
void step_verilator(void);
void exit_verilator(void);
void refresh_verilator_status(void);

enum {
  EXC_NOP,
  EXC_EBREAK,
  EXC_ECALL,
  EXC_ILLEGAL_INST,
  EXC_MRET,
  EXC_SRET,
  EXC_WFI,
  EXC_SFENCE_VMA,
  EXC_FENCE,
  EXC_FENCE_I
};

#endif