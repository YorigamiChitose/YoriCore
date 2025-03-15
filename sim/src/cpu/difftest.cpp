#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <dlfcn.h>

#include "common.h"
#include "cpu/cpu.h"
#include "debug.h"
#include "difftest-def.h"
#include "isa/isa.h"
#include "memory/memory.h"

void (*ref_difftest_memcpy)(paddr_t addr, void *buf, size_t n,
                            bool direction) = NULL;
void (*ref_difftest_regcpy)(void *dut, bool direction) = NULL;
void (*ref_difftest_exec)(uint64_t n) = NULL;
void (*ref_difftest_raise_intr)(uint64_t NO) = NULL;
void (*ref_difftest_init)(int) = NULL;
#ifdef CONFIG_DIFFTEST

static bool is_skip_ref = false;
bool enable_difftest = true;
extern const char *regs[];

bool isa_difftest_checkregs(CPU_state *ref_r, vaddr_t pc) {
  if (ref_r->pc != pc) {
    Log("Different values of the PC! REF: " FMT_VADDR " DUT: " FMT_VADDR,
        ref_r->pc, pc);
    return false;
  }
  for (int i = 0; i < RISCV_GPR_NUM; i++) {
    if (ref_r->gpr[i] != gpr(i)) {
      Log("PC in REF: " FMT_VADDR " DUT: " FMT_VADDR, ref_r->pc, pc);
      Log("Different values of reg %s! REF: " FMT_WORD " DUT: " FMT_WORD,
          regs[i], ref_r->gpr[i], gpr(i));
      return false;
    }
  }
  if (ref_r->csr.mepc != cpu.csr.mepc) {
    Log("Different values of csr mepc! REF: " FMT_WORD " DUT: " FMT_WORD,
        ref_r->csr.mepc, cpu.csr.mepc);
    return false;
  }
  if (ref_r->csr.mcause != cpu.csr.mcause) {
    Log("Different values of csr mcause! REF: " FMT_WORD " DUT: " FMT_WORD,
        ref_r->csr.mcause, cpu.csr.mcause);
    return false;
  }
  if (ref_r->csr.mstatus != cpu.csr.mstatus) {
    Log("Different values of csr mstatus! REF: " FMT_WORD " DUT: " FMT_WORD,
        ref_r->csr.mstatus, cpu.csr.mstatus);
    return false;
  }
  if (ref_r->csr.mtvec != cpu.csr.mtvec) {
    Log("Different values of csr mtvec! REF: " FMT_WORD " DUT: " FMT_WORD,
        ref_r->csr.mtvec, cpu.csr.mtvec);
    return false;
  }
  return true;
}

uint64_t skip_ringbuffer[3];
uint8_t skip_ringbuffer_b[3];
uint8_t point;

void difftest_skip_ref(paddr_t skipPC) {
  if (!enable_difftest || !skipPC) {
    return;
  }
  skip_ringbuffer[point] = skipPC;
  skip_ringbuffer_b[point] = true;
  point = (point + 1) % 3;
}

void init_difftest(char *ref_so_file, long img_size, int port) {
  assert(ref_so_file != NULL);

  void *handle;

  handle = dlopen(ref_so_file, RTLD_LAZY | RTLD_LOCAL);

  assert(handle);

  ref_difftest_memcpy =
      (void (*)(paddr_t, void *, size_t, bool))dlsym(handle, "difftest_memcpy");
  assert(ref_difftest_memcpy);

  ref_difftest_regcpy =
      (void (*)(void *, bool))dlsym(handle, "difftest_regcpy");
  assert(ref_difftest_regcpy);

  ref_difftest_exec = (void (*)(uint64_t n))dlsym(handle, "difftest_exec");
  assert(ref_difftest_exec);

  ref_difftest_raise_intr =
      (void (*)(uint64_t))dlsym(handle, "difftest_raise_intr");
  assert(ref_difftest_raise_intr);

  ref_difftest_init = (void (*)(int))dlsym(handle, "difftest_init");
  assert(ref_difftest_init);

  Log("Differential testing: %s", ANSI_FMT("ON", ANSI_FG_GREEN));
  Log("The result of every instruction will be compared with %s. "
      "This will help you a lot for debugging, but also significantly reduce "
      "the performance. "
      "If it is not necessary, you can turn it off in menuconfig.",
      ref_so_file);

  ref_difftest_init(port);
  ref_difftest_memcpy(RESET_VECTOR, guest_to_host(RESET_VECTOR), img_size,
                      DIFFTEST_TO_REF);
  ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
}

void reinit_difftest(void) {
  ref_difftest_memcpy(RESET_VECTOR, guest_to_host(RESET_VECTOR), CONFIG_MSIZE,
                      DIFFTEST_TO_REF);
  ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
}

static void checkregs(CPU_state *ref, vaddr_t pc) {
  if (!isa_difftest_checkregs(ref, pc)) {
    npc_state.state = NPC_ABORT;
    npc_state.halt_pc = pc;
    isa_reg_display();
  }
}

uint8_t find_skip(paddr_t skipPC) {
  for (int i = 0; i < 3; i++) {
    if (skip_ringbuffer[i] == skipPC && skip_ringbuffer_b[i]) {
      skip_ringbuffer_b[i] = false;
      return true;
    }
  }
  return false;
}

void difftest_step(vaddr_t pc, vaddr_t npc) {
  if (!enable_difftest) {
    return;
  }
  CPU_state ref_r;

  if (find_skip(pc)) {
    // Log("pc: %lx", skip_ringbuffer[now]);
    ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
    return;
  }

  ref_difftest_exec(1);
  ref_difftest_regcpy(&ref_r, DIFFTEST_TO_DUT);

  checkregs(&ref_r, npc);
}
#else
void init_difftest(char *ref_so_file, long img_size, int port) {}
#endif
