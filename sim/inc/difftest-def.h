#ifndef __DIFFTEST_DEF_H__
#define __DIFFTEST_DEF_H__

#include "common.h"

#define __EXPORT __attribute__((visibility("default")))
enum { DIFFTEST_TO_DUT, DIFFTEST_TO_REF };
void init_difftest(char *ref_so_file, long img_size, int port);
void difftest_step(vaddr_t pc, vaddr_t npc);
#define RISCV_GPR_TYPE uint64_t
#define RISCV_GPR_NUM 32
#define DIFFTEST_REG_SIZE                                                      \
  (sizeof(RISCV_GPR_TYPE) * (RISCV_GPR_NUM + 1)) // GPRs + pc

#endif
