package asuper.yt.cn.supermarket.base;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import asuper.yt.cn.supermarket.entities.ImgUploading;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.utils.ToolImageOptimizer;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Action;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolFile;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressPopupWindow;

/**
 * Created by liaoqinsen on 2017/9/14 0014.
 */

public abstract class UploadStore extends Store {

    public static final int REQUEST_UPLOAD = 0x1456;
    protected List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItemList = new ArrayList<>();
    private BaseActivity baseActivity;

    public UploadStore(StoreDependencyDelegate delegate) {
        super(delegate);
        if(delegate.getReducer() instanceof BaseActivity) {
            baseActivity = (BaseActivity) delegate.getReducer();
        }else if(delegate.getReducer() instanceof BaseFragment){
            baseActivity = (BaseActivity) ((BaseFragment) delegate.getReducer()).getActivity();
        }
    }

    @Override
    public final void doAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        if(type == REQUEST_UPLOAD){
            uploadImg(type,data,callBack);
        }else{
            DoAction(type,data,callBack);
        }
    }

    protected abstract void DoAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack);

    public void uploadImg(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        final HashMap<String, Object> uploadMap = new HashMap<>();
        final HashMap<String, PhotoInfo> uploadMapReWrite = new HashMap<>();
        if (imageGalleryItemList == null) {
            callBack.onResult(type,false);
            return;
        }
        ToolLog.i(imageGalleryItemList);
        String  json = ToolGson.toJson(imageGalleryItemList);
        ToolLog.json(json);
        for (int i = 0; i < imageGalleryItemList.size(); i++) {
            for (int j = 0; j < imageGalleryItemList.get(i).photoInfo.size(); j++) {
                if (imageGalleryItemList.get(i).photoInfo.get(j).getPhotoId() == 0) {
                    uploadMap.put(imageGalleryItemList.get(i).info.key + "&" + j, imageGalleryItemList.get(i).photoInfo.get(j).getPhotoPath());
                    uploadMapReWrite.put(imageGalleryItemList.get(i).info.key + "&" + j, imageGalleryItemList.get(i).photoInfo.get(j));
                }
            }
        }

        if (uploadMap.size() < 1) {
            callBack.onResult(type,true);
            return;
        }
        ToolImageOptimizer.optimizeImageAsync(uploadMap, new ToolImageOptimizer.OptimizerCallBack() {
            @Override
            public void onComplete() {
                uploadMap.put("systemCode", "sihd");
                ToolOkHTTP.upload(uploadMap, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        ImgUploading imgUploading = ToolGson.fromJson(response.toString(), ImgUploading.class);
                        if (imgUploading.getResponseCode() == 200) {
                            Set<String> keyset = imgUploading.getFileUrls().keySet();
                            for (String key : keyset) {
                                uploadMapReWrite.get(key).setPhotoId(1);
                                Log.d("lqs", imgUploading.getFileUrls().get(key).toString());
                                String path = String.valueOf(imgUploading.getFileUrls().get(key));
                                if (path.startsWith("[")) {
                                    path = path.substring(1);
                                }
                                if (path.endsWith("]")) {
                                    path = path.substring(0, path.length() - 1);
                                }
                                uploadMapReWrite.get(key).setPhotoPath(path);
                            }
                            File file = new File(YTApplication.get().getCacheDir() + "/" + Config.UserInfo.USER_ID + "/" + imageGalleryItemList.get(0).storeType);
                            ToolFile.deleteDir(file);
                            callBack.onResult(type, true);
                        } else {
                            callBack.onResult(type, false);
                        }

                        baseActivity.dismissProgress();
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, false);
                        baseActivity.dismissProgress();
                    }

                }, new ToolOkHTTP.ProgressRequestListener() {
                    @Override
                    public void onRequestProgress(long byteCount, final long bytesWritten, final long contentLength, final boolean done) {
                        if(baseActivity == null) return;
                        baseActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                baseActivity.showProgress("正在上传图片("+(int)(((float)bytesWritten/(float)contentLength)*100)+"%)");
                            }
                        });
                    }
                });
            }



            @Override
            public void onError(String msg) {
                callBack.onResult(type, false);
            }


            @Override
            public void onProgress(final int current, final int total) {
                if(baseActivity == null) return;
                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        baseActivity.showProgress("正在处理图片("+current+"/"+total+")");
                    }
                });
            }
        });
    }

}
