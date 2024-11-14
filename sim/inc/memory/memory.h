#ifndef __MEMORY_H__
#define __MEMORY_H__

#include "common.h"
#include <cassert>
#include <cstdint>

#define PMEM_LEFT ((uint32_t)CONFIG_MBASE)
#define PMEM_RIGHT ((uint32_t)CONFIG_MBASE + CONFIG_MSIZE - 1)
#define RESET_VECTOR (PMEM_LEFT + CONFIG_PC_RESET_OFFSET)

static inline bool in_pmem(uint32_t addr) {
  return (addr - CONFIG_MBASE) < CONFIG_MSIZE;
}

static inline uint32_t host_read(void *addr, int len) {
  switch (len) {
  case 1:
    return *(uint8_t *)addr;
  case 2:
    return *(uint16_t *)addr;
  case 4:
    return *(uint32_t *)addr;
  default:
    assert(0);
    return 0;
  }
}

static inline void host_write(void *addr, int len, uint32_t data) {
  switch (len) {
  case 1:
    *(uint8_t *)addr = data;
    return;
  case 2:
    *(uint16_t *)addr = data;
    return;
  case 4:
    *(uint32_t *)addr = data;
    return;
  default:
    assert(0);
  }
}

uint8_t *guest_to_host(uint32_t paddr);
uint32_t host_to_guest(uint8_t *haddr);

uint32_t pmem_read(uint32_t addr, int len);
void pmem_write(uint32_t addr, int len, uint32_t data);
void init_mem(void);

#endif
