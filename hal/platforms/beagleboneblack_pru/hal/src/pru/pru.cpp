/*
 * pru.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <unordered_map>
#include <pthread.h>

#include "pru.h"
#include "defines.h"
#include "pru_internal.h"
#include "handles.h"
#include "bbb.h"

#define PRUNUM             (0)
#define PRU_PROGRAM_FILE   ("pru.bin")

namespace flashlib{

namespace hal{

struct thread_data{
	bool stop = false;
};

std::unordered_map<uint8_t, DIO_handle> dio_map;
std::unordered_map<uint8_t, ADC_handle> adc_map;
std::unordered_map<uint8_t, PWM_handle> pwm_map;

pthread_t iothread;
struct thread_data iothread_data;

pru_data_t pru_data;
bool initialized = false;

/***********************************************************************\
 * IO Thread
\***********************************************************************/

void* iothread_function(void* args){
	/*uint8_t handle;

	DIO_handle dio_handle;
	std::unordered_map<uint8_t, DIO_handle>::iterator dio_it;
	ADC_handle adc_handle;
	std::unordered_map<uint8_t, ADC_handle>::iterator adc_it;
	PWM_handle pwm_handle;
	std::unordered_map<uint8_t, PWM_handle>::iterator pwm_it;

	while(!iothread_data.stop){

		dio_it = dio_map.begin();
		while(dio_it != dio_map.end()){
			handle = dio_it->first;
			dio_handle = dio_it->second;

			++dio_it;
		}
	}*/

	pthread_exit(NULL);
}

/***********************************************************************\
 * PRU initialization
\***********************************************************************/

bool PRU_initialize(uint32_t* status){
	if(initialized){
		*status = SUCCESS;
		return true;
	}

	pru_data.prunum = PRUNUM;
	pru_initialize(&pru_data, PRUNUM, status);
	if(*status){
		*status = PRU_FAIL;
		return false;
	}

	int result = pthread_create(&iothread, NULL, iothread_function, (void *)NULL);
	if(result){
		*status = THREAD_FAIL;
		return false;
	}

	initialized = true;
	*status = SUCCESS;
	return true;
}
bool PRU_shutdown(uint32_t* status){
	if(!initialized){
		*status = SUCCESS;
		return true;
	}

	int result = pthread_join(iothread, (void **)NULL);
	if(result){
		*status = THREAD_FAIL;
		return false;
	}

	pru_shutdown(&pru_data, status);
	if(*status){
		*status = PRU_FAIL;
		return false;
	}

	*status = SUCCESS;
	initialized = false;
	return true;
}

void PRU_disableIO(){

}
void PRU_enableIO(){

}

/***********************************************************************\
 * PRU direct port handling
\***********************************************************************/

uint8_t PRU_initializePort(uint8_t base, uint8_t port, uint8_t type){
	return 0;
}
void PRU_freePort(uint8_t handle, uint8_t type){

}

/***********************************************************************\
 * PRU digital
\***********************************************************************/

void PRU_digitalWrite(uint8_t handle, uint8_t val){

}
uint8_t PRU_digitalRead(uint8_t handle){
	return 0;
}

void PRU_pulseOut(uint8_t handle, uint32_t length){

}
uint32_t PRU_pulseIn(uint8_t handle){
	return 0;
}

/***********************************************************************\
 * PRU analog
\***********************************************************************/

uint16_t PRU_analogRead(uint8_t handle){
	return 0;
}

/***********************************************************************\
 * PRU pwm
\***********************************************************************/

void PRU_pwmWrite(uint8_t handle, uint16_t duty){

}
uint16_t PRU_pwmRead(uint8_t handle){
	return 0;
}

void PRU_pwmSetFrquency(uint8_t handle, uint32_t frequency){

}
uint32_t PRU_pwmGetFrequency(uint8_t handle){
	return 0;
}

} /* namespace hal */

} /* namespace flashlib */
