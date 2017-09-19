PRU_COMPILER_DIR := ti-cgt-pru_2.1.5
PRU_COMPILER := $(PRU_COMPILER_DIR)/bin/clpru
PRU_HEX := $(PRU_COMPILER_DIR)/bin/hexpru
PRU_SRC_DIR := src
PRU_C_FLAGS := -i$(PRU_COMPILER_DIR)/include -i$(PRU_COMPILER_DIR)/lib -i$(PRU_SRC_DIR)
PRU_FLAGS := --define=am3359 --define=pru0 --endian=little --hardware_mac=on
PRU_LD_FLAGS := -l libc.a

C_FILES := $(wildcard $(PRU_SRC_DIR)/*.c)
OBJ_FILES := $(notdir $(C_FILES:.c=.obj))

.PHONY: all
all:

	$(PRU_COMPILER) $(PRU_C_FLAGS) $(PRU_FLAGS) -c $(C_FILES)
	$(PRU_COMPILER) -z -m "pru.map" $(PRU_C_FLAGS) $(PRU_FLAGS) --reread_libs --stack_size=0x100 --heap_size=0x100 -o "pru.out" $(OBJ_FILES) $(PRU_SRC_DIR)/AM335x_PRU.cmd $(PRU_LD_FLAGS)
	$(PRU_HEX) $(PRU_COMPILER_DIR)/bin.cmd pru.out

.PHONY: clean
clean:
	-rm *.obj
	-rm *.map
	-rm *.bin
	-rm *.out