#include <cstring>
#include <ncurses.h>

#include "autoconf/common.h"
#include "isa/cpu.h"
#include "isa/reg.h"

void creat_reg_win(void);

void init_tui(void) {
  // 初始化ncurses
  initscr();

  // 启用颜色支持（如果需要）
  start_color();

  // 启用键盘输入
  cbreak();

  // 禁用行缓冲
  noecho();

  // 启用特殊键（如箭头键）
  keypad(stdscr, TRUE);

  creat_reg_win();
}

WINDOW *reg_win;

#define REG_WIN_W (CONFIG_REG_ROW * (8 + 2 + 4 + 1) + 2)
#define REG_WIN_H (CONFIG_REG_COL + 2)

void creat_reg_win(void) {
  reg_win = newwin(REG_WIN_H, REG_WIN_W, 0, 0);

  box(reg_win, 0, 0);

  const char titel[] = "General register";

  wattron(reg_win, A_BOLD);
  mvwprintw(reg_win, 0, 2, titel);
  wattroff(reg_win, A_BOLD);
}

void update_reg_win(void) {
  for (int i = 0; i < CONFIG_REG_COL; i++) {
    for (int j = 0; j < CONFIG_REG_ROW; j++) {
      mvwprintw(reg_win, i + 1, (j * 15) + 1, "%3s:0x%08X",
                regs[i * CONFIG_REG_ROW + j], cpu.gpr[i * CONFIG_REG_ROW + j]);
    }
  }
}

void update_tui(void) {
  update_reg_win();
  wrefresh(reg_win);
}

void exit_tui(void) { endwin(); }