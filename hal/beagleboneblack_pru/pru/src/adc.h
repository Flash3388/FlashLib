/*
 * adc.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef ADC_H_
#define ADC_H_

void adc_initialize();
void adc_free();

void adc_channel_enable(char channel);
void adc_channel_disable(char channel);

void adc_channel_update(char channel, unsigned int buffer[], unsigned int samples);

#endif /* ADC_H_ */
