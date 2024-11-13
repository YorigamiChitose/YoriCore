#include "memory/memory.h"
#include <VTop__Dpi.h>
#include <cstdio>
#include <verilated.h>

int dpic_dmem_read(int addr, char len) { return pmem_read(addr, len); }

void dpic_dmem_write(int addr, char len, int data) {
  pmem_write(addr, len, data);
}