/*
 * gpio.h
 *
 *  Created on: Jun 13, 2017
 *      Author: zuk
 */

#ifndef PRU_GPIO_H_
#define PRU_GPIO_H_

#include "hw/hw_gpio.h"
#include "hw/hw_types.h"
#include "types.h"

#define GPIO_DIR_INPUT    (0x1u)
#define GPIO_DIR_OUTPUT   (0x0u)

#define GPIO_PIN_HIGH     (0x1u)
#define GPIO_PIN_LOW      (0x0u)

/***********************************************************************\
 * GPIO base
\***********************************************************************/

extern void GPIO_initialize();
extern void GPIO_shutdown();

extern void GPIO_module_enable(uint8_t base);
extern void GPIO_module_disable(uint8_t base);
extern void GPIO_module_reset(uint8_t base);

/***********************************************************************\
 * GPIO io
\***********************************************************************/

extern void GPIO_dir_set(uint8_t base, uint8_t pin, uint8_t dir);
extern uint8_t GPIO_dir_get(uint8_t base, uint8_t pin);

extern void GPIO_write(uint8_t base, uint8_t pin, uint32_t value);
extern uint32_t GPIO_read(uint8_t base, uint8_t pin);

/***********************************************************************\
 * GPIO conversion
\***********************************************************************/

unsigned int GPIO_base_get(uint8_t base){
	switch(base){
		case 0: return GPIO0;
		case 1: return GPIO1;
		case 2: return GPIO2;
		case 3: return GPIO3;
	}
	return 0;
}

#endif /* PRU_GPIO_H_ */
