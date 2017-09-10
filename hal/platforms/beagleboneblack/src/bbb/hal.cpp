/*
 * hal.cpp
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#include <unordered_map>
#include <pthread.h>
#include <chrono>

#include <bbb_defines.h>

#include "hal.h"
#include "bbb.h"
#include "handles.h"

#define PWMSS_PORTS_COUNT       (BBB_PWMSS_MODULE_COUNT * BBB_PWMSS_PORT_COUNT)
#define PWMSS_MAX_VALUE         (255)
#define PWMSS_VALUE_TO_DUTY(d)  (d / PWMSS_MAX_VALUE)
#define PWMSS_DUTY_TO_VALUE(d)  (d * PWMSS_MAX_VALUE)

std::unordered_map<hal_handle_t, dio_pulse_t&> pulse_map;
std::unordered_map<hal_handle_t, dio_port_t&> dio_map;

pwm_port_t pwm_map[PWMSS_PORTS_COUNT];

std::unordered_map<hal_handle_t, adc_port_t&> adc_map;

typedef struct thread_data{
	bool run = true;
} thread_data_t;

pthread_t io_thread;
thread_data_t io_thread_data;
pthread_mutex_t pulse_map_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t thread_param_mutex = PTHREAD_MUTEX_INITIALIZER;

bool init = false;

/***********************************************************************\
 * INTERNAL METHODS
\***********************************************************************/

void* io_thread_function(void* param){

	thread_data_t data;

	long long us_passed;
	auto start = std::chrono::high_resolution_clock::now();

	while(data.run){

		//run pulses
		pthread_mutex_lock(&pulse_map_mutex);

		auto elapsed = std::chrono::high_resolution_clock::now() - start;
		us_passed = std::chrono::duration_cast<std::chrono::microseconds>(elapsed).count();
		for(auto it = pulse_map.begin(); it != pulse_map.end(); ){
			dio_pulse_t pulse = it->second;
			pulse.remaining_time -= us_passed;
			if(pulse.remaining_time <= 0){
				bbb_setlow(pulse.dio_handle.header, pulse.dio_handle.pin);
				it = pulse_map.erase(it);
			}else
				++it;
		}
		start = std::chrono::high_resolution_clock::now();

		pthread_mutex_unlock(&pulse_map_mutex);

		//get thread data
		pthread_mutex_lock(&thread_param_mutex);

		data.run = io_thread_data.run;

		pthread_mutex_unlock(&thread_param_mutex);
	}

	return NULL;
}

/***********************************************************************\
 * EXTERNAL METHODS
\***********************************************************************/

void BBB_initialize(int* status){
	if(init){
		*status = -1;
		//TODO: ALREADY INITIALIZED
		return;
	}

	*status = bbb_init();
	if(*status == 0){
		*status = pthread_create(&io_thread, NULL, &io_thread_function, NULL);
	}

	if(*status != 0){
		//TODO: HANDLE INIT ERROR
	}
}
void BBB_shutdown(){
	if(!init){
		//TODO: HAL NOT INITIALIZED
		return;
	}
	//TODO: KILL THREAD IF IO INIT WAS SUCCESSFUL
	pthread_mutex_lock(&thread_param_mutex);
	io_thread_data.run = false;
	pthread_mutex_unlock(&thread_param_mutex);
	pthread_join(io_thread, NULL);

	dio_map.clear();
	pulse_map.clear();

	bbb_free();
}

/***********************************************************************\
 * DIO
\***********************************************************************/

hal_handle_t BBB_initializeDIOPort(uint8_t port, uint8_t dir){
	hal_handle_t handle = (hal_handle_t)port;
	if(dio_map.count(handle)){
		dio_port_t dio = dio_map[handle];
		uint8_t cdir = bbb_getdir(dio.header, dio.pin);
		if(cdir != dir){
			handle = HAL_INVALID_HANDLE;
		}
	}else{
		//TODO: CREATE DIO HANDLE AND ADD TO MAP. ALSO CHECK IF PORT MATCHES
		dio_port_t dio;
		dio.header = BBB_GPIO_PORT_TO_HEADER(port);
		dio.pin = BBB_GPIO_PORT_TO_PIN(port);

		if(bbb_getportgpio(dio.header, dio.pin) < 0)
			handle = HAL_INVALID_HANDLE;
		else{
			bbb_setdir(dio.header, dio.pin, dir);
			dio_map.insert({handle, dio});
		}
	}
	return handle;
}
void BBB_freeDIOPort(hal_handle_t portHandle){
	if(dio_map.count(portHandle)){
		dio_port_t dio = dio_map[portHandle];
		if(bbb_ishigh(dio.header, dio.pin))
			bbb_setlow(dio.header, dio.pin);
		//TODO: REMOVE DIO FROM MAP AND DESTROY OBJECT...
		dio_map.erase(portHandle);
	}
}

