#ifndef __DEVICE_H__
#define __DEVICE_H__

#include "common.h"
#include <cstdint>

#define TIMER_HZ 60

typedef void (*alarm_handler_t)();
void add_alarm_handle(alarm_handler_t h);
void dev_raise_intr(void);
void init_alarm(void);
void init_map(void);
void init_serial(void);
void init_device(void);
void init_timer(void);
void init_vga(void);
void vga_update_screen(void);
void device_update(void);

void send_key(uint8_t scancode, bool is_keydown);
void init_i8042(void);

#endif