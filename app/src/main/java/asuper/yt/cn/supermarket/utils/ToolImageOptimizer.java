package asuper.yt.cn.supermarket.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolFile;

/**
 * Created by liaoqinsen on 2017/4/8 0008.
 */

public class ToolImageOptimizer {
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private static final String PATH_ROOT = "ytxctz_img_cache";
    private static final String FORMAT_ERR = "format_err";
    public static final int FORMAT_JPEG = 0x100;
    private static Handler handler;

    public static String optimizeImage(final String source) {
        if (source == null) return null;
        File fileSource = new File(source);
        if (!fileSource.exists()) return null;
        String[] temp = source.split("/");
        String target = temp[temp.length - 1];
        File file = new File(ToolFile.getAppRootPath() + "/" + PATH_ROOT + "/");
        if (!file.exists()) {
            file.mkdirs();
        }
        target = ToolFile.getAppRootPath() + "/" + PATH_ROOT + "/" + target;
        File file1 = new File(target);
        if (!file1.exists()) {
            ImageCompress.nativeCompressBitmap(source, 40, 1920, 1080, target, true);
        }
        if(!file1.exists()){
            FileOutputStream fileOutputStream = null;
            Bitmap bitmap = null;
            try {
                fileOutputStream = new FileOutputStream(file1);
                bitmap = BitmapFactory.decodeFile(source);
                bitmap.compress(Bitmap.CompressFormat.JPEG,40,fileOutputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(bitmap != null) bitmap.recycle();
            }
        }

        if(!file1.exists()){
            return  FORMAT_ERR;
        }

        file1 = null;
        return target;
    }

    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,
                                   double newHeight) {
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        bgimage.recycle();
        return bitmap;
    }

    public static void optimizeImageAsync(final String source, final OptimizerCallBack callBack) {
        if (handler == null && Looper.getMainLooper() != null) {
            handler = new Handler(Looper.getMainLooper());
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if (optimizeImage(source) == null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null)
                                callBack.onError("文件不存在,请检查是否被删除！\n(" + source + ")");
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) callBack.onComplete();
                        }
                    });
                }
            }
        });
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    public static void optimizeImageAsync(final List<PhotoInfo> sources, final OptimizerCallBack callBack) {
        if (handler == null && Looper.getMainLooper() != null) {
            handler = new Handler(Looper.getMainLooper());
        }
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for (PhotoInfo photoInfo : sources) {
                    if (photoInfo.getPhotoId() == 0) {
                        photoInfo.setPhotoPath(optimizeImage(photoInfo.getPhotoPath()));
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) callBack.onComplete();
                    }
                });
            }
        });
    }

    public static void  optimizeImageAsync(final HashMap<String, Object> sources, final OptimizerCallBack callBack) {
        optimizeImageAsync(sources, callBack, false);
    }

    public static void optimizeImageAsync(final HashMap<String, Object> sources, final OptimizerCallBack callBack, boolean multiThread) {
        if (handler == null && Looper.getMainLooper() != null) {
            handler = new Handler(Looper.getMainLooper());
        }
        if (sources == null) return;
        final Set<String> ketset = sources.keySet();
        final List<String> keylist = new ArrayList<>();
        keylist.addAll(ketset);
        final int size = keylist.size() / 3;
        if (size > 0 && multiThread) {
            final AtomicInteger atomicInteger = new AtomicInteger(0);
            final AtomicInteger multiThreadNumber = new AtomicInteger(0);
            multiThreadNumber.set(0);
            for (int i = 0; i < keylist.size(); i += size) {
                final int index = i;
                multiThreadNumber.addAndGet(1);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = index; i < index + size; i++) {
                            if (keylist.size() > i) {
                                final Object obj = sources.get(keylist.get(i));
                                if (obj == null) continue;
                                String target = optimizeImage(obj.toString());
                                if (target == null) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (callBack != null)
                                                callBack.onError("文件不存在,请检查是否被删除！\n(" + obj.toString() + ")");
                                        }
                                    });
                                    return;
                                }else if(FORMAT_ERR.equals(target)){
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (callBack != null)
                                                callBack.onError("暂时不支持该格式的图片！\n(" + obj.toString() + ")");
                                        }
                                    });
                                    return;
                                }
                                sources.put(keylist.get(i), new File(target));
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (callBack != null)
                                            callBack.onProgress(atomicInteger.addAndGet(1), ketset.size());
                                    }
                                });
                            }
                        }
                        if (multiThreadNumber.addAndGet(-1) == 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null) callBack.onComplete();
                                }
                            });
                        }
                    }
                });
            }
        } else {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    int i = 0;
                    for (String key : ketset) {
                        i++;
                        final Object obj = sources.get(key);
                        if (obj == null) continue;
                        String target = optimizeImage(obj.toString());
                        if (target == null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (callBack != null)
                                        callBack.onError("文件不存在,请检查是否被删除！\n(" + obj.toString() + ")");
                                }
                            });
                            return;
                        }
                        sources.put(key, new File(target));
                        final int current = i;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callBack != null) callBack.onProgress(current, ketset.size());
                            }
                        });
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) callBack.onComplete();
                        }
                    });
                }
            });
        }
    }

    public interface OptimizerCallBack {
        void onComplete();

        void onError(String msg);

        void onProgress(int current, int total);
    }
}
