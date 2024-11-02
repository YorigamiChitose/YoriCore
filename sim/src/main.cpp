#include <VCore.h>
#include <iostream>
#include <main.h>
#include <verilated.h>
#include <verilated_vcd_c.h>

VCore *vtop = NULL;
VerilatedContext *contextp = NULL;
VerilatedVcdC *tfp = NULL;

int main(void) {
  std::cout << HELLO << std::endl;

  contextp = new VerilatedContext;
  tfp = new VerilatedVcdC;
  vtop = new VCore;

  contextp->traceEverOn(true);
  vtop->trace(tfp, 3);
  tfp->open("wave.vcd");

  vtop->reset = 1;
  vtop->eval();
  contextp->timeInc(1);
  tfp->dump(contextp->time());
  vtop->clock = 1;
  vtop->eval();
  contextp->timeInc(1);
  tfp->dump(contextp->time());
  vtop->reset = 0;
  vtop->eval();
  vtop->clock = 0;
  contextp->timeInc(1);
  tfp->dump(contextp->time());

  vtop->ioIMem_inst = 0x00000297;
  vtop->ioIMem_ready = true;
  for (int i = 0; i < 10; i++) {
    vtop->clock = 1;
    vtop->eval();
    contextp->timeInc(1);
    tfp->dump(contextp->time());
    vtop->clock = 0;
    vtop->eval();
    contextp->timeInc(1);
    tfp->dump(contextp->time());
  }

  tfp->close();
  return 0;
}