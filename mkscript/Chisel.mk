.PHONY: chisel

# Chisel.mk
MODULE ?= $(TOP_MODULE)
CHISEL_BUILD_DIR      = $(BUILD_DIR)/chisel
CHISEL_TOP_MODULE     = $(CHISEL_BUILD_DIR)/$(MODULE).sv
CHISEL_SRC_DIR        = $(TOP_DIR)/src
CHISEL_SRC_FILES      = $(foreach dir, $(shell find $(CHISEL_SRC_DIR) -maxdepth 5 -type d 2>/dev/null), $(wildcard $(dir)/*.scala))
CHISEL_TOOL           = Tools.build

$(CHISEL_TOP_MODULE): $(CHISEL_SRC_FILES)
	@echo "$(COLOR_R)--- Building $(subst $(TOP_DIR)/,,$(CHISEL_TOP_MODULE)) ---$(COLOR_NO)"
	@mkdir -p $(CHISEL_BUILD_DIR)
	mill -i runMain $(CHISEL_TOOL) --target-dir $(CHISEL_BUILD_DIR) --split-verilog
	@echo "$(COLOR_G)--- $(subst $(TOP_DIR)/,,$(CHISEL_TOP_MODULE)) built ---$(COLOR_NO)"

chisel: $(CHISEL_TOP_MODULE)

