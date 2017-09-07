/*
 * main.c
 *
 *  Created on: Jun 15, 2017
 *      Author: root
 */


#include "pru.h"

#define ITERATION_DELAY (10) //ms

#define INTERRUPT_CONTROLLER (0x20000)
#define SICR_REGISTER        (0x24)

volatile register unsigned int __R31;

int main(void){

	uint32_t status;

	pru_initialize(&status);

	if(status != PRU_INITIALIZED){
		//initialization error
	}else{//EVERY THING IS OKAY, CONTINUE TO LOOP
		//writing our status!
		pru_mem_write(STATUS_REGISTER, PRU_INITIALIZED);

		__R31 = 35;

		uint8_t finish = 0;

		//while not finished running
		while(!finish){
			//if interrupt
			if(__R31 & (1 << 30)){

				//handle host request
				pru_handleHostRequest(&status);

				//stopping
				if(status == PRU_SHUTDOWN){
					finish = 1;
				}

				//clear interrupt
				HWREG(INTERRUPT_CONTROLLER + SICR_REGISTER) = 21;
				//interrupt ARM
				__R31 = 35;
			}
			//updating handles
			pru_updateHandles();
			//updating time
			pru_updateTime();
			//delay
			pru_delay(ITERATION_DELAY);
		}
	}

	//shutdown
	pru_shutdown(&status);
	//writing our status!
	pru_mem_write(STATUS_REGISTER, PRU_SHUTDOWN);

	//stop PRU processing
	__halt();
	return 0;
}
