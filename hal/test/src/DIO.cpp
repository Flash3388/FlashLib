/*
 * DIO.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <stdio.h>
#include <DIO.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

hal_handle_t HAL_initializeDigitalInputPort(uint8_t port){
	printf("INIT DIGITAL INPUT: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freeDigitalInputPort(hal_handle_t portHandle){
	printf("FREE DIGITAL INPUT: %d \n", portHandle);
}

hal_handle_t HAL_initializeDigitalOutputPort(uint8_t port){
	printf("INIT DIGITAL OUTPUT: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freeDigitalOutputPort(hal_handle_t portHandle){
	printf("FREE DIGITAL OUTPUT: %d \n", portHandle);
}

/***********************************************************************\
 * DIO data io
\***********************************************************************/

uint8_t HAL_getDIOHigh(hal_handle_t portHandle){
	printf("GET DIGITAL HIGH: %d \n", portHandle);
	return 0;
}
uint8_t HAL_getDIOLow(hal_handle_t portHandle){
	printf("GET DIGITAL LOW: %d \n", portHandle);
	return 0;
}
void HAL_setDIOHigh(hal_handle_t portHandle){
	printf("SET DIGITAL HIGH: %d \n", portHandle);
}
void HAL_setDIOLow(hal_handle_t portHandle){
	printf("SET DIGITAL LOW: %d \n", portHandle);
}
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength){
	printf("PULSE DIGITAL HIGH: %d, %f \n", portHandle, pulseLength);
}

} /* namespace hal */

} /* namespace flashlib */


