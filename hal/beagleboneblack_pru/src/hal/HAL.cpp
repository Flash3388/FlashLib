/*
 * HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <HAL.h>
#include "../../include/hal.h"

namespace flashlib{

namespace hal{

const char* name = "beagleboneblack";

int HAL_initialize(int mode){
	return BBB_initialize(mode);
}
void HAL_shutdown(){
	BBB_shutdown();
}

const char* HAL_boardName(){
	return name;
}

} /* namespace hal */

} /* namespace flashlib */


