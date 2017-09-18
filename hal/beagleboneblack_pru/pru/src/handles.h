/*
 * handles.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef HANDLES_H_
#define HANDLES_H_

#include "hal_defines.h"

typedef struct dio_port{
	char enabled = 0;

	char header;
	char pin;

	char dir;
	char value;

	char pulse_enabled = 0;
	unsigned int pulse_length = 0;
} dio_port_t;

typedef struct adc_port{
	char enabled = 0;

	unsigned int value_buffer[HAL_AIN_SAMPLING_SIZE];
	unsigned int value;
} adc_port_t;

typedef struct pwm_port{
	char enabled[2];
	char value[2];

	unsigned char clkdiv;
	unsigned char hspclkdiv;
} pwm_port_t;

#endif /* HANDLES_H_ */
