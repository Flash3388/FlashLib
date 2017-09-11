/*
 * edu_flash3388_flashlib_hal_ANALOGJNI.cpp
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */

#include <jni.h>
#include <Analog.h>
#include <haltypes.h>

#include "edu_flash3388_flashlib_hal_ANALOGJNI.h"

namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif


/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    initializeAnalogInputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_initializeAnalogInputPort
  (JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeAnalogInputPort((uint8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    initializeAnalogOutputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_initializeAnalogOutputPort
  (JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeAnalogOutputPort((uint8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    freeAnalogInputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_freeAnalogInputPort
  (JNIEnv *env, jclass obj, jint handle){
	HAL_freeAnalogInputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    freeAnalogOutputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_freeAnalogOutputPort
  (JNIEnv *env, jclass obj, jint handle){
	HAL_freeAnalogOutputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    getAnalogValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_getAnalogValue
  (JNIEnv *env, jclass obj, jint handle){
	uint32_t value = HAL_getAnalogVoltage((hal_handle_t)handle);
	return (jint)value;
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    getAnalogVoltage
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_getAnalogVoltage
  (JNIEnv *env, jclass obj, jint handle){
	return HAL_getAnalogVoltage((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    setAnalogVoltage
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_setAnalogVoltage
  (JNIEnv *env, jclass obj, jint handle, jfloat voltage){
	HAL_setAnalogVoltage((hal_handle_t)handle, voltage);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    setAnalogValue
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_setAnalogValue
  (JNIEnv *env, jclass obj, jint handle, jint value){
	HAL_setAnalogValue((hal_handle_t)handle, (uint32_t)value);
}


#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
