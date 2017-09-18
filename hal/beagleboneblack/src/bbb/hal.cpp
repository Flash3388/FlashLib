/*
 * hal.cpp
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#include <unordered_map>
#include <pthread.h>
#include <chrono>
#include <memory>
#include <unistd.h>

#include <bbb_defines.h>
#include <hal.h>

#include "iolib/BBBiolib.h"
#include "iolib/BBBiolib_ADCTSC.h"
#include "iolib/BBBiolib_PWMSS.h"
#include "handles.h"
#include "hal_defines.h"

#ifdef HAL_BBB_DEBUG
#include <iostream>
#endif

namespace flashlib{

namespace hal{

std::unordered_map<hal_handle_t, std::shared_ptr<dio_pulse_t>> pulse_map;
std::unordered_map<hal_handle_t, std::shared_ptr<dio_port_t>> dio_map;

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
			dio_pulse_t* pulse = it->second.get();
			pulse->remaining_time -= pulse_us_passed;
			if(pulse->remaining_time <= 0){
#ifdef HAL_USE_IO
				pthread_mutex_lock(&io_mutex);
				if(dio_map.count(it->first)){
					dio_port_t* dio = dio_map[it->first].get();
					if(dio->dir == BBB_DIR_OUTPUT)
						pin_low(dio->header + 8, dio->pin);
				}
				pthread_mutex_unlock(&io_mutex);
#endif
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
#ifdef HAL_USE_IO
			BBBIO_ADCTSC_work(HAL_AIN_SAMPLING_SIZE);
#endif
			for(adc_idx = 0; adc_idx < BBB_ADC_CHANNEL_COUNT; ++adc_idx){
				adc_port_t adc = adc_map[adc_idx];
				if(adc.enabled){
#ifdef HAL_USE_IO
					adc_smpl_val = 0;
					for(adc_smpl_idx = 0; adc_smpl_idx < HAL_AIN_SAMPLING_SIZE; ++adc_smpl_idx){
						adc_smpl_val += adc.sample_buffer[adc_smpl_idx];
					}
					adc_smpl_val /= HAL_AIN_SAMPLING_SIZE;
#endif
				}
			}
			pthread_mutex_unlock(&adc_sampling_mutex);
			adc_start = std::chrono::high_resolution_clock::now();
		}

		usleep(HAL_IO_THREAD_DELAY);

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

#ifdef HAL_BBB_DEBUG
	printf("Initializing HAL \n");
#endif

	int status = 0;

#ifdef HAL_USE_IO
	status = iolib_init();
	if(status){
		//TODO: ERROR
#ifdef HAL_BBB_DEBUG
		printf("Failed to initializing BBBIOLib \n");
#endif
		return -1;
	}
	BBBIO_ADCTSC_module_ctrl(BBBIO_ADC_WORK_MODE_BUSY_POLLING, HAL_AIN_CLK_DIVISOR);

#ifdef HAL_BBB_DEBUG
	printf("Set ADC module to mode %d, clkdiv: %d \n", BBBIO_ADC_WORK_MODE_BUSY_POLLING, HAL_AIN_CLK_DIVISOR);
#endif

#endif

#ifdef HAL_USE_THREAD
	status = pthread_create(&io_thread, NULL, &io_thread_function, NULL);
	if(status){
		//TODO: ERROR
#ifdef HAL_BBB_DEBUG
		printf("Failed to initializing IO thread \n");
#endif
		return -1;
	}
#endif

	init = true;
	return 0;
}
void BBB_shutdown(){
	if(!init){
		//TODO: HAL NOT INITIALIZED
#ifdef HAL_BBB_DEBUG
		printf("Shutdown failed, HAL was not initialized \n");
#endif
		return;
	}

#ifdef HAL_BBB_DEBUG
	printf("Shutting down IO thread \n");
#endif

	//TODO: KILL THREAD IF IO INIT WAS SUCCESSFUL
	pthread_mutex_lock(&thread_param_mutex);
	io_thread_data.run = false;
	pthread_mutex_unlock(&thread_param_mutex);

#ifdef HAL_USE_THREAD
	pthread_mutex_lock(&io_mutex);
	pthread_join(io_thread, NULL);
	pthread_mutex_unlock(&io_mutex);
#endif

	pulse_map.clear();

#ifdef HAL_BBB_DEBUG
	printf("Clearing PWM handles \n");
#endif

	//clear pwm handles
	for(int i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port_t* pwm = &pwm_map[i];

		BBB_freePWMPort(BBB_PWMSS_PORT(i, 0));
		BBB_freePWMPort(BBB_PWMSS_PORT(i, 1));

		pwm->frequency = 0.0f;
	}

#ifdef HAL_BBB_DEBUG
	printf("Clearing ADC handles \n");
#endif

	//clear adc handles
	for(int i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		adc_port_t* adc = &adc_map[i];

		BBB_freeAnalogInput(i);
	}

#ifdef HAL_BBB_DEBUG
	printf("Clearing DIO handles \n");
#endif

	//clear dio handles
	for(auto it = dio_map.begin(); it != dio_map.end();){
		dio_port_t* dio = it->second.get();

#ifdef HAL_USE_IO
		pin_low(dio->header + 8, dio->pin);
#endif

		it = dio_map.erase(it);
	}
	dio_map.clear();

	pthread_mutex_destroy(&pulse_map_mutex);
	pthread_mutex_destroy(&adc_sampling_mutex);
	pthread_mutex_destroy(&thread_param_mutex);

#ifdef HAL_USE_IO

#ifdef HAL_BBB_DEBUG
	printf("Shutting down BBBIOLib \n");
#endif

	pthread_mutex_lock(&io_mutex);
	iolib_free();
	pthread_mutex_unlock(&io_mutex);
#endif


	pthread_mutex_destroy(&io_mutex);

	init = false;
}

/***********************************************************************\
 * DIO
\***********************************************************************/

