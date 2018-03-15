//
// Created by Administrator on 12/21/2016.
//

#ifndef COMPRESS_COMPRESS_H_H
#define COMPRESS_COMPRESS_H_H

#include "lang.h"
#include <stdlib.h>
#include <android/bitmap.h>

#include <setjmp.h>
#include <jpeglib.h>
#include <math.h>

#define true 1
#define false 0

//#ifdef __cplusplus
//extern "C" {
//#endif
JNIEXPORT jint JNICALL
Java_asuper_yt_cn_supermarket_tools_ImageCompress_nativeCompressBitmap(JNIEnv *env, jclass type,
                                                               jstring srcFile_, jint quality,
                                                                       jint oversizeW,
                                                                       jint oversizeH,
                                                               jstring dstFile_,
                                                               jboolean optimize);
//
//#ifdef __cplusplus
//}
//#endif
#endif //COMPRESS_COMPRESS_H_H
