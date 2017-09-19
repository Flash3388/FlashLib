/*
 * hal.cpp
 *
 *  Created on: Sep 16, 2017
 *      Author: root
 */

#include <unordered_map>
#include <pthread.h>
#include <memory>
#include <unistd.h>

#include "../../include/bbb_defines.h"
#include "../../include/hal.h"

#include "hal_defines.h"
#include "pru_defines.h"
#include "handles.h"
#include "pru.h"

#ifdef HAL_BBB_DEBUG
#include <iostream>
#endif

namespace flashlib{

namespace hal{

std::unordered_map<hal_handle_t, std::shared_ptr<dio_port_t>> dio_ports;

pwm_port_t pwm_ports[HAL_PWMSS_PORTS_COUNT];
adc_port_t adc_ports[BBB_ADC_CHANNEL_COUNT];

pthread_mutex_t io_mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t watchdog_mutex = PTHREAD_MUTEX_INITIALIZER;

typedef struct thread_data{
	bool run = true;
} thread_data_t;

thread_data_t watchdog_thread_data;
pthread_t watchdog_thread;

pru_data_t pru_data;
int pru_watchdog_error;

bool init = false;

const char* pru_program_name = HAL_PRU_PROGRAM;
const char* pru_data_file = HAL_PRU_DATA;

/***********************************************************************\
 * INTERNAL METHOD
\***********************************************************************/

#ifdef HAL_USE_IO
void* pru_watchdog_function(void* param){

	thread_data_t data;
	unsigned int last_value = 0;
	unsigned int new_value;

	int errors = 0;

	while(data.run){


		pthread_mutex_lock(&io_mutex);

		new_value = pru_data.shared_memory[PRU_MEM_STATUS_REG];
		if(new_value <= last_value && (++errors) >= 3){

			//TODO: HANDLE PRU_UPDATE_ERROR
			pru_watchdog_error = WATCHDOG_PRU_UPDATE_ERROR;

			pthread_mutex_lock(&watchdog_mutex);
			data.run = false;
			watchdog_thread_data.run = false;
			pthread_mutex_unlock(&watchdog_mutex);

		}else{
			last_value = new_value;
			errors = 0;
		}

		pthread_mutex_unlock(&io_mutex);

		usleep(PRU_WATCHDOG_UPDATE);

		pthread_mutex_lock(&watchdog_mutex);
		data.run = watchdog_thread_data.run;
		pthread_mutex_unlock(&watchdog_mutex);
	}

	return NULL;
}

void pru_interrupt_wait(){
	int res = pru_interrupt_wait(&pru_data);
	if(res == 0){
#ifdef HAL_BBB_DEBUG
		printf("PRU EVENT WAIT ERROR \n");
#endif

		//TODO: HANDLE PRU_NOT_RESPONSIVE
		pru_watchdog_error = WATCHDOG_PRU_NOT_RESPONSIVE;
	}
}

void pru_pwm_calc_divisors(float hz, unsigned char* clkdiv, unsigned char* hspclkdiv){
	const float CLKDIV[] = {1.0f ,2.0f ,4.0f ,8.0f ,16.0f ,32.0f , 64.0f , 128.0f};
	const float HSPCLKDIV[] = {1.0f ,2.0f ,4.0f ,6.0f ,8.0f ,10.0f , 12.0f , 14.0f};

	float cyclens = 0.0f;
	float divisor = 0.0f;
	int i, j;

	unsigned char closest_clkdiv = 7, closest_hspclkdiv = 7;

	cyclens = 1000000000.0f / hz ;
	divisor =  cyclens / 655350.0f;

	for(i = 0 ; i < 8 ; ++i) {
		for(j = 0 ; j < 8 ; ++j) {
			if((CLKDIV[i] * HSPCLKDIV[j]) < (CLKDIV[closest_clkdiv] * HSPCLKDIV[closest_hspclkdiv]) &&
			  ((CLKDIV[i] * HSPCLKDIV[j]) > divisor)){
				closest_clkdiv = i;
				closest_hspclkdiv = j;
			}
		}
	}

	*clkdiv = closest_clkdiv;
	*hspclkdiv = closest_hspclkdiv;
}

int pru_initialize(){
	return pru_initialize(&pru_data, HAL_PRU_NUM, pru_program_name, pru_data_file);
}
void pru_shutdown(){
	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_SYS_FREE;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();

	pru_shutdown(&pru_data);
}

hal_handle_t pru_dio_initialize(uint16_t port, uint8_t dir, dio_port_t* dio){
	uint8_t handle_type = dir == BBB_DIR_OUTPUT? PRU_HANDLE_DO : PRU_HANDLE_DI;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_INIT;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = port;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();

	hal_handle_t handle = (hal_handle_t)pru_data.shared_memory[PRU_MEM_HANDLE_RES_REG];

	if(handle != HAL_INVALID_HANDLE)
		dio->val_addr_offset = PRU_MEM_DIO_OFFSET + handle;

	return handle;
}
void pru_dio_free(hal_handle_t handle, uint8_t dir){
	uint8_t handle_type = dir == BBB_DIR_OUTPUT? PRU_HANDLE_DO : PRU_HANDLE_DI;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_FREE;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = handle;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();
}
void pru_dio_pulse(hal_handle_t handle, float length){
	//TODO: CONVERT PULSE TO US FROM SEC
	uint32_t pulselength = (uint32_t)(length * 1000000);

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_DIO_PULSE;
	pru_data.shared_memory[PRU_MEM_ACTION_VAL_REG] = pulselength;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = handle;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();
}
void pru_dio_set(dio_port_t* port, uint8_t value){
	pru_data.shared_memory[port->val_addr_offset] = value;
}
uint8_t pru_dio_get(dio_port_t* port){
	return (uint8_t)pru_data.shared_memory[port->val_addr_offset];
}

hal_handle_t pru_adc_initialize(uint8_t channel, adc_port_t* adc){
	uint8_t handle_type = PRU_HANDLE_ADC;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_INIT;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = channel;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();

	hal_handle_t handle = (hal_handle_t)pru_data.shared_memory[PRU_MEM_HANDLE_RES_REG];

	if(handle != HAL_INVALID_HANDLE)
		adc->val_addr_offset = PRU_MEM_ADC_OFFSET + handle;

	return handle;
}
void pru_adc_free(hal_handle_t handle){
	uint8_t handle_type = PRU_HANDLE_ADC;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_FREE;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = handle;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();
}
uint32_t pru_adc_get(adc_port_t* adc){
	return (uint32_t)pru_data.shared_memory[adc->val_addr_offset];
}

hal_handle_t pru_pwm_initialize(uint8_t channel, pwm_port_t* pwm){
	uint8_t handle_type = PRU_HANDLE_PWM;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_INIT;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = channel;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();

	hal_handle_t handle = (hal_handle_t)pru_data.shared_memory[PRU_MEM_HANDLE_RES_REG];

	if(handle != HAL_INVALID_HANDLE)
		pwm->val_addr_offset = PRU_MEM_PWM_OFFSET + handle;

	return handle;
}
void pru_pwm_free(hal_handle_t handle){
	uint8_t handle_type = PRU_HANDLE_PWM;

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PORT_FREE;
	pru_data.shared_memory[PRU_MEM_HANDLE_TYPE_REG] = handle_type;
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = handle;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();
}
void pru_pwm_set(pwm_port_t* pwm, uint8_t val){
	pru_data.shared_memory[pwm->val_addr_offset] = val;
}
uint8_t pru_pwm_get(pwm_port_t* pwm){
	return (uint8_t)pru_data.shared_memory[pwm->val_addr_offset];
}
void pru_pwm_frequency_set(hal_handle_t handle, float frequency){
	unsigned char clkdiv;
	unsigned char hspclkdiv;

	pru_pwm_calc_divisors(frequency, &clkdiv, &hspclkdiv);

	pru_data.shared_memory[PRU_MEM_ACTION_TYPE_REG] = PRU_ACTION_PWM_FREQ_S;
	pru_data.shared_memory[PRU_MEM_ACTION_VAL_REG] = clkdiv | (hspclkdiv << 8);
	pru_data.shared_memory[PRU_MEM_HANDLE_VAL_REG] = handle;

	pru_interrupt_send(&pru_data);
	pru_interrupt_wait();
}

#endif

void bbb_pwm_limit(uint8_t* val){
	if(*val > HAL_PWMSS_MAX_VALUE)
		*val = HAL_PWMSS_MAX_VALUE;
}
/***********************************************************************\
 * EXTERNAL METHOD
\***********************************************************************/

int BBB_initialize(int mode){
	if(init){
		//TODO: already initialized
#ifdef HAL_BBB_DEBUG
		printf("HAL already initialized \n");
#endif
		return -1;
	}

#ifdef HAL_BBB_DEBUG
	printf("Initializing HAL \n");
#endif

	int status = 0;
#ifdef HAL_USE_IO
	status = pru_initialize();
#endif
	if(status){
		//TODO: init error
#ifdef HAL_BBB_DEBUG
		printf("Failed to initialize PRU \n");
#endif
		return -1;
	}

#ifdef HAL_USE_IO
	status = pthread_create(&watchdog_thread, NULL, &pru_watchdog_function, NULL);
	if(status){
		//TODO: ERROR
#ifdef HAL_BBB_DEBUG
		printf("Failed to initializing IO thread \n");
#endif
		return -1;
	}

#endif
	pru_watchdog_error = 0;

	init = true;
	return 0;
}
void BBB_shutdown(){
	if(!init){
		//TODO: not initialized
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

#ifdef HAL_USE_IO
#ifdef HAL_BBB_DEBUG
	printf("Stopping watchdog thread \n");
#endif


	pthread_mutex_lock(&watchdog_mutex);
	watchdog_thread_data.run = false;
	pthread_mutex_unlock(&watchdog_mutex);

	pthread_join(watchdog_thread, NULL);

	pthread_mutex_destroy(&watchdog_mutex);
#endif

#ifdef HAL_USE_IO
#ifdef HAL_BBB_DEBUG
	printf("HAL shutting down \n");
#endif

	pthread_mutex_lock(&io_mutex);
	pru_shutdown();
	pthread_mutex_unlock(&io_mutex);
#endif


#ifdef HAL_BBB_DEBUG
	printf("Clearing handles \n");
#endif

	pthread_mutex_lock(&io_mutex);
	//clear pwm handles
	for(int i = 0; i < HAL_PWMSS_PORTS_COUNT; ++i){
		pwm_port_t* pwm = &pwm_ports[i];
		pwm->enabled = false;
		pwm->val_addr_offset = PRU_MEM_PWM_EMPTY;
	}
	//clear adc handles
	for(int i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		adc_port_t* adc = &adc_ports[i];
		adc->enabled = false;
		adc->val_addr_offset = PRU_MEM_ADC_EMPTY;
	}
	//clear dio handles
	dio_ports.clear();
	pthread_mutex_unlock(&io_mutex);

	pthread_mutex_destroy(&io_mutex);

#ifdef HAL_BBB_DEBUG
	printf("HAL shutdown complete \n");
#endif

	init = false;
}

/***********************************************************************\
 * DIO
\***********************************************************************/

hal_handle_t BBB_initializeDIOPort(int16_t port, uint8_t dir){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return HAL_INVALID_HANDLE;
	}
	if(dir != BBB_DIR_INPUT && dir != BBB_DIR_OUTPUT){

#ifdef HAL_BBB_DEBUG
		printf("DIO direction value is invalid: %d \n", dir);
#endif

		return HAL_INVALID_HANDLE;
	}

	pthread_mutex_lock(&io_mutex);
	hal_handle_t portHandle = (hal_handle_t)port;
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

#ifdef HAL_BBB_DEBUG
		printf("DIO handle already initialized: %d \n", portHandle);
#endif

		if(dio->dir != dir){
			portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
			printf("DIO direction does not match initialized direction: %d != %d \n", dir, dio->dir);
#endif
		}
	}else if(port > 0){
		dio_port_t dio;
		dio.dir = dir;

#ifdef HAL_USE_IO
		portHandle = pru_dio_initialize(port, dir, &dio);
#endif

		if(portHandle != HAL_INVALID_HANDLE){
			dio_ports.emplace(portHandle, std::make_shared<dio_port_t>(dio));

#ifdef HAL_BBB_DEBUG
			printf("Initialized DIO handle: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("Failed to initialize DIO handle: %d \n", portHandle);
#endif
		}
	}else{
		portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
		printf("DIO port is invalid: %d \n", portHandle);
#endif
	}
	pthread_mutex_unlock(&io_mutex);
	return portHandle;
}
void BBB_freeDIOPort(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	pthread_mutex_lock(&io_mutex);
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

#ifdef HAL_USE_IO
		pru_dio_free(portHandle, dio->dir);
#endif

		dio_ports.erase(portHandle);

#ifdef HAL_BBB_DEBUG
		printf("DIO handle freed: %d \n", portHandle);
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
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}
	if(high != BBB_GPIO_HIGH && high != BBB_GPIO_LOW){

#ifdef HAL_BBB_DEBUG
		printf("DIO value invalid: %d \n", high);
#endif
		return;
	}

