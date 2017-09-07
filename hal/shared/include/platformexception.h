/*
 * platformexception.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef PLATFORMEXCEPTION_H_
#define PLATFORMEXCEPTION_H_

#include <stdexcept>

namespace flashlib {

namespace hal {

class platform_exception : public std::runtime_error {
public:
	platform_exception();
};

} /* namespace hal */

} /* namespace flashlib */

#endif /* INCLUDE_PLATFORMEXCEPTION_H_ */
