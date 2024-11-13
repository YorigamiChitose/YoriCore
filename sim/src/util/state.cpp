#include "utils.h"
#include "verilator/verilator.h"

NPCState npc_state = {.state = NPC_STOP};

bool is_exit_status_bad(void) {
  exit_verilator();
  bool good = (npc_state.state == NPC_END && npc_state.halt_ret == 0) ||
              (npc_state.state == NPC_QUIT);
  return !good;
}
