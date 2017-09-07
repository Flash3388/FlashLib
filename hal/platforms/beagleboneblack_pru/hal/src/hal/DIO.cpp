/*
 * DIO.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include "DIO.h"


namespace flashlib{

namespace hal{

/***********************************************************************\
 * DIO initialization
\***********************************************************************/

bool HAL_checkDIOPort(uint8_t port){
	return false;
}
uint8_t HAL_initializeDIOPort(uint8_t port, uint8_t dir){
	return 0;
}
void HAL_freeDIOPort(uint8_t portHandle){

}

/***********************************************************************\
 * DIO data io
\***********************************************************************/

uint8_t HAL_getDIO(uint8_t portHandle){
	return 0;
}
void HAL_setDIO(uint8_t portHandle, uint8_t value){

}
void HAL_pulseOutDIO(uint8_t portHandle, float pulseLength){

}

} /* namespace hal */

} /* namespace flashlib */


