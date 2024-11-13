#include "isa/isa.h"
#include "monitor/monitor.h"
#include "utils.h"

int main(int argc, char *argv[]) {
  init_monitor(argc, argv);
  engine_start();
  return is_exit_status_bad();
}