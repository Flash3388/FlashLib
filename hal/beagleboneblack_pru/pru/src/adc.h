/*
 * adc.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef ADC_H_
#define ADC_H_

#define BBB_ADCTSC_DATA(n)        (n & 0xfff)
#define BBB_ADCTSC_CHANNELID(n)   ((n >> 16) & 0xf)

void adc_initialize();
void adc_free();

void adc_channel_enable(char channel);
void adc_channel_disable(char channel);

unsigned int adc_fifo_data_count(char fifo);
unsigned int adc_fifo_data_read(char fifo);

#endif /* ADC_H_ */
