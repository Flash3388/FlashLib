/*
 * edu_flash3388_flashlib_hal_PWMJNI.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <jni.h>
#include <PWM.h>
#include "edu_flash3388_flashlib_hal_PWMJNI.h"

using namespace flashlib::hal;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    initializePWM
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_initializePWM
  (JNIEnv *env, jclass thisObj, jint port){
	return HAL_initializePWMPort(port);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    freePWM
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_freePWM
  (JNIEnv *env, jclass thisObj, jint handle){
	HAL_freePWMPort(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    setRaw
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_setRaw
  (JNIEnv *env, jclass thisObj, jint handle, jint value){
	HAL_setPWMValue(handle, value);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    setDuty
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_setDuty
  (JNIEnv *env, jclass thisObj, jint handle, jfloat duty){
	HAL_setPWMDuty(handle, duty);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    getRaw
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_getRaw
  (JNIEnv *env, jclass thisObj, jint handle){
	return HAL_getPWMValue(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    getDuty
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_getDuty
  (JNIEnv *env, jclass thisObj, jint handle){
	return HAL_getPWMDuty(handle);
}

#ifdef __cplusplus
}
#endif


