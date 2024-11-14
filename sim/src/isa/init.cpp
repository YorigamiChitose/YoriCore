#include "memory/memory.h"
#include "monitor/monitor.h"
#include <cassert>
#include <cpu/cpu.h>
#include <cstring>

void engine_start(void) { sdb_mainloop(); }

static const uint32_t img[] = {
    0x00000297, // auipc t0,0
    0x00028823, // sb  zero,16(t0)
    0x0102c503, // lbu a0,16(t0)
    0x00100073, // ebreak (used as nemu_trap)
    0xdeadbeef, // some data
};

void init_isa(void) { memcpy(guest_to_host(RESET_VECTOR), img, sizeof(img)); }