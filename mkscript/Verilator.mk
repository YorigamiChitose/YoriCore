.PHONY: verilator run menuconfig

# Verilator.mk
VERILATOR_BUILD_DIR = $(BUILD_DIR)/verilator
VERILATOR_TOP_DIR   = $(TOP_DIR)/sim
VERILATOR_TARGET    = $(VERILATOR_BUILD_DIR)/V$(MODULE)
VERILATOR_SRC_DIR = $(VERILATOR_TOP_DIR)/src
VERILATOR_SRC_FILES = $(foreach dir, $(shell find $(VERILATOR_SRC_DIR) -maxdepth 5 -type d), $(wildcard $(dir)/*.cpp))
VERILATOR_INC_DIR = $(VERILATOR_TOP_DIR)/inc
VERILATOR_VSRC_DIR = $(CHISEL_BUILD_DIR)
VERILATOR_VSRC_FILES = $(foreach dir, $(shell find $(VERILATOR_VSRC_DIR) -maxdepth 1 -type d 2>/dev/null), $(wildcard $(dir)/*.sv))
VERILATOR_VSRC_FILES += $(wildcard resources/*.sv)
VERILATOR_FLAGS = -MMD -cc -O3 --x-assign fast --x-initial fast --noassert --trace --exe
VERILATOR_CONFIG = $(VERILATOR_TOP_DIR)/verilator.vlt
CXXFLAGS = $(addprefix -CFLAGS , -I$(VERILATOR_INC_DIR) -g -Wall -Werror -Wno-unknown-pragmas)
LDFLAGS = $(addprefix -LDFLAGS , -lreadline)

AUTOCONFIG_H = $(VERILATOR_INC_DIR)/autoconf/autoconf.h
AUTOCONFIG_CONFIG = $(TOP_DIR)/.config

$(VERILATOR_TARGET): $(VERILATOR_VSRC_FILES) $(VERILATOR_SRC_FILES) $(CHISEL_TOP_MODULE) $(AUTOCONFIG_H) $(VERILATOR_CONFIG)
	@echo "$(COLOR_R)--- Building $(subst $(TOP_DIR)/,,$(VERILATOR_TARGET)) ---$(COLOR_NO)"
	@mkdir -p $(VERILATOR_BUILD_DIR)
	verilator \
		$(VERILATOR_FLAGS) \
		--top-module $(MODULE) \
		$(VERILATOR_CONFIG) \
		$(VERILATOR_SRC_FILES) \
		$(VERILATOR_VSRC_FILES) \
		$(CXXFLAGS) \
		$(LDFLAGS) \
		-Mdir $(VERILATOR_BUILD_DIR)
	@make -C $(VERILATOR_BUILD_DIR) -f V$(MODULE).mk -j $(shell nproc) -s
	@echo "$(COLOR_G)--- $(subst $(TOP_DIR)/,,$(VERILATOR_TARGET)) built ---$(COLOR_NO)"

verilator: $(VERILATOR_TARGET)

$(AUTOCONFIG_CONFIG):
	@echo "$(COLOR_R)--- Building $(subst $(TOP_DIR)/,,$(AUTOCONFIG_CONFIG)) ---$(COLOR_NO)"
	python3 /usr/lib/python3/dist-packages/savedefconfig.py
	python3 /usr/lib/python3/dist-packages/defconfig.py defconfig
	@echo "$(COLOR_G)--- $(subst $(TOP_DIR)/,,$(AUTOCONFIG_CONFIG)) built ---$(COLOR_NO)"

$(AUTOCONFIG_H): $(AUTOCONFIG_CONFIG)
	@echo "$(COLOR_R)--- Building $(subst $(TOP_DIR)/,,$(AUTOCONFIG_H)) ---$(COLOR_NO)"
	@mkdir -p $(VERILATOR_INC_DIR)/autoconf
	python3 /usr/lib/python3/dist-packages/genconfig.py --header-path $(AUTOCONFIG_H) --config-out $(AUTOCONFIG_CONFIG)
	@echo "$(COLOR_G)--- $(subst $(TOP_DIR)/,,$(AUTOCONFIG_H)) built ---$(COLOR_NO)"

menuconfig:
	@echo "$(COLOR_R)--- Menuconfig ---$(COLOR_NO)"
	@mkdir -p $(VERILATOR_INC_DIR)/autoconf
	python3 /usr/lib/python3/dist-packages/menuconfig.py
	python3 /usr/lib/python3/dist-packages/genconfig.py --header-path $(AUTOCONFIG_H) --config-out $(AUTOCONFIG_CONFIG)
	@echo "$(COLOR_G)--- Menuconfig completed ---$(COLOR_NO)"

run: $(VERILATOR_TARGET)
	@echo "$(COLOR_R)--- Simulation ---$(COLOR_NO)"
	@$(VERILATOR_BUILD_DIR)/V$(MODULE)
	@echo "$(COLOR_G)--- Simulation completed ---$(COLOR_NO)"