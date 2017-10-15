/*
 * PWM.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <stdio.h>
#include <PWM.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Port validation
\***********************************************************************/

bool HAL_checkPWMPortValid(int8_t port){
	printf("PWM VALID: %d \n", port);
	return true;
}
bool HAL_checkPWMPortTaken(int8_t port){
	printf("PWM TAKEN: %d \n", port);
	return false;
}

/***********************************************************************\
 * PWM initialization
\***********************************************************************/

hal_handle_t HAL_initializePWMPort(uint8_t port){
	printf("INIT PWM: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freePWMPort(hal_handle_t portHandle){
	printf("FREE PWM: %d \n", portHandle);
}

/***********************************************************************\
 * PWM data
\***********************************************************************/

void HAL_setPWMDuty(hal_handle_t portHandle, float dutyCycle){
	printf("SET PWM DUTY: %d, %f \n", portHandle, dutyCycle);
}
float HAL_getPWMDuty(hal_handle_t portHandle){
	printf("GET PWM DUTY: %d \n", portHandle);
	return 0.0f;
}

void HAL_setPWMValue(hal_handle_t portHandle, uint8_t value){
	printf("SET PWM VALUE: %d, %d \n", portHandle, value);
}
uint8_t HAL_getPWMValue(hal_handle_t portHandle){
	printf("GET PWM VALUE: %d \n", portHandle);
	return 0;
}

void HAL_setPWMFrequency(hal_handle_t portHandle, float frequency){
	printf("SET PWM FREQ: %d, %f \n", portHandle, frequency);
}
float HAL_getPWMFrequency(hal_handle_t portHandle){
	printf("GET PWM FREQ: %d \n", portHandle);
	return 0.0f;
}


} /* namespace hal */

} /* namespace flashlib */

