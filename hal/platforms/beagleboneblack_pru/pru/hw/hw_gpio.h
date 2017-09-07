/*
 * hw_gpio.h
 *
 *  Created on: Jun 16, 2017
 *      Author: root
 */

#ifndef HW_HW_GPIO_H_
#define HW_HW_GPIO_H_

#define GPIO0               (0x44e07000)
#define GPIO1               (0x4804c000)
#define GPIO2               (0x481ac000)
#define GPIO3               (0x481ae000)
#define GPIO_CTRL           (0x130)
#define GPIO_OE             (0x134)
#define GPIO_DATAIN         (0x138)
#define GPIO_DATAOUT        (0x13c)
#define GPIO_DEBOUNCENABLE  (0x150)
#define GPIO_DEBOUNCINGTIME (0x154)
#define GPIO_CLEARDATAOUT   (0x190)
#define GPIO_SETDATAOUT     (0x194)
#define GPIO_SYSCONFIG      (0x10)


#define GPIO_CTRL_DISABLEMODULE    (0x00000001u)
#define GPIO_SYSSTATUS_RESETDONE   (0x00000001u)
#define GPIO_SYSCONFIG_SOFTRESET   (0x00000002u)

#endif /* HW_HW_GPIO_H_ */
