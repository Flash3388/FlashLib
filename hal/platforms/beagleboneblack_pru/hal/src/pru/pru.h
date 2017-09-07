/*
 * pru.h
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */

#ifndef PRU_PRU_H_
#define PRU_PRU_H_

#include <stdint.h>

#define SUCCESS       (0)
#define THREAD_FAIL   (1)
#define PRU_FAIL      (2)

namespace flashlib{

namespace hal{

/***********************************************************************\
 * PRU initialization
\***********************************************************************/

extern bool PRU_initialize(uint32_t* status);
extern bool PRU_shutdown(uint32_t* status);

extern void PRU_disableIO();
extern void PRU_enableIO();

/***********************************************************************\
 * PRU direct port handling
\***********************************************************************/

extern uint8_t PRU_initializePort(uint8_t base, uint8_t port, uint8_t type);
extern void PRU_freePort(uint8_t handle, uint8_t type);

/***********************************************************************\
 * PRU digital
\***********************************************************************/

extern void PRU_digitalWrite(uint8_t handle, uint8_t val);
extern uint8_t PRU_digitalRead(uint8_t handle);

extern void PRU_pulseOut(uint8_t handle, uint32_t length);
extern uint32_t PRU_pulseIn(uint8_t handle);

/***********************************************************************\
 * PRU analog
\***********************************************************************/

extern uint16_t PRU_analogRead(uint8_t handle);

/***********************************************************************\
 * PRU pwm
\***********************************************************************/

extern void PRU_pwmWrite(uint8_t handle, uint16_t duty);
extern uint16_t PRU_pwmRead(uint8_t handle);

extern void PRU_pwmSetFrquency(uint8_t handle, uint32_t frequency);
extern uint32_t PRU_pwmGetFrequency(uint8_t handle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* PRU_PRU_H_ */
