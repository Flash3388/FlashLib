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

int HAL_initialize(int mode);
void HAL_shutdown();

const char* HAL_boardName();

} /* namespace hal */

} /* namespace flashlib */

#endif /* INCLUDE_HAL_H_ */
