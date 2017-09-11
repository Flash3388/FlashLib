/*
 * HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <HAL.h>
#include "../bbb/hal.h"

namespace flashlib{

namespace hal{

int HAL_initialize(int mode){
	return BBB_initialize(mode);
}
void HAL_shutdown(){
	BBB_shutdown();
}

char* HAL_boardName(){
	return "beagleboneblack";
}

} /* namespace hal */

} /* namespace flashlib */


