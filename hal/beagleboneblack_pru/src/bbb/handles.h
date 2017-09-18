/*
 * handles.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef BBB_HANDLES_H_
#define BBB_HANDLES_H_

#include <stdint.h>

typedef struct dio_port{
	uint8_t dir;

	uint32_t val_addr_offset;
} dio_port_t;

typedef struct pwm_port{
	bool enabled = false;

	float frequency = 0.0f;
	uint32_t val_addr_offset;
} pwm_port_t;

typedef struct adc_port{
	bool enabled = false;

	uint32_t val_addr_offset;
} adc_port_t;

#endif /* BBB_HANDLES_H_ */