hal_handle_t BBB_initializeDIOPort(int16_t port, uint8_t dir){
	if(!init){
		return HAL_INVALID_HANDLE;
	}

#ifdef HAL_BBB_DEBUG
	printf("Initializing DIO port \n");
#endif

	pthread_mutex_lock(&io_mutex);
	hal_handle_t handle = (hal_handle_t)port;
	if(dio_map.count(handle)){
		dio_port_t* dio = dio_map[handle].get();

#ifdef HAL_BBB_DEBUG
		printf("DIO handle already exists: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif

		if(dir != dio->dir){
			handle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
			printf("Wanted direction doesn't match already used direction: %d != %d \n", dir, dio->dir);
#endif
		}
	}else if(port > 0){
		//TODO: CREATE DIO HANDLE AND ADD TO MAP. ALSO CHECK IF PORT MATCHES
		dio_port_t dio;
		dio.header = BBB_GPIO_PORT_TO_HEADER(port);
		dio.pin = BBB_GPIO_PORT_TO_PIN(port);
		dio.dir = dir;

		int result;

#ifdef HAL_USE_IO
		result = iolib_setdir(dio.header + 8, dio.pin, dir);
#else
		result = 0;
#endif

		if(result == 0){
#ifdef HAL_USE_IO
			pin_low(dio.header + 8, dio.pin);
#endif
			dio_map.emplace(handle, std::make_shared<dio_port_t>(dio));

#ifdef HAL_BBB_DEBUG
			printf("DIO handle created: DIR= %d HEADER= %d, PIN= %d \n", dir, dio.header, dio.pin);
#endif
		}
		else{
			handle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
			printf("Unable to set direction for DIO: %d \n", dir);
#endif
		}
	}else{

#ifdef HAL_BBB_DEBUG
			printf("DIO port invalid, out of range: %d \n", port);
#endif
	}
	pthread_mutex_unlock(&io_mutex);

	return handle;
}
void BBB_freeDIOPort(hal_handle_t portHandle){
	if(!init){
		return;
	}

	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		pthread_mutex_lock(&pulse_map_mutex);
		if(pulse_map.count(portHandle)){
			dio_pulse_t* pulse = pulse_map[portHandle].get();
			pulse->remaining_time = 0;
		}
		pthread_mutex_unlock(&pulse_map_mutex);

		dio_port_t* dio = dio_map[portHandle].get();
#ifdef HAL_USE_IO
		pin_low(dio->header + 8, dio->pin);
#endif
		//TODO: REMOVE DIO FROM MAP AND DESTROY OBJECT...
		dio_map.erase(portHandle);

#ifdef HAL_BBB_DEBUG
		printf("DIO handle freed: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("DIO handle not initialized: %d \n", portHandle);
#endif
	}
	pthread_mutex_unlock(&io_mutex);
}

void BBB_setDIO(hal_handle_t portHandle, uint8_t high){
	if(!init){
		return;
	}

	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		dio_port_t* dio = dio_map[portHandle].get();
		if(dio->dir == BBB_DIR_OUTPUT){
			dio->val = high;
			if(high == BBB_GPIO_HIGH){
#ifdef HAL_USE_IO
				pin_high(dio->header + 8, dio->pin);
#endif
				dio->val = BBB_GPIO_HIGH;

#ifdef HAL_BBB_DEBUG
				printf("Set DIO to high: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
			}else if(high == BBB_GPIO_LOW){
#ifdef HAL_USE_IO
				pin_low(dio->header + 8, dio->pin);
#endif
				dio->val = BBB_GPIO_LOW;

#ifdef HAL_BBB_DEBUG
				printf("Set DIO to low: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
			}
		}else{
#ifdef HAL_BBB_DEBUG
			printf("Cannot set DIO, it is initialized as input: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
		}
	}
	pthread_mutex_unlock(&io_mutex);
}
void BBB_pulseDIO(hal_handle_t portHandle, float length){
	if(!init){
		return;
	}

	//TODO: CONVERT PULSE FROM SECONDS TO MICROSECONDS
	uint32_t pulseus = (uint32_t)(length * 1000000);

	pthread_mutex_lock(&io_mutex);
	if(dio_map.count(portHandle)){
		//TODO: use mutex here because of thread
		pthread_mutex_lock(&pulse_map_mutex);

		if(pulse_map.count(portHandle)){
			dio_pulse_t* pulse = pulse_map[portHandle].get();
			pulse->remaining_time += pulseus;
#ifdef HAL_BBB_DEBUG
			dio_port_t* dio = dio_map[portHandle].get();
			printf("Pulse handle already in use, adding time: LEN= %d, HEADER= %d, PIN= %d \n",
					pulse->remaining_time,dio->header, dio->pin);
#endif
		}else{
			dio_port_t* dio = dio_map[portHandle].get();
			if(dio->dir == BBB_DIR_OUTPUT){
				dio->val = BBB_GPIO_HIGH;

#ifdef HAL_USE_IO
				pin_high(dio->header + 8, dio->pin);
#endif

				dio_pulse_t pulse;
				pulse.remaining_time = pulseus;
				pulse_map.emplace(portHandle, std::make_shared<dio_pulse_t>(pulse));

#ifdef HAL_BBB_DEBUG
				printf("Created pulse handle: LEN= %d, HEADER= %d, PIN= %d \n", length, dio->header, dio->pin);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Cannot pulse out, direction is input: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
			}
		}

		pthread_mutex_unlock(&pulse_map_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Cannot pulse out, DIO not initialized \n");
#endif
	}
	pthread_mutex_unlock(&io_mutex);
}

uint8_t BBB_getDIO(hal_handle_t portHandle){
	if(!init){
		return 0;
	}

	pthread_mutex_lock(&io_mutex);
	uint8_t val = 0;
	if(dio_map.count(portHandle)){
		dio_port_t* dio = dio_map[portHandle].get();
		if(dio->dir == BBB_DIR_OUTPUT){
			val = dio->val;
#ifdef HAL_BBB_DEBUG
			printf("DIO is output, using last value: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
		}else{

#ifdef HAL_USE_IO
			val = is_high(dio->header + 8, dio->pin);
#endif

#ifdef HAL_BBB_DEBUG
			printf("DIO is input, reading: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
		}
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Cannot get DIO, not initialized\n");
#endif
	}
	pthread_mutex_unlock(&io_mutex);
	return val;
}

/***********************************************************************\
 * ANALOG
\***********************************************************************/

hal_handle_t BBB_initializeAnalogInput(int16_t port){
	if(!init){
		return HAL_INVALID_HANDLE;
	}

	hal_handle_t portHandle = (hal_handle_t)port;
	if(port < 0 || port >= BBB_ADC_CHANNEL_COUNT){
		portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", port);
#endif
	}
	else{
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_map[port];
		if(adc->enabled == 0){
			//TODO: INITIALIZE ADC PORT

#ifdef HAL_USE_IO
			BBBIO_ADCTSC_channel_ctrl(port, BBBIO_ADC_STEP_MODE_SW_CONTINUOUS, HAL_AIN_OPEN_DELAY,
					HAL_AIN_SMAPLING_RATE, BBBIO_ADC_STEP_AVG_1, adc->sample_buffer, HAL_AIN_SAMPLING_SIZE);
			BBBIO_ADCTSC_channel_enable(port);
#endif

			adc->enabled = 1;

#ifdef HAL_BBB_DEBUG
			printf("Initialized ADC channel: %d \n", port);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel is already initialized for use: %d \n", port);
#endif
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}

	return portHandle;
}
void BBB_freeAnalogInput(hal_handle_t portHandle){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_map[portHandle];
		if(adc->enabled){
			adc->enabled = 0;
			//TODO: STOP ADC CHANNEL
			adc->value = 0;

#ifdef HAL_USE_IO
			BBBIO_ADCTSC_channel_disable(portHandle);
#endif

#ifdef HAL_BBB_DEBUG
			printf("ADC channel freed: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel is already not enabled: %d \n", portHandle);
#endif
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);

	}else{
#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", portHandle);
#endif
	}
}

uint32_t BBB_getAnalogValue(hal_handle_t portHandle){
	if(!init){
		return 0;
	}

	int32_t val = 0;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_map[portHandle];
		if(adc->enabled){
			val = adc->value;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel value read: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel was not enabled: %d \n", portHandle);
#endif
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", portHandle);
#endif
	}
	return val;
}
float BBB_getAnalogVoltage(hal_handle_t portHandle){
	if(!init){
		return 0.0f;
	}

	uint32_t value = BBB_getAnalogValue(portHandle);
	return HAL_AIN_VALUE_TO_VOLTAGE(value);
}

/***********************************************************************\
 * PWM
\***********************************************************************/

hal_handle_t BBB_initializePWMPort(int16_t port){
	if(!init){
		return HAL_INVALID_HANDLE;
	}

	hal_handle_t portHandle = (hal_handle_t)port;
	if(port < 0 || port >= HAL_PWMSS_PORTS_COUNT){
		portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", port);
#endif
	}
	else{
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(port);
		uint8_t pin = BBB_PWMSS_PORT_TO_PIN(port);
		pwm_port_t* pwm = &pwm_map[module];

#ifdef HAL_USE_IO
		if(pwm->enabledA == 0 && pwm->enabledB == 0){
			BBBIO_ehrPWM_Enable(module);


#ifdef HAL_BBB_DEBUG
			printf("PWM module enabled: %d \n", module);
#endif
		}
#endif

		bool initmodule = false;

		if(pin == BBB_PWMSSA && pwm->enabledA == 0){
			pwm->enabledA = 1;
			pwm->dutyA = 0.0f;
			initmodule = true;
		}
		else if(pin == BBB_PWMSSB && pwm->enabledB == 0){
			pwm->enabledB = 1;
			pwm->dutyB = 0.0f;
			initmodule = true;
		}

		if(initmodule){
#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port enabled: MODULE= %d, PIN= %d \n", module, pin);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port was already: MODULE= %d, PIN= %d \n", module, pin);
#endif
		}
		pthread_mutex_unlock(&io_mutex);
	}
	return portHandle;
}
void BBB_freePWMPort(hal_handle_t portHandle){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		uint8_t pin = BBB_PWMSS_PORT_TO_PIN(portHandle);
		pwm_port_t* pwm = &pwm_map[module];

		bool freed = false;

		if(pin == BBB_PWMSSA && pwm->enabledA){
			pwm->enabledA = 0;
			pwm->dutyA = 0.0f;
			freed = true;
		}
		else if(pin == BBB_PWMSSB && pwm->enabledB){
			pwm->enabledB = 0;
			pwm->dutyB = 0.0f;
			freed = true;
		}

		if(freed){
#ifdef HAL_BBB_DEBUG
			printf("PWM port disabled: MODULE= %d, PIN= %d \n", module, pin);
#endif

#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
			if(pwm->enabledA == 0 && pwm->enabledB == 0){
				BBBIO_ehrPWM_Disable(module);


#ifdef HAL_BBB_DEBUG
				printf("PWM module disabled: %d \n", module);
#endif
			}
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port was already disabled: MODULE= %d, PIN= %d \n", module, pin);
#endif
		}
		pthread_mutex_unlock(&io_mutex);

	}else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", portHandle);
#endif
	}
}
float BBB_getPWMDuty(hal_handle_t portHandle){
	if(!init){
		return 0.0f;
	}

	float val = 0.0f;
	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		uint8_t port = BBB_PWMSS_PORT_TO_PIN(portHandle);
		pwm_port_t* pwm = &pwm_map[module];

		if(port == BBB_PWMSSA && pwm->enabledA)
			val = pwm->dutyA;
		else if(port == BBB_PWMSSB && pwm->enabledB)
			val = pwm->dutyB;

#ifdef HAL_BBB_DEBUG
		printf("PWM value get: MODULE= %d, PIN= %d \n", module, port);
#endif

		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", portHandle);
#endif
	}
	return val;
}
uint8_t BBB_getPWMValue(hal_handle_t portHandle){
	if(!init){
		return 0;
	}

	float duty = BBB_getPWMDuty(portHandle);
	return (uint8_t)HAL_PWMSS_DUTY_TO_VALUE(duty);
}

void BBB_setPWMValue(hal_handle_t portHandle, uint8_t value){
	if(!init){
		return;
	}

	float duty = HAL_PWMSS_VALUE_TO_DUTY(value);
	limitPWMDuty(&duty);
	BBB_setPWMDuty(portHandle, duty);
}
void BBB_setPWMDuty(hal_handle_t portHandle, float duty){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		pwm_port_t* pwm = &pwm_map[module];
		if(pwm->enabledA || pwm->enabledB){
			limitPWMDuty(&duty);

			uint8_t port = BBB_PWMSS_PORT_TO_PIN(portHandle);
			if(port == BBB_PWMSSA && pwm->enabledA)
				pwm->dutyA = duty;
			else if(port == BBB_PWMSSB && pwm->enabledB)
				pwm->dutyB = duty;

#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port set: %f - MODULE= %d, PIN= %d \n", duty, module, port);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM ports are not enabled:  MODULE= %d \n", module);
#endif
		}
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", portHandle);
#endif
	}
}

void BBB_setPWMFrequency(hal_handle_t portHandle, float frequency){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		pwm_port_t* pwm = &pwm_map[module];
		if(pwm->enabledA || pwm->enabledB){
			//TODO: LIMIT FREQUENCY
			pwm->frequency = frequency;

#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port set frequency: %f - MODULE= %d, PIN= %d \n", frequency, module, port);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM ports are not enabled:  MODULE= %d \n", module);
#endif
		}
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", portHandle);
#endif
	}
}
float BBB_getPWMFrequency(hal_handle_t portHandle){
	if(!init){
		return 0.0f;
	}

	float frequency = 0.0f;
	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);
		uint8_t module = BBB_PWMSS_PORT_TO_MODULE(portHandle);
		pwm_port_t* pwm = &pwm_map[module];
		if(pwm->enabledA || pwm->enabledB){

			frequency = pwm->frequency;

#ifdef HAL_BBB_DEBUG
			printf("PWM port get frequency: MODULE= %d, PIN= %d \n", frequency, module, port);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM ports are not enabled:  MODULE= %d \n", module);
#endif
		}
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port is invalid, out of range: %d \n", portHandle);
#endif
	}

	return frequency;
}

} /* namespace hal */

} /* namespace flashlib */
