#include <VCore.h>
#include <VCore_Core.h>
#include <VCore_PipeStage.h>
#include <VCore_PipeStage_1.h>
#include <VCore_PipeStage_2.h>
#include <VCore_PipeStage_3.h>
#include <iostream>
#include <main.h>
#include <verilated.h>
#include <verilated_sym_props.h>
#include <verilated_vcd_c.h>

VCore *vtop = NULL;
VerilatedContext *contextp = NULL;
VerilatedVcdC *tfp = NULL;

void init_verilator(void) {
  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  vtop = new VCore;

  contextp->traceEverOn(true);
  vtop->trace(tfp, 3);
  tfp->open("wave.vcd");

  vtop->reset = 1;
  vtop->clock = 0;
  eval_verilator();
  vtop->reset = 1;
  vtop->clock = 1;
  eval_verilator();
  vtop->reset = 0;
  vtop->clock = 0;
  eval_verilator();
}

uint32_t *cpu_gpr = NULL;
extern "C" void set_gpr_ptr(const svOpenArrayHandle r) {
  VerilatedDpiOpenVar *g = (VerilatedDpiOpenVar *)r;
  cpu_gpr = (uint32_t *)(g->datap());
}

// 一个输出RTL中通用寄存器的值的示例
void dump_gpr() {
  int i;
  for (i = 0; i < 32; i++) {
    printf("gpr[%d] = 0x%x\n", i, cpu_gpr[i]);
  }
}

int main(void) {
  std::cout << HELLO << std::endl;
  init_verilator();
  vtop->ioIMem_inst = 0x00000297;
  vtop->ioIMem_ready = true;
  for (int i = 0; i < 10; i++) {
    vtop->clock = 1;
    eval_verilator();
    vtop->clock = 0;
    eval_verilator();
    dump_gpr();
  }

  tfp->close();
  return 0;
}