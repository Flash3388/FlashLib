/*
 * edu_flash3388_flashlib_hal_DIOJNI.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <jni.h>
#include <DIO.h>
#include "edu_flash3388_flashlib_hal_DIOJNI.h"

using namespace flashlib::hal;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    initializeDigitalInput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_initializeDigitalInput
  (JNIEnv *enc, jclass thisObj, jint port){
	return HAL_initializeDIOPort(port, DIO_INPUT);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    initializeDigitalOutput
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_initializeDigitalOutput
  (JNIEnv *enc, jclass thisObj, jint port){
	return HAL_initializeDIOPort(port, DIO_OUTPUT);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    freeDigitalInput
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_freeDigitalInput
  (JNIEnv *enc, jclass thisObj, jint handle){
	HAL_freeDIOPort(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    freeDigitalOutput
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_freeDigitalOutput
  (JNIEnv *enc, jclass thisObj, jint handle){
	HAL_freeDIOPort(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    get
 * Signature: (I)Z
 */
JNIEXPORT jboolean JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_get
  (JNIEnv *enc, jclass thisObj, jint handle){
	return HAL_getDIO(handle);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    set
 * Signature: (IZ)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_set
  (JNIEnv *enc, jclass thisObj, jint handle, jboolean high){
	HAL_setDIO(handle, high);
}

/*
 * Class:     edu_flash3388_flashlib_hal_DIOJNI
 * Method:    pulseOut
 * Signature: (ID)V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_DIOJNI_pulseOut
  (JNIEnv *enc, jclass thisObj, jint handle, jfloat pulseLength){
	HAL_pulseOutDIO(handle, pulseLength);
}

#ifdef __cplusplus
}
#endif

