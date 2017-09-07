/*
 * hw_pru.h
 *
 *  Created on: Jun 16, 2017
 *      Author: root
 */

#ifndef HW_HW_PRU_H_
#define HW_HW_PRU_H_

#define PRU_ICSS_CFG           (0x26000)
#define PRU_ICSS_CFG_SYSCFG    (0x04)


#define CM_PER                 (0x44e00000)
#define CM_PER_GPIO1_CLKCTRL   (0xac)
#define CM_PER_GPIO2_CLKCTRL   (0xb0)
#define CM_PER_GPIO3_CLKCTRL   (0xb4)
#define CM_WKUP                (0x44e00400)
#define CM_WKUP_GPIO0_CLKCTRL  (0x08)
#define CM_WKUP_ADC_TSK_CLKCTL (0xbc)

#endif /* HW_HW_PRU_H_ */
