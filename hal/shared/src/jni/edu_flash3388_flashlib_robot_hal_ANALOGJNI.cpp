/*
 * edu_flash3388_flashlib_robot_hal_ANALOGJNI.cpp
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */

#include "edu_flash3388_flashlib_robot_hal_ANALOGJNI.h"

#include <jni.h>
#include <Analog.h>
#include <haltypes.h>


namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getGlobalSampleRate
 * Signature: (V)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getGlobalSampleRate
  (JNIEnv *env, jclass obj){
	return (jfloat)HAL_getGlobalAnalogSampleRate();
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getMaxAnalogPortVoltage
 * Signature: (V)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getMaxAnalogPortVoltage
  (JNIEnv *env, jclass obj){
	return (jfloat)HAL_getAnalogMaxVoltage();
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getMaxAnalogPortValue
 * Signature: (V)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getMaxAnalogPortValue
  (JNIEnv *env, jclass obj){
	return (jint)HAL_getAnalogMaxValue();
}


/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    initializeAnalogInputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_initializeAnalogInputPort
  (JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeAnalogInputPort((int8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    initializeAnalogOutputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_initializeAnalogOutputPort
  (JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeAnalogOutputPort((int8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    freeAnalogInputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_freeAnalogInputPort
  (JNIEnv *env, jclass obj, jint handle){
	HAL_freeAnalogInputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    freeAnalogOutputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_freeAnalogOutputPort
  (JNIEnv *env, jclass obj, jint handle){
	HAL_freeAnalogOutputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getAnalogValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getAnalogValue
  (JNIEnv *env, jclass obj, jint handle){
	uint32_t value = HAL_getAnalogVoltage((hal_handle_t)handle);
	return (jint)value;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getAnalogVoltage
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getAnalogVoltage
  (JNIEnv *env, jclass obj, jint handle){
	return HAL_getAnalogVoltage((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    setAnalogVoltage
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_setAnalogVoltage
  (JNIEnv *env, jclass obj, jint handle, jfloat voltage){
	HAL_setAnalogVoltage((hal_handle_t)handle, voltage);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    setAnalogValue
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_setAnalogValue
  (JNIEnv *env, jclass obj, jint handle, jint value){
	HAL_setAnalogValue((hal_handle_t)handle, (uint32_t)value);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    enableAnalogInputAccumulator
 * Signature: (IB)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_enableAnalogInputAccumulator
  (JNIEnv *env, jclass obj, jint handle, jboolean enable){
	int result = 0;
	if(enable){
		result = HAL_enableAnalogInputAccumulator((hal_handle_t)handle);
	}else{
		result = HAL_disableAnalogInputAccumulator((hal_handle_t)handle);
	}
	return result;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    resetAnalogInputAccumulator
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_resetAnalogInputAccumulator
  (JNIEnv *env, jclass obj, jint handle){
	HAL_resetAnalogInputAccumulator((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    setAnalogInputAccumulatorCenter
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_setAnalogInputAccumulatorCenter
  (JNIEnv *env, jclass obj, jint handle, jint center){
	HAL_setAnalogInputAccumulatorCenter((hal_handle_t)handle, (uint32_t)center);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getAnalogInputAccumulatorValue
 * Signature: (I)L
 */
JNIEXPORT jlong JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getAnalogInputAccumulatorValue
  (JNIEnv *env, jclass obj, jint handle){
	int64_t count = HAL_getAnalogInputAccumulatorValue((hal_handle_t)handle);
	return (jlong)count;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    getAnalogInputAccumulatorCount
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_getAnalogInputAccumulatorCount
  (JNIEnv *env, jclass obj, jint handle){
	uint32_t count = HAL_getAnalogInputAccumulatorCount((hal_handle_t)handle);
	return (jint)count;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    convertAnalogValueToVoltage
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_convertAnalogValueToVoltage
  (JNIEnv *env, jclass obj, jint value){
	return (jfloat)HAL_convertAnalogValueToVoltage((uint32_t)value);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_ANALOGJNI
 * Method:    convertAnalogValueToVoltage
 * Signature: (F)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_ANALOGJNI_convertAnalogVoltageToValue
  (JNIEnv *env, jclass obj, jfloat voltage){
	return (jint)HAL_convertAnalogVoltageToValue((float)voltage);
}


#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
