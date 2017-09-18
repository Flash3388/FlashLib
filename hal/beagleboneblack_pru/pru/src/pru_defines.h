/*
 * pru_defines.h
 *
 *  Created on: Sep 16, 2017
 *      Author: root
 */

#ifndef BBB_PRU_DEFINES_H_
#define BBB_PRU_DEFINES_H_

#define PRU_MEM_SHARED_RAM      (0x10000)

#define PRU_MEM_STATUS_REG      (0x2)
#define PRU_MEM_ACTION_TYPE_REG (0x3)
#define PRU_MEM_ACTION_VAL_REG  (0x4)
#define PRU_MEM_HANDLE_TYPE_REG (0x5)
#define PRU_MEM_HANDLE_VAL_REG  (0x6)
#define PRU_MEM_HANDLE_RES_REG  (0x7)


#define PRU_MEM_ADC_OFFSET      (0xa)//10
#define PRU_MEM_ADC_EMPTY       (0x13)//19
#define PRU_MEM_PWM_OFFSET      (0x14)//20
#define PRU_MEM_PWM_EMPTY       (0x1d)//29
#define PRU_MEM_DIO_OFFSET      (0x1e)//30
#define PRU_MEM_DIO_EMPTY       (0x7b)//123


#define PRU_ACTION_SYS_FREE     (0x1)

#define PRU_ACTION_PORT_INIT    (0x5)
#define PRU_ACTION_PORT_FREE    (0x6)
#define PRU_ACTION_DIO_PULSE    (0x7)
#define PRU_ACTION_PWM_FREQ_S   (0x8)
#define PRU_ACTION_PWM_FREQ_G   (0x9)

#define PRU_HANDLE_DI           (0x1)
#define PRU_HANDLE_DO           (0x2)
#define PRU_HANDLE_ADC          (0x3)
#define PRU_HANDLE_PWM          (0x4)


#define PRU_WATCHDOG_UPDATE     (5000)//us
#define PRU_EVT_WAIT_TIMEOUT    (500)//us

#endif /* BBB_PRU_DEFINES_H_ */
