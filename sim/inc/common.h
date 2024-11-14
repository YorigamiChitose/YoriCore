#ifndef __COMMON_H__
#define __COMMON_H__

#include "autoconf/autoconf.h"

#include <cstdint>
#include <inttypes.h>

typedef uint32_t word_t;
typedef int32_t sword_t;
#define FMT_WORD "0x%08" PRIx32

typedef word_t vaddr_t;
typedef word_t paddr_t;
#define FMT_PADDR "0x%08" PRIx32
#define FMT_VADDR "0x%08" PRIx32

#endif