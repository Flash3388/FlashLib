/*
 * hw_types.h
 *
 *  Created on: Jun 16, 2017
 *      Author: root
 */

#ifndef HW_HW_TYPES_H_
#define HW_HW_TYPES_H_

#define HWREG(x)       (*((volatile unsigned int *)(x)))
#define HWREGH(x)      (*((volatile unsigned short *)(x)))
#define HWREGB(x)      (*((volatile unsigned char *)(x)))

#endif /* HW_HW_TYPES_H_ */
