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
 * Analog data
\***********************************************************************/

float HAL_getGlobalAnalogSampleRate(){
	printf("ANALOG SAMPLE RATE \n");
	return 0.0f;
}
float HAL_getAnalogMaxVoltage(){
	printf("ANALOG MAX VOLTAGE \n");
	return 0.0f;
}
uint32_t HAL_getAnalogMaxValue(){
	printf("ANALOG MAX VALUE \n");
	return 0;
}

/***********************************************************************\
 * Port validation
\***********************************************************************/

bool HAL_checkAnalogInputPortValid(int8_t port){
	printf("ANALOG INPUT VALID: %d \n", port);
	return true;
}
bool HAL_checkAnalogInputPortTaken(int8_t port){
	printf("ANALOG INPUT TAKEN: %d \n", port);
	return false;
}

bool HAL_checkAnalogOutputPortValid(int8_t port){
	printf("ANALOG OUTPUT VALID: %d \n", port);
	return true;
}
bool HAL_checkAnalogOutputPortTaken(int8_t port){
	printf("ANALOG OUTPUT TAKEN: %d \n", port);
	return false;
}

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

float HAL_convertAnalogValueToVoltage(uint32_t value){
	printf("ANALOG VALUE TO VOLTAGE: %d \n", value);
	return 0.0f;
}
uint32_t HAL_convertAnalogVoltageToValue(float voltage){
	printf("ANALOG VOLTAGE TO VALUE: %f \n", voltage);
	return 0;
}

/***********************************************************************\
 * Analog accumulator
\***********************************************************************/

int HAL_enableAnalogInputAccumulator(hal_handle_t portHandle){
	printf("ANALOG ACCUMULATOR ENABLE: %d \n", portHandle);
	return 0;
}
int HAL_disableAnalogInputAccumulator(hal_handle_t portHandle){
	printf("ANALOG ACCUMULATOR DISABLE: %d \n", portHandle);
	return 0;
}
void HAL_resetAnalogInputAccumulator(hal_handle_t portHandle){
	printf("ANALOG ACCUMULATOR RESET: %d \n", portHandle);
}
void HAL_setAnalogInputAccumulatorCenter(hal_handle_t portHandle, uint32_t center){
	printf("ANALOG ACCUMULATOR CENTER: %d, %d \n", portHandle, center);
}
int64_t HAL_getAnalogInputAccumulatorValue(hal_handle_t portHandle){
	printf("ANALOG ACCUMULATOR VALUE GET: %d \n", portHandle);
	return 0;
}
uint32_t HAL_getAnalogInputAccumulatorCount(hal_handle_t portHandle){
	printf("ANALOG ACCUMULATOR COUNT GET: %d \n", portHandle);
	return 0;
}

} /* namespace hal */

} /* namespace flashlib */
