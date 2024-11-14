#include "cpu/cpu.h"
#include "isa/isa.h"
#include "utils.h"
#include <cstdio>
#include <cstring>

const char *regs[] = {"$0", "ra", "sp",  "gp",  "tp", "t0", "t1", "t2",
                      "s0", "s1", "a0",  "a1",  "a2", "a3", "a4", "a5",
                      "a6", "a7", "s2",  "s3",  "s4", "s5", "s6", "s7",
                      "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

void isa_reg_display(void) {
  for (int i = 0; i < 32 / 4; i++) {
    for (int j = 0; j < 4; j++) {
      printf(ANSI_FG_GREEN "%-3s" ANSI_NONE ": " FMT_WORD "\t",
             reg_name(i * 4 + j), gpr(i * 4 + j));
    }
    printf("\n");
  }
  printf(ANSI_FG_RED "%-7s" ANSI_NONE ": " FMT_WORD "\n", "pc", cpu.pc);
  printf(ANSI_FG_RED "%-7s" ANSI_NONE ": "
                     "%d"
                     "\n",
         "mod", cpu.mode);
  printf(ANSI_FG_RED "%-7s" ANSI_NONE ": " FMT_WORD "\n", "mepc", cpu.csr.mepc);
  printf(ANSI_FG_RED "%-7s" ANSI_NONE ": " FMT_WORD "\n", "mcause",
         cpu.csr.mcause);
  printf(ANSI_FG_RED "%-7s" ANSI_NONE ": " FMT_WORD "\n", "mstatus",
         cpu.csr.mstatus);
}

word_t isa_reg_str2val(const char *s, bool *success) {
  for (int i = 0; i < 32; i++) {
    if (strcmp(s, regs[i]) == 0) {
      (*success) = true;
      return gpr(i);
    }
  }
  (*success) = false;
  printf("Reg name error, Please retry!\n");
  return 0;
}