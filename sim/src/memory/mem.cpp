#include "common.h"
#include "debug.h"
#include "macro.h"
#include "memory/memory.h"
#include <cstdint>
#include <cstdio>
#include <cstdlib>

static uint8_t pmem[CONFIG_MSIZE] PG_ALIGN = {};

uint8_t *guest_to_host(uint32_t paddr) { return pmem + paddr - CONFIG_MBASE; }
uint32_t host_to_guest(uint8_t *haddr) { return haddr - pmem + CONFIG_MBASE; }

void init_mem(void) {
  IFDEF(CONFIG_MEM_RANDOM, memset(pmem, rand(), CONFIG_MSIZE));
  Log("physical memory area [" FMT_PADDR ", " FMT_PADDR "]", PMEM_LEFT,
      PMEM_RIGHT);
}

static void out_of_bound(uint32_t addr) {
  printf("address = 0x%08x is out of bound of pmem [0x%08x, 0x%08x]\n", addr,
         PMEM_LEFT, PMEM_RIGHT);
  assert(0);
}

uint32_t pmem_read(uint32_t addr, int len) {
  if (in_pmem(addr)) {
    return host_read(guest_to_host(addr), len);
  }
  out_of_bound(addr);
  return 0;
}

void pmem_write(uint32_t addr, int len, uint32_t data) {
  if (in_pmem(addr)) {
    host_write(guest_to_host(addr), len, data);
    return;
  }
  out_of_bound(addr);
}