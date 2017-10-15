/*
 * Counter.cpp
 *
 *  Created on: Oct 5, 2017
 *      Author: root
 */

#include <Counter.h>
#include <stdio.h>

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePulseCounter(hal_handle_t port){
	printf("INIT PULSE COUNTER: %d \n", port);
	return HAL_INVALID_HANDLE;
}
hal_handle_t HAL_initializeQuadPulseCounter(hal_handle_t upPort, hal_handle_t downPort){
	printf("INIT QUAD PULSE COUNTER: %d, %d \n", upPort, downPort);
	return HAL_INVALID_HANDLE;
}
void HAL_freePulseCounter(hal_handle_t counterHandle){
	printf("FREE PULSE COUNTER: %d \n", counterHandle);
}

void HAL_resetPulseCounter(hal_handle_t counterHandle){
	printf("RESET PULSE COUNTER: %d \n", counterHandle);
}

uint8_t HAL_getPulseCounterDirection(hal_handle_t counterHandle){
	printf("GET DIRECTION PULSE COUNTER: %d \n", counterHandle);
	return 0;
}
uint32_t HAL_getPulseCounterPulseCount(hal_handle_t counterHandle){
	printf("GET COUNT PULSE COUNTER: %d \n", counterHandle);
	return 0;
}
float HAL_getPulseCounterPulseLength(hal_handle_t counterHandle){
	printf("GET LENGTH PULSE COUNTER: %d \n", counterHandle);
	return 0.0;
}
float HAL_getPulseCounterPulsePeriod(hal_handle_t counterHandle){
	printf("GET PERIOD PULSE COUNTER: %d \n", counterHandle);
	return 0.0;
}

bool HAL_isPulseCounterQuadrature(hal_handle_t counterHandle){
	printf("GET QUADRATURE PULSE COUNTER: %d \n", counterHandle);
	return false;
}

} /* namespace hal */

} /* namespace flashlib */



