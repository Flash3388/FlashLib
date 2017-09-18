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

short pru_dio_initialize(short port, char dir);
void pru_dio_free(short handle);
void pru_dio_set(short handle, char value);
char pru_dio_get(short handle);
void pru_dio_pulse(short handle, unsigned int length);

short pru_adc_initialize(short channel);
void pru_adc_free(short handle);
unsigned int pru_adc_get(short handle);

short pru_pwm_initialize(short port);
void pru_pwm_free(short handle);
void pru_pwm_set(short handle, char value);
char pru_pwm_get(short handle);
void pru_pwm_frequency_set(short handle, unsigned int frequency);
unsigned int pru_pwm_frequency_get(short handle);

#endif /* PRU_H_ */
