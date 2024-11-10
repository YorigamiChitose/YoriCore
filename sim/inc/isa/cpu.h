#ifndef __CPU_H__
#define __CPU_H__

#include <cstdint>

struct CPU {
  uint32_t *gpr;
};

extern struct CPU cpu;

#endif