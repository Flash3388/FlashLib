/*
 * pru.h
 *
 *  Created on: Sep 16, 2017
 *      Author: root
 */

#ifndef BBB_PRU_H_
#define BBB_PRU_H_

#include <stdint.h>

#ifdef  __cplusplus
extern "C" {
#endif

typedef struct pru_data{
	uint8_t prunum;
	unsigned int* shared_memory;
} pru_data_t;

int pru_initialize(pru_data_t* prudata, uint8_t prunum, char* progfile);
void pru_shutdown(pru_data_t* prudata);

void pru_interrupt_wait(pru_data_t* pru_data);
void pru_interrupt_send(pru_data_t* pru_data);

#ifdef  __cplusplus
}
#endif

#endif /* BBB_PRU_H_ */
