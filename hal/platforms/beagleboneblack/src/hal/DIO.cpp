/*
 * DIO.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <DIO.h>
#include <hal.h>


namespace flashlib{

namespace hal{

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

hal_handle_t HAL_initializeDIOPort(uint8_t port, uint8_t dir){
	return BBB_initializeDIOPort(port, dir);
}
void HAL_freeDIOPort(hal_handle_t portHandle){
	BBB_freeDIOPort(portHandle);
}

/***********************************************************************\
 * DIO data io
\***********************************************************************/

uint8_t HAL_getDIO(hal_handle_t portHandle){
	return BBB_getDIO(portHandle);
}
void HAL_setDIO(hal_handle_t portHandle, uint8_t value){
	BBB_setDIO(portHandle, value);
}
void HAL_pulseOutDIO(hal_handle_t portHandle, float pulseLength){
	BBB_pulseDIO(portHandle, pulseLength);
}

} /* namespace hal */

} /* namespace flashlib */