	pthread_mutex_lock(&io_mutex);
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

		if(dio->dir == BBB_DIR_OUTPUT){
#ifdef HAL_USE_IO
			pru_dio_set(dio, high);
#endif

#ifdef HAL_BBB_DEBUG
			printf("DIO value set: %d -> %d \n", portHandle, high);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("DIO handle is INPUT, cannot set value: %d \n", portHandle);
#endif
		}

	}else{
#ifdef HAL_BBB_DEBUG
		printf("DIO handle not initialized: %d \n", portHandle);
#endif
	}
	pthread_mutex_unlock(&io_mutex);
}
void BBB_pulseDIO(hal_handle_t portHandle, float length){
	if(!init){
		return;
	}

	pthread_mutex_lock(&io_mutex);
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

		if(dio->dir == BBB_DIR_OUTPUT){
#ifdef HAL_USE_IO
			pru_dio_set(dio, BBB_GPIO_HIGH);
			pru_dio_pulse(portHandle, length);
#endif

#ifdef HAL_BBB_DEBUG
			printf("DIO pulse started: %d -> %d \n", portHandle, length);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("DIO handle direction is INPUT, cannot pulse: %d \n", portHandle);
#endif
		}
	}else{
#ifdef HAL_BBB_DEBUG
		printf("DIO handle not initialized: %d \n", portHandle);
#endif
	}
	pthread_mutex_unlock(&io_mutex);
}

