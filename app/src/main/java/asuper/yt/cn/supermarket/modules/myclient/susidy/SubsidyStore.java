package asuper.yt.cn.supermarket.modules.myclient.susidy;

import com.google.gson.Gson;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/15 0015.
 */

public class SubsidyStore extends UploadStore {

    private boolean fromLocal = false;
    private JSONObject pageData;
    private int shopId;
    private boolean isUpdate;
    private boolean isNew;
    private JSONObject result4save;
    NewSubsidyVO subsidyVO;

    public SubsidyStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    protected void DoAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case SubsidyFragment.REQUEST_GET_SUBSIDY_DETAIL:
                getDetail(type, data, callBack);
                break;
            case SubsidyFragment.REQUEST_GET_SUBSIDY_NODE_INFO:
                getNodeInfo(type, data, callBack);
                break;
            case SubsidyFragment.REQUEST_SUBSIDY_COMMIT:
                commmit(type, data, callBack);
        }

    }

    private void getNodeInfo(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDIT_MESSAGE), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String res = response.toString();
                NodeList nodeInfos = ToolGson.fromJson(res, NodeList.class);
                callBack.onResult(type, nodeInfos);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }
        });
    }

    private void commmit(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL(isUpdate ? Config.URL.URL_UPDATE_SUBSDIY : Config.URL.URL_ADD_SUBSDIY), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                SubsidyLocalVO localVO = new SubsidyLocalVO();
                localVO.user_id = Config.UserInfo.USER_ID;
                localVO.intentionId = shopId+"";
                ToolDbOperation.deleteSync(localVO, SubsidyLocalVO.class);
                callBack.onResult(type, true);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, false);
            }
        });
    }

    private void getDetail(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        shopId = (int) data.get("shopId");
        isUpdate = (boolean) data.get("isUpdate");
        isNew = (boolean) data.get("isNew");
        requestLocalData();
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_SUBSDIY), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if (fromLocal && pageData != null) {
                    try {
                        JSONObject res = response.optJSONObject("resultObject");
                        pageData.put("fileResultInfos",res.optJSONArray("fileResultInfos"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    pageData = response.optJSONObject("resultObject");
                }

                imageGalleryItemList = dealWithResultSubsidy(pageData);
                Map<String, Object> map = new HashMap<>();
                map.put("pageData", pageData);
                map.put("items", imageGalleryItemList);

                String isSendFrozen = "";
                try {
                JSONObject res = response.optJSONObject("resultObject");
                if(res != null && (res.optJSONObject("subsidySubjectWebDTO") != null)){
                    JSONObject jsonObject = res.optJSONObject("subsidySubjectWebDTO");
                    if(jsonObject != null ){
                        isSendFrozen = jsonObject.optString("isSendFrozen");
                    }
                }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                map.put("isSendFrozen",isSendFrozen);
                map.put("local", fromLocal);
                callBack.onResult(type, map);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }

        });
    }

    public List<ImageGalleryActivity.ImageGalleryItem> dealWithResultSubsidy(JSONObject jsonObject) {
        result4save = ((JSONObject) jsonObject);
        if (jsonObject != null) {
            JSONObject jsonObject1 = jsonObject.optJSONObject("subsidySubjectWebDTO");
            if (jsonObject1 != null) {
                String jsonObject2 = jsonObject1.optString("dataJson");
                if (jsonObject2 != null) {
                    Gson gson = new Gson();
                    NewSubsidyVO joinVO = gson.fromJson(jsonObject2, NewSubsidyVO.class);
//                    getState().data.subsidyVO = joinVO;
//                    subsidyVO.auditMessage = jsonObject1.optString("auditMessage");
//                    subsidyVO.auditNodeName = jsonObject1.optString("auditNodeName");
//                    subsidyVO.auditStatus = jsonObject1.optString("auditStatus");
//                    subsidyVO.isSendFrozen = jsonObject1.optString("isSendFrozen");
//                    subsidyVO.auditTime = jsonObject1.optLong("auditTime");
//                    subsidyVO.auditNodeIndex = jsonObject1.optString("auditNodeIndex");
                }


            }

        }

//        if (fromLocal) return imageGalleryItemList;

        JSONArray jsonObj = jsonObject.optJSONArray("fileResultInfos");
        List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItemList = new ArrayList<>();
        if (jsonObj != null) {
            List<AttachmentInfo> attachmentInfos = ToolGson.fromJson(jsonObj.toString(),
                    new TypeToken<List<AttachmentInfo>>() {
                    }.getType());
            for (int i = 0; i < attachmentInfos.size(); i++) {
                ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfos.get(i));
                imageGalleryItem.storeType = shopId + "_3";
                imageGalleryItemList.add(imageGalleryItem);
            }
            JSONObject jsonObject3 = jsonObject.optJSONObject("imgs");

            if (jsonObject3 != null) {
                Iterator<String> keys = jsonObject3.keys();
                while (keys.hasNext()) {
                    String s = keys.next();
                    String imgs = jsonObject3.optString(s);
                    if (imgs != null) {
                        ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
                        if (imgs.startsWith("L")) {
                            List<Object> paths = ToolStringToList.StringToList(imgs);
                            for (Object obj : paths) {
                                if (obj == null) continue;
                                PhotoInfo photoInfo = new PhotoInfo();
                                photoInfo.setPhotoId(1);
                                photoInfo.setPhotoPath(obj.toString());
                                photoInfos.add(photoInfo);
                            }
                        } else {
                            try {
                                photoInfos = ToolGson.fromJson(imgs, new TypeToken<ArrayList<PhotoInfo>>() {
                                }.getType());
                            } catch (Exception e) {
                                photoInfos = new ArrayList<>();
                            }
                        }
                        for (int i = 0; i < imageGalleryItemList.size(); i++) {
                            AttachmentInfo attachmentInfo = imageGalleryItemList.get(i).info;
                            if (imageGalleryItemList.get(i).photoInfo == null)
                                imageGalleryItemList.get(i).photoInfo = new ArrayList<PhotoInfo>();
                            if (attachmentInfo.key != null && attachmentInfo.key.equals(s)) {
                                imageGalleryItemList.get(i).photoInfo = photoInfos;
                                continue;
                            }
                        }
                    }
                }
            } else {
                for (int i = 0; i < imageGalleryItemList.size(); i++) {
                    ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
                    AttachmentInfo attachmentInfo = imageGalleryItemList.get(i).info;
                    imageGalleryItemList.get(i).photoInfo = photoInfos;
                    if (BuildConfig.DEBUG && Config.isAutoFillAttachment && isUpdate) {
                        int size = new BigDecimal(attachmentInfo.max.trim()).intValue();
                        for (int j = 0; j < size; j++) {
                            PhotoInfo photoInfo = new PhotoInfo();
                            photoInfo.setPhotoId(0);
                            photoInfo.setPhotoPath(Config.autoFillAttachmentPath);
                            photoInfos.add(photoInfo);
                        }
                    }
                }
            }
        }
        return imageGalleryItemList;

    }

    public void requestLocalData() {
        try {
            SubsidyLocalVO subsidyLocalVO = ToolDbOperation.getSubsidyDao().queryForId(shopId + "");
            if (subsidyLocalVO != null) {
                subsidyVO = new NewSubsidyVO();
                pageData = new JSONObject(subsidyLocalVO.dataJson);
                fromLocal = true;
                if(pageData.optJSONObject("resultObject") != null){
                    pageData = pageData.optJSONObject("resultObject");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fromLocal = false;
        } catch (JSONException e) {
            e.printStackTrace();
            fromLocal = false;
        }
    }

}
