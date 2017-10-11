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
 * Port validation
\***********************************************************************/

bool HAL_checkDigitalInputPortValid(int8_t port);
bool HAL_checkDigitalInputPortTaken(int8_t port);

bool HAL_checkDigitalOutputPortValid(int8_t port);
bool HAL_checkDigitalOutputPortTaken(int8_t port);

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

bool HAL_getDIOHigh(hal_handle_t portHandle);
bool HAL_getDIOLow(hal_handle_t portHandle);

void HAL_setDIOHigh(hal_handle_t portHandle);
void HAL_setDIOLow(hal_handle_t portHandle);
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength);

} /* namespace hal */

} /* namespace flashlib */

#endif /* DIO_H_ */
