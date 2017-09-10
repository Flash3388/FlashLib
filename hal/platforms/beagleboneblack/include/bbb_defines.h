/*
 * bbb_defines.h
 *
 *  Created on: Sep 7, 2017
 *      Author: root
 */

#ifndef BBB_DEFINES_H_
#define BBB_DEFINES_H_

#define BBB_DIR_OUTPUT               (1)
#define BBB_DIR_INPUT                (0)

#define BBB_HEADER_PIN_COUNT         (46)

#define BBB_P8_HEADER                (0)
#define BBB_P9_HEADER                (1)

#define BBB_PWMSS_MODULE_COUNT       (3)
#define BBB_PWMSS_PORT_COUNT         (2)

#define BBB_PWMSS0                   (0)
#define BBB_PWMSS1                   (1)
#define BBB_PWMSS2                   (2)
#define BBB_PWMSSA                   (0)
#define BBB_PWMSSB                   (1)


#define BBB_GPIO_PORT_TO_HEADER(p)   (p / BBB_HEADER_PIN_COUNT)
#define BBB_GPIO_PORT_TO_PIN(p)      (p % BBB_HEADER_PIN_COUNT)
#define BBB_GPIO_PORT(h, p)          (h * BBB_HEADER_PIN_COUNT + p)

#define BBB_PWMSS_PORT_TO_MODULE(p)   (p / BBB_PWMSS_PORT_COUNT)
#define BBB_PWMSS_PORT_TO_PIN(p)      (p % BBB_PWMSS_PORT_COUNT)
#define BBB_PWMSS_PORT(m, p)          (m * BBB_PWMSS_PORT_COUNT + p)

#endif /* BBB_DEFINES_H_ */
