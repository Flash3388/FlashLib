/*
 * gpio.c
 *
 *  Created on: Oct 8, 2017
 *      Author: root
 */

#include "../../include/bbb_defines.h"

const signed char port_set[2][46] = {
		{
			-1, -1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 0, 0, 1, 1,
			0, 2, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 0, 0,
			0, 2, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2
		},
		{
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 0, 1, 1,
			1, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 3, 3, 3, 3, 3, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, 0, 0, -1, -1, -1, -1
		}
};
const unsigned int port_id_set[2][46] = {
		{
			0, 0, 6, 7, 2,	3, 2, 3,
			5, 4, 13, 12, 23, 26, 15,
			14, 27, 1, 22, 31, 30, 5,
			4, 1, 0, 29, 22, 24, 23,
			25, 10, 11, 9, 17, 8, 16,
			14, 15, 12, 13, 10, 11, 8,
			9, 6, 7
		},
		{
			0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 30, 28, 31, 18, 16, 19,
			5, 4, 13, 12, 3, 2, 17,
			15, 21, 14, 19, 17, 15, 16,
			14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20,
			7, 0, 0, 0, 0
		}
};

signed char gpio_module_get(unsigned char header, unsigned char pin){
	if(header > 1 || pin >= BBB_HEADER_PIN_COUNT)
		return -1;

	return port_set[header][pin - 1];
}
unsigned char gpio_pin_get(unsigned char header, unsigned char pin){
	if(header > 1 || pin >= BBB_HEADER_PIN_COUNT)
		return -1;

	return port_id_set[header][pin - 1];
}

