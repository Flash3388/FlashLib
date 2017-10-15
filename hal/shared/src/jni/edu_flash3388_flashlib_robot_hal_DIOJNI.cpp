/*
 * edu_flash3388_flashlib_robot_hal_DIOJNI.cpp
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */


#include "edu_flash3388_flashlib_robot_hal_DIOJNI.h"

#include <jni.h>
#include <DIO.h>
#include <haltypes.h>


namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    checkDigitalInputPortValid
 * Signature: (B)I
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_checkDigitalInputPortValid
  (JNIEnv *env, jclass obj, jint port){
	return HAL_checkDigitalInputPortValid((int8_t)port);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    checkDigitalInputPortTaken
 * Signature: (B)I
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_checkDigitalInputPortTaken
  (JNIEnv *env, jclass obj, jint port){
	return HAL_checkDigitalInputPortTaken((int8_t)port);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    checkDigitalOutputPortValid
 * Signature: (B)I
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_checkDigitalOutputPortValid
  (JNIEnv *env, jclass obj, jint port){
	return HAL_checkDigitalOutputPortValid((int8_t)port);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    checkDigitalOutputPortTaken
 * Signature: (B)I
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_checkDigitalOutputPortTaken
  (JNIEnv *env, jclass obj, jint port){
	return HAL_checkDigitalOutputPortTaken((int8_t)port);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    initializeDigitalInputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_initializeDigitalInputPort
	(JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeDigitalInputPort((int8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    initializeDigitalOutputPort
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_initializeDigitalOutputPort
	(JNIEnv *env, jclass obj, jint port){
	hal_handle_t handle = HAL_initializeDigitalOutputPort((int8_t)port);
	return (jint)handle;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    freeDigitalInputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_freeDigitalInputPort
	(JNIEnv *env, jclass obj, jint handle){
	HAL_freeDigitalInputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    freeDigitalOutputPort
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_freeDigitalOutputPort
	(JNIEnv *env, jclass obj, jint handle){
	HAL_freeDigitalOutputPort((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    getDIO
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_getDIO
	(JNIEnv *env, jclass obj, jint handle){
	if(HAL_getDIOHigh((hal_handle_t)handle))
		return 1;
	return 0;
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    setDIO
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_setDIO
	(JNIEnv *env, jclass obj, jint handle, jboolean high){
	if(high)
		HAL_setDIOHigh((hal_handle_t)handle);
	else
		HAL_setDIOLow((hal_handle_t)handle);
}

/*
 * Class:     edu_flash3388_flashlib_robot_hal_DIOJNI
 * Method:    pulseOutDIO
 * Signature: (IF)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_robot_hal_DIOJNI_pulseOutDIO
	(JNIEnv *env, jclass obj, jint handle, jfloat length){
	HAL_pulseOutDIO((hal_handle_t)handle, length);
}

#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
