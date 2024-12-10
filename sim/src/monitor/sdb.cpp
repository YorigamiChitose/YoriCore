#include "cpu/cpu.h"
#include "isa/isa.h"
#include "macro.h"
#include "monitor/monitor.h"
#include "utils.h"
#include <cstdlib>
#include <cstdio>
#include <readline/history.h>
#include <readline/readline.h>

static char *rl_gets() {
  static char *line_read = NULL;
  HIST_ENTRY *last_line = NULL;

  if (line_read) {
    free(line_read);
    line_read = NULL;
  }

  line_read = readline("(npc) ");

  if (line_read && *line_read) {
    add_history(line_read);
  } else if (line_read && (history_length > 0)) {
    last_line = history_get(history_length);
    free(line_read);
    line_read = (char *)malloc((strlen(last_line->line) + 1) * sizeof(char));
    memcpy(line_read, last_line->line,
           (strlen(last_line->line) + 1) * sizeof(char));
  }

  return line_read;
}

static int cmd_help(char *args);
static int cmd_c(char *args);
static int cmd_q(char *args);
static int cmd_si(char *args);
static int cmd_sr(char *args);
static int cmd_info(char *args);
// static int cmd_x(char *args);
// static int cmd_p(char *args);
// static int cmd_w(char *args);
// static int cmd_d(char *args);
// static int cmd_trace(char *args);

static struct {
  const char *name;
  const char *description;
  int (*handler)(char *);
} cmd_table[] = {
    {"help", "Usage: help\t[str]\t\t - Display information about all commands.",
     cmd_help},
    {"c", "Usage: c\t\t\t - Continue the execution of the program.", cmd_c},
    {"q", "Usage: q\t\t\t - Exit NEMU.", cmd_q},
    {"si", "Usage: si\t[int]\t\t - Run step by step.", cmd_si},
    {"sr", "Usage: sr\t\t\t - Run step by step with reg info.", cmd_sr},
    {"info", "Usage: info\tr | w\t\t - Get the info.", cmd_info},
    // {"x", "Usage: x\t[int] [str]\t - Scan the memory.", cmd_x},
    // {"p", "Usage: p\t[str]\t\t - Calculate.", cmd_p},
    // {"w", "Usage: w\t[str]\t\t - Set a new watchpoint.", cmd_w},
    // {"d", "Usage: d\t[int]\t\t - Delete a watchpoint.", cmd_d},
    // {"trace", "Usage: trace\t[str]\t\t - Check the inst.", cmd_trace}
};

char *find_next_str(char *args) {
  if (args == NULL) {
    return NULL;
  }
  int i = 0;
  while (args[++i] != '\0') {
  }
  for (int j = i; j < i + 64; j++) {
    if (args[j] == '\0') {
      return &args[++i];
    }
  }
  return NULL;
}

char *get_arguments(char *args, int num) {
  char *arg = args;
  int num_temp = num;
  int i = 0;
  while (--num_temp + 1 && arg != NULL) {
    if (num == num_temp + 1) {
      arg = strtok(arg, " ");
    } else {
      i = 0;
      while (arg[i++] != '\0') {
      }
      arg = strtok(arg + i, " ");
    }
  }
  return arg;
}

#define NR_CMD ARRLEN(cmd_table)

static int cmd_c(char *args) {
  cpu_exec(-1);
  return 0;
}

static int cmd_q(char *args) {
  if (npc_state.state == NPC_ABORT) {
    npc_state.halt_ret = 1;
  }
  if (npc_state.halt_ret == 0) {
    npc_state.state = NPC_QUIT;
  }
  return -1;
}

static int cmd_help(char *args) {
  char *arg = strtok(NULL, " ");
  int i;
  if (arg == NULL) {
    for (i = 0; i < NR_CMD; i++) {
      printf("%-6s- %s\n", cmd_table[i].name, cmd_table[i].description);
    }
  } else {
    for (i = 0; i < NR_CMD; i++) {
      if (strcmp(arg, cmd_table[i].name) == 0) {
        printf("%-6s- %s\n", cmd_table[i].name, cmd_table[i].description);
        return 0;
      }
    }
    printf("Error! Unknown command '%s'\n", arg);
  }
  return 0;
}

static int cmd_si(char *args) {
  if (args == NULL) {
    cpu_exec(1);
    return 0;
  }
  bool flag = true;
  word_t steps = expr(args, &flag);
  if (flag) {
    cpu_exec(steps);
  } else {
    printf("Error, please retry!\n");
  }
  return 0;
}

static int cmd_sr(char *args) {
  cpu_exec(1);
  isa_reg_display();
  return 0;
}

static int cmd_info(char *args) {
  char *arg = get_arguments(args, 1);
  if (arg == NULL) {
    printf("Error, please retry!\n");
    return 0;
  }
  if (strcmp(arg, "r") == 0) {
    isa_reg_display();
  } else if (strcmp(arg, "w") == 0) {
    // #ifdef CONFIG_WATCH_POINT
    //     check_wp();
    // #else
    //     printf("Watch point not enabled\n");
    // #endif
  }
  return 0;
}

int is_batch_mode = false;

void sdb_set_batch_mode(void) { is_batch_mode = true; }

void sdb_mainloop(void) {
  if (is_batch_mode) {
    cmd_c(NULL);
    return;
  }

  for (char *str; (str = rl_gets()) != NULL;) {
    char *str_end = str + strlen(str);

    char *cmd = strtok(str, " ");
    if (cmd == NULL) {
      continue;
    }

    char *args = cmd + strlen(cmd) + 1;
    if (args >= str_end) {
      args = NULL;
    }

    // #ifdef CONFIG_DEVICE
    //     extern void sdl_clear_event_queue();
    //     sdl_clear_event_queue();
    // #endif

    int i;
    for (i = 0; i < NR_CMD; i++) {
      if (strcmp(cmd, cmd_table[i].name) == 0) {
        if (cmd_table[i].handler(args) < 0) {
          return;
        }
        break;
      }
    }

    if (i == NR_CMD) {
      printf("Unknown command '%s'\n", cmd);
    }
  }
}

void init_sdb(void) { init_regex(); }