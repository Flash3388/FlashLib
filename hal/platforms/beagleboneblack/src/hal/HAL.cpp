/*
 * HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <HAL.h>
#include <hal.h>

namespace flashlib{

namespace hal{

void HAL_initialize(int* status){
	BBB_initialize(status);
}
void HAL_shutdown(){
	BBB_shutdown();
}

uint32_t HAL_clockMS(){
	return 0;
}

char* HAL_boardName(){
	return "beagleboneblack";
}

} /* namespace hal */

} /* namespace flashlib */


