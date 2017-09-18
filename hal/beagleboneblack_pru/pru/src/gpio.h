/*
 * gpio.h
 *
 *  Created on: Sep 18, 2017
 *      Author: root
 */

#ifndef SRC_GPIO_H_
#define SRC_GPIO_H_

void gpio_initialize();
void gpio_free();

char gpio_module_get(char header, char pin);

void gpio_setdir(char header, char pin, char dir);

void gpio_sethigh(char header, char pin);
void gpio_setlow(char header, char pin);

char gpio_ishigh(char header, char pin);

#endif /* SRC_GPIO_H_ */
