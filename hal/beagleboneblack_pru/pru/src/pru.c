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
//#include "pwm.h"
//#include "adc.h"

#include <time.h>
#include <stdlib.h>

dio_port_t* dio_ports[BBB_GPIO_PORTS_COUNT];
//adc_port_t* adc_ports[BBB_ADC_CHANNEL_COUNT];
//pwm_port_t* pwm_ports[BBB_PWMSS_MODULE_COUNT];

//unsigned long adc_last_update = 0;
unsigned long dio_last_update = 0;

unsigned long pru_clock_us(){
	return clock();
}


int pru_initialize(){
	//int i, j;

	//adc_port_t* adc_port;
	//dio_port_t* dio_port;
	//pwm_port_t* pwm_port;

	//GPIO initialize
	gpio_initialize();

	/*for(i = 0; i < BBB_GPIO_PORTS_COUNT; ++i){
		dio_port = &dio_ports[i];
		dio_port->enabled = 0;
		dio_port->value = 0;
		dio_port->dir = 0;

		dio_port->header = BBB_GPIO_PORT_TO_HEADER(i);
		dio_port->pin = BBB_GPIO_PORT_TO_PIN(i);
	}*/

	//ADC initialize
	//adc_initialize();

	/*for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		adc_port = &adc_ports[i];
		adc_port->enabled = 0;
		adc_port->value = 0;
		adc_port->buffer_sample_count = 0;
	}*/

	//PWM initialize
	//pwm_initialize();

	/*for(i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port = &pwm_ports[i];

		for(j = 0; j < BBB_PWMSS_PORT_COUNT; ++j){
			pwm_port->enabled[j] = 0;
			pwm_port->value[j] = 0;
		}
	}*/

	return 0;
}
void pru_shutdown(){
	int i;

	for(i = 0; i < BBB_GPIO_PORTS_COUNT; ++i){
		pru_dio_free(i);
	}
	/*for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
		pru_adc_free(i);
	}
	for(i = 0; i < HAL_PWMSS_PORTS_COUNT; ++i){
		pru_pwm_free(i);
	}*/

	//PWM shutdown
	//pwm_free();

	//ADC shutdown
	//adc_free();

	//GPIO shutdown
	gpio_free();
}

