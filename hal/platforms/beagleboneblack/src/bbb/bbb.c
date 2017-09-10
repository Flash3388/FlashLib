/*
 * bbb.c
 *
 *  Created on: Sep 7, 2017
 *      Author: root
 */

#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

#include <bbb_defines.h>

#include "bbb.h"
#include "defines.h"

const unsigned int gpio_addr_offset[] = {BBB_GPIO0_ADDR, BBB_GPIO1_ADDR, BBB_GPIO2_ADDR, BBB_GPIO3_ADDR};

const char port_set[] = {
		{
			-1, -1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 0, 0, 1, 1,
			0, 2, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0,
			0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
		},
		{
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 0, 1, 1,
			1, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 3, 3, 3, 3, 3, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1
		}
};
const unsigned int port_id_set[] = {
		{
			0, 0, 1<<6, 1<<7, 1<<2,	1<<3, 1<<2, 1<<3,
			1<<5, 1<<4, 1<<13, 1<<12, 1<<23, 1<<26, 1<<15,
			1<<14, 1<<27, 1<<1, 1<<22, 1<<31, 1<<30, 1<<5,
			1<<4, 1<<1, 1<<0, 1<<29, 1<<22, 1<<24, 1<<23,
			1<<25, 1<<10, 1<<11, 1<<9, 1<<17, 1<<8, 1<<16,
			1<<14, 1<<15, 1<<12, 1<<13, 1<<10, 1<<11, 1<<8,
			1<<9, 1<<6, 1<<7
		},
		{
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 1<<30, 1<<28, 1<<31, 1<<18, 1<<16, 1<<19,
			1<<5, 1<<4, 1<<13, 1<<12, 1<<3, 1<<2, 1<<17,
			1<<15, 1<<21, 1<<14, 1<<19, 1<<17, 1<<15, 1<<16,
			1<<14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1<<20,
			1<<7, 0, 0, 0, 0
		}
};

volatile unsigned int* gpio_addr[4] = {0, 0, 0, 0};

int memh = 0;

int bbb_init(){
	if(memh){
		return -1;
	}

	memh = open("/dev/mem", O_RDWR);
	for(int i = 0; i < 4; ++i){
		gpio_addr[i] = mmap(0, BBB_GPIOX_LEN, PROT_READ | PROT_WRITE, MAP_SHARED, memh, gpio_addr_offset[i]);
		if(gpio_addr[i] == MAP_FAILED){
			return -1;
		}
	}

	return 0;
}
void bbb_free(){
	if(memh != 0){
		close(memh);
	}
}

char bbb_getportgpio(unsigned char header, unsigned char pin){
	if(memh != 0 || header > 1 || pin >= BBB_HEADER_PIN_COUNT || pin == 0)
		return -1;

	return port_set[header][pin - 1];
}
unsigned int bbb_getportid(unsigned char header, unsigned char pin){
	if(memh != 0 || header > 1 || pin >= BBB_HEADER_PIN_COUNT || pin == 0)
		return 0;

	return port_id_set[header][pin - 1];
}

void bbb_setdir(unsigned char header, unsigned char pin, unsigned char dir){
	if(bbb_getportgpio(header, pin) < 0)
		return;
	if(dir == BBB_DIR_OUTPUT)
		HWREG(gpio_addr[port_set[header][pin - 1]] + BBB_GPIO_OE) &= (port_id_set[header][pin - 1]);
	else
		HWREG(gpio_addr[port_set[header][pin - 1]] + BBB_GPIO_OE) |= (port_id_set[header][pin - 1]);
}
char bbb_getdir(unsigned char header, unsigned char pin){
	if(bbb_getportgpio(header, pin) < 0)
		return -1;

	if(HWREG(gpio_addr[port_set[header][pin - 1]] + BBB_GPIO_OE) & port_id_set[header][pin - 1])
		return BBB_DIR_OUTPUT;
	else
		return 0;
}

void bbb_sethigh(unsigned char header, unsigned char pin){
	HWREG(gpio_addr[port_set[header][pin-1]] + BBB_GPIO_SETDATAOUT) = port_id_set[header][pin - 1];
}
void bbb_setlow(unsigned char header, unsigned char pin){
	HWREG(gpio_addr[port_set[header][pin-1]] + BBB_GPIO_CLEARDATAOUT) = port_id_set[header][pin - 1];
}

unsigned char bbb_ishigh(unsigned char header, unsigned char pin){
	return (HWREG(gpio_addr[port_set[header][pin-1]] + BBB_GPIO_DATAIN) & port_id_set[header][pin - 1]) != 0;
}
unsigned char bbb_islow(unsigned char header, unsigned char pin){
	return (HWREG(gpio_addr[port_set[header][pin-1]] + BBB_GPIO_DATAIN) & port_id_set[header][pin - 1]) == 0;
}
