package asuper.yt.cn.supermarket.modules.newclient;

import android.util.Log;

import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.entities.ImgUploading;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclew.NewMission;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolImageOptimizer;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolFile;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressPopupWindow;

/**
 * Created by liaoqinsen on 2017/9/8 0008.
 */

public class AddNewClientStore extends Store {
    private boolean isNew = false;
    private NewMission newMission;
    private ClientInfoDetail clientInfoDetail;
    private BaseActivity baseActivity;

    public AddNewClientStore(StoreDependencyDelegate delegate) {
        super(delegate);
        baseActivity = (BaseActivity) delegate.getReducer();
    }

    @Override
    public void doAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        switch (type) {
            case AddNewClientActivity.REQUEST_DO_INIT:
                initClient(type,data,callBack);
            break;
            case AddNewClientActivity.REQUEST_COMMIT:
                addNewClient(type,data,callBack);
                break;
            case AddNewClientActivity.REQUEST_SAVE:
                saveClient(type,data,callBack);
                break;
            case AddNewClientActivity.REQUEST_UPLOAD:
                uploadImg(type,data,callBack);
                break;
        }
    }

    private void saveClient(int type, HashMap<String, Object> data, StoreResultCallBack callBack){
        try {
            clientInfoDetail.setUser_id(Config.UserInfo.USER_ID);
            clientInfoDetail.setShopAddree(data.get("addres").toString());
            clientInfoDetail.setPhoneNumber(data.get("phone").toString());
            clientInfoDetail.setShopName(data.get("shopName").toString());
            clientInfoDetail.setLegalpersonName(data.get("name").toString());
            clientInfoDetail.setLocationAddress(data.get("location").toString());
            if(clientInfoDetail.fileRule != null && clientInfoDetail.fileRule.size() > 0) {
                clientInfoDetail.setPictur(clientInfoDetail.fileRule.get(0).photoInfo);
            }
            ToolDbOperation.getClientDao().createOrUpdate(clientInfoDetail);
            callBack.onResult(type,true);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.error("保存失败");
            callBack.onResult(type,false);
        }
    }

    private void addNewClient(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack){
        Map<String,Object> map = new HashMap<>();
        map.put("employeeId",Config.UserInfo.USER_ID);
        map.put("provinceCode", clientInfoDetail.getProvinceCode());
        map.put("cityCode", clientInfoDetail.getCityCode());
        map.put("countyCode", clientInfoDetail.getCountyCode());
        map.put("applyStreetCode", clientInfoDetail.getStreetCode());
        map.put("shopName", data.get("shopName"));
        map.put("legalpersonName", data.get("name"));
        map.put("phoneNumber", data.get("phone"));
        map.put("shopAddree", data.get("addres"));
        map.put("latitude", clientInfoDetail.getLatitude());
        map.put("longitude", clientInfoDetail.getLongitude());
        map.put("locationAddress", data.get("location"));
        map.put("isInterested", clientInfoDetail.getIsInterested());
        map.put("tid", clientInfoDetail.tid);
        map.put("dataSources","xccrm");
        if(newMission != null) {
            map.put("tid",newMission.id);
            map.put("dataSources",newMission.customSourceCode);
        }
        for (int i = 0; i < clientInfoDetail.fileRule.size(); i++) {
            List<String> paths = new ArrayList<>();
            List<PhotoInfo> photoInfos = clientInfoDetail.fileRule.get(i).photoInfo;
            for (PhotoInfo photoInfo : photoInfos) {
                paths.add(photoInfo.getPhotoPath());
            }
            map.put(clientInfoDetail.fileRule.get(i).info.key, ToolStringToList.ListToString(paths));
        }
        ToolOkHTTP.post(Config.getURL("oles/app/myClient/saveMyClient.htm"), map, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                callBack.onResult(type,response);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type,null);
            }

        });
    }

    private void initClient(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        isNew = (boolean) data.get("isNew");
        newMission = (NewMission) data.get("newMission");
        clientInfoDetail = (ClientInfoDetail) data.get("clientInfoDetail");
        AttachmentInfo attachmentInfo = new AttachmentInfo();
        attachmentInfo.des = "";
        attachmentInfo.key = "pictures";
        attachmentInfo.name = "客户照片";
        attachmentInfo.max = "3";
        attachmentInfo.min = "3";
        List<ImageGalleryActivity.ImageGalleryItem> attachmentInfos = new ArrayList<>();
        ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfo);
        imageGalleryItem.storeType = clientInfoDetail.getId()+"_0";
        attachmentInfos.add(imageGalleryItem);
        clientInfoDetail.fileRule = attachmentInfos;
        ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
        photoInfos.addAll(clientInfoDetail.getPictur());
        photoInfos.addAll(clientInfoDetail.getPictur_up());
        attachmentInfos.get(0).photoInfo = photoInfos;

        if (newMission != null) {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("tid", newMission.id);
                params.put("user_id", Config.UserInfo.USER_ID);
                List<ClientInfoDetail> clientInfoDetails = ToolDbOperation.getClientDao().queryForFieldValues(params);
                if (clientInfoDetails != null && clientInfoDetails.size() > 0) {
                    clientInfoDetail = clientInfoDetails.get(0);
                    ToolLog.i(clientInfoDetail.toString());
                } else {
                    clientInfoDetail.setProvinceCode(newMission.shopProvincesCode);
                    clientInfoDetail.setProvinceName(newMission.shopProvinces);
                    clientInfoDetail.setCityCode(newMission.shopCityCode);
                    clientInfoDetail.setCityName(newMission.shopCity);
                    clientInfoDetail.setCountyCode(newMission.shopCountryCode);
                    clientInfoDetail.setCountyName(newMission.shopCountry);
                    clientInfoDetail.setPhoneNumber(newMission.shopkeeperMobile);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                clientInfoDetail.setProvinceCode(newMission.shopProvincesCode);
                clientInfoDetail.setProvinceName(newMission.shopProvinces);
                clientInfoDetail.setCityCode(newMission.shopCityCode);
                clientInfoDetail.setCityName(newMission.shopCity);
                clientInfoDetail.setCountyCode(newMission.shopCountryCode);
                clientInfoDetail.setCountyName(newMission.shopCountry);
                clientInfoDetail.setPhoneNumber(newMission.shopkeeperMobile);
            }
            clientInfoDetail.tid = newMission.id;
        }
        callBack.onResult(type,clientInfoDetail);
    }

    public void uploadImg(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        final HashMap<String, Object> uploadMap = new HashMap<>();
        final HashMap<String, PhotoInfo> uploadMapReWrite = new HashMap<>();
        if (clientInfoDetail.fileRule == null) {
            callBack.onResult(type,false);
            return;
        }
        for (int i = 0; i < clientInfoDetail.fileRule.size(); i++) {
            for (int j = 0; j < clientInfoDetail.fileRule.get(i).photoInfo.size(); j++) {
                if (clientInfoDetail.fileRule.get(i).photoInfo.get(j).getPhotoId() == 0) {
                    uploadMap.put(clientInfoDetail.fileRule.get(i).info.key + "&" + j, clientInfoDetail.fileRule.get(i).photoInfo.get(j).getPhotoPath());
                    uploadMapReWrite.put(clientInfoDetail.fileRule.get(i).info.key + "&" + j, clientInfoDetail.fileRule.get(i).photoInfo.get(j));
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
                            File file = new File(YTApplication.get().getCacheDir() + "/" + Config.UserInfo.USER_ID + "/" + clientInfoDetail.fileRule.get(0).storeType);
                            ToolFile.deleteDir(file);
                            callBack.onResult(type, true);
                        } else {
                            callBack.onResult(type, false);
                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, false);
                    }

                }, new ToolOkHTTP.ProgressRequestListener() {
                    @Override
                    public void onRequestProgress(long byteCount,final long bytesWritten,final long contentLength,final boolean done) {
                        if (baseActivity==null) return;
                        baseActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                baseActivity.showProgress("正在上传图片("+(int)(((float)bytesWritten/(float)contentLength)*100)+"%)");
                                if(done){
                                    baseActivity.dismissProgress();
                                }
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
