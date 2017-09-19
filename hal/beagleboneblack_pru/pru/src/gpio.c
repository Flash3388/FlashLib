/*
 * gpio.c
 *
 *  Created on: Sep 18, 2017
 *      Author: root
 */

#include "gpio.h"
#include "hw_types.h"
#include "hw_defines.h"
#include "bbb_defines.h"

const signed char port_set[2][46] = {
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
const unsigned int port_id_set[2][46] = {
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

unsigned int gpio_addr[4] = {BBB_GPIO0_ADDR, BBB_GPIO1_ADDR, BBB_GPIO2_ADDR, BBB_GPIO3_ADDR};

void gpio_initialize(){
	//enable gpio0
	HWREG(BBB_GPIO0_ADDR + BBB_GPIO_CTRL) = 0x0;
	//enable clock for gpio0
	HWREG(BBB_CM_WKUP + BBB_CM_WKUP_GPIO0_CLKCTRL) = 0x02 | (1 << 18);
	//set debounce time for gpio0
	//time = (debounce + 1) * 31us
	HWREG(BBB_GPIO0_ADDR + BBB_GPIO_DEBOUNCINGTIME) = 255;
	//enable gpio module
	HWREG(BBB_GPIO0_ADDR + BBB_GPIO_CTRL) &= ~(BBB_GPIO_CTRL_DISABLEMODULE);

	//same for gpio1
	HWREG(BBB_GPIO1_ADDR + BBB_GPIO_CTRL) = 0x0;
	HWREG(BBB_CM_WKUP + BBB_CM_PER_GPIO1_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(BBB_GPIO1_ADDR + BBB_GPIO_DEBOUNCINGTIME) = 255;
	HWREG(BBB_GPIO1_ADDR + BBB_GPIO_CTRL) &= ~(BBB_GPIO_CTRL_DISABLEMODULE);

	//same for gpio2
	HWREG(BBB_GPIO2_ADDR + BBB_GPIO_CTRL) = 0x0;
	HWREG(BBB_CM_WKUP + BBB_CM_PER_GPIO2_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(BBB_GPIO2_ADDR + BBB_GPIO_DEBOUNCINGTIME) = 255;
	HWREG(BBB_GPIO2_ADDR + BBB_GPIO_CTRL) &= ~(BBB_GPIO_CTRL_DISABLEMODULE);

	//same for gpio3
	HWREG(BBB_GPIO3_ADDR + BBB_GPIO_CTRL) = 0x0;
	HWREG(BBB_CM_WKUP + BBB_CM_PER_GPIO3_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(BBB_GPIO3_ADDR + BBB_GPIO_DEBOUNCINGTIME) = 255;
	HWREG(BBB_GPIO3_ADDR + BBB_GPIO_CTRL) &= ~(BBB_GPIO_CTRL_DISABLEMODULE);
}
void gpio_free(){
	//disable modules
	HWREG(BBB_GPIO0_ADDR + BBB_GPIO_CTRL) |= BBB_GPIO_CTRL_DISABLEMODULE;
	HWREG(BBB_GPIO1_ADDR + BBB_GPIO_CTRL) |= BBB_GPIO_CTRL_DISABLEMODULE;
	HWREG(BBB_GPIO2_ADDR + BBB_GPIO_CTRL) |= BBB_GPIO_CTRL_DISABLEMODULE;
	HWREG(BBB_GPIO3_ADDR + BBB_GPIO_CTRL) |= BBB_GPIO_CTRL_DISABLEMODULE;
}

signed char gpio_module_get(char header, char pin){
	if(header > 1 || pin >= BBB_HEADER_PIN_COUNT)
		return -1;

	return port_set[header][pin];
}

void gpio_setdir(char header, char pin, char dir){
	if(dir == BBB_DIR_OUTPUT)
		HWREG(gpio_addr[port_set[header][pin - 1]] + BBB_GPIO_OE) &= (port_id_set[header][pin]);
	else
		HWREG(gpio_addr[port_set[header][pin - 1]] + BBB_GPIO_OE) |= (port_id_set[header][pin]);
}

void gpio_sethigh(char header, char pin){
	HWREG(gpio_addr[port_set[header][pin]] + BBB_GPIO_SETDATAOUT) = port_id_set[header][pin];
}
void gpio_setlow(char header, char pin){
	HWREG(gpio_addr[port_set[header][pin]] + BBB_GPIO_CLEARDATAOUT) = port_id_set[header][pin];
}

char gpio_ishigh(char header, char pin){
	return (HWREG(gpio_addr[port_set[header][pin]] + BBB_GPIO_DATAIN) & port_id_set[header][pin]) != 0;
}
