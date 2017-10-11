/*
 * Analog.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <Analog.h>
#include <platformexception.h>
#include <hal.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Analog data
\***********************************************************************/

float HAL_getGlobalAnalogSampleRate(){
	return BBB_getGlobalAnalogSampleRate();
}
float HAL_getAnalogMaxVoltage(){
	return BBB_getAnalogMaxVoltage();
}
uint32_t HAL_getAnalogMaxValue(){
	return BBB_getAnalogMaxValue();
}

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

hal_handle_t HAL_initializeAnalogInputPort(int8_t port){
	return BBB_initializeAnalogInput(port);
}
void HAL_freeAnalogInputPort(hal_handle_t portHandle){
	BBB_freeAnalogInput(portHandle);
}

hal_handle_t HAL_initializeAnalogOutputPort(int8_t port){
	throw platform_exception();
}
void HAL_freeAnalogOutputPort(hal_handle_t portHandle){
	throw platform_exception();
}

/***********************************************************************\
 * Analog data io
\***********************************************************************/

void HAL_setAnalogValue(hal_handle_t portHandle, uint32_t value){
	throw platform_exception();
}
void HAL_setAnalogVoltage(hal_handle_t portHandle, float volts){
	throw platform_exception();
}

float HAL_getAnalogVoltage(hal_handle_t portHandle){
	return BBB_getAnalogVoltage(portHandle);
}
uint32_t HAL_getAnalogValue(hal_handle_t portHandle){
	return BBB_getAnalogValue(portHandle);
}

float HAL_convertAnalogValueToVoltage(uint32_t value){
	return BBB_convertAnalogValueToVoltage(value);
}
uint32_t HAL_convertAnalogVoltageToValue(float voltage){
	return BBB_convertAnalogVoltageToValue(voltage);
}

/***********************************************************************\
 * Analog accumulator
\***********************************************************************/

int HAL_enableAnalogInputAccumulator(hal_handle_t portHandle){
	return BBB_enableAnalogInputAccumulator(portHandle, true);
}
int HAL_disableAnalogInputAccumulator(hal_handle_t portHandle){
	return BBB_enableAnalogInputAccumulator(portHandle, false);
}
void HAL_resetAnalogInputAccumulator(hal_handle_t portHandle){
	BBB_resetAnalogInputAccumulator(portHandle);
}
void HAL_setAnalogInputAccumulatorCenter(hal_handle_t portHandle, uint32_t center){
	BBB_setAnalogInputAccumulatorCenter(portHandle, center);
}
int64_t HAL_getAnalogInputAccumulatorValue(hal_handle_t portHandle){
	return BBB_getAnalogInputAccumulatorValue(portHandle);
}
uint32_t HAL_getAnalogInputAccumulatorCount(hal_handle_t portHandle){
	return BBB_getAnalogInputAccumulatorCount(portHandle);
}

} /* namespace hal */

} /* namespace flashlib */
