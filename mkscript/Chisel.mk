.PHONY: test help verilog

# Chisel Config
CHISEL_BUILD_DIR      = $(BUILD_DIR)/chisel
CHISEL_BUILD_TOP_VSRC = $(CHISEL_BUILD_DIR)/$(TOP_MODULE).sv
CHISEL_BUILD_VSRC     = $(wildcard $(CHISEL_BUILD_DIR)/*.sv)
CHISEL_DIR            = $(TOP_DIR)/src
CHISEL_MAIN_DIR       = $(CHISEL_DIR)/main/scala
CHISEL_TEST_DIR       = $(CHISEL_DIR)/test/scala
CHISEL_SRC_PATH       = $(foreach dir, $(shell find $(CHISEL_MAIN_DIR) -maxdepth 5 -type d), $(wildcard $(dir)/*.scala)) \
												$(wildcard $(CHISEL_MAIN_DIR)/resources/*.sv)
CHISEL_TOOL           = Tools.build

$(CHISEL_BUILD_TOP_VSRC): $(CHISEL_SRC_PATH)
	@echo -e "$(COLOR_R)--- verilog start  ---$(COLOR_NO)"
	@mkdir -p $(CHISEL_BUILD_DIR)
	millw -i $(PRJ).runMain $(CHISEL_TOOL) --split-verilog -td $(CHISEL_BUILD_DIR)
	@echo -e "$(COLOR_R)--- verilog finish ---$(COLOR_NO)"

verilog: $(CHISEL_BUILD_TOP_VSRC)

test:
	millw -i $(PRJ).test

help:
	millw -i $(PRJ).runMain Elaborate --help