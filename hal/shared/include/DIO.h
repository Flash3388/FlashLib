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

namespace flashlib{

namespace hal{

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

hal_handle_t HAL_initializeDigitalInputPort(int8_t port);
void HAL_freeDigitalInputPort(hal_handle_t portHandle);

hal_handle_t HAL_initializeDigitalOutputPort(int8_t port);
void HAL_freeDigitalOutputPort(hal_handle_t portHandle);

/***********************************************************************\
 * DIO data io
\***********************************************************************/

uint8_t HAL_getDIOHigh(hal_handle_t portHandle);
uint8_t HAL_getDIOLow(hal_handle_t portHandle);

void HAL_setDIOHigh(hal_handle_t portHandle);
void HAL_setDIOLow(hal_handle_t portHandle);
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength);

} /* namespace hal */

} /* namespace flashlib */

#endif /* DIO_H_ */
