#include "common.h"
#include <VTop__Dpi.h>
#include <verilated.h>
#include <verilated_sym_props.h>

word_t *cpu_gpr;

extern "C" void set_gpr_ptr(const svOpenArrayHandle r) {
  VerilatedDpiOpenVar *g = (VerilatedDpiOpenVar *)r;
  cpu_gpr = (uint32_t *)(g->datap());
}