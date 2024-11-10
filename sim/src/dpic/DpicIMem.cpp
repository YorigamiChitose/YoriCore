#include <VTop__Dpi.h>
#include <cstdio>
#include <verilated.h>

int dpic_imem_read(int pc) {
  uint32_t img[128] = {
      0x00000297, // auipc t0,0
      0x00028823, // sb  zero,16(t0)
      0x0102c503, // lbu a0,16(t0)
      0x00100073, // ebreak (used as nemu_trap)
  };

  return img[(pc - 0x80000000) / 4];
}