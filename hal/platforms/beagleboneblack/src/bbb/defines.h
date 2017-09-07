/*
 * defines.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef DEFINES_H_
#define DEFINES_H_

#define HWREG(x)                         (*((volatile unsigned int *)(x)))

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
#define BBB_GPIOX_LEN                    (0x1000)

#define BBB_PWMSS0_ADDR                  (0x48300000)
#define BBB_PWMSS1_ADDR                  (0x48302000)
#define BBB_PWMSS2_ADDR                  (0x48304000)
#define BBB_PWMSS_LEN                    (0x1000)

#endif /* DEFINES_H_ */
