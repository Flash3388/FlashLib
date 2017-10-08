/*
 * pru.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef PRU_H_
#define PRU_H_

unsigned long pru_clock_us();

int pru_initialize();
void pru_shutdown();

void pru_handles_update(unsigned int* shared_memory);

signed short pru_dio_initialize(signed short port, signed short module, signed short pin, char dir);
void pru_dio_free(signed short handle);
void pru_dio_set(signed short handle, char value);
char pru_dio_get(signed short handle);
void pru_dio_pulse(signed short handle, unsigned int length);

signed short pru_adc_initialize(signed short channel);
void pru_adc_free(signed short handle);
unsigned int pru_adc_get(signed short handle);

signed short pru_pwm_initialize(signed short port);
void pru_pwm_free(signed short handle);
void pru_pwm_set(signed short handle, char value);
char pru_pwm_get(signed short handle);
void pru_pwm_frequency_set(signed short handle, unsigned char clkdiv, unsigned char hspclkdiv);

signed short pru_counter_initialize(signed short port);
void pru_counter_free(signed short handle);
void pru_counter_reset(signed short handle);
unsigned int pru_counter_count(signed short handle);
unsigned int pru_counter_period(signed short handle);
unsigned int pru_counter_length(signed short handle);

#endif /* PRU_H_ */
