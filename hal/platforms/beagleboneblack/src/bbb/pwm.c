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

void bbb_pwm_setduty(uint8_t id, uint8_t port, float duty){

}
float bbb_pwm_getduty(uint8_t id, uint8_t port){
	return 0;
}

void bbb_pwm_enable(uint8_t id){

}
void bbb_pwm_disable(uint8_t id){

}
