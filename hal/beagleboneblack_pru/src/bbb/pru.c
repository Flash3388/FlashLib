/*
 * pru.c
 *
 *  Created on: Sep 17, 2017
 *      Author: root
 */

#include "pru.h"
#include "pru_sw/prussdrv.h"
#include "pru_sw/pruss_intc_mapping.h"

int pru_initialize(pru_data_t* pru_data, int prunum, const char* progfile){
	tpruss_intc_initdata pru_initdata = PRUSS_INTC_INITDATA;

	int status = 0;

	status = prussdrv_init();
	if(status){
		return status;
	}
	status = prussdrv_open(PRU_EVTOUT0);
	if(status){
		return status;
	}
	status = prussdrv_pruintc_init(&pru_initdata);
	if(status){
		return status;
	}

	status = prussdrv_exec_program(prunum, progfile);
	if(status){
		return status;
	}

	void* p;
	status = prussdrv_map_prumem(PRUSS0_SHARED_DATARAM, &p);
	if(status){
		return status;
	}

	pru_data->prunum = prunum;
	pru_data->shared_memory = (unsigned int*)p;
	return 0;
}
void pru_shutdown(pru_data_t* pru_data){
	prussdrv_pru_disable(pru_data->prunum);
	prussdrv_exit();
}

void pru_interrupt_wait(pru_data_t* pru_data){
	prussdrv_pru_wait_event(PRU_EVTOUT_0);
	prussdrv_pru_clear_event(PRU_EVTOUT_0,  pru_data->prunum == 0? PRU0_ARM_INTERRUPT : PRU1_ARM_INTERRUPT);
}
void pru_interrupt_send(pru_data_t* pru_data){
	prussdrv_pru_send_event(pru_data->prunum? ARM_PRU0_INTERRUPT : ARM_PRU1_INTERRUPT);
}
