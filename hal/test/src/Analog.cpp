/*
 * Analog.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <stdio.h>
#include <Analog.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

hal_handle_t HAL_initializeAnalogInputPort(uint8_t port){
	printf("INIT ANALOG INPUT: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freeAnalogInputPort(hal_handle_t portHandle){
	printf("FREE ANALOG INPUT: %d \n", portHandle);
}

hal_handle_t HAL_initializeAnalogOutputPort(uint8_t port){
	printf("INIT ANALOG OUTPUT: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freeAnalogOutputPort(hal_handle_t portHandle){
	printf("FREE ANALOG OUTPUT: %d \n", portHandle);
}

/***********************************************************************\
 * Analog data io
\***********************************************************************/

void HAL_setAnalogValue(hal_handle_t portHandle, uint32_t value){
	printf("SET ANALOG VALUE: %d, %d \n", portHandle, value);
}
void HAL_setAnalogVoltage(hal_handle_t portHandle, float volts){
	printf("SET ANALOG VOLTAGE: %d, %f \n", portHandle, volts);
}

float HAL_getAnalogVoltage(hal_handle_t portHandle){
	printf("GET ANALOG VOLTAGE: %d \n", portHandle);
	return 0.0f;
}
uint32_t HAL_getAnalogValue(hal_handle_t portHandle){
	printf("GET ANALOG VALUE: %d \n", portHandle);
	return 0;
}

} /* namespace hal */

} /* namespace flashlib */
