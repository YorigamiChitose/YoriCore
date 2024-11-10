#include <VTop__Dpi.h>
#include <verilated.h>
#include <verilated_sym_props.h>

#include "isa/cpu.h"

extern "C" void set_gpr_ptr(const svOpenArrayHandle r) {
  VerilatedDpiOpenVar *g = (VerilatedDpiOpenVar *)r;
  cpu.gpr = (uint32_t *)(g->datap());
}