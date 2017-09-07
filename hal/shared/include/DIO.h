/*
 * DIO.h
 *
 *  Created on: Jun 14, 2017
 *      Author: zuk
 */

#ifndef HAL_DIO_H_
#define HAL_DIO_H_

#include <stdint.h>
#include "haltypes.h"

#define DIO_OUTPUT    (0x0)
#define DIO_INPUT     (0x1)

#define DIO_HIGH      (0x1)
#define DIO_LOW       (0x0)

namespace flashlib{

namespace hal{

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

hal_handle_t HAL_initializeDIOPort(uint8_t port, uint8_t output);
void HAL_freeDIOPort(hal_handle_t portHandle);

/***********************************************************************\
 * DIO data io
\***********************************************************************/

uint8_t HAL_getDIO(hal_handle_t portHandle);
void HAL_setDIO(hal_handle_t portHandle, uint8_t value);
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength);

} /* namespace hal */

} /* namespace flashlib */

#endif /* DIO_H_ */
