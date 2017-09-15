/*
 * Analog.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <Analog.h>
#include <platformexception.h>
#include "../bbb/hal.h"

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

hal_handle_t HAL_initializeAnalogInputPort(uint8_t port){
	return BBB_initializeAnalogInput(port);
}
void HAL_freeAnalogInputPort(hal_handle_t portHandle){
	BBB_freeAnalogInput(portHandle);
}

hal_handle_t HAL_initializeAnalogOutputPort(uint8_t port){
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

} /* namespace hal */

} /* namespace flashlib */
