#ifndef __ISA_H__
#define __ISA_H__

#include "common.h"
extern const char *regs[];
void engine_start(void);

typedef struct {
  vaddr_t mepc;
  word_t mcause;
  word_t mstatus;
  word_t mtvec;
} CSR;

typedef struct {
  word_t *gpr;
  vaddr_t pc;
  CSR csr;
  uint8_t mode;
} CPU_state;

extern CPU_state cpu;
word_t isa_reg_str2val(const char *s, bool *success);
void engine_start(void);
#endif