void pru_handles_update(volatile unsigned int* shared_memory){
	int i;
	unsigned long timepassed = 0;

	//unsigned int adc_fifo_count;
	//unsigned int adc_fifo_data;

	dio_port_t* dio_port;
	//adc_port_t* adc_port;
	//pwm_port_t* pwm_port;

	//update dio
	timepassed = pru_clock_us() - dio_last_update;
	for(i = 0; i < BBB_GPIO_PORTS_COUNT; ++i){
		dio_port = dio_ports[i];

		if(dio_port != NULL){
			if(dio_port->dir == BBB_DIR_INPUT){
				char value = gpio_ishigh(dio_port->header, dio_port->pin);

				if(value != dio_port->value){
				    if(dio_port->counter != NULL){
						if(value == BBB_GPIO_HIGH){
							//TODO: RISING
							dio_port->counter->count++;

							unsigned long time = pru_clock_us();

							//shared_memory[PRU_MEM_COUNTER_OFFSET + (i * 3)] = dio_port->counter->count;
							//shared_memory[PRU_MEM_COUNTER_OFFSET + (i * 3) + 1] = (time - dio_port->counter->start_time);

							dio_port->counter->start_time = time;
						}else{
							//TODO: FALLING

							unsigned long time = pru_clock_us();

							//shared_memory[PRU_MEM_COUNTER_OFFSET + (i * 3) + 2] = (time - dio_port->counter->start_time);

							dio_port->counter->start_time = time;
						}
					}

					dio_port->value = value;
					shared_memory[PRU_MEM_DIO_OFFSET + i] = value;
				}
			}
			else{
				char value = shared_memory[PRU_MEM_DIO_OFFSET + i];

				if(value != dio_port->value){
					dio_port->value = value;

					if(value == BBB_GPIO_HIGH)
						gpio_sethigh(dio_port->header, dio_port->pin);
					else
						gpio_setlow(dio_port->header, dio_port->pin);

					if(dio_port->pulse != NULL && value == BBB_GPIO_LOW){
						free(dio_port->pulse);
					}
				}

				if(dio_port->pulse != NULL){
					if(dio_port->pulse->pulse_length <= timepassed)
						dio_port->pulse->pulse_length = 0;
					else
						dio_port->pulse->pulse_length -= timepassed;

					if(dio_port->pulse->pulse_length == 0){
						dio_port->value = BBB_GPIO_LOW;
						shared_memory[PRU_MEM_DIO_OFFSET + i] = BBB_GPIO_LOW;

						gpio_setlow(dio_port->header, dio_port->pin);

						free(dio_port->pulse);
					}
				}
			}
		}
	}
	dio_last_update = pru_clock_us();

	//update adc
	/*timepassed = pru_clock_us() - adc_last_update;
	if(timepassed >= HAL_AIN_SMAPLING_RATE){
		for(i = 0; i < 2; ++i){
			adc_fifo_count = adc_fifo_data_count(i);
			if(adc_fifo_count > 0){
				for(j = 0; j < adc_fifo_count; ++j){
					adc_fifo_data = adc_fifo_data_read(i);
					adc_port = adc_ports[BBB_ADCTSC_CHANNELID(adc_fifo_data)];

					if(adc_port != NULL && adc_port->buffer_sample_count < HAL_AIN_SAMPLING_SIZE){
						adc_port->value_buffer[adc_port->buffer_sample_count++] = BBB_ADCTSC_DATA(adc_fifo_data);
					}
				}
			}
		}
		for(i = 0; i < BBB_ADC_CHANNEL_COUNT; ++i){
			adc_port = adc_ports[i];
			if(adc_port != NULL && adc_port->buffer_sample_count > 0){
				for(j = 0; j < adc_port->buffer_sample_count; ++j){
					adc_port->value += adc_port->value_buffer[j];
				}
				adc_port->value /= adc_port->buffer_sample_count;
				adc_port->buffer_sample_count = 0;

				//shared_memory adc value
			}else if(adc_port->buffer_sample_count > 0){
				adc_port->buffer_sample_count = 0;
			}
		}

		adc_last_update = pru_clock_us();
	}*/

	//update pwm
	/*for(i = 0; i < BBB_PWMSS_MODULE_COUNT; ++i){
		pwm_port = pwm_ports[i];

		if(pwm_port != NULL){
			char changed = 0;

			for(j = 0; j < BBB_PWMSS_PORT_COUNT; ++j){
				if(pwm_port->enabled[j]){
					char value = shared_memory[PRU_MEM_PWM_OFFSET + i + j];

					if(value != pwm_port->value[j]){
						pwm_port->value[j] = value;
						changed = 1;
					}
				}
			}

			if(changed){
				pwm_module_settings(i, pwm_port->clkdiv, pwm_port->hspclkdiv, pwm_port->value[0], pwm_port->value[1]);
			}
		}
	}*/
}

signed short pru_dio_initialize(signed short port, signed short module, signed short pin, char dir){
	if(port > 0 && port < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[port];

		if(dio == NULL){
			dio = (dio_port_t*)malloc(sizeof(dio_port_t));

			dio->header = module;
			dio->pin = pin;

			dio->dir = dir;
			//dio->enabled = 1;
			dio->value = 0;
			dio->pulse = 0;

			gpio_setdir(dio->header, dio->pin, dir);

			if(dir == BBB_DIR_OUTPUT){
				gpio_setlow(dio->header, dio->pin);
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
void pru_dio_free(signed short handle){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL){
			//dio->enabled = 0;

			if(dio->pulse != NULL){
				free(dio->pulse);
			}
			if(dio->counter != NULL){
				free(dio->counter);
			}

			if(dio->dir == BBB_DIR_OUTPUT){
				gpio_setlow(dio->header, dio->pin);
			}

			free(dio);
		}
	}
}
void pru_dio_set(signed short handle, char value){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->dir == BBB_DIR_OUTPUT){
			dio->value = value;

			if(value == BBB_GPIO_HIGH)
				gpio_sethigh(dio->header, dio->pin);
			else{
				gpio_setlow(dio->header, dio->pin);

				if(dio->pulse != NULL){
					free(dio->pulse);
				}
			}
		}
	}
}
char pru_dio_get(signed short handle){
	char value = 0;
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL){
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
void pru_dio_pulse(signed short handle, unsigned int length){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->dir == BBB_DIR_OUTPUT){
			if(dio->pulse != NULL){
				dio->pulse->pulse_length += length;
			}
			else{
				dio->pulse = (dio_pulse_t*)malloc(sizeof(dio_pulse_t));
				dio->pulse->pulse_length = length;

				gpio_sethigh(dio->header, dio->pin);
				dio->value = 1;
			}
		}
	}
}


