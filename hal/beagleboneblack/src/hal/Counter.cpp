/*
 * Counter.cpp
 *
 *  Created on: Oct 5, 2017
 *      Author: root
 */

#include <Counter.h>
#include <hal.h>

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePulseCounter(int8_t port){
	return BBB_initializePulseCounter(port);
}
hal_handle_t HAL_initializeQuadPulseCounter(int8_t upPort, int8_t downPort){
	return BBB_initializePulseCounter(upPort, downPort);
}
void HAL_freePulseCounter(hal_handle_t counterHandle){
	BBB_freePulseCounter(counterHandle);
}

void HAL_resetPulseCounter(hal_handle_t counterHandle){
	BBB_resetPulseCounter(counterHandle);
}

uint8_t HAL_getPulseCounterDirection(hal_handle_t counterHandle){
	return BBB_getPulseCounterDirection(counterHandle);
}
uint32_t HAL_getPulseCounterPulseCount(hal_handle_t counterHandle){
	return BBB_getPulseCounterCount(counterHandle);
}
float HAL_getPulseCounterPulsePeriod(hal_handle_t counterHandle){
	return BBB_getPulseCounterPeriod(counterHandle);
}
float HAL_getPulseCounterPulseLength(hal_handle_t counterHandle){
	return BBB_getPulseCounterLength(counterHandle);
}

uint8_t HAL_isPulseCounterQuadrature(hal_handle_t counterHandle){
	bool quad = BBB_isPulseCounterQuadrature(counterHandle);
	if(quad)
		return 1;
	return 0;
}


} /* namespace hal */

} /* namespace flashlib */



