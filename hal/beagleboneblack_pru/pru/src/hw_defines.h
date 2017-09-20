/*
 * hw_defines.h
 *
 *  Created on: Sep 18, 2017
 *      Author: root
 */

#ifndef SRC_HW_DEFINES_H_
#define SRC_HW_DEFINES_H_

#define BBB_CM_PER_ADDR                  (0x44e00000)
#define BBB_CONTROL_MODULE               (0x44e10000)
#define BBB_WKUP_OFFSET_FROM_CM          (0x400)

#define BBB_GPIO0_ADDR                   (0x44e07000)
#define BBB_GPIO1_ADDR                   (0x4804c000)
#define BBB_GPIO2_ADDR                   (0x481ac000)
#define BBB_GPIO3_ADDR                   (0x481ae000)
#define BBB_GPIO_CTRL                    (0x130)
#define BBB_GPIO_OE                      (0x134)
#define BBB_GPIO_DATAIN                  (0x138)
#define BBB_GPIO_DATAOUT                 (0x13c)
#define BBB_GPIO_DEBOUNCENABLE           (0x150)
#define BBB_GPIO_DEBOUNCINGTIME          (0x154)
#define BBB_GPIO_CLEARDATAOUT            (0x190)
#define BBB_GPIO_SETDATAOUT              (0x194)
#define BBB_GPIO_SYSCONFIG               (0x10)

#define BBB_PWMSS0_ADDR                  (0x48300000)
#define BBB_PWMSS1_ADDR                  (0x48302000)
#define BBB_PWMSS2_ADDR                  (0x48304000)

#define PRU_ICSS_CFG                     (0x26000)
#define PRU_ICSS_CFG_SYSCFG              (0x04)

#define BBB_CM_PER                       (0x44e00000)
#define BBB_CM_PER_GPIO1_CLKCTRL         (0xac)
#define BBB_CM_PER_GPIO2_CLKCTRL         (0xb0)
#define BBB_CM_PER_GPIO3_CLKCTRL         (0xb4)
#define BBB_CM_WKUP                      (0x44e00400)
#define BBB_CM_WKUP_GPIO0_CLKCTRL        (0x08)
#define BBB_CM_WKUP_ADC_TSK_CLKCTL       (0xbc)

#define BBB_GPIO_CTRL_DISABLEMODULE      (0x00000001u)
#define BBB_GPIO_SYSSTATUS_RESETDONE     (0x00000001u)
#define BBB_GPIO_SYSCONFIG_SOFTRESET     (0x00000002u)


#define BBB_PWMSS_CTRL	                 (0x664)

#define BBB_CM_PER_EPWMSS0_CLKCTRL	     (0xd4)
#define BBB_CM_PER_EPWMSS1_CLKCTRL	     (0xcc)
#define BBB_CM_PER_EPWMSS2_CLKCTRL	     (0xd8)

#define BBB_EPWM_TBCTL	                 (0x0)
#define BBB_EPWM_TBSTS	                 (0x2)
#define BBB_EPWM_TBPHSHR	             (0x4)
#define BBB_EPWM_TBPHS	                 (0x6)
#define BBB_EPWM_TBCNT	                 (0x8)
#define BBB_EPWM_TBPRD	                 (0xa)
#define BBB_EPWM_CMPCTL	                 (0xe)
#define BBB_EPWM_CMPAHR	                 (0x10)
#define BBB_EPWM_CMPA	                 (0x12)
#define BBB_EPWM_CMPB	                 (0x14)
#define BBB_EPWM_AQCTLA	                 (0x16)
#define BBB_EPWM_AQCTLB	                 (0x18)

#define BBB_TBCTL_CTRMODE_UP             (0x0)
#define BBB_TBCTL_CTRMODE_DOWN           (0x1)
#define BBB_TBCTL_CTRMODE_UPDOWN         (0x2)
#define BBB_TBCTL_CTRMODE_FREEZE         (0x3)


#define BBB_ADCTSC_0_REGS                (0x44e0d000)

#define BBB_ADCTSC_WKUP_CLKCTRL          (0xbc)
#define BBB_ADCTSC_STEPENABLE            (0x54)
#define BBB_ADCTSC_ADCRANGE              (0x48)
#define BBB_ADCTSC_CTRL                  (0x40)
#define BBB_ADCTSC_CLKDIV                (0x4c)
#define BBB_ADCTSC_STEPCONFIG1           (0x64)
#define BBB_ADCTSC_STEPDELAY1            (0x68)

#define BBB_ADCTSC_FIFO0                 (0)
#define BBB_ADCTSC_FIFO1                 (1)

#define BBB_ADCTSC_FIFODATA_DATA         (0x00000fffu)

#define BBB_ADCTSC_FIFODATA(n)           (0x100 + (n * 0x100))
#define BBB_ADCTSC_FIFOCOUNT(n)          (0xe4 + (n * 0xc))

#endif /* SRC_HW_DEFINES_H_ */
