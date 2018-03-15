package asuper.yt.cn.supermarket.utils;

/**
 * Created by liaoqinsen on 2017/4/28 0008.
 */

public class ImageCompress {

    static {
        System.loadLibrary("compress");
    }

    private ImageCompress() {
    }

    /**
     * @param srcFile   源文件路径
     * @param quality   目标文件路径
     * @param overSizeW 最大分辨率
     * @param overSizeH 最大分辨率
     * @param dstFile   目标文件路径
     * @param optimize  压缩质量
     * @return
     */
    public static native int nativeCompressBitmap(String srcFile, int quality, int overSizeW, int overSizeH, String dstFile, boolean optimize);
}
