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

void gpio_setdir(char header, char pin, char dir){
	if(dir == BBB_DIR_OUTPUT)
		HWREG(gpio_addr[header] + BBB_GPIO_OE) &= (1 << pin);
	else
		HWREG(gpio_addr[header] + BBB_GPIO_OE) |= (1 << pin);
}

void gpio_sethigh(char header, char pin){
	HWREG(gpio_addr[header] + BBB_GPIO_SETDATAOUT) = (1 << pin);
}
void gpio_setlow(char header, char pin){
	HWREG(gpio_addr[header] + BBB_GPIO_CLEARDATAOUT) = (1 << pin);
}

char gpio_ishigh(char header, char pin){
	return (HWREG(gpio_addr[header] + BBB_GPIO_DATAIN) & (1 << pin)) != 0;
}
