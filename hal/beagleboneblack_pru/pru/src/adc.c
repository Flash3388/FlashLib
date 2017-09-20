/*
 * adc.c
 *
 *  Created on: Sep 19, 2017
 *      Author: root
 */


#include "adc.h"
#include "hw_types.h"
#include "hw_defines.h"
#include "bbb_defines.h"
#include "hal_defines.h"

void adc_initialize(){
	int i, j;
	unsigned int fifo_count;
	unsigned fifo_data;

	//enable clock
	HWREG(BBB_CM_PER_ADDR + BBB_WKUP_OFFSET_FROM_CM + BBB_ADCTSC_WKUP_CLKCTRL) = 0x3;

	//pre-disable module
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_CTRL) &= ~0x1;

	//set range
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_ADCRANGE) |= (0x000 | 0xfff << 16);
	//set clock div
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_CLKDIV) = HAL_AIN_CLK_DIVISOR;

	//config channels:
	//cancel step config register protection
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_CTRL) |= 0x4;
	for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		//setting step config
		HWREG(BBB_ADCTSC_0_REGS + (BBB_ADCTSC_STEPCONFIG1 + i * 0x8)) &= ~(0x1f);
		HWREG(BBB_ADCTSC_0_REGS + (BBB_ADCTSC_STEPCONFIG1 + i * 0x8)) |=
				(HAL_AIN_CONFIG_MODE | (HAL_AIN_STEP_AVG << 2) | (i << 19) | (i << 15) | ((i % 2) << 26));

		//setting delay
		HWREG(BBB_ADCTSC_0_REGS + (BBB_ADCTSC_STEPDELAY1 + i * 0x8)) = 0;
		HWREG(BBB_ADCTSC_0_REGS + (BBB_ADCTSC_STEPDELAY1 + i * 0x8)) |= ((HAL_AIN_SAMPLE_DELAY - 1) << 24 || HAL_AIN_OPEN_DELAY);
	}
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_CTRL) &= ~(0x4);

	//clean clock divisor
	for(i = 0; i < 2; ++i){
		fifo_count = adc_fifo_data_count(i);
		for(j = 0; j < fifo_count; ++j){
			fifo_data = adc_fifo_data_read(i);
		}
	}
}
void adc_free(){

	//disable module
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_CTRL) &= ~0x1;
}

void adc_channel_enable(char channel){
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_STEPENABLE) |= (0x0001 << (channel + 1));
}
void adc_channel_disable(char channel){
	HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_STEPENABLE) &= ~(0x0001 << (channel + 1));
}

unsigned int adc_fifo_data_count(char fifo){
	return HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_FIFOCOUNT(fifo));
}
unsigned int adc_fifo_data_read(char fifo){
	return HWREG(BBB_ADCTSC_0_REGS + BBB_ADCTSC_FIFODATA(fifo));
}
