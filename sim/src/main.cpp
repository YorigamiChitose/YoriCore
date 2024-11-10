#include "tui/tui.h"
#include "util/util.h"
#include "verilator/verilator.h"
#include <ncurses.h>

int main(void) {
  init_all();

  vtop->ioDMem_ready = true;

  int ch = '\0';
  bool running = true;
  while (running) {
    ch = getch();
    switch (ch) {
    case KEY_UP:
      break;
    case KEY_DOWN:
      break;
    case KEY_LEFT:
      break;
    case KEY_RIGHT:
      break;
    case 'q':
      running = FALSE; // 按下 'q' 键退出循环
      break;
    case 'c':
      step_verilator();
      break;
    default:
      break;
    }
    refresh_cpu_status();
    update_tui();

    if (cpu_status.EX_WB_excType == 1) {
      break;
    }
  }

  exit_all();
  return 0;
}