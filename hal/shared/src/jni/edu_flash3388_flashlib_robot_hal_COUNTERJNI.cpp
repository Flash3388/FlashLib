/*
 * edu_flash3388_flashlib_robot_hal_COUNTERJNI.cpp
 *
 *  Created on: Oct 5, 2017
 *      Author: root
 */

#include "edu_flash3388_flashlib_robot_hal_COUNTERJNI.h"

#include <jni.h>
#include <Counter.h>
#include <haltypes.h>

namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    initializePulseCounter
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_initializePulseCounter
	(JNIEnv *env, jclass obj, jint dioPort){
	hal_handle_t handle = HAL_initializePulseCounter(dioPort);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    freePulseCounter
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_freePulseCounter
	(JNIEnv *env, jclass obj, jint handle){
	HAL_freePulseCounter((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    resetPulseCounter
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_resetPulseCounter
	(JNIEnv *env, jclass obj, jint handle){
	HAL_resetPulseCounter((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    getPulseCounterCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterCount
	(JNIEnv *env, jclass obj, jint handle){
	uint32_t value = HAL_getPulseCounterCount((hal_handle_t)handle);
	return (jint)value;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    getPulseCounterPeriod
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterPeriod
(JNIEnv *env, jclass obj, jint handle){
	float value = HAL_getPulseCounterPeriod((hal_handle_t)handle);
	return (jfloat)value;
}


#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
