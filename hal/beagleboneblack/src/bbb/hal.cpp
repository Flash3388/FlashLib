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
#include "iolib/BBBiolib.h"
#include "iolib/BBBiolib_ADCTSC.h"
#include "iolib/BBBiolib_PWMSS.h"
#include "handles.h"

namespace flashlib{

namespace hal{

std::unordered_map<hal_handle_t, dio_pulse_t*> pulse_map;
std::unordered_map<hal_handle_t, dio_port_t*> dio_map;

pwm_port_t pwm_map[BBB_PWMSS_MODULE_COUNT];
adc_port_t adc_map[BBB_ADC_CHANNEL_COUNT];

typedef struct thread_data{
	bool run = true;
} thread_data_t;

pthread_t io_thread;
thread_data_t io_thread_data;

pthread_mutex_t pulse_map_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t thread_param_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t io_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t adc_sampling_mutex = PTHREAD_MUTEX_INITIALIZER;

bool init = false;

/***********************************************************************\
 * INTERNAL METHODS
\***********************************************************************/

void limitPWMDuty(float* duty){
	if(*duty > 1.0f)
		*duty = 1.0f;
	else if(*duty < 0.0f)
		*duty = 0.0f;
}

void* io_thread_function(void* param){

	thread_data_t data;


	int adc_idx, adc_smpl_idx;
	uint32_t adc_smpl_val;

	auto adc_start = std::chrono::high_resolution_clock::now();
	long long adc_ms_passed;

	long long pulse_us_passed;
	auto pulse_start = std::chrono::high_resolution_clock::now();

	while(data.run){

		//run pulses
		pthread_mutex_lock(&pulse_map_mutex);
		auto pulse_elapsed = std::chrono::high_resolution_clock::now() - pulse_start;
		pulse_us_passed = std::chrono::duration_cast<std::chrono::microseconds>(pulse_elapsed).count();
		for(auto it = pulse_map.begin(); it != pulse_map.end(); ){
			dio_pulse_t* pulse = it->second;
			pulse->remaining_time -= pulse_us_passed;
			if(pulse->remaining_time <= 0){
				pthread_mutex_lock(&io_mutex);
				pin_low(pulse->dio_handle->header, pulse->dio_handle->pin);
				pthread_mutex_unlock(&io_mutex);
				it = pulse_map.erase(it);
			}else
				++it;
		}
		pulse_start = std::chrono::high_resolution_clock::now();
		pthread_mutex_unlock(&pulse_map_mutex);

		//sample adc channels
		auto adc_elapsed = std::chrono::high_resolution_clock::now() - adc_start;
		adc_ms_passed = std::chrono::duration_cast<std::chrono::milliseconds>(adc_elapsed).count();
		if(adc_ms_passed >= HAL_AIN_SMAPLING_RATE){
			pthread_mutex_lock(&adc_sampling_mutex);
			for(adc_idx = 0; adc_idx < BBB_ADC_CHANNEL_COUNT; ++adc_idx){
				adc_port_t adc = adc_map[adc_idx];
				if(adc.enabled){
					BBBIO_ADCTSC_work(HAL_AIN_SAMPLING_SIZE);
					adc_smpl_val = 0;
					for(adc_smpl_idx = 0; adc_smpl_idx < HAL_AIN_SAMPLING_SIZE; ++adc_smpl_idx){
						adc_smpl_val += adc.sample_buffer[adc_smpl_idx];
					}
					adc_smpl_val /= HAL_AIN_SAMPLING_SIZE;
				}
			}
			pthread_mutex_unlock(&adc_sampling_mutex);
			adc_start = std::chrono::high_resolution_clock::now();
		}

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

int BBB_initialize(int mode){
	if(init){
		//TODO: ALREADY INITIALIZED
		return -1;
	}

	int status = iolib_init();
	if(status){
		//TODO: ERROR
		return -1;
	}

	status = pthread_create(&io_thread, NULL, &io_thread_function, NULL);
	if(status){
		//TODO: ERROR
		return -1;
	}
	return 0;
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

	pthread_mutex_destroy(&pulse_map_mutex);
	pthread_mutex_destroy(&io_mutex);
	pthread_mutex_destroy(&adc_sampling_mutex);
	pthread_mutex_destroy(&thread_param_mutex);

	dio_map.clear();
	pulse_map.clear();

	iolib_free();
}

/***********************************************************************\
 * DIO
\***********************************************************************/

hal_handle_t BBB_initializeDIOPort(uint8_t port, uint8_t dir){
	pthread_mutex_lock(&io_mutex);
	hal_handle_t handle = (hal_handle_t)port;
	if(dio_map.count(handle)){
		dio_port_t* dio = dio_map[handle];
		if(dir != dio->dir)
			handle = HAL_INVALID_HANDLE;
	}else{
		//TODO: CREATE DIO HANDLE AND ADD TO MAP. ALSO CHECK IF PORT MATCHES
		dio_port_t dio;
		dio.header = BBB_GPIO_PORT_TO_HEADER(port);
		dio.pin = BBB_GPIO_PORT_TO_PIN(port);
		dio.dir = dir;

		if(iolib_setdir(dio.header, dio.pin, dir) == 0)
			dio_map.insert({handle, &dio});
		else
			handle = HAL_INVALID_HANDLE;
	}
	pthread_mutex_unlock(&io_mutex);

	return handle;
}
void BBB_freeDIOPort(hal_handle_t portHandle){
	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		dio_port_t* dio = dio_map[portHandle];
		pin_low(dio->header, dio->pin);
		//TODO: REMOVE DIO FROM MAP AND DESTROY OBJECT...
		dio_map.erase(portHandle);
	}
	pthread_mutex_unlock(&io_mutex);
}

void BBB_setDIO(hal_handle_t portHandle, uint8_t high){
	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		dio_port_t* dio = dio_map[portHandle];
		if(dio->dir == BBB_DIR_OUTPUT){
			dio->val = high;
			if(high == BBB_GPIO_HIGH){
				pin_high(dio->header, dio->pin);
				dio->val = BBB_GPIO_HIGH;
			}else if(high == BBB_GPIO_LOW){
				pin_low(dio->header, dio->pin);
				dio->val = BBB_GPIO_LOW;
			}
		}
	}
	pthread_mutex_unlock(&io_mutex);
}
void BBB_pulseDIO(hal_handle_t portHandle, uint32_t length){
	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		//TODO: use mutex here because of thread
		pthread_mutex_lock(&pulse_map_mutex);

		if(pulse_map.count(portHandle)){
			dio_pulse_t* pulse = pulse_map[portHandle];
			pulse->remaining_time += length;
		}else{
			dio_port_t* dio = dio_map[portHandle];
			if(dio->dir == BBB_DIR_OUTPUT){
				dio->val = BBB_GPIO_HIGH;
				pin_high(dio->header, dio->pin);

				dio_pulse_t pulse;
				pulse.dio_handle = dio;
				pulse.remaining_time = length;
				pulse_map.insert({portHandle, &pulse});
			}
		}

		pthread_mutex_unlock(&pulse_map_mutex);
	}
	pthread_mutex_unlock(&io_mutex);
}

uint8_t BBB_getDIO(hal_handle_t portHandle){
	pthread_mutex_lock(&io_mutex);
	uint8_t val = 0;
	if(dio_map.count(portHandle)){
		dio_port_t* dio = dio_map[portHandle];
		if(dio->dir == BBB_DIR_OUTPUT){
			val = dio->val;
		}else{
			val = is_high(dio->header, dio->pin);
		}
	}
	pthread_mutex_unlock(&io_mutex);
	return val;
}

/***********************************************************************\
 * ANALOG
\***********************************************************************/

hal_handle_t BBB_initializeAnalogInput(uint8_t port){
	hal_handle_t portHandle = (hal_handle_t)port;
	if(port >= BBB_ADC_CHANNEL_COUNT){
		portHandle = HAL_INVALID_HANDLE;
	}
	else{
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t adc = adc_map[port];
		if(adc.enabled == 0){
			//TODO: INITIALIZE ADC PORT
			BBBIO_ADCTSC_channel_ctrl(port, BBBIO_ADC_STEP_MODE_SW_CONTINUOUS, HAL_AIN_OPEN_DELAY,
					HAL_AIN_SMAPLING_RATE, BBBIO_ADC_STEP_AVG_1, adc.sample_buffer, HAL_AIN_SAMPLING_SIZE);
			BBBIO_ADCTSC_channel_enable(port);

			adc.enabled = 1;
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
	}
	return portHandle;
}
void BBB_freeAnalogInput(hal_handle_t portHandle){
	if(portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t adc = adc_map[portHandle];
		if(adc.enabled){
			adc.enabled = 0;
			//TODO: STOP ADC CHANNEL
			BBBIO_ADCTSC_channel_disable(portHandle);
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
	}
}

uint32_t BBB_getAnalogValue(hal_handle_t portHandle){
	int32_t val = 0;
	if(portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t adc = adc_map[portHandle];
		if(adc.enabled){
			val = adc.value;
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
	}
	return val;
}
float BBB_getAnalogVoltage(hal_handle_t portHandle){
	uint32_t value = BBB_getAnalogValue(portHandle);
	return HAL_AIN_VALUE_TO_VOLTAGE(value);
}

/***********************************************************************\
 * PWM
\***********************************************************************/

hal_handle_t BBB_initializePWMPort(uint8_t port){
	pthread_mutex_lock(&io_mutex);
	hal_handle_t portHandle = (hal_handle_t)port;
	if(port >= HAL_PWMSS_PORTS_COUNT){
		portHandle = HAL_INVALID_HANDLE;
	}
	else{
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(port);
		uint8_t pin = BBB_PWMSS_PORT_TO_PIN(port);
		pwm_port_t pwm = pwm_map[module];

		if(pwm.enabledA == 0 && pwm.enabledB == 0)
			BBBIO_ehrPWM_Enable(module);

		if(pin == BBB_PWMSSA)
			pwm.enabledA = 1;
		else if(pin == BBB_PWMSSB)
			pwm.enabledB = 1;
	}
	pthread_mutex_unlock(&io_mutex);
	return portHandle;
}
void BBB_freePWMPort(hal_handle_t portHandle){
	pthread_mutex_lock(&io_mutex);
	if(portHandle < HAL_PWMSS_PORTS_COUNT){
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		uint8_t pin = BBB_PWMSS_PORT_TO_PIN(portHandle);
		pwm_port_t pwm = pwm_map[module];

		if(pin == BBB_PWMSSA){
			pwm.enabledA = 0;
			pwm.dutyA = 0.0f;
		}
		else if(pin == BBB_PWMSSB){
			pwm.enabledB = 0;
			pwm.dutyB = 0.0f;
		}

		BBBIO_PWMSS_Setting(module, pwm.frequency, pwm.dutyA, pwm.dutyB);
		if(pwm.enabledA == 0 && pwm.enabledB == 0)
			BBBIO_ehrPWM_Disable(module);
	}
	pthread_mutex_unlock(&io_mutex);
}
float BBB_getPWMDuty(hal_handle_t portHandle){
	pthread_mutex_lock(&io_mutex);
	float val = 0.0f;
	if(portHandle < HAL_PWMSS_PORTS_COUNT){
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		uint8_t port = BBB_PWMSS_PORT_TO_PIN(portHandle);
		pwm_port_t pwm = pwm_map[module];

		if(port == BBB_PWMSSA && pwm.enabledA)
			val = pwm.dutyA;
		else if(port == BBB_PWMSSB && pwm.enabledB)
			val = pwm.dutyB;
	}
	pthread_mutex_unlock(&io_mutex);
	return val;
}
uint8_t BBB_getPWMValue(hal_handle_t portHandle){
	float duty = BBB_getPWMDuty(portHandle);
	return (uint8_t)HAL_PWMSS_DUTY_TO_VALUE(duty);
}

void BBB_setPWMValue(hal_handle_t portHandle, uint8_t value){
	float duty = HAL_PWMSS_VALUE_TO_DUTY(value);
	limitPWMDuty(&duty);
	BBB_setPWMDuty(portHandle, duty);
}
void BBB_setPWMDuty(hal_handle_t portHandle, float duty){
	pthread_mutex_lock(&io_mutex);
	if(portHandle < HAL_PWMSS_PORTS_COUNT){
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		pwm_port_t pwm = pwm_map[module];
		if(pwm.enabledA || pwm.enabledB){
			limitPWMDuty(&duty);

			uint8_t port = BBB_PWMSS_PORT_TO_PIN(portHandle);
			if(port == BBB_PWMSSA && pwm.enabledA)
				pwm.dutyA = duty;
			else if(port == BBB_PWMSSB && pwm.enabledB)
				pwm.dutyB = duty;

			BBBIO_PWMSS_Setting(module, pwm.frequency, pwm.dutyA, pwm.dutyB);
		}
	}
	pthread_mutex_unlock(&io_mutex);
}

} /* namespace hal */

} /* namespace flashlib */
