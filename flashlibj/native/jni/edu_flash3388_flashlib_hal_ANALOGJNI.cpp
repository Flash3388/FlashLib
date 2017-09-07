/*
 * edu.flash3388.flashlib.hal.ANALOGJNI.h
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <jni.h>
#include <Analog.h>
#include "edu_flash3388_flashlib_hal_ANALOGJNI.h"

using namespace flashlib::hal;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    initializeAnalogInput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_initializeAnalogInput
  (JNIEnv *env, jclass thisobj , jint port){
	return HAL_initializeAnalogInputPort(port);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    initializeAnalogOutput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_initializeAnalogOutput
  (JNIEnv *env, jclass thisobj , jint port){
	return HAL_initializeAnalogOutputPort(port);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    freeAnalogInput
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_freeAnalogInput
  (JNIEnv *env, jclass thisobj , jint handle){
	HAL_freeAnalogInputPort(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    freeAnalogOutput
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_freeAnalogOutput
  (JNIEnv *env, jclass thisobj , jint handle){
	HAL_freeAnalogOutputPort(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    getAnalogValue
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_getAnalogValue
  (JNIEnv *env, jclass thisobj , jint handle){
	return HAL_getAnalogInputValue(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    getAnalogVoltage
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_getAnalogVoltage
  (JNIEnv *env, jclass thisobj , jint handle){
	return HAL_getAnalogInputVoltage(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    setAnalogVoltage
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_setAnalogVoltage
  (JNIEnv *env, jclass thisobj , jint handle, jfloat voltage){
	HAL_setAnalogOutputVoltage(handle, voltage);
}

/*
 * Class:     edu_flash3388_flashlib_hal_ANALOGJNI
 * Method:    setAnalogValue
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_ANALOGJNI_setAnalogValue
  (JNIEnv *env, jclass thisobj , jint handle, jint value){
	HAL_setAnalogOutputValue(handle, value);
}

#ifdef __cplusplus
}
#endif
