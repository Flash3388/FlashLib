/*
 * HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include "HAL.h"

namespace flashlib{

namespace hal{

void HAL_initialize(int* status){

}
void HAL_shutdown(int* status){

}

uint32_t HAL_clockMS(){
	return 0;
}

char* HAL_boardName(){
	return "beagleboneblack";
}

} /* namespace hal */

} /* namespace flashlib */


