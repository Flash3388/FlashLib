/*
 * HAL.h
 *
 *  Created on: Jun 14, 2017
 *      Author: root
 */

#ifndef HAL_HAL_H_
#define HAL_HAL_H_

#include <stdint.h>

namespace flashlib{

namespace hal{

void HAL_initialize(int* status);
void HAL_shutdown();

uint32_t HAL_clockMS();

char* HAL_boardName();

} /* namespace hal */

} /* namespace flashlib */

#endif /* INCLUDE_HAL_H_ */
