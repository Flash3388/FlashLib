/*
 * pru.c
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */

#include "pru.h"

volatile uint32_t* shared_memory = 0;
uint8_t pru_status = PRU_IDLE;

adc_handle_t adc_handles[ADC_COUNT];
uint8_t adc_handles_count = 0;

pwm_handle_t pwm_handles[PWM_COUNT];
uint8_t pwm_handles_count = 0;

dio_handle_t dio_handles[DIO_COUNT];
uint8_t dio_handles_count = 0;

/***********************************************************************\
 * pru utilities
\***********************************************************************/

uint32_t pru_clock(){
	return 0;
}
void pru_delay(uint32_t ms){

}

/***********************************************************************\
 * pru memory handling
\***********************************************************************/

void pru_mem_write(uint32_t memoffset, uint32_t data){
	shared_memory[memoffset] = data;
}
uint32_t pru_mem_read(uint32_t memoffset){
	return shared_memory[memoffset];
}
void pru_mem_clear(uint32_t memoffset){
	shared_memory[memoffset] = 0;
}

/***********************************************************************\
 * pru initialization
\***********************************************************************/

void pru_initialize(uint32_t* status){
	//enable access to whole memory map from pru
	HWREG(PRU_ICSS_CFG + PRU_ICSS_CFG_SYSCFG) &= ~(1 << 4);
	//pointer to shared ram
	shared_memory = (volatile uint32_t *)SHARED_MEMORY;

	//initializing gpio
	GPIO_initialize();

	*status = PRU_INITIALIZED;
	pru_status = PRU_INITIALIZED;
}
void pru_shutdown(uint32_t* status){

}

/***********************************************************************\
 * pru port init/shutdown
\***********************************************************************/

uint8_t pru_initializePort(uint8_t handle, uint8_t type){
	switch(type){
		case HANDLE_PWM:
			return pru_pwm_initializePort(handle);
		case HANDLE_DIO:
			return pru_dio_initializePort(handle);
		case HANDLE_ADC:
			return pru_adc_initializePort(handle);
	}
	return INVALID_HANDLE;
}
void pru_freePort(uint8_t handle, uint8_t type){
	switch(type){
		case HANDLE_PWM:
			pru_pwm_freePort(handle);
			break;
		case HANDLE_DIO:
			pru_dio_freePort(handle);
			break;
		case HANDLE_ADC:
			pru_adc_freePort(handle);
			break;
	}
}

uint8_t pru_dio_initializePort(uint8_t handle){
	return 0;
}
void pru_dio_freePort(uint8_t handle);
uint8_t pru_pwm_initializePort(uint8_t handle){
	return 0;
}
void pru_pwm_freePort(uint8_t handle){

}
uint8_t pru_adc_initializePort(uint8_t handle){
	return 0;
}
void pru_adc_freePort(uint8_t handle){

}

/***********************************************************************\
 * pru settings
\***********************************************************************/

void pru_settingsPort(uint8_t handle, uint8_t type, uint8_t dir, uint32_t* setting){
	switch(type){
		case HANDLE_PWM:
			pru_pwm_settings(handle, dir, setting);
			break;
		case HANDLE_DIO:
			pru_dio_settings(handle, dir, setting);
			break;
		case HANDLE_ADC:
			pru_adc_settings(handle, dir, setting);
			break;
	}
}


/***********************************************************************\
 * pru handles
\***********************************************************************/

void pru_updateHandles(){
	uint8_t i;
	for(i = 0; i < dio_handles_count; ++i){
		dio_handle_t chandle = dio_handles[i];
		if(chandle.dir == GPIO_DIR_OUTPUT){
			uint8_t cval;
			pru_mem_read(DIO_REGISTERS + i, &cval);
			if(cval != chandle.value){
				chandle.value = cval;
				GPIO_write(chandle.base, chandle.pin, cval);
			}
		}else{
			uint8_t cval = GPIO_read(chandle.base, chandle.dir);
			if(cval != chandle.value){
				chandle.value = cval;
				pru_mem_write(DIO_REGISTERS + i, cval);
			}
		}

	}
	for(i = 0; i < adc_handles_count; ++i){

	}
	for(i = 0; i < pwm_handles_count; ++i){

	}
}
void pru_updateTime(){
	uint32_t time = pru_clock();
	pru_mem_write(TIME_REGISTER, time);
}

/***********************************************************************\
 * pru user interaction
\***********************************************************************/

void pru_handleHostRequest(uint32_t* status){
	uint32_t data_holder = pru_mem_read(TYPE_REGISTER);

	//getting the type of task to execute
	uint8_t t_type = T_TAG(*data_holder);
	uint8_t e_type = T_TAK(*data_holder);

	if(t_type == TYPE_SYS){//pru general tasks
		switch(e_type){
			case TYPE_INIT:
				*status = PRU_INITIALIZED;
				break;
			case TYPE_SHUT:
				*status = PRU_INITIALIZED;
				break;
		}
	}else if(t_type == TYPE_IO){//io related tasks
		data_holder = pru_mem_read(HANDLE_REGISTER);

		uint8_t handle = H_BAS(data_holder);
		uint8_t h_type = H_TYP(data_holder);

		switch(e_type){
			case TYPE_INIT:
				data_holder = pru_initializePort(handle, h_type);
				pru_mem_write(DATA_REGISTER, data_holder);
				break;
			case TYPE_SHUT:
				pru_freePort(handle, h_type);
				break;
			case TYPE_SETTING_SET:
				data_holder = pru_mem_read(DATA_REGISTER);
				pru_settingsPort(handle, h_type, TYPE_SETTING_SET, data_holder);
				break;
			case TYPE_SETTING_GET:
				data_holder = pru_mem_read(DATA_REGISTER);
				pru_settingsPort(handle, h_type, TYPE_SETTING_GET, data_holder);
				pru_mem_write(DATA_REGISTER, data_holder);
				break;
		}
	}

	pru_mem_clear(TYPE_REGISTER);
	pru_mem_clear(HANDLE_REGISTER);
	pru_mem_clear(DATA_REGISTER);
}



