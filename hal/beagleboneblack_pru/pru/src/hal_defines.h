/*
 * hal_defines.h
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */

#ifndef BBB_HAL_DEFINES_H_
#define BBB_HAL_DEFINES_H_

#include "bbb_defines.h"

#define HAL_AIN_SAMPLING_SIZE        (10)
#define HAL_AIN_SMAPLING_RATE        (1000)//us
#define HAL_AIN_CLK_DIVISOR          (160)
#define HAL_AIN_OPEN_DELAY           (0)
#define HAL_AIN_MAX_VALUE            (4095)
#define HAL_AIN_CONFIG_MODE          (0x1)//SW CONTINUOUS
#define HAL_AIN_STEP_AVG             (0x0)//STEP_AVG_1
#define HAL_AIN_SAMPLE_DELAY         (1)

#define HAL_PWMSS_PORTS_COUNT       (BBB_PWMSS_MODULE_COUNT * BBB_PWMSS_PORT_COUNT)
#define HAL_PWMSS_MAX_VALUE         (255)

//#define HAL_BBB_DEBUG
#define HAL_USE_IO

#endif /* BBB_HAL_DEFINES_H_ */
