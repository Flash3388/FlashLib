/*
 * PWM.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <PWM.h>
#include <platformexception.h>
#include "../../include/hal.h"

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePWMPort(uint8_t port){
	throw platform_exception();
	//return BBB_initializePWMPort(port);
}
void HAL_freePWMPort(hal_handle_t portHandle){
	throw platform_exception();
	//BBB_freePWMPort(portHandle);
}

void HAL_setPWMDuty(hal_handle_t portHandle, float dutyCycle){
	throw platform_exception();
	//BBB_setPWMDuty(portHandle, dutyCycle);
}
float HAL_getPWMDuty(hal_handle_t portHandle){
	throw platform_exception();
	//return BBB_getPWMDuty(portHandle);
}

void HAL_setPWMValue(hal_handle_t portHandle, uint8_t value){
	throw platform_exception();
	//BBB_setPWMValue(portHandle, value);
}
uint8_t HAL_getPWMValue(hal_handle_t portHandle){
	throw platform_exception();
	//return BBB_getPWMValue(portHandle);
}


} /* namespace hal */

} /* namespace flashlib */

