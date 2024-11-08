TOP_DIR   = $(PWD)
BUILD_DIR = $(TOP_DIR)/build
PRJ = playground
TOP_MODULE = Core

COLOR_R  := \e[31m
COLOR_G  := \e[32m
COLOR_Y  := \e[33m
COLOR_B  := \e[34m
COLOR_P  := \e[35m
COLOR_NO := \e[0m

include $(TOP_DIR)/mkscript/Chisel.mk
include $(TOP_DIR)/mkscript/Verilator.mk
include $(TOP_DIR)/mkscript/Clean.mk
include $(TOP_DIR)/mkscript/Mill.mk