uint8_t BBB_getDIO(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return 0;
	}

	uint8_t val = 0;
	pthread_mutex_lock(&io_mutex);
	if(dio_ports.count(portHandle)){
		dio_port_t* dio = dio_ports[portHandle].get();

#ifdef HAL_USE_IO
		val = pru_dio_get(dio);
#endif

#ifdef HAL_BBB_DEBUG
		printf("DIO value read: %d -> %d \n", portHandle, val);
#endif
	}else{
#ifdef HAL_BBB_DEBUG
		printf("DIO handle not initialized: %d \n", portHandle);
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
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return HAL_INVALID_HANDLE;
	}

	hal_handle_t portHandle = (hal_handle_t)port;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);

		adc_port_t* adc = &adc_ports[portHandle];
		if(!adc->enabled){

#ifdef HAL_USE_IO
			portHandle = pru_adc_initialize(port, adc);
#endif

			if(portHandle != HAL_INVALID_HANDLE){
				adc->enabled = true;

#ifdef HAL_BBB_DEBUG
				printf("ADC channel initialized: %d \n", portHandle);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Failed to initialize ADC channel: %d \n", portHandle);
#endif
			}
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel already initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
		portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel invalid: %d \n", portHandle);
#endif
	}
	return portHandle;
}
void BBB_freeAnalogInput(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);

		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled){
#ifdef HAL_USE_IO
			pru_adc_free(portHandle);
#endif

			adc->enabled = false;

#ifdef HAL_BBB_DEBUG
			printf("ADC channel freed: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("ADC channel invalid: %d \n", portHandle);
#endif
	}
}

