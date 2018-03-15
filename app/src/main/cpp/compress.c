/*
 * create by Chanson 2017/04/28
 */

#include "compress.h"

typedef u_int8_t BYTE;

struct my_error_mgr {
    struct jpeg_error_mgr pub;
    jmp_buf setjmp_buffer;
};

typedef struct my_error_mgr *my_error_ptr;

METHODDEF(void)

my_error_exit(j_common_ptr cinfo) {
my_error_ptr myerr = (my_error_ptr) cinfo->err;
(*cinfo->err->output_message)(cinfo);
LOGW("jpeg_message_table[%d]:%s",
     myerr->pub.msg_code, myerr->pub.jpeg_message_table[myerr->pub.msg_code]);
longjmp(myerr
->setjmp_buffer, 1);
}

const char *jstringToString(JNIEnv *env, jstring jstr);

const BYTE *read_and_compress_jpeg(char *filename, int quality, int ow, int oh, const char *name,
                                   boolean optimize);

JNIEXPORT jint

JNICALL
Java_asuper_yt_cn_supermarket_tools_ImageCompress_nativeCompressBitmap(JNIEnv *env, jclass type,
                                                                       jstring srcFile_,
                                                                       jint quality,
                                                                       jint oversizeW,
                                                                       jint oversizeH,
                                                                       jstring dstFile_,
                                                                       jboolean optimize) {
    int ret;
    const char *srcFileName = jstringToString(env, srcFile_);
    const char *dstFileName = jstringToString(env, dstFile_);
    ret = read_and_compress_jpeg(srcFileName, quality, oversizeW, oversizeH, dstFileName, optimize);
    free((void *) dstFileName);
    return ret;
}

const char *jstringToString(JNIEnv *env, jstring jstr) {
    char *ret;
    const char *tempStr = (*env)->GetStringUTFChars(env, jstr, NULL);
    jsize len = (*env)->GetStringUTFLength(env, jstr);
    if (len > 0) {
        ret = (char *) malloc(len + 1);
        memcpy(ret, tempStr, len);
        ret[len] = 0;
    }
    (*env)->ReleaseStringUTFChars(env, jstr, tempStr);
    return ret;
}

const BYTE *read_and_compress_jpeg(char *filename, int quality, int ow, int oh, const char *name,
                                   boolean optimize) {

    struct jpeg_decompress_struct cinfo;
    struct my_error_mgr jerr;

    int w = 0, h = 0;
    FILE *infile;
    JSAMPARRAY buffer;
    int row_stride;

    if ((infile = fopen(filename, "rb")) == NULL) {
        fprintf(stderr, "can't open %s\n", filename);
        return 0;
    }
    cinfo.err = jpeg_std_error(&jerr.pub);
    jerr.pub.error_exit = my_error_exit;
    if (setjmp(jerr.setjmp_buffer)) {
        jpeg_destroy_decompress(&cinfo);
        fclose(infile);
        return 0;
    }
    jpeg_create_decompress(&cinfo);

    jpeg_stdio_src(&cinfo, infile);

    (void) jpeg_read_header(&cinfo, TRUE);

    w = cinfo.image_height;
    h = cinfo.image_width;
    double scale = sqrt(((double) (ow * oh)) / (w * h));
    int scale_num = 1;
    int scale_denom = 1;
    if (scale < 1) {
        scale_num = 10;
        scale_denom = (int) (10 / scale) + 1;
    }
    cinfo.scale_denom = scale_denom;
    cinfo.scale_num = scale_num;

    (void) jpeg_start_decompress(&cinfo);
    w = cinfo.output_width;
    h = cinfo.output_height;

    row_stride = cinfo.output_width * cinfo.output_components;
    buffer = (*cinfo.mem->alloc_sarray)
            ((j_common_ptr) & cinfo, JPOOL_IMAGE, row_stride, 1);

    int nComponent = 3;
    struct jpeg_compress_struct jcs;
    struct my_error_mgr jem;

    jcs.err = jpeg_std_error(&jem.pub);
    jem.pub.error_exit = my_error_exit;

    if (setjmp(jem.setjmp_buffer)) {
        return 0;
    }
    jpeg_create_compress(&jcs);
    FILE *f = fopen(name, "wb");
    if (f == NULL) {
        return 0;
    }

    jpeg_stdio_dest(&jcs, f);
    jcs.image_width = w;
    jcs.image_height = h;
    jcs.arith_code = false;
    jcs.input_components = nComponent;
    jcs.in_color_space = JCS_RGB;

    jpeg_set_defaults(&jcs);
    jcs.optimize_coding = optimize;

    jpeg_set_quality(&jcs, quality, true);
    jpeg_start_compress(&jcs, true);


    while (cinfo.output_scanline < cinfo.output_height) {
        (void) jpeg_read_scanlines(&cinfo, buffer, 1);
        jpeg_write_scanlines(&jcs, &buffer[0], 1);
    }

    (void) jpeg_finish_decompress(&cinfo);
    jpeg_finish_compress(&jcs);

    jpeg_destroy_compress(&jcs);
    fclose(f);
    jpeg_destroy_decompress(&cinfo);
    fclose(infile);
//    free((void*)buffer); //不需要再次free buffer，jpeg_destroy_decompress已经释放了buffer的数据，再次释放会报错
    return 1;
}

JNIEXPORT jint JNICALL
Java_asuper_yt_cn_supermarket_utils_ImageCompress_nativeCompressBitmap(JNIEnv *env, jclass type,
                                                                       jstring srcFile_,
                                                                       jint quality, jint overSizeW,
                                                                       jint overSizeH,
                                                                       jstring dstFile_,
                                                                       jboolean optimize) {
    const char *srcFile = (*env)->GetStringUTFChars(env, srcFile_, 0);
    const char *dstFile = (*env)->GetStringUTFChars(env, dstFile_, 0);

    // TODO

    (*env)->ReleaseStringUTFChars(env, srcFile_, srcFile);
    (*env)->ReleaseStringUTFChars(env, dstFile_, dstFile);
}