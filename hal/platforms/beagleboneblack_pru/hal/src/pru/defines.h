/*
 * defines.h
 *
 *  Created on: Aug 23, 2017
 *      Author: root
 */

#ifndef DEFINES_H_
#define DEFINES_H_

//bbb specs
#define ADC_COUNT         (6)
#define PWM_COUNT         (6)
#define DIO_COUNT         (10)

//memory
#define SHARED_MEMORY     (0x10000)
#define HANDLE_REGISTER   (0x1)
#define IN_REGISTER       (0x2)
#define OUT_REGISTER      (0x3)
#define TYPE_REGISTER     (0x4)
#define TIME_REGISTER     (0x5)
#define STATUS_REGISTER   (0x10)

#define GPIO_HANDLE_ADDR  (0x100)

//status data
#define PRU_IDLE           (0x00)
#define PRU_INITIALIZED    (0x01)
#define PRU_SHUTDOWN       (0x02)

//handle data - ports
#define HANDLE_PWM        (0x1)
#define HANDLE_ADC        (0x2)
#define HANDLE_DIO        (0x3)

//handle data - buses

//handle data - devices

//type data
#define TYPE_INIT         (0x1)
#define TYPE_SHUT         (0x2)
#define TYPE_IN           (0x3)
#define TYPE_OUT          (0x4)
#define TYPE_SETTING_SET  (0x5)
#define TYPE_SETTING_GET  (0x6)
//type relation
#define TYPE_SYS          (0x1)
#define TYPE_IO           (0x2)
#define TYPE_BUS	      (0x3)
#define TYPE_DEV          (0x4)

//conversion macros - handle
#define H_BAS(h)          (h & 0xff)
#define H_TYP(h)          ((h >> 8) & 0xff)
#define H_HAN(b, t)       (b | (t << 8))

#define HAN_BASE(x)       (x & 0xf)
#define HAN_PIN(x)        ((x >> 4) & 0xf)
#define HAN_HANDLE(b, p)  (b | (p << 4))

//conversion macros - type
#define T_TAG(t)          (t & 0xf)
#define T_TAK(t)          ((t >> 4) & 0xf)
#define T_TYP(ta, t)      (ta | (t << 4))

//conversion macros - settings
#define S_TYPE(t)         (t & 0xffff)
#define S_VAL(t)          ((t >> 16) & 0xffff)
#define S_DATA(t, v)      (t | (v << 16))

#endif /* DEFINES_H_ */
