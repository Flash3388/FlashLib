/*
 * pwm.c
 *
 *  Created on: Sep 7, 2017
 *      Author: root
 */

#include <pwm.h>

#include "defines.h"

const unsigned int pwmss_addr_offset[] = {BBB_PWMSS0_ADDR, BBB_PWMSS1_ADDR, BBB_PWMSS2_ADDR};


int bbb_pwm_init(){
	return 0;
}
void bbb_pwm_free(){

}

void bbb_pwm_setduty(unsigned char module, unsigned char pin, float duty){

}
float bbb_pwm_getduty(unsigned char module, unsigned char pin){
	return 0.0;
}

void bbb_pwm_setfrequency(unsigned char module, float frequency){

}
float bbb_pwm_getfrequency(unsigned char module){
	return 0.0;
}

void bbb_pwm_enable(unsigned char module){

}
void bbb_pwm_disable(unsigned char module){

}
