.PHONY: reformat checkformat

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat