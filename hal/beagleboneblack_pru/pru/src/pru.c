/*
 * pru.c
 *
 *  Created on: Sep 18, 2017
 *      Author: root
 */

#include "pru.h"

#include "handles.h"

#include "pru_defines.h"
#include "bbb_defines.h"
#include "hal_defines.h"

#include "gpio.h"
#include "pwm.h"
#include "adc.h"

#include <time.h>

dio_port_t dio_ports[BBB_GPIO_PORTS_COUNT];
adc_port_t adc_ports[BBB_ADC_CHANNEL_COUNT];
pwm_port_t pwm_ports[BBB_PWMSS_MODULE_COUNT];

unsigned long adc_last_update;
unsigned long dio_last_update;

unsigned long pru_clock_us(){
	return clock();
}


int pru_initialize(){
	int i, j;

	//GPIO initialize
	gpio_initialize();

	for(i = 0; i < BBB_GPIO_PORTS_COUNT; ++i){
		dio_port_t* dio = &dio_ports[i];
		dio->enabled = 0;
		dio->value = 0;
		dio->dir = 0;

		dio->header = BBB_GPIO_PORT_TO_HEADER(i);
		dio->pin = BBB_GPIO_PORT_TO_PIN(i);
	}

	//ADC initialize
	adc_initialize();

	for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		adc_port_t* adc = &adc_ports[i];
		adc->enabled = 0;
		adc->value = 0;
	}

	//PWM initialize
	pwm_initialize();

	for(i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port_t* pwm = &pwm_ports[i];

		for(j = 0; j < BBB_PWMSS_PORT_COUNT; ++j){
			pwm->enabled[j] = 0;
			pwm->value[j] = 0;
		}
	}

	return 0;
}
void pru_shutdown(){

	//PWM shutdown
	pwm_free();

	//ADC shutdown
	adc_free();

	//GPIO shutdown
	gpio_free();
}

void pru_handles_update(unsigned int* shared_memory){
	int i, j;
	unsigned long timepasssed = 0;

	//update dio
	timepasssed = clock() - dio_last_update;

	for(i = 0; i < BBB_GPIO_PORTS_COUNT; ++i){
		dio_port_t* dio = &dio_ports[i];

		if(dio->enabled){
			if(dio->dir == BBB_DIR_INPUT){
				char value = 0;//TODO: READ GPIO VALUE

				if(value != dio->value){
					dio->value = value;
					shared_memory[PRU_MEM_DIO_OFFSET + i] = value;
				}
			}
			else{
				char value = shared_memory[PRU_MEM_DIO_OFFSET + i];

				if(value != dio->value){
					dio->value = value;

					if(value == BBB_GPIO_HIGH)
						gpio_sethigh(dio->header, dio->pin);
					else
						gpio_setlow(dio->header, dio->pin);

					if(dio->pulse_enabled && value == BBB_GPIO_LOW){
						dio->pulse_enabled = 0;
						dio->pulse_length = 0;
					}
				}

				if(dio->pulse_enabled){
					//TODO: CHECK TIME PASSED AND UPDATE
					if(dio->pulse_length <= timepasssed)
						dio->pulse_length = 0;
					else dio->pulse_length -= timepasssed;

					if(dio->pulse_length == 0){
						gpio_setlow(dio->header, dio->pin);
						dio->pulse_enabled = 0;
					}
				}
			}
		}
	}
	dio_last_update = pru_clock_us();

	//update adc
	timepasssed = pru_clock_us() - adc_last_update;
	if(timepasssed >= HAL_AIN_SMAPLING_RATE){
		for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
			adc_port_t* adc = &adc_ports[i];

			if(adc->enabled){
				adc_channel_update(i, adc->value_buffer, HAL_AIN_SAMPLING_SIZE);
			}
		}

		adc_last_update = pru_clock_us();
	}

	//update pwm
	for(i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port_t* pwm = &pwm_ports[i];

		char changed = 0;

		for(j = 0; j < BBB_PWMSS_PORT_COUNT; ++j){
			if(pwm->enabled[j]){
				char value = shared_memory[PRU_MEM_PWM_OFFSET + i + j];

				if(value != pwm->value[j]){
					pwm->value[j] = value;
					changed = 1;
				}
			}
		}

		if(changed){
			pwm_module_settings(i, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);
		}
	}
}

short pru_dio_initialize(short port, char dir){

	if(port > 0 && port < BBB_GPIO_PORTS_COUNT){
		--port;//decrease to match our actual counting which is from 0
		dio_port_t* dio = &dio_ports[port];

		if(!dio->enabled){
			if(gpio_module_get(dio->header, dio->pin) >= 0){
				dio->dir = dir;
				dio->enabled = 1;
				dio->value = 0;
				dio->pulse_enabled = 0;
				dio->pulse_length = 0;

				gpio_setdir(dio->header, dio->pin, dir);

				if(dir == BBB_DIR_OUTPUT){
					gpio_setlow(dio->header, dio->pin);
				}
			}else{
				port = -1;
			}
		}
		else if(dio->dir != dir){
			port = -1;
		}
	}else{
		port = -1;
	}

	return port;
}
void pru_dio_free(short handle){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = &dio_ports[handle];

		if(dio->enabled){
			dio->enabled = 0;

			if(dio->dir == BBB_DIR_OUTPUT){
				gpio_setlow(dio->header, dio->pin);
			}
		}
	}
}
void pru_dio_set(short handle, char value){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = &dio_ports[handle];

		if(dio->enabled && dio->dir == BBB_DIR_OUTPUT){
			dio->value = value;

			if(value == BBB_GPIO_HIGH)
				gpio_sethigh(dio->header, dio->pin);
			else
				gpio_setlow(dio->header, dio->pin);
		}
	}
}
char pru_dio_get(short handle){
	char value = 0;
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = &dio_ports[handle];

		if(dio->enabled){
			if(dio->dir == BBB_DIR_INPUT){
				value = gpio_ishigh(dio->header, dio->pin);
			}
			else{
				value = dio->value;
			}
		}
	}

	return value;
}
void pru_dio_pulse(short handle, unsigned int length){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = &dio_ports[handle];

		if(dio->enabled && dio->dir == BBB_DIR_OUTPUT){
			if(dio->pulse_enabled){
				dio->pulse_length += length;
			}
			else{
				dio->pulse_length = length;
				dio->pulse_enabled = 1;

				gpio_sethigh(dio->header, dio->pin);
				dio->value = 1;
			}
		}
	}
}


short pru_adc_initialize(short channel){
	if(channel >= 0 && channel < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = &adc_ports[channel];

		if(!adc->enabled){
			adc->enabled = 1;
			adc->value = 0;

			adc_channel_enable(channel);
		}
	}else{
		channel = -1;
	}

	return channel;
}
void pru_adc_free(short handle){
	if(handle >= 0 && handle < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = &adc_ports[handle];

		if(adc->enabled){
			adc->enabled = 0;
			adc->value = 0;

			adc_channel_disable(handle);
		}
	}
}
unsigned int pru_adc_get(short handle){
	unsigned int value = 0;
	if(handle >= 0 && handle < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = &adc_ports[handle];

		if(adc->enabled){
			value = adc->value;
		}
	}
	return value;
}


short pru_pwm_initialize(short port){
	if(port >= 0 && port < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(port);
		pwm_port_t* pwm = &pwm_ports[module];

		if(!pwm->enabled[0] && !pwm->enabled[1]){
			pwm_module_enable(module);
		}

		char pin = BBB_PWMSS_PORT_TO_PIN(port);

		pwm->enabled[pin] = 1;
		pwm->value[pin] = 0;

		pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);

	}else{
		port = -1;
	}

	return port;
}
void pru_pwm_free(short handle){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = &pwm_ports[module];

		char pin = BBB_PWMSS_PORT_TO_PIN(handle);

		pwm->enabled[pin] = 0;
		pwm->value[pin] = 0;

		pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);

		if(!pwm->enabled[0] && !pwm->enabled[1]){
			pwm_module_disable(module);
		}
	}
}
void pru_pwm_set(short handle, char value){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = &pwm_ports[module];

		char pin = BBB_PWMSS_PORT_TO_PIN(handle);

		if(pwm->enabled[pin]){
			pwm->value[pin] = value;
			pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);
		}
	}
}
char pru_pwm_get(short handle){
	char value = 0;
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = &pwm_ports[module];

		char pin = BBB_PWMSS_PORT_TO_PIN(handle);

		if(pwm->enabled[pin]){
			value = pwm->value[pin];
		}
	}
	return value;
}
void pru_pwm_frequency_set(short handle, unsigned char clkdiv, unsigned char hspclkdiv){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = &pwm_ports[module];

		char pin = BBB_PWMSS_PORT_TO_PIN(handle);

		if(pwm->enabled[pin]){
			pwm->clkdiv = clkdiv;
			pwm->hspclkdiv = hspclkdiv;
			pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);
		}
	}
}
