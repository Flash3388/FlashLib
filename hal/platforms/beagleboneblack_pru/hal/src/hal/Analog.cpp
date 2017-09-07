/*
 * Analog.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include "Analog.h"

namespace flashlib{

namespace hal{

/***********************************************************************\
 * Analog initialization
\***********************************************************************/

bool HAL_checkAnalogInputPort(uint8_t port){
	return false;
}
uint8_t HAL_initializeAnalogInputPort(uint8_t port){
	return 0;
}
void HAL_freeAnalogInputPort(uint8_t portHandle){

}

bool HAL_checkAnalogOutputPort(uint8_t port){
	return false;
}
uint8_t HAL_initializeAnalogOutputPort(uint8_t port){
	return 0;
}
void HAL_freeAnalogOutputPort(uint8_t portHandle){

}

/***********************************************************************\
 * Analog data io
\***********************************************************************/

void HAL_setAnalogOutputValue(uint8_t portHandle, uint32_t value){

}
void HAL_setAnalogOutputVoltage(uint8_t portHandle, float volts){

}

float HAL_getAnalogInputVoltage(uint8_t portHandle){
	return 0.0f;
}
uint32_t HAL_getAnalogInputValue(uint8_t portHandle){
	return 0;
}

/***********************************************************************\
 * Analog data conversion
\***********************************************************************/

uint32_t HAL_voltsToValue(uint8_t portHandle, float volts){
	return 0;
}
float HAL_valueToVolts(uint8_t portHandle, uint32_t value){
	return 0.0f;
}

} /* namespace hal */

} /* namespace flashlib */
