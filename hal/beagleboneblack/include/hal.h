/*
 * hal.h
 *
 *  Created on: Sep 5, 2017
 *      Author: root
 */

#ifndef BBB_HAL_H_
#define BBB_HAL_H_

#include <stdint.h>
#include <haltypes.h>

namespace flashlib{

namespace hal{


int BBB_initialize(int mode);
void BBB_shutdown();

/***********************************************************************\
 * DIO
\***********************************************************************/

bool BBB_checkDigitalPortValid(int8_t port);
bool BBB_checkDigitalPortTaken(int8_t port);

hal_handle_t BBB_initializeDIOPort(int8_t port, uint8_t dir);
void BBB_freeDIOPort(hal_handle_t portHandle);

void BBB_setDIO(hal_handle_t portHandle, int8_t high);
void BBB_pulseDIO(hal_handle_t portHandle, float length);

uint8_t BBB_getDIO(hal_handle_t portHandle);

/***********************************************************************\
 * ANALOG
\***********************************************************************/

bool BBB_checkAnalogInputPortValid(int8_t port);
bool BBB_checkAnalogInputPortTaken(int8_t port);

hal_handle_t BBB_initializeAnalogInput(int8_t port);
void BBB_freeAnalogInput(hal_handle_t portHandle);

uint32_t BBB_getAnalogValue(hal_handle_t portHandle);
float BBB_getAnalogVoltage(hal_handle_t portHandle);

float BBB_convertAnalogValueToVoltage(uint32_t value);
uint32_t BBB_convertAnalogVoltageToValue(float voltage);

int BBB_enableAnalogInputAccumulator(hal_handle_t portHandle, bool enable);
void BBB_resetAnalogInputAccumulator(hal_handle_t portHandle);
void BBB_setAnalogInputAccumulatorCenter(hal_handle_t portHandle, uint32_t center);
int64_t BBB_getAnalogInputAccumulatorValue(hal_handle_t portHandle);
uint32_t BBB_getAnalogInputAccumulatorCount(hal_handle_t portHandle);

float BBB_getGlobalAnalogSampleRate();
float BBB_getAnalogMaxVoltage();
uint32_t BBB_getAnalogMaxValue();

/***********************************************************************\
 * PWM
\***********************************************************************/

bool BBB_checkPWMPortValid(int8_t port);
bool BBB_checkPWMPortTaken(int8_t port);

hal_handle_t BBB_initializePWMPort(int8_t port);
void BBB_freePWMPort(hal_handle_t portHandle);

uint8_t BBB_getPWMValue(hal_handle_t portHandle);
float BBB_getPWMDuty(hal_handle_t portHandle);

void BBB_setPWMValue(hal_handle_t portHandle, int8_t value);
void BBB_setPWMDuty(hal_handle_t portHandle, float duty);

void BBB_setPWMFrequency(hal_handle_t portHandle, float frequency);
float BBB_getPWMFrequency(hal_handle_t portHandle);

/***********************************************************************\
 * Pulse Counter
\***********************************************************************/

hal_handle_t BBB_initializePulseCounter(hal_handle_t dioPort);
hal_handle_t BBB_initializePulseCounter(hal_handle_t upPort, hal_handle_t downPort);
void BBB_freePulseCounter(hal_handle_t counterHandle);

void BBB_resetPulseCounter(hal_handle_t counterHandle);

uint8_t BBB_getPulseCounterDirection(hal_handle_t counterHandle);
uint32_t BBB_getPulseCounterCount(hal_handle_t counterHandle);
float BBB_getPulseCounterPeriod(hal_handle_t counterHandle);
float BBB_getPulseCounterLength(hal_handle_t counterHandle);

bool BBB_isPulseCounterQuadrature(hal_handle_t counterHandle);

} /* namespace hal */

} /* namespace flashlib */

#endif /* BBB_HAL_H_ */
