.PHONY: clean clean-c clean-v clean-mill

clean: clean-c clean-v

clean-c:
	rm -rf $(subst $(TOP_DIR)/,,$(CHISEL_BUILD_DIR))

clean-v:
	rm -rf $(subst $(TOP_DIR)/,,$(VERILATOR_BUILD_DIR))

clean-mill:
	mill - clean
