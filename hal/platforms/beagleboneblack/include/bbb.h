/*
 * bbb.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_H_
#define BBB_H_

#include <stdint.h>

#define BBB_HEADER_PIN_COUNT    (46)
#define BBB_PORT_TO_HEADER(p)   (p / BBB_HEADER_PIN_COUNT) //port / HEADER_PIN_COUNT
#define BBB_PORT_TO_PIN(p)      (p % BBB_HEADER_PIN_COUNT) //port % HEADER_PIN_COUNT

#define BBB_P8_HEADER           (0)
#define BBB_P9_HEADER           (1)

#define BBB_DIR_OUTPUT          (1)
#define BBB_DIR_INPUT           (0)

#ifdef __cplusplus
extern "C"{
#endif

int bbb_init();
void bbb_free();

char bbb_getportgpio(unsigned char header, unsigned char pin);
unsigned int bbb_getportid(unsigned char header, unsigned char pin);

void bbb_setdir(unsigned char header, unsigned char pin, unsigned char dir);
char bbb_getdir(unsigned char header, unsigned char pin);

void bbb_sethigh(unsigned char header, unsigned char pin);
void bbb_setlow(unsigned char header, unsigned char pin);

unsigned char bbb_ishigh(unsigned char header, unsigned char pin);
unsigned char bbb_islow(unsigned char header, unsigned char pin);

#ifdef __cplusplus
}
#endif

#endif /* BBB_H_ */
