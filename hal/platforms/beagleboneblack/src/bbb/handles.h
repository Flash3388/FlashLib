/*
 * handles.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_HANDLES_H_
#define BBB_HANDLES_H_

#include <stdint.h>

typedef struct dio_port{
	uint8_t header;
	uint8_t pin;
} dio_port_t;

typedef struct dio_pulse{
	dio_port_t dio_handle;
	int32_t remaining_time;
} dio_pulse_t;

typedef struct pwm_port{
	int8_t module = -1;
	int8_t port = -1;
} pwm_port_t;

typedef struct adc_port{

} adc_port_t;

#endif /* BBB_HANDLES_H_ */
