#ifndef __CPU_H__
#define __CPU_H__

#include <cassert>
#include <cstdint>

void assert_fail_msg(void);
void cpu_exec(uint64_t n);

static inline int check_reg_idx(int idx) {
  assert(idx >= 0 && idx < 32);
  return idx;
}

#define gpr(idx) (cpu.gpr[check_reg_idx(idx)])
#define NPCTRAP(thispc, code) set_npc_state(NPC_END, thispc, code)

static inline const char *reg_name(int idx) {
  extern const char *regs[];
  return regs[check_reg_idx(idx)];
}

#endif