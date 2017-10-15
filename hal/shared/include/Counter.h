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

#define COUNTER_DIR_FORWARD  (1)
#define COUNTER_DIR_UNKNOWN  (0)
#define COUNTER_DIR_BACKWARD (1)

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePulseCounter(hal_handle_t port);
hal_handle_t HAL_initializeQuadPulseCounter(hal_handle_t upPort, hal_handle_t downPort);
void HAL_freePulseCounter(hal_handle_t counterHandle);

void HAL_resetPulseCounter(hal_handle_t counterHandle);

uint8_t HAL_getPulseCounterDirection(hal_handle_t counterHandle);
uint32_t HAL_getPulseCounterPulseCount(hal_handle_t counterHandle);
float HAL_getPulseCounterPulseLength(hal_handle_t counterHandle);
float HAL_getPulseCounterPulsePeriod(hal_handle_t counterHandle);

bool HAL_isPulseCounterQuadrature(hal_handle_t counterHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* HAL_COUNTER_H_ */
