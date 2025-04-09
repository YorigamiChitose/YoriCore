#include "common.h"
#include "cpu/cpu.h"
#include "debug.h"
#include "device/device.h"
#include "isa/isa.h"
#include "macro.h"
#include "monitor/monitor.h"
#include "utils.h"
#include "verilator/verilator.h"
#include <cstdint>
#include <cstdio>

bool g_print_step = false;

static void printInst(uint32_t pc, uint32_t inst) {
  printf("pc: %008x ", pc);
  printf("inst: %08x ", inst);
  char buf[128] = {};
  disassemble(buf, 128, (uint64_t)pc, (uint8_t *)&inst, 4);
  puts(buf);
}

static void trace_and_difftest(vaddr_t pc, vaddr_t npc) {
  IFDEF(CONFIG_WATCH_POINT, if (is_change()) { npc_state.state = NPC_STOP; })
}

void exec_once(void) {
  int count_cycle = 0;
  paddr_t pc = 0;
  word_t inst = 0;
  while (cpu_status.EX_WB_valid == 0) {
    step_verilator();
    refresh_verilator_status();
    if (count_cycle++ > 200) {
      panic("verilator simulation timeout");
    }
  }

  switch (cpu_status.EX_WB_excType) {
  case EXC_EBREAK:
    NPCTRAP(cpu.pc, cpu.gpr[10]);
    break;
  case EXC_ECALL:
    break;
  case EXC_ILLEGAL_INST:
    invalid_inst(cpu.pc, cpu_status.EX_WB_inst);
    break;
  case EXC_MRET:
    break;
  default:
    break;
  }

  pc = cpu_status.EX_WB_pc;
  inst = cpu_status.EX_WB_inst;
  step_verilator();
  refresh_verilator_status();

  if (npc_state.state == NPC_END || npc_state.state == NPC_ABORT) {
    return;
  }

  while (cpu_status.EX_WB_valid == 0) {
    step_verilator();
    refresh_verilator_status();
    if (count_cycle++ > 200) {
      panic("verilator simulation timeout");
    }
  }
  cpu.pc = cpu_status.EX_WB_pc;
  if (g_print_step) {
    printInst(pc, inst);
  }
  trace_and_difftest(pc, cpu.pc);
}

uint64_t g_nr_guest_inst = 0;
void execute(uint64_t n) {
  while (n-- > 0) {
    exec_once();
    g_nr_guest_inst++;

    if (npc_state.state != NPC_RUNNING) {
      break;
    }
    IFDEF(CONFIG_DEVICE, device_update());
  }
}

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

void assert_fail_msg(void) {
  statistic();
  exit_verilator();
}

void cpu_exec(uint64_t n) {
#ifdef CONFIG_ITRACE_PRINT_MAXNUM
  g_print_step = (n < CONFIG_ITRACE_PRINT_MAXNUM);
#else
  g_print_step = false;
#endif
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