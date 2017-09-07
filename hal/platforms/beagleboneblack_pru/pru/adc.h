/*
 * adc.h
 *
 *  Created on: Jun 16, 2017
 *      Author: root
 */

#ifndef ADC_H_
#define ADC_H_

#include "handles.h"


extern void ADC_initialize();
extern void ADC_shutdown();

extern void ADC_channel_enable(uint8_t channel);
extern void ADC_channel_disable(uint8_t channel);

extern void ADC_module_ctrl(uint8_t type, uint8_t clkdiv);

extern uint32_t ADC_read(uint8_t channel);

#endif /* ADC_H_ */
