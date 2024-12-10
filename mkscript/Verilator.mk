.PHONY: verilator run

# Verilator
SIM_DIR               = $(TOP_DIR)/sim
SIM_BUILD_DIR         = $(BUILD_DIR)/sim
SIM_TARGET            = $(SIM_BUILD_DIR)/V$(TOP_MODULE)
SIM_SRC_DIR           = $(SIM_DIR)/src
SIM_INC_DIR           = $(SIM_DIR)/inc
SIM_TOOL_DIR          = $(SIM_DIR)/tool
SIM_SRC_PATH          = $(foreach dir, $(shell find $(SIM_SRC_DIR) -maxdepth 5 -type d), $(wildcard $(dir)/*.cpp))
SIM_FLAG              = --error-limit 0
SIM_CFLAGS            = "-I$(SIM_INC_DIR) -g"
SIM_LDFLAGS           = "-lreadline"
SIM_CONFIG            = $(SIM_DIR)/verilator.vlt
SIM_AUTOCONFIG_H      = $(SIM_INC_DIR)/autoconf/autoconf.h
SIM_AUTOCONFIG_CONFIG = $(TOP_DIR)/.config

SIM_ARGS ?=
SIM_IMG ?=

$(SIM_TARGET): $(SIM_SRC_PATH) $(CHISEL_BUILD_TOP_VSRC) $(SIM_AUTOCONFIG_H)
	@echo -e "$(COLOR_R)--- verilator start  ---$(COLOR_NO)"
	verilator \
		--cc $(SIM_CONFIG) $(CHISEL_BUILD_VSRC) \
		--exe $(SIM_SRC_PATH) \
		-Mdir $(SIM_BUILD_DIR) \
		-top $(TOP_MODULE) \
		-CFLAGS $(SIM_CFLAGS) \
		-LDFLAGS $(SIM_LDFLAGS) \
		--trace-fst \
		$(SIM_FLAG)
	make -C $(SIM_BUILD_DIR) -f V$(TOP_MODULE).mk -s
	@echo -e "$(COLOR_R)--- verilator finish ---$(COLOR_NO)"

$(SIM_AUTOCONFIG_H): $(SIM_AUTOCONFIG_CONFIG)
	@echo -e "$(COLOR_R)--- generate autoconf start  ---$(COLOR_NO)"
	genconfig --header-path $(SIM_AUTOCONFIG_H) --config-out $(SIM_AUTOCONFIG_CONFIG)
	@echo -e "$(COLOR_R)--- generate autoconf finish ---$(COLOR_NO)"

menuconfig:
	menuconfig
	genconfig --header-path $(SIM_AUTOCONFIG_H) --config-out $(SIM_AUTOCONFIG_CONFIG)

verilator: $(SIM_TARGET)

run: $(SIM_TARGET)
	@echo -e "$(COLOR_R)--- run start ---$(COLOR_NO)"
	@cd $(SIM_BUILD_DIR) && $(SIM_TARGET) $(SIM_ARGS) $(SIM_IMG)
	@echo -e "$(COLOR_R)--- run finish ---$(COLOR_NO)"

wave:
	@cd $(SIM_BUILD_DIR) && gtkwave ./wave.fst