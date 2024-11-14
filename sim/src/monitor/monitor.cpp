#include "monitor/monitor.h"
#include "debug.h"
#include "isa/isa.h"
#include "macro.h"
#include "memory/memory.h"
#include "verilator/verilator.h"
#include <cstdlib>
#include <getopt.h>

char *img_file = NULL;
char *elf_file = NULL;

void welcome(void) {
  Log("Trace: %s", MUXDEF(CONFIG_TRACE, ANSI_FMT("ON", ANSI_FG_GREEN),
                          ANSI_FMT("OFF", ANSI_FG_RED)));
  Log("Build time: %s, %s", __TIME__, __DATE__);
  printf("Welcome to %s-NPC!\n",
         ANSI_FMT("riscv32", ANSI_FG_YELLOW ANSI_BG_RED));
  printf("For help, type \"help\"\n");
}

long load_img(void) {
  if (img_file == NULL) {
    Log("No image is given. Use the default build-in image.");
    return 4096; // built-in image size
  }

  FILE *fp = fopen(img_file, "rb");
  Assert(fp, "Can not open '%s'", img_file);

  fseek(fp, 0, SEEK_END);
  long size = ftell(fp);

  Log("The image is %s, size = %ld", img_file, size);

  fseek(fp, 0, SEEK_SET);
  int ret = fread(guest_to_host(RESET_VECTOR), size, 1, fp);
  assert(ret == 1);

  fclose(fp);
  return size;
}

static int parse_args(int argc, char *argv[]) {
  const struct option table[] = {
      {"batch", no_argument, NULL, 'b'},
      {"log", required_argument, NULL, 'l'},
      {"diff", required_argument, NULL, 'd'},
      {"port", required_argument, NULL, 'p'},
      {"help", no_argument, NULL, 'h'},
      {"elf", required_argument, NULL, 'e'},
      {0, 0, NULL, 0},
  };
  int o;
  while ((o = getopt_long(argc, argv, "-bh:e:", table, NULL)) != -1) {
    switch (o) {
    case 'b':
      sdb_set_batch_mode();
      break;
    case 'e':
      elf_file = optarg;
      break;
    case 1:
      img_file = optarg;
      return 0;
    default:
      printf("Usage: %s [OPTION...] IMAGE [args]\n\n", argv[0]);
      printf("\t-b,--batch              run with batch mode\n");
      printf("\t-e,--elf=FILE           elf FILE to be parsed\n");
      printf("\n");
      exit(0);
    }
  }
  return 0;
}

void init_monitor(int argc, char *argv[]) {
  /* Parse arguments. */
  parse_args(argc, argv);

  /* init mem */
  init_mem();

  /* init isa */
  init_isa();

  /* read img */
  load_img();

  /* init verilator */
  init_verilator();

  /* Display welcome message. */
  welcome();
}