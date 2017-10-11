/*
 * handles.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_HANDLES_H_
#define BBB_HANDLES_H_

#include <stdint.h>
#include <haltypes.h>
#include <pthread.h>
#include <memory>
#include <sys/epoll.h>

#include "hal_defines.h"

typedef struct dio_pulse{
	hal_handle_t dio_handle;
	int32_t remaining_time;
} dio_pulse_t;

typedef struct dio_port{
	uint8_t header;
	uint8_t pin;

	uint8_t dir;
	uint8_t val;

	std::shared_ptr<dio_pulse_t> pulse;
	bool pulsing;
} dio_port_t;

typedef struct pwm_port{
	bool enabledA;
	bool enabledB;

	float frequency;
	float dutyA;
	float dutyB;
} pwm_port_t;

typedef struct adc_accumulator{
	int64_t value;
	uint32_t count;
	uint32_t center;
} adc_accumulator_t;
typedef struct adc_port{
	bool enabled;
	uint32_t value;

	unsigned int sample_buffer[HAL_AIN_SAMPLING_SIZE];

	bool accumulator_enabled;
	adc_accumulator_t accumulator;
} adc_port_t;

typedef struct pulse_counter{
	hal_handle_t up_port;
	hal_handle_t down_port;

	uint32_t count;
	uint32_t period;
	uint32_t length;
	uint8_t direction;

	bool quadrature;

	pthread_t pthread;
	pthread_mutex_t mutex;

	int epoll_fd;

	bool release;
} pulse_counter_t;

typedef std::shared_ptr<dio_port_t> dio_port_shared_ptr;
typedef std::shared_ptr<dio_pulse_t> dio_pulse_shared_ptr;
typedef std::shared_ptr<pulse_counter_t> pulse_counter_shared_ptr;

#endif /* BBB_HANDLES_H_ */
