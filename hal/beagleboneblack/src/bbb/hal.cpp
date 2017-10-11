/*
 * hal.cpp
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#include <unordered_map>
#include <vector>
#include <pthread.h>
#include <chrono>
#include <memory>
#include <unistd.h>
#include <sys/epoll.h>
#include <fcntl.h>
#include <string>

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

std::unordered_map<hal_handle_t, std::shared_ptr<dio_port_t>> dio_ports;
std::unordered_map<hal_handle_t, std::shared_ptr<pulse_counter_t>> dio_pulse_counters;
std::vector<std::shared_ptr<dio_pulse_t>> dio_pulses;

pwm_port_t pwm_ports[BBB_PWMSS_MODULE_COUNT];
adc_port_t adc_ports[BBB_ADC_CHANNEL_COUNT];

typedef struct thread_data{
	bool run = true;
} thread_data_t;

pthread_t pulse_thread;
pthread_t adc_thread;
thread_data_t thread_data;

pthread_mutex_t dio_pulsing_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t thread_param_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t io_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t adc_sampling_mutex = PTHREAD_MUTEX_INITIALIZER;

bool init = false;

/***********************************************************************\
 * INTERNAL METHODS
\***********************************************************************/

const char* resolveFilepath(dio_port_t* dio){
	char bank = pin_bank(dio->header, dio->pin);
	if(bank < 0)
		return 0;

	char offset = pin_offset(dio->header, dio->pin);
	if(offset <= 0)
		return 0;

	std::string path = "/sys/class/gpio";
	path.append(std::to_string(bank * 32 + offset));
	path.append("/value");
	return path.c_str();
}

