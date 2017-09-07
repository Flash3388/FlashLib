/*
 * pru.h
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */

#ifndef PRU_H_
#define PRU_H_

#include "types.h"
#include "handles.h"
#include "gpio.h"
#include "adc.h"
#include "pwm.h"
#include "defines.h"

#include "hw/hw_pru.h"
#include "hw/hw_types.h"

/***********************************************************************\
 * pru utilities
\***********************************************************************/

extern uint32_t pru_clock();
extern void pru_delay(uint32_t ms);

/***********************************************************************\
 * pru memory handling
\***********************************************************************/

extern void pru_mem_write(uint32_t memoffset, uint32_t data);
extern uint32_t pru_mem_read(uint32_t memoffset);
extern void pru_mem_clear(uint32_t memoffset);

/***********************************************************************\
 * pru initialization
\***********************************************************************/

extern void pru_initialize(uint32_t* status);
extern void pru_shutdown(uint32_t* status);

/***********************************************************************\
 * pru port init/shutdown
\***********************************************************************/

extern uint8_t pru_initializePort(uint8_t handle, uint8_t type);
extern void pru_freePort(uint8_t handle, uint8_t type);

extern uint8_t pru_dio_initializePort(uint8_t handle);
extern void pru_dio_freePort(uint8_t handle);
extern uint8_t pru_pwm_initializePort(uint8_t handle);
extern void pru_pwm_freePort(uint8_t handle);
extern uint8_t pru_adc_initializePort(uint8_t handle);
extern void pru_adc_freePort(uint8_t handle);

/***********************************************************************\
 * pru settings
\***********************************************************************/

extern void pru_settingsPort(uint8_t handle, uint8_t type, uint8_t dir, uint32_t* setting);

extern void pru_dio_settings(uint8_t handle, uint8_t dir, uint32_t* setting);
extern void pru_pwm_settings(uint8_t handle, uint8_t dir, uint32_t* setting);
extern void pru_adc_settings(uint8_t handle, uint8_t dir, uint32_t* setting);

/***********************************************************************\
 * pru handles
\***********************************************************************/

extern void pru_updateHandles();
extern void pru_updateTime();

/***********************************************************************\
 * pru user interaction
\***********************************************************************/

extern void pru_handleHostRequest(uint32_t* status);

#endif /* PRU_H_ */
