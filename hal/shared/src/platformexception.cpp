/*
 * platformexception.cpp
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#include <platformexception.h>
#include <HAL.h>
#include <string>

namespace flashlib {

namespace hal {

platform_exception::platform_exception()
			: runtime_error("Operation is incompatible with platform: " + std::string(HAL_boardName())){
}

} /* namespace hal */

} /* namespace flashlib */
