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
 * Analog data
\***********************************************************************/

float HAL_getGlobalAnalogSampleRate();
float HAL_getAnalogMaxVoltage();
uint32_t HAL_getAnalogMaxValue();

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

hal_handle_t HAL_initializeAnalogInputPort(int8_t port);
void HAL_freeAnalogInputPort(hal_handle_t portHandle);

hal_handle_t HAL_initializeAnalogOutputPort(int8_t port);
void HAL_freeAnalogOutputPort(hal_handle_t portHandle);

/***********************************************************************\
 * Analog data io
\***********************************************************************/

void HAL_setAnalogValue(hal_handle_t portHandle, uint32_t value);
void HAL_setAnalogVoltage(hal_handle_t portHandle, float volts);

float HAL_getAnalogVoltage(hal_handle_t portHandle);
uint32_t HAL_getAnalogValue(hal_handle_t portHandle);

float HAL_convertAnalogValueToVoltage(uint32_t value);
uint32_t HAL_convertAnalogVoltageToValue(float voltage);

/***********************************************************************\
 * Analog accumulator
\***********************************************************************/

int HAL_enableAnalogInputAccumulator(hal_handle_t portHandle);
int HAL_disableAnalogInputAccumulator(hal_handle_t portHandle);
void HAL_resetAnalogInputAccumulator(hal_handle_t portHandle);
void HAL_setAnalogInputAccumulatorCenter(hal_handle_t portHandle, uint32_t center);
int64_t HAL_getAnalogInputAccumulatorValue(hal_handle_t portHandle);
uint32_t HAL_getAnalogInputAccumulatorCount(hal_handle_t portHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* ANALOG_H_ */
