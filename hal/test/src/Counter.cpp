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

hal_handle_t HAL_initializePulseCounter(int16_t port){
	printf("INIT PULSE COUNTER: %d \n", port);
	return HAL_INVALID_HANDLE;
}
void HAL_freePulseCounter(hal_handle_t counterHandle){
	printf("FREE PULSE COUNTER: %d \n", counterHandle);
}

void HAL_resetPulseCounter(hal_handle_t counterHandle){
	printf("RESET PULSE COUNTER: %d \n", counterHandle);
}

uint32_t HAL_getPulseCounterCount(hal_handle_t counterHandle){
	printf("GET COUNT PULSE COUNTER: %d \n", counterHandle);
	return 0;
}
float HAL_getPulseCounterPeriod(hal_handle_t counterHandle){
	printf("GET PERIOD PULSE COUNTER: %d \n", counterHandle);
	return 0.0;
}

} /* namespace hal */

} /* namespace flashlib */



