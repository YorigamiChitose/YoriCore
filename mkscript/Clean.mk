.PHONY: clean clean-c clean-s clean-mill

clean: clean-c clean-s

clean-c:
	rm -rf $(CHISEL_BUILD_DIR)

clean-s:
	rm -rf $(SIM_BUILD_DIR)
	rm -rf $(SIM_AUTOCONFIG_H) $(SIM_CONFIG)

clean-mill:
	mill clean
