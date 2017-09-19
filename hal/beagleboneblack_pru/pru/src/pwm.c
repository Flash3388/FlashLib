/*
 * pwm.c
 *
 *  Created on: Sep 18, 2017
 *      Author: root
 */


#include "pwm.h"
#include "hw_types.h"
#include "hw_defines.h"
#include "bbb_defines.h"
#include "hal_defines.h"

const unsigned char CLKDIV[] = {1 ,2 ,4 ,8 ,16 ,32 , 64 , 128};
const unsigned char HSPCLKDIV[] = {1 ,2 ,4 ,6 ,8 ,10 , 12 , 14};

unsigned int epwm_ptr[3] = {BBB_PWMSS0_ADDR, BBB_PWMSS1_ADDR, BBB_PWMSS2_ADDR};

void pwm_initialize(){
	if(HWREG(BBB_CONTROL_MODULE + BBB_PWMSS_CTRL) & (1 << BBB_PWMSS0)){
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS0_CLKCTRL) = 0x2;//enable module clock
	}else{
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS0_CLKCTRL) = 0x3 << 16; //disable module
	}

	if(HWREG(BBB_CONTROL_MODULE + BBB_PWMSS_CTRL) & (1 << BBB_PWMSS1)){
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS1_CLKCTRL) = 0x2;//enable module clock
	}else{
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS1_CLKCTRL) = 0x3 << 16; //disable module
	}

	if(HWREG(BBB_CONTROL_MODULE + BBB_PWMSS_CTRL) & (1 << BBB_PWMSS2)){
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS2_CLKCTRL) = 0x2;//enable module clock
	}else{
		HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS2_CLKCTRL) = 0x3 << 16; //disable module
	}
}
void pwm_free(){
	HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS0_CLKCTRL) = 0x3 << 16; //disable module
	HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS1_CLKCTRL) = 0x3 << 16; //disable module
	HWREG(BBB_CM_PER_ADDR + BBB_CM_PER_EPWMSS2_CLKCTRL) = 0x3 << 16; //disable module
}

void pwm_module_enable(char module){
	HWREGH(epwm_ptr[module] + BBB_EPWM_AQCTLA) = 0x2 | (0x3 << 4);
	HWREGH(epwm_ptr[module] + BBB_EPWM_AQCTLB) = 0x2 | (0x3 << 8);
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCNT) = 0;
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCTL) &= ~0x3;
}
void pwm_module_disable(char module){
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCTL) |= 0x3;
	HWREGH(epwm_ptr[module] + BBB_EPWM_AQCTLA) = 0x1 | (0x3 << 4);
	HWREGH(epwm_ptr[module] + BBB_EPWM_AQCTLB) = 0x1 | (0x3 << 8);
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCNT) = 0;
}

void pwm_module_settings(char module, unsigned char clkdiv, unsigned char hspclkdiv,
		unsigned char dutyA, unsigned char dutyB){

	unsigned int TBPRD = (unsigned int)(1000000000 / (10 * CLKDIV[clkdiv] * HSPCLKDIV[hspclkdiv]));

	//setting clock diver and freeze time base
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCTL) = BBB_TBCTL_CTRMODE_FREEZE | (clkdiv << 10) | (hspclkdiv << 7);
	//setting duty cycles A and B
	HWREGH(epwm_ptr[module] + BBB_EPWM_CMPA) = (unsigned short)(TBPRD * dutyA / HAL_PWMSS_MAX_VALUE);
	HWREGH(epwm_ptr[module] + BBB_EPWM_CMPB) = (unsigned short)(TBPRD * dutyB / HAL_PWMSS_MAX_VALUE);
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBPRD) = TBPRD;
	//reset time base counter
	HWREGH(epwm_ptr[module] + BBB_EPWM_TBCNT) = 0;
}
