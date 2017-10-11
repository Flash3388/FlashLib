/*
 * PWM.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <PWM.h>
#include <hal.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Port validation
\***********************************************************************/

bool HAL_checkPWMPortValid(int8_t port){
	return BBB_checkPWMPortValid(port);
}
bool HAL_checkPWMPortTaken(int8_t port){
	return BBB_checkPWMPortTaken(port);
}

/***********************************************************************\
 * PWM initialization
\***********************************************************************/

hal_handle_t HAL_initializePWMPort(int8_t port){
	return BBB_initializePWMPort(port);
}
void HAL_freePWMPort(hal_handle_t portHandle){
	BBB_freePWMPort(portHandle);
}

/***********************************************************************\
 * PWM data
\***********************************************************************/

void HAL_setPWMDuty(hal_handle_t portHandle, float dutyCycle){
	BBB_setPWMDuty(portHandle, dutyCycle);
}
float HAL_getPWMDuty(hal_handle_t portHandle){
	return BBB_getPWMDuty(portHandle);
}

void HAL_setPWMValue(hal_handle_t portHandle, uint8_t value){
	BBB_setPWMValue(portHandle, value);
}
uint8_t HAL_getPWMValue(hal_handle_t portHandle){
	return BBB_getPWMValue(portHandle);
}

void HAL_setPWMFrequency(hal_handle_t portHandle, float frequency){
	BBB_setPWMFrequency(portHandle, frequency);
}
float HAL_getPWMFrequency(hal_handle_t portHandle){
	return BBB_getPWMFrequency(portHandle);
}


} /* namespace hal */

} /* namespace flashlib */

