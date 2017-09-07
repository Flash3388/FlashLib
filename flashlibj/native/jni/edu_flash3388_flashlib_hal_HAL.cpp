/*
 * edu_flash3388_flashlib_hal_HAL.cpp
 *
 *  Created on: Aug 27, 2017
 *      Author: root
 */

#include <jni.h>
#include <HAL.h>
#include "edu_flash3388_flashlib_hal_HAL.h"

using namespace flashlib::hal;

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    initialize
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_HAL_initialize
  (JNIEnv *env, jclass thisObj){
	int status = 0;
	HAL_initialize(&status);
	return status;
}

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    shutdown
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_HAL_shutdown
  (JNIEnv *env, jclass thisObj){
	HAL_shutdown();
	return 0;
}

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    boardName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_edu_flash3388_flashlib_hal_HAL_boardName
  (JNIEnv *env, jclass thisObj){
	return env->NewStringUTF(HAL_boardName());
}

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    getClockTime
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_edu_flash3388_flashlib_hal_HAL_getClockTime
  (JNIEnv *env, jclass thisObj){
	return HAL_clockMS();
}

#ifdef __cplusplus
}
#endif


