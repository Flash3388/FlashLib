/*
 * pwm.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef PWM_H_
#define PWM_H_

void pwm_initialize();
void pwm_free();

void pwm_module_enable(char module);
void pwm_module_disable(char module);

void pwm_module_settings(char module, unsigned char clkdiv, unsigned char hspclkdiv,
		unsigned char dutyA, unsigned char dutyB);

#endif /* PWM_H_ */
