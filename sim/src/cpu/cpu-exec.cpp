#include "cpu/cpu.h"
#include "debug.h"
#include "isa/isa.h"
#include "utils.h"
#include "verilator/verilator.h"
#include <cstdint>

void refresh_cpu_next_status(void) {}

void exec_once(void) {
  step_verilator();
  refresh_verilator_status();
  if (cpu_status.SIM_valid) {
    cpu.pc = cpu_status.SIM_pc;
    if (cpu_status.SIM_excType == EXC_EBREAK) {
      NPCTRAP(cpu.pc, cpu.gpr[10]);
    }
  }
}

void execute(uint64_t n) {
  while (n-- > 0) {
    exec_once();

    if (npc_state.state != NPC_RUNNING) {
      break;
    }
  }
}

uint64_t g_nr_guest_inst = 0;
uint64_t g_timer = 0; // unit: us
void statistic(void) {
#define NUMBERIC_FMT "%'" PRIu64
  Log("host time spent = " NUMBERIC_FMT " us", g_timer);
  Log("total guest instructions = " NUMBERIC_FMT, g_nr_guest_inst);
  if (g_timer > 0)
    Log("simulation frequency = " NUMBERIC_FMT " inst/s",
        g_nr_guest_inst * 1000000 / g_timer);
  else
    Log("Finish running in less than 1 us and can not calculate the simulation "
        "frequency");
}

void assert_fail_msg(void) { statistic(); }

bool g_print_step = false;
void cpu_exec(uint64_t n) {
  g_print_step = (n < CONFIG_ITRACE_PRINT_MAXNUM);
  switch (npc_state.state) {
  case NPC_END:
  case NPC_ABORT:
  case NPC_QUIT:
    printf("Program execution has ended. To restart the program, exit NPC and "
           "run again.\n");
    return;
  default:
    npc_state.state = NPC_RUNNING;
  }

  uint64_t timer_start = get_time();

  execute(n);

  uint64_t timer_end = get_time();
  g_timer += timer_end - timer_start;

  switch (npc_state.state) {
  case NPC_RUNNING:
    npc_state.state = NPC_STOP;
    break;
  case NPC_END:
  case NPC_ABORT:
    Log("npc: %s at pc = " FMT_WORD,
        (npc_state.state == NPC_ABORT
             ? ANSI_FMT("ABORT", ANSI_FG_RED)
             : (npc_state.halt_ret == 0
                    ? ANSI_FMT("HIT GOOD TRAP", ANSI_FG_GREEN)
                    : ANSI_FMT("HIT BAD TRAP", ANSI_FG_RED))),
        npc_state.halt_pc);
  case NPC_QUIT:
    statistic();
  }
}