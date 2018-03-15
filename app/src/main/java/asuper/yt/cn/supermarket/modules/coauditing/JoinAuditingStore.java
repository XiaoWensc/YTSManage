package asuper.yt.cn.supermarket.modules.coauditing;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/16 0016.
 */

public class JoinAuditingStore extends UploadStore {

    protected static final int POST_JOIN_AUDITING_SHIMG = 0x8543248;
    private JSONArray attaRes;
    private JSONObject res;


    public JoinAuditingStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    protected void DoAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case JoinAuditingFragment.REQUEST_GET_DETAIL:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_INFO), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        res = response.optJSONObject("resultObject");
                        callBack.onResult(type, res);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }

                });
                break;
            case JoinAuditingFragment.REQUEST_GET_MESSAGE:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_MESSAGE), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONObject res = response.optJSONObject("resultObject");
                        callBack.onResult(type, res);
                    }

                    @Override
                    public void onFailure() {

                    }
                });
                break;
            case POST_JOIN_AUDITING_SHIMG:
                getAttachment(type, data, callBack);
                break;
        }
    }

    private void getAttachment(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        if (attaRes == null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("relationShip", "key2");
            ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_JOIN_ATTACHMENT), map, new ToolOkHTTP.OKHttpCallBack() {
                @Override
                public void onSuccess(JSONObject response) {
                    attaRes = response.optJSONArray("resultObject");
                    List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems = AttachmentInfo((JSONObject) data.get("files"), attaRes);
                    callBack.onResult(type, imageGalleryItems);
                }

                @Override
                public void onFailure() {
                    callBack.onResult(type, null);
                }
            });
        } else {
            List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems = AttachmentInfo((JSONObject) data.get("files"), attaRes);
            callBack.onResult(type, imageGalleryItems);
        }
    }

    private List<ImageGalleryActivity.ImageGalleryItem> AttachmentInfo(JSONObject files, JSONArray attaRes) {
        Map<Keys, List<String>> map = getImageList(attaRes);
        ToolLog.i("图片集合："+map.toString());
        Iterator<String> keys = files.keys();
        List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems = new ArrayList<>();
        int index = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            AttachmentInfo attachmentInfo = new AttachmentInfo();
            attachmentInfo.name = key;
            attachmentInfo.key = key;
            ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfo);
            JSONArray pics = files.optJSONArray(key);
            ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
            imageGalleryItem.photoInfo = photoInfos;
            for (Keys string : map.keySet()) {
                if (map.get(string).indexOf(key.replace("(或房产证)","")) > -1) {
                    imageGalleryItem.title = string.groupZn;
                    imageGalleryItem.groupEn = string.groupEn;
                    if(imageGalleryItem.info.name.equals("营业执照")){
                        if (res.optJSONObject("formData").optString("originalScript").equals("2"))  //营业执照回执
                            imageGalleryItem.info.name = "营业执照回执";
                    }else if (imageGalleryItem.info.name.equals("房租合同(或房产证)")){
                        if (res.optJSONObject("formData").optString("storeSource").equals("2")) {   // 租金缴费凭证
                            imageGalleryItem.info.name = "房屋所有证";
                        }else{
                            imageGalleryItem.info.name = "房租合同";
                        }
                    }
                }
            }
            if (pics != null) {
                for (int i = 0; i < pics.length(); i++) {
                    String path = pics.optString(i);
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setPhotoPath(path);
                    photoInfo.setPhotoId(1);
                    photoInfos.add(photoInfo);
                }
            }
            if (imageGalleryItem.groupEn==null) {
                imageGalleryItem.title = "证照照片";
                imageGalleryItem.groupEn = "group2";
                if (imageGalleryItem.info.name.equals("其他附件")){
                    imageGalleryItem.title = "店铺照片";
                    imageGalleryItem.groupEn = "group3";
                }
            }
            imageGalleryItems.add(imageGalleryItem);
            index++;
        }
        Collections.sort(imageGalleryItems, (imageGalleryItem, t1) -> {
            String en1 = imageGalleryItem.groupEn;
            String en2 = t1.groupEn;
//            if (en1==null||en2==null) return 1;
            return en1.compareTo(en2);
        });
        ToolLog.i("排序集合："+imageGalleryItems.toString());
        return imageGalleryItems;
    }


    private Map<Keys, List<String>> getImageList(JSONArray attaRes) {
        Map<Keys, List<String>> map = new HashMap<>();
        for (int i = 0; i < attaRes.length(); i++) {
            JSONObject object = attaRes.optJSONObject(i);
            JSONArray array = object.optJSONArray("fileList");
            List<String> files = new ArrayList<>();
            for (int j = 0; j < array.length(); j++) {
                JSONObject jsonObject = array.optJSONObject(j);
                String name = jsonObject.optString("name");
                files.add(name);
            }
            Keys keys = new Keys(object.optString("groupEn", "default"),object.optString("groupZn", "default"));
            map.put(keys, files);
        }
        return map;
    }

    public class Keys {
        public String groupEn;
        public String groupZn;

        public Keys(String groupEn, String groupZn) {
            this.groupEn = groupEn;
            this.groupZn = groupZn;
        }
    }
}
