/*
 * gpio.h
 *
 *  Created on: Oct 8, 2017
 *      Author: root
 */

#ifndef BBB_GPIO_H_
#define BBB_GPIO_H_

signed char gpio_module_get(unsigned char header, unsigned char pin);
unsigned char gpio_pin_get(unsigned char header, unsigned char pin);

#endif /* BBB_GPIO_H_ */
