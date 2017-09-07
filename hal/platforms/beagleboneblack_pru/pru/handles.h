/*
 * handles.h
 *
 *  Created on: Jun 14, 2017
 *      Author: root
 */

#ifndef PRU_HANDLES_H_
#define PRU_HANDLES_H_

#include "types.h"

typedef struct dio_handle{
	uint8_t base;
	uint8_t pin;

	uint8_t value;
	uint8_t dir;
} dio_handle_t;

typedef struct pwm_handle{
	uint8_t base;
	uint8_t pin;

	uint8_t value;
} pwm_handle_t;

typedef struct adc_handle{
	uint8_t base;
	uint8_t pin;

	uint32_t value;
	uint32_t value_buffer[8];
} adc_handle_t;

#endif /* PRU_HANDLES_H_ */
