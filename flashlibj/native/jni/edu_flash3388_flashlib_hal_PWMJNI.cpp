/*
 * edu_flash3388_flashlib_hal_PWMJNI.cpp
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */


#include <jni.h>
#include <PWM.h>
#include <haltypes.h>

#include "edu_flash3388_flashlib_hal_PWMJNI.h"

namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    initializePWMPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_initializePWMPort
 	 (JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializePWMPort((uint8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    freePWMPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_freePWMPort
	(JNIEnv *env, jclass obj, jint handle){
	HAL_freePWMPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    setPWMRaw
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_setPWMRaw
	(JNIEnv *env, jclass obj, jint handle, jint raw){
	HAL_setPWMValue((hal_handle_t)handle, (uint8_t)raw);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    setPWMDuty
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_setPWMDuty
	(JNIEnv *env, jclass obj, jint handle, jfloat duty){
	HAL_setPWMDuty((hal_handle_t)handle, duty);
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    getPWMRaw
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_getPWMRaw
	(JNIEnv *env, jclass obj, jint handle){
	uint8_t value = HAL_getPWMValue((hal_handle_t)handle);
	return (jint)value;
}

/*
 * Class:     edu_flash3388_flashlib_hal_PWMJNI
 * Method:    getPWMDuty
 * Signature: (I)F
 */
JNIEXPORT jfloat JNICALL Java_edu_flash3388_flashlib_hal_PWMJNI_getPWMDuty
	(JNIEnv *env, jclass obj, jint handle){
	return HAL_getPWMDuty((hal_handle_t)handle);
}

#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
