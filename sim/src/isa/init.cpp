#include "monitor/monitor.h"
#include <cassert>
#include <cpu/cpu.h>

void engine_start(void) { sdb_mainloop(); }
