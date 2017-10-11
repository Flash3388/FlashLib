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
	hal_handle_t handle = HAL_initializePulseCounter((int8_t)dioPort);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    initializeQuadPulseCounter
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_initializeQuadPulseCounter
  (JNIEnv *env, jclass obj, jint upPort, jint downPort){
	hal_handle_t handle = HAL_initializeQuadPulseCounter((int8_t)upPort, (int8_t)downPort);
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
 * Method:    getPulseCounterDirection
 * Signature: (B)I
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterDirection
  (JNIEnv *env, jclass obj, jint handle){
	uint8_t direction = HAL_getPulseCounterDirection((hal_handle_t)handle);
	if(direction != COUNTER_DIR_BACKWARD)
		return 1;
	return 0;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    getPulseCounterCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterPulseCount
	(JNIEnv *env, jclass obj, jint handle){
	uint32_t value = HAL_getPulseCounterPulseCount((hal_handle_t)handle);
	return (jint)value;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    getPulseCounterPulseLength
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterPulseLength
(JNIEnv *env, jclass obj, jint handle){
	float value = HAL_getPulseCounterPulseLength((hal_handle_t)handle);
	return (jfloat)value;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    getPulseCounterPeriod
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_getPulseCounterPulsePeriod
(JNIEnv *env, jclass obj, jint handle){
	float value = HAL_getPulseCounterPulsePeriod((hal_handle_t)handle);
	return (jfloat)value;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_COUNTERJNI
 * Method:    isPulseCounterQuadrature
 * Signature: (I)B
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_COUNTERJNI_isPulseCounterQuadrature
  (JNIEnv *env, jclass obj, jint handle){
	uint8_t quad = HAL_isPulseCounterQuadrature((hal_handle_t)handle);
	return (jboolean)quad;
}


#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
