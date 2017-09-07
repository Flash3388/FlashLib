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

void bbb_pwm_setduty(uint8_t id, uint8_t port, float duty);
float bbb_pwm_getduty(uint8_t id, uint8_t port);

void bbb_pwm_enable(uint8_t id);
void bbb_pwm_disable(uint8_t id);

#ifdef __cplusplus
}
#endif

#endif /* PWM_H_ */
