/*
 * pwm.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef PWM_H_
#define PWM_H_

#ifdef __cplusplus
extern "C"{
#endif

int bbb_pwm_init();
void bbb_pwm_free();

void bbb_pwm_setduty(unsigned char module, unsigned char pin, float duty);
float bbb_pwm_getduty(unsigned char module, unsigned char pin);

void bbb_pwm_setfrequency(unsigned char module, float frequency);
float bbb_pwm_getfrequency(unsigned char module);

void bbb_pwm_enable(unsigned char module);
void bbb_pwm_disable(unsigned char module);
unsigned char bbb_pwm_enabled(unsigned char module);

#ifdef __cplusplus
}
#endif

#endif /* PWM_H_ */
