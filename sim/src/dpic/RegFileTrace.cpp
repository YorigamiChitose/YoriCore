#include <svdpi.h>
#include <verilated.h>
#include <verilated_sym_props.h>

uint32_t *cpu_gpr = NULL;

extern "C" void set_gpr_ptr(const svOpenArrayHandle r) {
  VerilatedDpiOpenVar *g = (VerilatedDpiOpenVar *)r;
  cpu_gpr = (uint32_t *)(g->datap());
}