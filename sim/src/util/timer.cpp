#include <cstdint>
#include <cstdlib>
#include <sys/time.h>

uint64_t get_time_internal(void) {
  struct timeval now;
  gettimeofday(&now, NULL);
  uint64_t us = now.tv_sec * 1000000 + now.tv_usec;

  return us;
}

uint64_t get_time(void) {
  uint64_t now = get_time_internal();
  return now;
}

void init_rand(void) { srand(get_time_internal()); }
