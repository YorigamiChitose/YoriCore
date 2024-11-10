#include "tui/tui.h"
#include "verilator/verilator.h"

void init_all(void) {
  init_verilator();
  init_tui();
}

void exit_all(void) {
  exit_tui();
  exit_verilator();
}