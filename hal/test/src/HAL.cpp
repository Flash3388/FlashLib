/*
 * HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <stdio.h>
#include <HAL.h>

namespace flashlib{

namespace hal{

const char* name = "beagleboneblack";

int HAL_initialize(int mode){
	printf("INIT HAL: %d \n", mode);
	return 0;
}
void HAL_shutdown(){
	printf("SHUTDOWN HAL \n");
}

const char* HAL_boardName(){
	return name;
}

} /* namespace hal */

} /* namespace flashlib */


