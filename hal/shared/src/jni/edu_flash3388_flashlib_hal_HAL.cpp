/*
 * edu_flash3388_flashlib_hal_HAL.cpp
 *
 *  Created on: Sep 11, 2017
 *      Author: root
 */


#include <jni.h>
#include <HAL.h>
#include <haltypes.h>

#include "edu_flash3388_flashlib_hal_HAL.h"

namespace flashlib{

namespace hal{

#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    initialize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_edu_flash3388_flashlib_hal_HAL_initialize
	(JNIEnv *env, jclass obj, jint mode){
	return HAL_initialize(mode);
}

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    shutdown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_edu_flash3388_flashlib_hal_HAL_shutdown
	(JNIEnv *env, jclass obj){
	HAL_shutdown();
}

/*
 * Class:     edu_flash3388_flashlib_hal_HAL
 * Method:    boardName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_edu_flash3388_flashlib_hal_HAL_boardName
	(JNIEnv *env, jclass obj){
	const char* name = HAL_boardName();
	return env->NewStringUTF(name);
}

#ifdef __cplusplus
}
#endif

} /* namespace hal */

} /* namespace flashlib */
