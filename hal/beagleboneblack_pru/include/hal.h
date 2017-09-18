/*
 * hal.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_HAL_H_
#define BBB_HAL_H_

#include <stdint.h>
#include <haltypes.h>

namespace flashlib{

namespace hal{

#define WATCHDOG_PRU_NOT_RESPONSIVE   (1)
#define WATCHDOG_PRU_UPDATE_ERROR     (2)


int BBB_initialize(int mode);
void BBB_shutdown();

/***********************************************************************\
 * DIO
\***********************************************************************/

hal_handle_t BBB_initializeDIOPort(int16_t port, uint8_t dir);
void BBB_freeDIOPort(hal_handle_t portHandle);

void BBB_setDIO(hal_handle_t portHandle, uint8_t high);
void BBB_pulseDIO(hal_handle_t portHandle, float length);

uint8_t BBB_getDIO(hal_handle_t portHandle);

/***********************************************************************\
 * ANALOG
\***********************************************************************/

hal_handle_t BBB_initializeAnalogInput(int16_t port);
void BBB_freeAnalogInput(hal_handle_t portHandle);

uint32_t BBB_getAnalogValue(hal_handle_t portHandle);
float BBB_getAnalogVoltage(hal_handle_t portHandle);

/***********************************************************************\
 * PWM
\***********************************************************************/

hal_handle_t BBB_initializePWMPort(int16_t port);
void BBB_freePWMPort(hal_handle_t portHandle);

uint8_t BBB_getPWMValue(hal_handle_t portHandle);
float BBB_getPWMDuty(hal_handle_t portHandle);

void BBB_setPWMValue(hal_handle_t portHandle, uint8_t value);
void BBB_setPWMDuty(hal_handle_t portHandle, float duty);

void BBB_setPWMFrequency(hal_handle_t portHandle, float frequency);
float BBB_getPWMFrequency(hal_handle_t portHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* BBB_HAL_H_ */