void BBB_setDIO(hal_handle_t portHandle, uint8_t high){
	if(dio_map.count(portHandle)){
		dio_port_t dio = dio_map[portHandle];
		if(high)
			bbb_sethigh(dio.header, dio.pin);
		else
			bbb_setlow(dio.header, dio.pin);
	}
}
void BBB_pulseDIO(hal_handle_t portHandle, uint32_t length){
	if(dio_map.count(portHandle)){
		//TODO: use mutex here because of thread
		pthread_mutex_lock(&pulse_map_mutex);

		if(pulse_map.count(portHandle)){
			dio_pulse_t pulse = pulse_map[portHandle];
			pulse.remaining_time += length;
		}else{
			dio_pulse_t pulse;
			pulse.dio_handle = dio_map[portHandle];
			pulse.remaining_time = length;
			pulse_map[portHandle] = pulse;
		}

		pthread_mutex_unlock(&pulse_map_mutex);
	}
}

uint8_t BBB_getDIO(hal_handle_t portHandle){
	if(dio_map.count(portHandle)){
		dio_port_t dio = dio_map[portHandle];
		return bbb_ishigh(dio.header, dio.pin);
	}
	return 0;
}

/***********************************************************************\
 * ANALOG
\***********************************************************************/

hal_handle_t BBB_initializeAnalogInput(uint8_t port){
	return 0;
}
void BBB_freeAnalogInput(hal_handle_t portHandle){

}

uint8_t BBB_getAnalogValue(hal_handle_t portHandle){
	return 0;
}
float BBB_getAnalogVoltage(hal_handle_t portHandle){
	return 0;
}

/***********************************************************************\
 * PWM
\***********************************************************************/

hal_handle_t BBB_initializePWMPort(uint8_t port){
	hal_handle_t portHandle = (hal_handle_t)port;

	if(port >= PWMSS_PORTS_COUNT)
		portHandle = HAL_INVALID_HANDLE;
	else{
		pwm_port_t pwm = pwm_map[port];
		pwm.module = BBB_PWMSS_PORT_TO_MODULE(port);
		pwm.port = BBB_PWMSS_PORT_TO_PIN(port);

		bbb_pwm_enable(pwm.module);
	}

	return portHandle;
}
void BBB_freePWMPort(hal_handle_t portHandle){

}

uint8_t BBB_getPWMValue(hal_handle_t portHandle){
	float duty = BBB_getPWMDuty(portHandle);
	return (uint8_t)PWMSS_DUTY_TO_VALUE(duty);
}
float BBB_getPWMDuty(hal_handle_t portHandle){
	if(portHandle < PWMSS_PORTS_COUNT){
		pwm_port_t pwm = pwm_map[portHandle];
		if(pwm.module >= 0 || pwm.port >= 0){
			return bbb_pwm_getduty(pwm.module, pwm.port);
		}
	}
	return 0.0f;
}

void BBB_setPWMValue(hal_handle_t portHandle, uint8_t value){
	float duty = PWMSS_VALUE_TO_DUTY(value);
	BBB_limitPWMDuty(&duty);
	BBB_setPWMDuty(portHandle, duty);
}
void BBB_setPWMDuty(hal_handle_t portHandle, float duty){
	if(portHandle < PWMSS_PORTS_COUNT){
		pwm_port_t pwm = pwm_map[portHandle];
		if(pwm.module >= 0 || pwm.port >= 0){
			BBB_limitPWMDuty(&duty);
			bbb_pwm_setduty(pwm.module, pwm.port, duty);
		}
	}
}

void BBB_limitPWMDuty(float* duty){
	if(*duty > 1.0f)
		*duty = 1.0f;
	else if(*duty < 0.0f)
		*duty = 0.0f;
}