uint32_t BBB_getAnalogValue(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return 0;
	}

	uint32_t val = 0;
	if(portHandle >= 0 && portHandle < BBB_ADC_CHANNEL_COUNT){
		pthread_mutex_lock(&io_mutex);

		adc_port_t* adc = &adc_ports[portHandle];
		if(adc->enabled){
#ifdef HAL_USE_IO
			val = pru_adc_get(adc);
#endif

#ifdef HAL_BBB_DEBUG
			printf("ADC channel read: %d -> %d \n", portHandle, val);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("ADC channel not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("ADC channel invalid: %d \n", portHandle);
#endif
	}
	return val;
}
float BBB_getAnalogVoltage(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
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
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return HAL_INVALID_HANDLE;
	}

	hal_handle_t portHandle = (hal_handle_t)port;
	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(!pwm->enabled){
#ifdef HAL_USE_IO
			portHandle = pru_pwm_initialize(port, pwm);
#endif
			if(portHandle != HAL_INVALID_HANDLE){
				pwm->enabled = true;

#ifdef HAL_BBB_DEBUG
				printf("PWM port initialized: %d \n", portHandle);
#endif
			}else{
#ifdef HAL_BBB_DEBUG
				printf("Failed to initialize PWM port: %d \n", portHandle);
#endif
			}
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port already initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
		portHandle = HAL_INVALID_HANDLE;

#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}
	return portHandle;
}
void BBB_freePWMPort(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(pwm->enabled){
#ifdef HAL_USE_IO
			pru_pwm_free(portHandle);
#endif

			pwm->enabled = false;

#ifdef HAL_BBB_DEBUG
			printf("PWM port freed: %d \n", portHandle);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}
}

uint8_t BBB_getPWMValue(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return 0;
	}

	uint8_t val = 0;
	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(pwm->enabled){
#ifdef HAL_USE_IO
			val = pru_pwm_get(pwm);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port read: %d -> %d \n", portHandle, val);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}

	return val;
}
float BBB_getPWMDuty(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return 0.0f;
	}

	uint8_t val = BBB_getPWMValue(portHandle);
	return HAL_PWMSS_VALUE_TO_DUTY(val);
}