/*signed short pru_adc_initialize(signed short channel){
	if(channel >= 0 && channel < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = adc_ports[channel];

		if(adc == NULL){
			adc = (adc_port_t*)malloc(sizeof(adc_port_t));
			adc->value = 0;

			adc_channel_enable(channel);
		}
	}else{
		channel = -1;
	}

	return channel;
}
void pru_adc_free(signed short handle){
	if(handle >= 0 && handle < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = adc_ports[handle];

		if(adc != NULL){
			adc_channel_disable(handle);

			free(adc);
		}
	}
}
unsigned int pru_adc_get(signed short handle){
	unsigned int value = 0;
	if(handle >= 0 && handle < BBB_ADC_CHANNEL_COUNT){
		adc_port_t* adc = adc_ports[handle];

		if(adc != NULL){
			value = adc->value;
		}
	}
	return value;
}*/


/*signed short pru_pwm_initialize(signed short port){
	if(port >= 0 && port < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(port);
		pwm_port_t* pwm = pwm_ports[module];

		if(pwm == NULL){
			pwm = (pwm_port_t*)malloc(sizeof(pwm_port_t));
		}

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
void pru_pwm_free(signed short handle){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = pwm_ports[module];

		if(pwm != NULL){
			char pin = BBB_PWMSS_PORT_TO_PIN(handle);

			pwm->enabled[pin] = 0;
			pwm->value[pin] = 0;

			pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);

			if(!pwm->enabled[0] && !pwm->enabled[1]){
				pwm_module_disable(module);
				free(pwm);
			}
		}
	}
}
void pru_pwm_set(signed short handle, char value){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = pwm_ports[module];

		if(pwm != NULL){
			char pin = BBB_PWMSS_PORT_TO_PIN(handle);

			if(pwm->enabled[pin]){
				pwm->value[pin] = value;
				pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);
			}
		}
	}
}
char pru_pwm_get(signed short handle){
	char value = 0;
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = pwm_ports[module];

		if(pwm != NULL){
			char pin = BBB_PWMSS_PORT_TO_PIN(handle);

			if(pwm->enabled[pin]){
				value = pwm->value[pin];
			}
		}
	}
	return value;
}
void pru_pwm_frequency_set(signed short handle, unsigned char clkdiv, unsigned char hspclkdiv){
	if(handle >= 0 && handle < HAL_PWMSS_PORTS_COUNT){
		char module = BBB_PWMSS_PORT_TO_MODULE(handle);
		pwm_port_t* pwm = pwm_ports[module];

		if(pwm != NULL){
			char pin = BBB_PWMSS_PORT_TO_PIN(handle);

			if(pwm->enabled[pin]){
				pwm->clkdiv = clkdiv;
				pwm->hspclkdiv = hspclkdiv;
				pwm_module_settings(module, pwm->clkdiv, pwm->hspclkdiv, pwm->value[0], pwm->value[1]);
			}
		}
	}
}*/

signed short pru_counter_initialize(signed short port){
	if(port > 0 && port < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[port];

		if(port >= 0 && dio != NULL && dio->counter != NULL){
			dio->counter = (pulse_counter_t*)malloc(sizeof(pulse_counter_t));

			dio->counter->count = 0;
			dio->counter->start_time = 0;
		}
	}else{
		port = -1;
	}

	return port;
}
void pru_counter_free(signed short handle){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->counter != NULL){
			free(dio->counter);
		}
	}
}
void pru_counter_reset(signed short handle){
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->counter != NULL){
			dio->counter->count = 0;
		}
	}
}
unsigned int pru_counter_count(signed short handle){
	unsigned int count = 0;
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->counter != NULL){
			count = dio->counter->count;
		}
	}
	return count;
}
unsigned int pru_counter_period(signed short handle){
	unsigned int period = 0;
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->counter != NULL && dio->counter->start_time > 0){
			period = pru_clock_us() - dio->counter->start_time;
		}
	}
	return period;
}
unsigned int pru_counter_length(signed short handle){
	unsigned int length = 0;
	if(handle >= 0 && handle < BBB_GPIO_PORTS_COUNT){
		dio_port_t* dio = dio_ports[handle];

		if(dio != NULL && dio->counter != NULL && dio->counter->start_time > 0){
			length = pru_clock_us() - dio->counter->start_time;
		}
	}
	return length;
}
