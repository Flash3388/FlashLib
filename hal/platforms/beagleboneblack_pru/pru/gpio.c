/*
 * gpio.c
 *
 *  Created on: Jun 16, 2017
 *      Author: root
 */

#include "gpio.h"
#include "hw/hw_pru.h"

#define BASE_ADDR(x)      (GPIO_base_get(x))

/***********************************************************************\
 * GPIO base
\***********************************************************************/

void GPIO_initialize(){
	//enable gpio0
	HWREG(GPIO0 + GPIO_CTRL) = 0x0;
	//enable clock for gpio0
	HWREG(CM_WKUP + CM_WKUP_GPIO0_CLKCTRL) = 0x02 | (1 << 18);
	//set debounce time for gpio0
	//time = (debounce + 1) * 31us
	HWREG(GPIO0 + GPIO_DEBOUNCINGTIME) = 255;

	//same for gpio1
	HWREG(GPIO1 + GPIO_CTRL) = 0x0;
	HWREG(CM_WKUP + CM_PER_GPIO1_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(GPIO1 + GPIO_DEBOUNCINGTIME) = 255;

	//same for gpio2
	HWREG(GPIO2 + GPIO_CTRL) = 0x0;
	HWREG(CM_WKUP + CM_PER_GPIO2_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(GPIO2 + GPIO_DEBOUNCINGTIME) = 255;

	//same for gpio3
	HWREG(GPIO3 + GPIO_CTRL) = 0x0;
	HWREG(CM_WKUP + CM_PER_GPIO3_CLKCTRL) = 0x02 | (1 << 18);
	HWREG(GPIO3 + GPIO_DEBOUNCINGTIME) = 255;
}
void GPIO_shutdown(){

}

void GPIO_module_enable(uint8_t base){
	HWREG(BASE_ADDR(base) + GPIO_CTRL) &= ~(GPIO_CTRL_DISABLEMODULE);
}
void GPIO_module_disable(uint8_t base){
	HWREG(BASE_ADDR(base) + GPIO_CTRL) |= GPIO_CTRL_DISABLEMODULE;
}
void GPIO_module_reset(uint8_t base){
	HWREG(BASE_ADDR(base) + GPIO_SYSCONFIG) |= GPIO_SYSCONFIG_SOFTRESET;
}

/***********************************************************************\
 * GPIO base
\***********************************************************************/

void GPIO_dir_set(uint8_t base, uint8_t pin, uint8_t dir){
	if(dir == GPIO_DIR_OUTPUT)
		HWREG(BASE_ADDR(base) + GPIO_OE) &= ~(1 << pin);
	else
		HWREG(BASE_ADDR(base) + GPIO_OE) |= (1 << pin);
}
uint8_t GPIO_dir_get(uint8_t base, uint8_t pin){
	if(!(HWREG(BASE_ADDR(base) + GPIO_OE) & (1 << pin)))
		return GPIO_DIR_OUTPUT;
	return GPIO_DIR_INPUT;
}

void GPIO_write(uint8_t base, uint8_t pin, uint32_t value){
	if(value == GPIO_PIN_HIGH)
		HWREG(BASE_ADDR(base) + GPIO_SETDATAOUT) |= (1 << pin);
	else
		HWREG(BASE_ADDR(base) + GPIO_CLEARDATAOUT) |= (1 << pin);
}
uint32_t GPIO_read(uint8_t base, uint8_t pin){
	if(HWREG(BASE_ADDR(base) + GPIO_DATAIN) & (1 << pin))
		return GPIO_PIN_HIGH;
	return GPIO_PIN_LOW;
}


