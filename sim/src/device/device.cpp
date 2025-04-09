#include "device/device.h"
#include "common.h"
#include "macro.h"
#include "utils.h"
#include <SDL2/SDL.h>

void device_update(void) {
  static uint64_t last = 0;
  uint64_t now = get_time();
  if (now - last < 1000000 / TIMER_HZ) {
    return;
  }
  last = now;

  IFDEF(CONFIG_HAS_VGA, vga_update_screen());

  SDL_Event event;
  while (SDL_PollEvent(&event)) {
    switch (event.type) {
    case SDL_QUIT:
      npc_state.state = NPC_QUIT;
      break;
#ifdef CONFIG_HAS_KEYBOARD
    // If a key was pressed
    case SDL_KEYDOWN:
    case SDL_KEYUP: {
      uint8_t k = event.key.keysym.scancode;
      bool is_keydown = (event.key.type == SDL_KEYDOWN);
      send_key(k, is_keydown);
      break;
    }
#endif
    default:
      break;
    }
  }
}

void sdl_clear_event_queue() {
#ifndef CONFIG_TARGET_AM
  SDL_Event event;
  while (SDL_PollEvent(&event))
    ;
#endif
}

void init_device() {
  init_map();

  IFDEF(CONFIG_HAS_SERIAL, init_serial());
  IFDEF(CONFIG_HAS_TIMER, init_timer());
  IFDEF(CONFIG_HAS_VGA, init_vga());
  IFDEF(CONFIG_HAS_KEYBOARD, init_i8042());
  // IFDEF(CONFIG_HAS_AUDIO, init_audio());
  init_alarm();
}
