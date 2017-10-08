/*
 * bbb_defines.h
 *
 *  Created on: Sep 15, 2017
 *      Author: root
 */

#ifndef BBB_DEFINES_H_
#define BBB_DEFINES_H_

/***********************************************************************\
 * GPIO
\***********************************************************************/

#define BBB_DIR_OUTPUT               (1)
#define BBB_DIR_INPUT                (0)

#define BBB_GPIO_HIGH                (1)
#define BBB_GPIO_LOW                 (0)

#define BBB_HEADER_PIN_COUNT         (46)

#define BBB_P8_HEADER                (0)
#define BBB_P9_HEADER                (1)

#define BBB_GPIO_PORTS_COUNT         (96)//headers * pins per header

#define BBB_GPIO_PORT_TO_HEADER(p)   (p / BBB_HEADER_PIN_COUNT)
#define BBB_GPIO_PORT_TO_PIN(p)      (p % BBB_HEADER_PIN_COUNT)
#define BBB_GPIO_PORT(h, p)          (h * BBB_HEADER_PIN_COUNT + p)

#define BBB_MODULE_PIN_COUNT         (32)

#define BBB_GPIO_PORT_TO_MODULE(p)   (p / BBB_HEADER_PIN_COUNT)
#define BBB_GPIO_PORT_TO_MPIN(p)     (p % BBB_HEADER_PIN_COUNT)

#define BBB_GPIO_MPORTS_COUNT        (128)//modules * pins per module

/***********************************************************************\
 * PWMSS
\***********************************************************************/

#define BBB_PWMSS_MODULE_COUNT       (3)
#define BBB_PWMSS_PORT_COUNT         (2)

#define BBB_PWMSS0                   (0)
#define BBB_PWMSS1                   (1)
#define BBB_PWMSS2                   (2)
#define BBB_PWMSSA                   (0)
#define BBB_PWMSSB                   (1)

#define BBB_PWMSS_PORT_TO_MODULE(p)   (p / BBB_PWMSS_PORT_COUNT)
#define BBB_PWMSS_PORT_TO_PIN(p)      (p % BBB_PWMSS_PORT_COUNT)
#define BBB_PWMSS_PORT(m, p)          (m * BBB_PWMSS_PORT_COUNT + p)

#define BBB_PWMSS0A                  (0)
#define BBB_PWMSS0B                  (1)
#define BBB_PWMSS1A                  (2)
#define BBB_PWMSS1B                  (3)
#define BBB_PWMSS2A                  (4)
#define BBB_PWMSS2B                  (5)

/***********************************************************************\
 * ADCTCS
\***********************************************************************/

#define BBB_ADC_CHANNEL_COUNT        (7)
#define BBB_ADC_MAX_VOLTAGE          (1.8f)

#define BBB_ADC_AIN0                 (0)
#define BBB_ADC_AIN1                 (1)
#define BBB_ADC_AIN2                 (2)
#define BBB_ADC_AIN3                 (3)
#define BBB_ADC_AIN4                 (4)
#define BBB_ADC_AIN5                 (5)
#define BBB_ADC_AIN6                 (6)

#endif /* BBB_DEFINES_H_ */
