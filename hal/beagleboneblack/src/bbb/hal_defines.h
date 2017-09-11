/*
 * hal_defines.h
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */

#ifndef SRC_BBB_HAL_DEFINES_H_
#define SRC_BBB_HAL_DEFINES_H_

#include <bbb_defines.h>

#define HAL_AIN_SAMPLING_SIZE        (10)
#define HAL_AIN_SMAPLING_RATE        (10)//ms
#define HAL_AIN_CLK_DIVISOR          (160)
#define HAL_AIN_OPEN_DELAY           (0)//ms
#define HAL_AIN_MAX_VALUE            (4095.0f)
#define HAL_AIN_VALUE_TO_VOLTAGE(v)  (v / HAL_AIN_MAX_VALUE * BBB_ADC_MAX_VOLTAGE)

#define HAL_PWMSS_PORTS_COUNT       (BBB_PWMSS_MODULE_COUNT * BBB_PWMSS_PORT_COUNT)
#define HAL_PWMSS_MAX_VALUE         (255.0f)
#define HAL_PWMSS_VALUE_TO_DUTY(d)  (d / HAL_PWMSS_MAX_VALUE)
#define HAL_PWMSS_DUTY_TO_VALUE(d)  (d * HAL_PWMSS_MAX_VALUE)

#endif /* SRC_BBB_HAL_DEFINES_H_ */
