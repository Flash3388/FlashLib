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

hal_handle_t HAL_initializePulseCounter(int16_t port){
	return BBB_initializePulseCounter(port);
}
void HAL_freePulseCounter(hal_handle_t counterHandle){
	BBB_freePulseCounter(counterHandle);
}

void HAL_resetPulseCounter(hal_handle_t counterHandle){
	BBB_resetPulseCounter(counterHandle);
}

uint32_t HAL_getPulseCounterCount(hal_handle_t counterHandle){
	return BBB_getPulseCounterCount(counterHandle);
}
float HAL_getPulseCounterPeriod(hal_handle_t counterHandle){
	return BBB_getPulseCounterPeriod(counterHandle);
}


} /* namespace hal */

} /* namespace flashlib */



