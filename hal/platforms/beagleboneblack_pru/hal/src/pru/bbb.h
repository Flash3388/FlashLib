/*
 * bbb.h
 *
 *  Created on: Aug 23, 2017
 *      Author: root
 */

#ifndef PRU_BBB_H_
#define PRU_BBB_H_

#include <stdint.h>

bool BBB_ADC_checkValidPort(uint8_t base, uint8_t port);
bool BBB_DIO_checkValidPort(uint8_t base, uint8_t port);
bool BBB_PWM_checkValidPort(uint8_t base, uint8_t port);


#endif /* PRU_BBB_H_ */
