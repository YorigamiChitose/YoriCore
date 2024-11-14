#include "memory/memory.h"
#include <VTop__Dpi.h>
#include <cstdio>
#include <verilated.h>

int dpic_imem_read(int pc) { return pmem_read(pc, 4); }