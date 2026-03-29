.PHONY: reformat checkformat

help:
	mill -i __.help

test:
	mill -i __.test

fmt:
	mill -i __.reformat

check:
	mill -i __.checkFormat