void stopDIOPulse(dio_port_t* dio){
	if(dio->pulsing){
#ifdef HAL_BBB_DEBUG
		printf("DIO, stopping pulse: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif

		pthread_mutex_lock(&dio_pulsing_mutex);
		dio_pulse_t* pulse = dio->pulse.get();
		pulse->remaining_time = 0;
		pulse->dio_handle = HAL_INVALID_HANDLE;
		pthread_mutex_unlock(&dio_pulsing_mutex);

		dio->pulsing = false;
		dio->pulse = nullptr;
	}
}
void stopDIOPulse(dio_pulse_t* pulse){
	if(pulse->dio_handle != HAL_INVALID_HANDLE){
		pthread_mutex_lock(&io_mutex);
		if(dio_ports.count(pulse->dio_handle)){
			dio_port_t* dio = dio_ports[pulse->dio_handle].get();

#ifdef HAL_BBB_DEBUG
		printf("DIO, stopping pulse: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif

			dio->pulsing = false;
			dio->pulse = nullptr;
		}
		pthread_mutex_unlock(&io_mutex);

		pulse->dio_handle = HAL_INVALID_HANDLE;
	}
}

void limitPWMDuty(float* duty){
	if(*duty > 1.0f)
		*duty = 1.0f;
	else if(*duty < 0.0f)
		*duty = 0.0f;
}
void limitADCValue(uint32_t* value){
	if(*value > HAL_AIN_MAX_VALUE)
		*value = HAL_AIN_MAX_VALUE;
}

void* counter_thread_function(void* param){

	std::shared_ptr<pulse_counter_t> shared = *((std::shared_ptr<pulse_counter_t>*)param);
	pulse_counter_t* counter = shared.get();

#ifdef HAL_BBB_DEBUG
	printf("DIO counter thread start: %d \n", counter->dio_port);
#endif

	epoll_event epoll_events;
	int epoll_r = 0;

	uint8_t current_value;
	uint8_t last_value;

	auto time_start = std::chrono::high_resolution_clock::now();
	auto p_time_start = time_start;
	long long us_passed;

	bool rise = false, fall = false;
	bool run = true;

	uint8_t run_check_counter = 0;

	while(run){
		//TODO: USE EPOLL TO WAIT FOR VALUE CHANGES!!!!
		epoll_r = epoll_wait(counter->epoll_fd, &epoll_events, 1, HAL_PCOUNTER_TIMEOUT);
		if(epoll_r < 0){
			//TODO: HANDLE EPOLL ERROR
#ifdef HAL_BBB_DEBUG
			printf("Pulse counter epoll error \n");
#endif
			run = false;
			counter->release = true;
		}else{
			current_value = BBB_getDIO(counter->up_port);
			if(current_value == BBB_GPIO_HIGH && last_value == BBB_GPIO_LOW){
				//TODO: RISING
				rise = true;

				time_start = std::chrono::high_resolution_clock::now();

				auto time_elapsed = std::chrono::high_resolution_clock::now() - p_time_start;
				us_passed = std::chrono::duration_cast<std::chrono::microseconds>(time_elapsed).count();
			}
			else if(current_value == BBB_GPIO_LOW && last_value == BBB_GPIO_HIGH){
				//TODO: FALLING
				fall = true;

				auto time_elapsed = std::chrono::high_resolution_clock::now() - time_start;
				us_passed = std::chrono::duration_cast<std::chrono::microseconds>(time_elapsed).count();

				p_time_start = std::chrono::high_resolution_clock::now();
			}

			last_value = current_value;

			if((++run_check_counter) >= HAL_THREAD_RUN_CHECK || fall || rise){
				run_check_counter = 0;

				pthread_mutex_lock(&counter->mutex);
				if(fall){
					counter->length = us_passed;
				}
				if(rise){
					counter->period = us_passed;
					++(counter->count);
				}

				run = !counter->release;
				pthread_mutex_unlock(&counter->mutex);

				rise = fall = false;
			}
		}
	}

#ifdef HAL_BBB_DEBUG
	printf("DIO counter thread end: %d \n", counter->dio_port);
#endif

	return NULL;
}
void* pulse_thread_function(void* param){

#ifdef HAL_BBB_DEBUG
	printf("DIO pulse thread start \n");
#endif

	thread_data_t data;

	long long pulse_us_passed;
	auto pulse_start = std::chrono::high_resolution_clock::now();
	int32_t pulse_min_time;

	uint8_t run_check_counter = 0;

	while(data.run){

		//run pulses
		pulse_min_time = HAL_PULSE_THREAD_DELAY;

		pthread_mutex_lock(&dio_pulsing_mutex);
		if(dio_pulses.size() > 0){
			auto pulse_elapsed = std::chrono::high_resolution_clock::now() - pulse_start;
			pulse_us_passed = std::chrono::duration_cast<std::chrono::microseconds>(pulse_elapsed).count();

			for(auto it = dio_pulses.begin(); it != dio_pulses.end(); ){
				dio_pulse_t* pulse = it->get();
				pulse->remaining_time -= pulse_us_passed;
				if(pulse->remaining_time <= 0 || pulse->dio_handle == HAL_INVALID_HANDLE){
#ifdef HAL_BBB_DEBUG
					printf("DIO pulse ended: TIME= %d, PORT= %d \n", pulse->remaining_time, pulse->dio_handle);
#endif

					//TODO: SET DIO LOW
					stopDIOPulse(pulse);

					it = dio_pulses.erase(it);
				}else{
					++it;
					if(pulse->remaining_time < pulse_min_time)
						pulse_min_time = pulse->remaining_time;
				}
			}
		}
		pthread_mutex_unlock(&dio_pulsing_mutex);
		pulse_start = std::chrono::high_resolution_clock::now();

		if((++run_check_counter) >= HAL_THREAD_RUN_CHECK){
			//get thread data
			pthread_mutex_lock(&thread_param_mutex);
			data.run = thread_data.run;
			pthread_mutex_unlock(&thread_param_mutex);

			run_check_counter = 0;
		}

		if(data.run){
			usleep(pulse_min_time);
		}
	}

#ifdef HAL_BBB_DEBUG
	printf("DIO pulse thread end \n");
#endif

	return NULL;
}
void* adc_thread_function(void* param){

#ifdef HAL_BBB_DEBUG
	printf("ADC thread start \n");
#endif

	thread_data_t data;

	int adc_idx, adc_smpl_idx;
	uint32_t adc_smpl_val;

	auto adc_start = std::chrono::high_resolution_clock::now();
	long long adc_ms_passed;

	uint8_t run_check_counter = 0;

	while(data.run){
		//sample adc channels
		auto adc_elapsed = std::chrono::high_resolution_clock::now() - adc_start;
		adc_ms_passed = std::chrono::duration_cast<std::chrono::milliseconds>(adc_elapsed).count();
		if(adc_ms_passed >= HAL_AIN_SMAPLING_RATE){
#ifdef HAL_USE_IO
			BBBIO_ADCTSC_work(HAL_AIN_SAMPLING_SIZE);
#endif

			pthread_mutex_lock(&adc_sampling_mutex);
			for(adc_idx = 0; adc_idx < BBB_ADC_CHANNEL_COUNT; ++adc_idx){
				adc_port_t* adc = &adc_ports[adc_idx];
				if(adc->enabled){
					adc_smpl_val = 0;
#ifdef HAL_USE_IO

					for(adc_smpl_idx = 0; adc_smpl_idx < HAL_AIN_SAMPLING_SIZE; ++adc_smpl_idx){
						adc_smpl_val += adc->sample_buffer[adc_smpl_idx];
					}
					adc_smpl_val /= HAL_AIN_SAMPLING_SIZE;
#endif
					adc->value = adc_smpl_val;
					if(adc->accumulator_enabled){
						adc->accumulator.value += (adc_smpl_val - adc->accumulator.center);
						++(adc->accumulator.count);
					}
				}
			}
			pthread_mutex_unlock(&adc_sampling_mutex);
			adc_start = std::chrono::high_resolution_clock::now();
		}

		if((++run_check_counter) >= HAL_THREAD_RUN_CHECK){
			//get thread data
			pthread_mutex_lock(&thread_param_mutex);
			data.run = thread_data.run;
			pthread_mutex_unlock(&thread_param_mutex);

			run_check_counter = 0;
		}

		if(data.run){
			usleep(HAL_AIN_SMAPLING_RATE * 1000);
		}
	}

#ifdef HAL_BBB_DEBUG
	printf("ADC thread end \n");
#endif

	return NULL;
}

/***********************************************************************\
 * EXTERNAL METHODS
\***********************************************************************/

int BBB_initialize(int mode){
	if(init){
		//TODO: ALREADY INITIALIZED
#ifdef HAL_BBB_DEBUG
		printf("Initialization failed, HAL already initialized \n");
#endif
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
	status = pthread_create(&pulse_thread, NULL, &pulse_thread_function, NULL);
	if(status){
		//TODO: ERROR
#ifdef HAL_BBB_DEBUG
		printf("Failed to initialize DIO pulse thread \n");
#endif
		return -1;
	}

	status = pthread_create(&adc_thread, NULL, &adc_thread_function, NULL);
	if(status){
		//TODO: ERROR
#ifdef HAL_BBB_DEBUG
		printf("Failed to initialize ADC sampling thread \n");
#endif
		return -1;
	}
#endif

	init = true;

#ifdef HAL_BBB_DEBUG
		printf("HAL initialization complete \n");
#endif

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

	//pthread_mutex_lock(&io_mutex);

#ifdef HAL_BBB_DEBUG
	printf("Shutting down threads \n");
#endif

	//TODO: KILL THREAD IF IO INIT WAS SUCCESSFUL
	pthread_mutex_lock(&thread_param_mutex);
	thread_data.run = false;
	pthread_mutex_unlock(&thread_param_mutex);

#ifdef HAL_USE_THREAD
	pthread_join(pulse_thread, NULL);
	pthread_join(adc_thread, NULL);
#endif

	//clear pulse counter handles
	for(auto it = dio_pulse_counters.begin(); it != dio_pulse_counters.end();){
		pulse_counter_t* counter = it->second.get();

		pthread_mutex_lock(&counter->mutex);
		counter->release = true;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_USE_THREAD
		pthread_join(counter->pthread, NULL);
#endif

		close(counter->epoll_fd);
		pthread_mutex_destroy(&counter->mutex);

		it = dio_pulse_counters.erase(it);
	}

	dio_pulse_counters.clear();

#ifdef HAL_BBB_DEBUG
	printf("Clearing PWM handles \n");
#endif

	//clear pwm handles
	for(int i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port_t* pwm = &pwm_ports[i];

		BBB_freePWMPort(BBB_PWMSS_PORT(i, 0));
		BBB_freePWMPort(BBB_PWMSS_PORT(i, 1));

		pwm->frequency = 0.0f;
	}

#ifdef HAL_BBB_DEBUG
	printf("Clearing ADC handles \n");
#endif

	//clear adc handles
	for(int i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		//adc_port_t* adc = &adc_map[i];

		BBB_freeAnalogInput(i);
	}

#ifdef HAL_BBB_DEBUG
	printf("Clearing DIO handles \n");
#endif

	//clear dio handles
	for(auto it = dio_ports.begin(); it != dio_ports.end();){
		dio_port_t* dio = it->second.get();
		stopDIOPulse(dio);

#ifdef HAL_USE_IO
		pin_low(HAL_HEADER(dio->header), dio->pin);
#endif

#ifdef HAL_BBB_DEBUG
		printf("DIO handle freed: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif

		it = dio_ports.erase(it);
	}
	dio_ports.clear();
	dio_pulses.clear();

	pthread_mutex_destroy(&dio_pulsing_mutex);
	pthread_mutex_destroy(&adc_sampling_mutex);
	pthread_mutex_destroy(&thread_param_mutex);

#ifdef HAL_USE_IO

#ifdef HAL_BBB_DEBUG
	printf("Shutting down BBBIOLib \n");
#endif

	iolib_free();
#endif

	//pthread_mutex_unlock(&io_mutex);
	pthread_mutex_destroy(&io_mutex);

	init = false;

#ifdef HAL_BBB_DEBUG
		printf("HAL shutdown complete \n");
#endif
}

/***********************************************************************\
 * DIO
\***********************************************************************/

bool BBB_checkDigitalPortValid(int8_t port){
	int header = BBB_GPIO_PORT_TO_HEADER(port + 1);
	int pin = BBB_GPIO_PORT_TO_PIN(port + 1);

	char bank = pin_bank(header, pin);
	if(bank < 0)
		return false;

	char offset = pin_offset(header, pin);
	if(offset <= 0)
		return false;

	return true;
}
bool BBB_checkDigitalPortTaken(int8_t port){
	if(!init){
		return false;
	}

	bool res = false;

	pthread_mutex_lock(&io_mutex);
	hal_handle_t handle = (hal_handle_t)port;
	if(dio_ports.count(handle)){
		res = true;
	}
	pthread_mutex_unlock(&io_mutex);

	return res;
}

hal_handle_t BBB_initializeDIOPort(int8_t port, uint8_t dir){
	if(!init){
		return HAL_INVALID_HANDLE;
	}

#ifdef HAL_BBB_DEBUG
	printf("Initializing DIO port \n");
#endif

	pthread_mutex_lock(&io_mutex);
	hal_handle_t handle = (hal_handle_t)port;
	if(dio_ports.count(handle)){
		dio_port_t* dio = dio_ports[handle].get();

#ifdef HAL_BBB_DEBUG
		printf("DIO handle already exists: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif

		if(dir != dio->dir){
			handle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
			printf("Wanted direction doesn't match already used direction: %d != %d \n", dir, dio->dir);
#endif
		}
	}else if(port >= 0){
		//TODO: CREATE DIO HANDLE AND ADD TO MAP. ALSO CHECK IF PORT MATCHES
		dio_port_t dio;
		dio.header = BBB_GPIO_PORT_TO_HEADER(port + 1);
		dio.pin = BBB_GPIO_PORT_TO_PIN(port + 1);
		dio.dir = dir;
		dio.pulsing = false;
		dio.pulse = nullptr;

		int result;

#ifdef HAL_USE_IO
		result = iolib_setdir(HAL_HEADER(dio.header + 8), dio.pin, dir);
#else
		result = 0;
#endif

		if(result == 0){
#ifdef HAL_USE_IO
			pin_low(HAL_HEADER(dio.header + 8), dio.pin);
#endif
			dio_ports.emplace(handle, std::make_shared<dio_port_t>(dio));

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
	if(dio_ports.count(portHandle)){
		if(dio_pulse_counters.count(portHandle)){
			BBB_freePulseCounter(portHandle);
		}

		dio_port_t* dio = dio_ports[portHandle].get();

		stopDIOPulse(dio);

#ifdef HAL_USE_IO
		pin_low(HAL_HEADER(dio->header), dio->pin);
#endif
		//TODO: REMOVE DIO FROM MAP AND DESTROY OBJECT...
		dio_ports.erase(portHandle);

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
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();
		if(dio->dir == BBB_DIR_OUTPUT){
			if(dio->val != high){
				dio->val = high;
				if(high == BBB_GPIO_HIGH){
#ifdef HAL_USE_IO
					pin_high(HAL_HEADER(dio->header), dio->pin);
#endif
					dio->val = BBB_GPIO_HIGH;

#ifdef HAL_BBB_DEBUG
					printf("Set DIO to high: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
				}else if(high == BBB_GPIO_LOW){
					//TODO: CHECK IF PULSING
					stopDIOPulse(dio);
#ifdef HAL_USE_IO
					pin_low(HAL_HEADER(dio->header), dio->pin);
#endif
					dio->val = BBB_GPIO_LOW;

#ifdef HAL_BBB_DEBUG
					printf("Set DIO to low: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
				}
			}else{
#ifdef HAL_BBB_DEBUG
				printf("DIO already set to given value: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
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
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

		//TODO: use mutex here because of thread
		pthread_mutex_lock(&dio_pulsing_mutex);

		if(dio->pulsing){
			dio_pulse_t* pulse = dio->pulse.get();
			pulse->remaining_time += pulseus;
#ifdef HAL_BBB_DEBUG
			printf("Pulse handle already in use, adding time: LEN= %d, HEADER= %d, PIN= %d \n",
					pulse->remaining_time,dio->header, dio->pin);
#endif
		}else{
			if(dio->dir == BBB_DIR_OUTPUT){
				dio->val = BBB_GPIO_HIGH;

#ifdef HAL_USE_IO
				pin_high(HAL_HEADER(dio->header), dio->pin);
#endif

				dio_pulse_t pulse;
				pulse.dio_handle = portHandle;
				pulse.remaining_time = pulseus;

				std::shared_ptr<dio_pulse_t> shared = std::make_shared<dio_pulse_t>(pulse);

				dio_pulses.push_back(shared);

				dio->pulsing = true;
				dio->pulse = shared;

#ifdef HAL_BBB_DEBUG
				printf("Created pulse handle: LEN= %d, HEADER= %d, PIN= %d \n", pulseus, dio->header, dio->pin);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Cannot pulse out, direction is input: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
			}
		}

		pthread_mutex_unlock(&dio_pulsing_mutex);
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
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();
		if(dio->dir == BBB_DIR_OUTPUT){
			val = dio->val;
#ifdef HAL_BBB_DEBUG
			printf("DIO is output, using last value: HEADER= %d, PIN= %d \n", dio->header, dio->pin);
#endif
		}else{

#ifdef HAL_USE_IO
			val = is_high(HAL_HEADER(dio->header), dio->pin);
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

bool BBB_checkAnalogInputPortValid(int8_t port){
	return port >= 0 && port < BBB_ADC_CHANNEL_COUNT;
}
bool BBB_checkAnalogInputPortTaken(int8_t port){
	if(!init){
		return false;
	}

	bool taken = false;
	if(BBB_checkAnalogInputPortValid(port)){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);

		adc_port_t* adc = &adc_ports[port];
		taken = adc->enabled;

		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}
	return taken;
}

hal_handle_t BBB_initializeAnalogInput(int8_t port){
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
		adc_port_t* adc = &adc_ports[port];
		if(adc->enabled){
			//TODO: INITIALIZE ADC PORT

#ifdef HAL_USE_IO
			BBBIO_ADCTSC_channel_ctrl(port, BBBIO_ADC_STEP_MODE_SW_CONTINUOUS, HAL_AIN_OPEN_DELAY,
					HAL_AIN_SAMPLE_DELAY, BBBIO_ADC_STEP_AVG_1, adc->sample_buffer, HAL_AIN_SAMPLING_SIZE);
			BBBIO_ADCTSC_channel_enable(port);
#endif

			adc->enabled = true;
			adc->value = 0;
			adc->accumulator.value = 0;
			adc->accumulator.count = 0;
			adc->accumulator.center = 0;
			adc->accumulator_enabled = false;

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
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled){
			adc->enabled = 0;
			//TODO: STOP ADC CHANNEL
			adc->accumulator_enabled = false;

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
		adc_port_t* adc = &adc_ports[portHandle];
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
	return (float)HAL_AIN_VALUE_TO_VOLTAGE(value);
}

float BBB_convertAnalogValueToVoltage(uint32_t value){
	return (float)HAL_AIN_VALUE_TO_VOLTAGE(value);
}
uint32_t BBB_convertAnalogVoltageToValue(float voltage){
	return (uint32_t)HAL_AIN_VOLTAGE_TO_VALUE(voltage);
}

int BBB_enableAnalogInputAccumulator(hal_handle_t portHandle, bool enable){
	if(!init){
		return -1;
	}

	int retval = 0;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled){
			if(enable != adc->accumulator_enabled){
				if(enable){
					adc->accumulator.count = 0;
					adc->accumulator.value = 0;
				}
				adc->accumulator_enabled = enable;

#ifdef HAL_BBB_DEBUG
				printf("ADC channel accumulator enabled: %d, %d \n", portHandle, enable);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("ADC channel accumulator already at state: %d, %d \n", portHandle, enable);
#endif
			}
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel was not enabled: %d \n", portHandle);
#endif
			retval = -1;
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", portHandle);
#endif
		retval = -1;
	}

	return retval;
}
void BBB_resetAnalogInputAccumulator(hal_handle_t portHandle){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled && adc->accumulator_enabled){
			adc->accumulator.value = 0;
			adc->accumulator.count = 0;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel accumulator reset: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel or accumulator are not enabled: %d \n", portHandle);
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
void BBB_setAnalogInputAccumulatorCenter(hal_handle_t portHandle, uint32_t center){
	if(!init){
		return;
	}

	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled && adc->accumulator_enabled){
			limitADCValue(&center);
			adc->accumulator.center = center;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel accumulator center: %d, %d \n", portHandle, center);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel or accumulator are not enabled: %d \n", portHandle);
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
int64_t BBB_getAnalogInputAccumulatorValue(hal_handle_t portHandle){
	if(!init){
		return 0;
	}

	int64_t value = 0;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled && adc->accumulator_enabled){
			value = adc->accumulator.value;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel accumulator get value: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel or accumulator are not enabled: %d \n", portHandle);
#endif
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", portHandle);
#endif
	}
	return value;
}
uint32_t BBB_getAnalogInputAccumulatorCount(hal_handle_t portHandle){
	if(!init){
		return 0;
	}

	uint32_t value = 0;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);
		pthread_mutex_lock(&adc_sampling_mutex);
		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled && adc->accumulator_enabled){
			value = adc->accumulator.count;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel accumulator get count: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel or accumulator are not enabled: %d \n", portHandle);
#endif
		}
		pthread_mutex_unlock(&adc_sampling_mutex);
		pthread_mutex_unlock(&io_mutex);
	}else{
#ifdef HAL_BBB_DEBUG
		printf("ADC port is invalid, out of range: %d \n", portHandle);
#endif
	}
	return value;
}

float BBB_getGlobalAnalogSampleRate(){
	float secs = (HAL_AIN_SMAPLING_RATE * 0.001);
	return secs;
}
float BBB_getAnalogMaxVoltage(){
	return (float)BBB_ADC_MAX_VOLTAGE;
}
uint32_t BBB_getAnalogMaxValue(){
	return (uint32_t)HAL_AIN_MAX_VALUE;
}

/***********************************************************************\
 * PWM
\***********************************************************************/

bool BBB_checkPWMPortValid(int8_t port){
	return port >= 0 && port < HAL_PWMSS_PORTS_COUNT;
}
bool BBB_checkPWMPortTaken(int8_t port){
	if(!init || !BBB_checkPWMPortValid(port)){
		return false;
	}

	bool taken = false;

	pthread_mutex_lock(&io_mutex);
	uint8_t module = BBB_PWMSS_PORT_TO_MODULE(port);
	uint8_t portv = BBB_PWMSS_PORT_TO_PIN(port);

	pwm_port_t* pwm = &pwm_ports[module];

	if((pwm->enabledA && portv == BBB_PWMSSA) || (pwm->enabledB && portv == BBB_PWMSSB))
		taken = true;

	pthread_mutex_unlock(&io_mutex);

	return taken;
}

hal_handle_t BBB_initializePWMPort(int8_t port){
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
		pwm_port_t* pwm = &pwm_ports[module];

#ifdef HAL_USE_IO
		if(pwm->enabledA && pwm->enabledB){
			BBBIO_ehrPWM_Enable(module);


#ifdef HAL_BBB_DEBUG
			printf("PWM module enabled: %d \n", module);
#endif
		}
#endif

		bool initmodule = false;

		if(pin == BBB_PWMSSA && pwm->enabledA){
			pwm->enabledA = true;
			pwm->dutyA = 0.0f;
			initmodule = true;
		}
		else if(pin == BBB_PWMSSB && pwm->enabledB){
			pwm->enabledB = true;
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
		pwm_port_t* pwm = &pwm_ports[module];

		bool freed = false;

		if(pin == BBB_PWMSSA && pwm->enabledA){
			pwm->enabledA = false;
			pwm->dutyA = 0.0f;
			freed = true;
		}
		else if(pin == BBB_PWMSSB && pwm->enabledB){
			pwm->enabledB = false;
			pwm->dutyB = 0.0f;
			freed = true;
		}

		if(freed){
#ifdef HAL_BBB_DEBUG
			printf("PWM port disabled: MODULE= %d, PIN= %d \n", module, pin);
#endif

#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
			if(pwm->enabledA && pwm->enabledB){
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
		pwm_port_t* pwm = &pwm_ports[module];

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
		pwm_port_t* pwm = &pwm_ports[module];
		if(pwm->enabledA || pwm->enabledB){
			limitPWMDuty(&duty);

			uint8_t port = BBB_PWMSS_PORT_TO_PIN(portHandle);
			bool update = false;

			if(port == BBB_PWMSSA && pwm->enabledA && pwm->dutyA != duty){
				pwm->dutyA = duty;
				update = true;
			}
			else if(port == BBB_PWMSSB && pwm->enabledB && pwm->dutyB != duty){
				pwm->dutyB = duty;
				update = true;
			}

			if(update){
#ifdef HAL_USE_IO
				BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
#endif
#ifdef HAL_BBB_DEBUG
				printf("PWM port set: %f - MODULE= %d, PIN= %d \n", duty, module, port);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("PWM port is already set to given cycle: %f - MODULE= %d, PIN= %d \n", duty, module, port);
#endif
			}
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
		pwm_port_t* pwm = &pwm_ports[module];
		if(pwm->enabledA || pwm->enabledB){
			//TODO: LIMIT FREQUENCY
			pwm->frequency = frequency;

#ifdef HAL_USE_IO
			BBBIO_PWMSS_Setting(module, pwm->frequency, pwm->dutyA, pwm->dutyB);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port set frequency: %f - MODULE= %d \n", frequency, module);
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
		pwm_port_t* pwm = &pwm_ports[module];
		if(pwm->enabledA || pwm->enabledB){

			frequency = pwm->frequency;

#ifdef HAL_BBB_DEBUG
			printf("PWM port get frequency: FREQ= %f, MODULE= %d\n", frequency, module);
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

/***********************************************************************\
 * Pulse Counter
\***********************************************************************/

hal_handle_t BBB_initializePulseCounter(int8_t dioPort){
	if(!init){
		return HAL_INVALID_HANDLE;
	}

	hal_handle_t handle = (hal_handle_t)dioPort;
	if(dio_pulse_counters.count(handle)){
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter for port exists: %d \n", dioPort);
#endif
	}else{
		pulse_counter_t counter;
		counter.period = 0;
		counter.count = 0;
		counter.length = 0;
		counter.release = false;
		counter.quadrature = false;
		counter.down_port = HAL_INVALID_HANDLE;
		counter.up_port = BBB_initializeDIOPort(dioPort, BBB_DIR_INPUT);

		if(counter.up_port != HAL_INVALID_HANDLE){
			pthread_mutex_lock(&io_mutex);
			dio_port_t* dio = dio_ports[counter.up_port].get();
			const char* filepath = resolveFilepath(dio);
			pthread_mutex_unlock(&io_mutex);

			int file_fd = -1, epoll_fd = -1;
			if(filepath != 0){
#ifdef HAL_BBB_DEBUG
				printf("Pulse counter filepath resolved: %d, %s\n", dioPort, filepath);
#endif

				file_fd = open(filepath, O_RDONLY);

				if(file_fd >= 0){
					epoll_fd = epoll_create(1);

					if(epoll_fd >= 0){
						epoll_event epoll_ev;
						epoll_ev.events = EPOLLIN;
						epoll_ev.data.fd = epoll_fd;

						int ctl = epoll_ctl(epoll_fd, EPOLL_CTL_ADD, file_fd, &epoll_ev);
						if(ctl < 0){
							close(epoll_fd);
							epoll_fd = -1;
						}else{
#ifdef HAL_BBB_DEBUG
							printf("Pulse counter failed to config epoll: %d\n", dioPort);
#endif
						}
					}else{
#ifdef HAL_BBB_DEBUG
						printf("Pulse counter failed to create epoll: %d\n", dioPort);
#endif
					}
				}else{
#ifdef HAL_BBB_DEBUG
					printf("Pulse counter failed to open file: %d, %s\n", dioPort, filepath);
#endif
				}
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Pulse counter failed to resolve filepath: %d\n", dioPort);
#endif
			}

			if(epoll_fd >= 0){
				std::shared_ptr<pulse_counter_t> shared = std::make_shared<pulse_counter_t>(counter);
				dio_pulse_counters.emplace(handle, shared);

				counter.epoll_fd = epoll_fd;
				counter.mutex = PTHREAD_MUTEX_INITIALIZER;

#ifdef HAL_USE_THREAD
				pthread_create(&counter.pthread, NULL, &counter_thread_function,
						(void *)(&shared));
#endif

#ifdef HAL_BBB_DEBUG
				printf("Pulse counter initialized for DIO port: %d \n", dioPort);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Pulse counter failed to initialize epoll: %d \n", dioPort);
#endif
			}
		}else{
			handle = HAL_INVALID_HANDLE;
#ifdef HAL_BBB_DEBUG
			printf("Pulse counter failed to initialize DIO port: %d \n", dioPort);
#endif
		}
	}

	return handle;
}
hal_handle_t BBB_initializePulseCounter(int8_t upPort, int8_t downPort){
	return 0;
}
void BBB_freePulseCounter(hal_handle_t counterHandle){
	if(!init){
		return;
	}

	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		counter->release = true;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_USE_THREAD
		pthread_join(counter->pthread, NULL);
#endif

		pthread_mutex_destroy(&counter->mutex);
		close(counter->epoll_fd);

		dio_pulse_counters.erase(counterHandle);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is set for release: %d \n", counterHandle);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}
}

void BBB_resetPulseCounter(hal_handle_t counterHandle){
	if(!init){
		return;
	}

	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		counter->count = 0;
		counter->period = 0;
		counter->length = 0;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter reset: %d \n", counterHandle);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}
}

uint8_t BBB_getPulseCounterDirection(hal_handle_t counterHandle){
	if(!init){
		return 0;
	}

	uint8_t direction = 0;
	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		direction = counter->direction;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter direction get: %d, %d \n", counterHandle, count);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}

	return direction;
}
uint32_t BBB_getPulseCounterCount(hal_handle_t counterHandle){
	if(!init){
		return 0;
	}

	uint32_t count = 0;
	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		count = counter->count;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter count get: %d, %d \n", counterHandle, count);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}

	return count;
}
float BBB_getPulseCounterPeriod(hal_handle_t counterHandle){
	if(!init){
		return 0.0f;
	}

	uint32_t period = 0;
	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		period = counter->period;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter period get: %d, %d \n", counterHandle, period);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}

	//TODO: CONVERT US TO SEC
	float periodSec = (float)(period * 0.000001f);
	return periodSec;
}
float BBB_getPulseCounterLength(hal_handle_t counterHandle){
	if(!init){
		return 0.0f;
	}

	uint32_t length = 0;
	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		length = counter->length;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter length get: %d, %d \n", counterHandle, length);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}

	//TODO: CONVERT US TO SEC
	float lengthSec = (float)(length * 0.000001f);
	return lengthSec;
}
bool BBB_isPulseCounterQuadrature(hal_handle_t counterHandle){
	if(!init){
		return false;
	}

	bool quad = false;
	if(dio_pulse_counters.count(counterHandle)){
		pulse_counter_t* counter = dio_pulse_counters[counterHandle].get();
		pthread_mutex_lock(&counter->mutex);
		quad = counter->quadrature;
		pthread_mutex_unlock(&counter->mutex);

#ifdef HAL_BBB_DEBUG
		printf("Pulse counter quadrature get: %d, %d \n", counterHandle, count);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("Pulse counter is not initialized: %d \n", counterHandle);
#endif
	}

	return quad;
}

} /* namespace hal */

} /* namespace flashlib */
