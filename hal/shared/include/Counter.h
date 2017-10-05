/*
 * Counter.h
 *
 *  Created on: Oct 5, 2017
 *      Author: root
 */

#ifndef HAL_COUNTER_H_
#define HAL_COUNTER_H_

#include <stdint.h>
#include "haltypes.h"

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePulseCounter(int16_t port);
void HAL_freePulseCounter(hal_handle_t counterHandle);

void HAL_resetPulseCounter(hal_handle_t counterHandle);

uint32_t HAL_getPulseCounterCount(hal_handle_t counterHandle);
float HAL_getPulseCounterPeriod(hal_handle_t counterHandle);


} /* namespace hal */

} /* namespace flashlib */

#endif /* HAL_COUNTER_H_ */