void BBB_setPWMValue(hal_handle_t portHandle, uint8_t value){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(pwm->enabled){
			bbb_pwm_limit(&value);
#ifdef HAL_USE_IO
			pru_pwm_set(pwm, value);
#endif

#ifdef HAL_BBB_DEBUG
			printf("PWM port set: %d -> %d \n", portHandle, value);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}
}
void BBB_setPWMDuty(hal_handle_t portHandle, float duty){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	BBB_setPWMValue(portHandle, (uint8_t)HAL_PWMSS_VALUE_TO_DUTY(duty));
}

void BBB_setPWMFrequency(hal_handle_t portHandle, float frequency){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return;
	}

	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(pwm->enabled){
#ifdef HAL_USE_IO
			pru_pwm_frequency_set(portHandle, frequency);
#endif
			pwm->frequency = frequency;

#ifdef HAL_BBB_DEBUG
			printf("PWM port set: %d -> %d \n", portHandle, value);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}
}
float BBB_getPWMFrequency(hal_handle_t portHandle){
	if(!init){
#ifdef HAL_BBB_DEBUG
		printf("HAL not initialized \n");
#endif
		return 0.0f;
	}

	float val = 0;
	if(portHandle >= 0 && portHandle < HAL_PWMSS_PORTS_COUNT){
		pthread_mutex_lock(&io_mutex);

		pwm_port_t* pwm = &pwm_ports[portHandle];
		if(pwm->enabled){
			val = pwm->frequency;

#ifdef HAL_BBB_DEBUG
			printf("PWM port read: %d -> %d \n", portHandle, val);
#endif
		}else{
#ifdef HAL_BBB_DEBUG
			printf("PWM port not initialized: %d \n", portHandle);
#endif
		}

		pthread_mutex_unlock(&io_mutex);
	}
	else{
#ifdef HAL_BBB_DEBUG
		printf("PWM port invalid: %d \n", portHandle);
#endif
	}

	return val;
}

} /* namespace hal */

} /* namespace flashlib */
