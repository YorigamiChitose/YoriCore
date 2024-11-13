#include "utils.h"
#include <cstdio>

void set_npc_state(int state, vaddr_t pc, int halt_ret) {
  npc_state.state = state;
  npc_state.halt_pc = pc;
  npc_state.halt_ret = halt_ret;
}

__attribute__((noinline)) void invalid_inst(vaddr_t pc, word_t inst) {
  uint8_t *p = (uint8_t *)&inst;
  printf("invalid opcode(PC = " FMT_WORD "):\n"
         "\t%02x %02x %02x %02x %02x %02x %02x %02x ...\n",
         pc, p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);

  set_npc_state(NPC_ABORT, pc, -1);
}
