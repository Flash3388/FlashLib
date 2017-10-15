/*
 * DIO.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <DIO.h>
#include <bbb_defines.h>
#include <hal.h>

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Port validation
\***********************************************************************/

bool HAL_checkDigitalInputPortValid(int8_t port){
	return BBB_checkDigitalPortValid(port);
}
bool HAL_checkDigitalInputPortTaken(int8_t port){
	return BBB_checkDigitalPortTaken(port);
}

bool HAL_checkDigitalOutputPortValid(int8_t port){
	return BBB_checkDigitalPortValid(port);
}
bool HAL_checkDigitalOutputPortTaken(int8_t port){
	return BBB_checkDigitalPortTaken(port);
}

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

hal_handle_t HAL_initializeDigitalInputPort(int8_t port){
	return BBB_initializeDIOPort(port, BBB_DIR_INPUT);
}
void HAL_freeDigitalInputPort(hal_handle_t portHandle){
	BBB_freeDIOPort(portHandle);
}

hal_handle_t HAL_initializeDigitalOutputPort(int8_t port){
	return BBB_initializeDIOPort(port, BBB_DIR_OUTPUT);
}
void HAL_freeDigitalOutputPort(hal_handle_t portHandle){
	BBB_freeDIOPort(portHandle);
}

/***********************************************************************\
 * DIO data io
\***********************************************************************/

bool HAL_getDIOHigh(hal_handle_t portHandle){
	return BBB_getDIO(portHandle) == BBB_GPIO_HIGH;
}
bool HAL_getDIOLow(hal_handle_t portHandle){
	return BBB_getDIO(portHandle) == BBB_GPIO_LOW;
}
void HAL_setDIOHigh(hal_handle_t portHandle){
	BBB_setDIO(portHandle, BBB_GPIO_HIGH);
}
void HAL_setDIOLow(hal_handle_t portHandle){
	BBB_setDIO(portHandle, BBB_GPIO_LOW);
}
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength){
	BBB_pulseDIO(portHandle, pulseLength);
}

} /* namespace hal */

} /* namespace flashlib */


