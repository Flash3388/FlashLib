/*
 * main.c
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#include "pru.h"
#include "pru_defines.h"
#include "bbb_defines.h"
#include "hw_types.h"

volatile register unsigned int __R31;
unsigned int* shared_memory;
unsigned char loop_run;

void handle_host_interrupt();

void initialize_port();
void free_port();

void dio_pulse();

void pwm_frequency_set();

int main(void){

	int status_value = 0;
	unsigned long status_last_update = 0;

	shared_memory = (unsigned int*)PRU_MEM_SHARED_RAM;

	pru_initialize();
	shared_memory[PRU_MEM_STATUS_REG] = 1;

	//TODO: PRU LOOP
	loop_run = 1;
	status_last_update = pru_clock_us();
	status_value = 2;

	while(loop_run){

		if(__R31 & (1 << 30)){
			//TODO: HANDLE HOST INTERRUPT
			handle_host_interrupt();

			HWREG(0x20024) = 21; //clear interrupt
			__R31 = 35;//send interrupt to host
		}

		pru_handles_update(shared_memory);

		if(pru_clock_us() - status_last_update >= PRU_WATCHDOG_UPDATE){
			shared_memory[PRU_MEM_STATUS_REG] = status_value++;
			status_last_update = pru_clock_us();
		}
	}

	pru_shutdown();
	shared_memory[PRU_MEM_STATUS_REG] = 0;
	__R31 = 35;//send interrupt to host

	__halt();
	return 0;
}

void handle_host_interrupt(){
	unsigned char pru_action = shared_memory[PRU_MEM_ACTION_TYPE_REG];

	switch(pru_action){
		case PRU_ACTION_SYS_FREE: //FREE PRU... STOP ALL ACTIONS
			loop_run = 0;//stopping the loop
			break;
		case PRU_ACTION_PORT_INIT:
			initialize_port();
			break;
		case PRU_ACTION_PORT_FREE:
			free_port();
			break;

		case PRU_ACTION_DIO_PULSE:
			dio_pulse();
			break;

		case PRU_ACTION_PWM_FREQ_S:
			pwm_frequency_set();
			break;
	}
}

void initialize_port(){
	unsigned char type = shared_memory[PRU_MEM_HANDLE_TYPE_REG];
	short port = shared_memory[PRU_MEM_HANDLE_VAL_REG];

	short handle = -1;

	switch(type){
		case PRU_HANDLE_ADC:
			handle = pru_adc_initialize(port);
			break;
		case PRU_HANDLE_PWM:
			handle = pru_pwm_initialize(port);
			break;
		case PRU_HANDLE_DO:
			handle = pru_dio_initialize(port, BBB_DIR_OUTPUT);
			break;
		case PRU_HANDLE_DI:
			handle = pru_dio_initialize(port, BBB_DIR_INPUT);
			break;
	}

	shared_memory[PRU_MEM_HANDLE_RES_REG] = (unsigned int)handle;
}
void free_port(){
	unsigned char type = shared_memory[PRU_MEM_HANDLE_TYPE_REG];
	short port = shared_memory[PRU_MEM_HANDLE_VAL_REG];

	switch(type){
		case PRU_HANDLE_ADC:
			pru_adc_free(port);
			break;
		case PRU_HANDLE_PWM:
			pru_pwm_free(port);
			break;
		case PRU_HANDLE_DO:
		case PRU_HANDLE_DI:
			pru_dio_free(port);
			break;
	}
}
void dio_pulse(){
	unsigned int length = shared_memory[PRU_MEM_ACTION_VAL_REG];
	short port = shared_memory[PRU_MEM_HANDLE_VAL_REG];

	pru_dio_pulse(port, length);
}
void pwm_frequency_set(){
	unsigned char clkdiv = (shared_memory[PRU_MEM_ACTION_VAL_REG] & 0xff);
	unsigned char hspclkdiv = ((shared_memory[PRU_MEM_ACTION_VAL_REG] >> 8) & 0xff);
	short port = shared_memory[PRU_MEM_HANDLE_VAL_REG];

	pru_pwm_frequency_set(port, clkdiv, hspclkdiv);
}

