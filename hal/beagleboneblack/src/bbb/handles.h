/*
 * handles.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_HANDLES_H_
#define BBB_HANDLES_H_

#include <stdint.h>
#include "hal_defines.h"
#include <haltypes.h>

typedef struct dio_port{
	uint8_t header;
	uint8_t pin;

	uint8_t dir;
	uint8_t val;
} dio_port_t;

typedef struct dio_pulse{
	int32_t remaining_time;
} dio_pulse_t;

typedef struct pwm_port{
	uint8_t enabledA = 0;
	uint8_t enabledB = 0;

	float frequency = 0.0f;
	float dutyA = 0.0f;
	float dutyB = 0.0f;
} pwm_port_t;

typedef struct adc_port{
	uint8_t enabled = 0;
	uint32_t value = 0;

	unsigned int sample_buffer[HAL_AIN_SAMPLING_SIZE];
} adc_port_t;

typedef struct pulse_counter{
	hal_handle_t dio_port;

	uint8_t last_value;

	uint32_t count;
	uint32_t period;

	bool release;
} pulse_counter_t;

#endif /* BBB_HANDLES_H_ */
