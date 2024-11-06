# Top
TOP_DIR   = $(PWD)
BUILD_DIR = $(TOP_DIR)/build
PRJ = playground
TOP_MODULE = Core

# Chisel Config
CHISEL_BUILD_DIR      = $(BUILD_DIR)/chisel
CHISEL_BUILD_TOP_VSRC = $(CHISEL_BUILD_DIR)/$(TOP_MODULE).sv
CHISEL_BUILD_VSRC     = $(wildcard $(CHISEL_BUILD_DIR)/*.sv)
CHISEL_DIR            = $(TOP_DIR)/src
CHISEL_MAIN_DIR       = $(CHISEL_DIR)/main
CHISEL_TEST_DIR       = $(CHISEL_DIR)/test
CHISEL_SRC_PATH       = $(foreach dir, $(shell find $(CHISEL_MAIN_DIR) -maxdepth 5 -type d), $(wildcard $(dir)/*.scala))
CHISEL_TOOL           = Tools.build

# Verilator
SIM_DIR       = $(TOP_DIR)/sim
SIM_BUILD_DIR = $(BUILD_DIR)/sim
SIM_TARGET    = $(SIM_BUILD_DIR)/V$(TOP_MODULE)
SIM_SRC_DIR   = $(SIM_DIR)/src
SIM_INC_DIR   = $(SIM_DIR)/inc
SIM_SRC_PATH  = $(foreach dir, $(shell find $(SIM_SRC_DIR) -maxdepth 5 -type d), $(wildcard $(dir)/*.cpp))
SIM_FLAG      = 
SIM_CFLAG     = "-I$(SIM_INC_DIR) -g"
SIM_CONFIG    = $(SIM_DIR)/verilator.vlt

verilog: $(CHISEL_BUILD_TOP_VSRC)

verilator: $(SIM_TARGET)

$(SIM_TARGET): $(SIM_SRC_PATH) $(CHISEL_BUILD_TOP_VSRC)
	@verilator \
		--cc $(SIM_CONFIG) $(CHISEL_BUILD_VSRC) \
		--exe $(SIM_SRC_PATH) \
		-Mdir $(SIM_BUILD_DIR) \
		-top $(TOP_MODULE) \
		-CFLAGS $(SIM_CFLAG) \
		--trace
	
run: $(SIM_TARGET)
	@make -C $(SIM_BUILD_DIR) -f V$(TOP_MODULE).mk
	@cd $(SIM_BUILD_DIR) && $(SIM_TARGET)

wave:
	@cd $(SIM_BUILD_DIR) && gtkwave ./wave.vcd

$(CHISEL_BUILD_TOP_VSRC): $(CHISEL_SRC_PATH)
	@echo --- verilog start  ---
	@mkdir -p $(CHISEL_BUILD_DIR)
	mill -i $(PRJ).runMain $(CHISEL_TOOL) --split-verilog -td $(CHISEL_BUILD_DIR)
	@echo --- verilog finish ---

test:
	mill -i $(PRJ).test

help:
	mill -i $(PRJ).runMain Elaborate --help

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	rm -rf $(BUILD_DIR)

clean-c:
	rm -rf $(CHISEL_BUILD_DIR)

clean-s:
	rm -rf $(SIM_BUILD_DIR)

clean-mill:
	mill clean

.PHONY: test verilog help reformat checkformat clean
