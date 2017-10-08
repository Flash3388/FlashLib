/*
 * Counter.cpp
 *
 *  Created on: Oct 5, 2017
 *      Author: root
 */

#include <Counter.h>
#include <hal.h>
#include <platformexception.h>

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePulseCounter(int16_t port){
	throw platform_exception();
}
void HAL_freePulseCounter(hal_handle_t counterHandle){
	throw platform_exception();
}

void HAL_resetPulseCounter(hal_handle_t counterHandle){
	throw platform_exception();
}

uint32_t HAL_getPulseCounterPulseCount(hal_handle_t counterHandle){
	throw platform_exception();
}
float HAL_getPulseCounterPulsePeriod(hal_handle_t counterHandle){
	throw platform_exception();
}
float HAL_getPulseCounterPulseLength(hal_handle_t counterHandle){
	throw platform_exception();
}


} /* namespace hal */

} /* namespace flashlib */



