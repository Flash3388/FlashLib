/*
 * analog.h
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */

#ifndef HAL_ANALOG_H_
#define HAL_ANALOG_H_

#include <stdint.h>
#include "haltypes.h"

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

hal_handle_t HAL_initializeAnalogInputPort(uint8_t port);
void HAL_freeAnalogInputPort(hal_handle_t portHandle);

hal_handle_t HAL_initializeAnalogOutputPort(uint8_t port);
void HAL_freeAnalogOutputPort(hal_handle_t portHandle);

/***********************************************************************\
 * Analog data io
\***********************************************************************/

void HAL_setAnalogValue(hal_handle_t portHandle, uint32_t value);
void HAL_setAnalogVoltage(hal_handle_t portHandle, float volts);

float HAL_getAnalogVoltage(hal_handle_t portHandle);
uint32_t HAL_getAnalogValue(hal_handle_t portHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* ANALOG_H_ */
