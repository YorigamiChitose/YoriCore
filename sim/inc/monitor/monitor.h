#ifndef __MONITOR_H__
#define __MONITOR_H__

#include "common.h"
void sdb_set_batch_mode();
void init_monitor(int argc, char *argv[]);
word_t expr(char *e, bool *success);
void init_regex(void);
void sdb_mainloop(void);

#endif