/*
 * handles.h
 *
 *  Created on: Aug 23, 2017
 *      Author: root
 */

#ifndef PRU_HANDLES_H_
#define PRU_HANDLES_H_

#include <stdint.h>

typedef struct DIO_handle{
	uint8_t base;
	uint8_t pin;
	uint8_t dir;
	uint8_t value;
} DIO_handle;

typedef struct PWM_handle{
	uint8_t base;
	uint8_t pin;
	uint8_t value;
} PWM_handle;

typedef struct ADC_handle{
	uint8_t base;
	uint8_t pin;
	uint32_t value;
} ADC_handle;


#endif /* PRU_HANDLES_H_ */
