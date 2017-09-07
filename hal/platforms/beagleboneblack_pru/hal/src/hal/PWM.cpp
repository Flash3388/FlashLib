/*
 * PWM.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include "PWM.h"

namespace flashlib{

namespace hal{

bool HAL_checkPWMPort(uint8_t port){
	return false;
}
uint8_t HAL_initializePWMPort(uint8_t port){
	return 0;
}
void HAL_freePWMPort(uint8_t portHandle){

}

void HAL_setPWMDutyCycle(uint8_t portHandle, float dutyCycle){

}
float HAL_getPWMDutyCycle(uint8_t portHandle){
	return 0.0f;
}

void HAL_setPWMValue(uint8_t portHandle, int value){

}
int HAL_getPWMValue(uint8_t portHandle){
	return 0;
}

void HAL_setPWMFrequency(uint8_t portHandle, float frequency){

}
float HAL_getPWMFrequency(uint8_t portHandle){
	return 0.0f;
}

uint32_t HAL_getPWMLoopTime(){
	return 0;
}

} /* namespace hal */

} /* namespace flashlib */

