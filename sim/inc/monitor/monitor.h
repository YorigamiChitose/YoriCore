#ifndef __MONITOR_H__
#define __MONITOR_H__

#include "common.h"

#ifdef CONFIG_WATCH_POINT
typedef struct watchpoint {
  int NO;
  struct watchpoint *next;
  word_t last_value;
  char expression[CONFIG_WP_EXPR_SIZE];
} WP;
void init_wp_pool(void);
bool is_change(void);
void check_wp(void);
WP *new_wp(char *expression, bool *success);
void free_wp(int NO, bool *success);
#endif

void sdb_set_batch_mode();
void init_monitor(int argc, char *argv[]);
word_t expr(char *e, bool *success);
void init_regex(void);
void init_sdb(void);
void sdb_mainloop(void);

#endif