/*
 * PWM.h
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#ifndef HAL_PWM_H_
#define HAL_PWM_H_

#include <stdint.h>
#include "haltypes.h"

namespace flashlib{

namespace hal{

hal_handle_t HAL_initializePWMPort(uint8_t port);
void HAL_freePWMPort(hal_handle_t portHandle);

void HAL_setPWMDuty(hal_handle_t portHandle, float dutyCycle);
float HAL_getPWMDuty(hal_handle_t portHandle);

void HAL_setPWMValue(hal_handle_t portHandle, uint8_t value);
uint8_t HAL_getPWMValue(hal_handle_t portHandle);

void HAL_setPWMFrequency(hal_handle_t portHandle, float frequency);
float HAL_getPWMFrequency(hal_handle_t portHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* PWM_H_ */
