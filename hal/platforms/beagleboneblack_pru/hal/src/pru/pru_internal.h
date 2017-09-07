/*
 * pru_internal.h
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */

#ifndef PRU_PRU_INTERNAL_H_
#define PRU_PRU_INTERNAL_H_

#include "prussdrv.h"
#include "pruss_intc_mapping.h"

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct pru_data{
	uint8_t prunum;
	unsigned int* shared_memory;
} pru_data_t;

/***********************************************************************\
 * PRU initialization
\***********************************************************************/

extern void pru_initialize(pru_data_t* pru_data, char* programfile, uint32_t* status);
extern void pru_shutdown(pru_data_t* pru_data, uint32_t* status);

/***********************************************************************\
 * PRU memory handle
\***********************************************************************/

extern void pru_mapMemory(pru_data_t* pru_data);

/***********************************************************************\
 * PRU interrupts
\***********************************************************************/

extern void pru_waitInterrupt(pru_data_t* pru_data);
extern void pru_sendInterrupt(pru_data_t* pru_data);

/***********************************************************************\
 * PRU IO
\***********************************************************************/

extern void pru_io_write(pru_data_t* pru_data, uint8_t handle, uint8_t type, uint32_t data);
extern uint32_t pru_io_read(pru_data_t* pru_data, uint8_t handle, uint8_t type);

extern void pru_io_settings_write(pru_data_t* pru_data, uint8_t handle, uint8_t type, uint16_t setting, int16_t value);
extern int16_t pru_io_settings_read(pru_data_t* pru_data, uint8_t handle, uint8_t type, uint16_t setting);

#ifdef __cplusplus
}
#endif

#endif /* PRU_PRU_INTERNAL_H_ */
