package asuper.yt.cn.supermarket.modules.myclient;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/14 0014.
 */

public class ToolAttachment {

    public static void addFujian(List<ImageGalleryActivity.ImageGalleryItem> items, LinearLayout fujian, Activity activity) {
        fujian.removeAllViews();
        if (items == null || items.size() < 1) return;
        for (int i = 0; i < items.size(); i++) {
            int size = 0;
            if (items.get(i).photoInfo != null) {
                size = items.get(i).photoInfo.size();
            }
            if ((items.get(i).min() > 0 && size < items.get(i).min()) || size > items.get(i).max()) {
                TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.layout_simple_text, null);
                textView.setText("*" + items.get(i).info.name + "(" + size + "/" + items.get(i).max() + ")");
                fujian.addView(textView);
            }
        }

        if (fujian.getChildCount() < 1) {
            TextView textView = (TextView) activity.getLayoutInflater().inflate(R.layout.layout_simple_text, null);
            textView.setText("附件数量符合上传要求");
            fujian.addView(textView);
        }
    }

    public static void gotoUpload(boolean canUpdate, List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems, String formData) {
        if (imageGalleryItems != null) {
            EventBus.getDefault().postSticky(imageGalleryItems);
        }
        YTApplication.getOperation().addParameter("canUpdate", canUpdate);
        YTApplication.getOperation().addParameter("json", formData);
        YTApplication.getOperation().forward(ImageGalleryActivity.class);
    }

    public static void gotoUpload(boolean canUpdate, List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems) {
        gotoUpload(canUpdate, imageGalleryItems, null);
    }

    public static List<ImageGalleryActivity.ImageGalleryItem> dealWithResult(JSONObject result, String type) {
        List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems = new ArrayList<>();
        List<AttachmentInfo> attachmentInfos;
        try {
            String jsonObject = result.getString("fileRule");
            attachmentInfos = ToolGson.fromJson(jsonObject, new TypeToken<List<AttachmentInfo>>() {
            }.getType());
        } catch (Exception e) {
            attachmentInfos = new ArrayList<>();
        }

        if (attachmentInfos != null) {
            for (int i = 0; i < attachmentInfos.size(); i++) {
                ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfos.get(i));
                imageGalleryItem.storeType = type;
                String value = result.optString(attachmentInfos.get(i).key, null);
                ArrayList<PhotoInfo> photoinfos = new ArrayList<>();
                imageGalleryItem.photoInfo = photoinfos;
                imageGalleryItems.add(imageGalleryItem);
                if (value == null || value.trim().isEmpty() || "null".equals(value)) {
                    if (BuildConfig.DEBUG && Config.isAutoFillAttachment) {
                        int integer = imageGalleryItem.max();
                        for (int j = 0; j < integer; j++) {
                            PhotoInfo photoinfo = new PhotoInfo();
                            photoinfo.setPhotoPath(Config.autoFillAttachmentPath);
                            photoinfo.setPhotoId(0);
                            photoinfos.add(photoinfo);
                        }
                    }
                    continue;
                }
                if (value.startsWith("L")) {
                    List<Object> photopaths = ToolStringToList.StringToList(value);
                    for (Object path : photopaths) {
                        PhotoInfo photoinfo = new PhotoInfo();
                        photoinfo.setPhotoPath(path.toString());
                        photoinfo.setPhotoId(1);
                        photoinfos.add(photoinfo);
                    }
                } else {
                    photoinfos = ToolGson.fromJson(value, new TypeToken<ArrayList<PhotoInfo>>() {
                    }.getType());
                    imageGalleryItem.photoInfo = photoinfos;
                }

            }
        }
        return imageGalleryItems;
    }

    /**
     * 附件信息
     *
     * @param res               加盟表信息
     * @param imageGalleryItems 附件信息集合
     * @param jsonArray         附件数据
     * @param type              type
     * @param data              布局内容
     * @param isUpdate          是否允许修改
     */
    public static void reGenerateAttachmentInfo(JSONObject res, List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems, JSONArray jsonArray, String type, HashMap<String, Object> data, boolean isUpdate) {
        if (imageGalleryItems == null || jsonArray == null) return;
        imageGalleryItems.clear();
        int length = jsonArray.length();
        ArrayList<PhotoInfo> oldPhotos = null;
        ArrayList<PhotoInfo> newPhotos = null;
        for (int i = 0; i < length; i++) {
            JSONObject object = jsonArray.optJSONObject(i);
            String title = object.optString("groupZn", "");
            JSONArray tmp = object.optJSONArray("fileList");
            if (tmp != null) {
                int tmpLength = tmp.length();
                for (int j = 0; j < tmpLength; j++) {
                    try {
                        AttachmentInfo attachmentInfo = ToolGson.fromJson(tmp.optString(j), AttachmentInfo.class);
                        ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfo);
                        imageGalleryItem.storeType = type;
                        imageGalleryItem.groupId = i;
                        imageGalleryItem.title = title;
                        if (res.has(attachmentInfo.key)) {
                            String phs = res.optString(attachmentInfo.key, "");
                            ArrayList<PhotoInfo> ps = new ArrayList<>();
                            if (phs.startsWith("L")) {
                                List<Object> photopaths = ToolStringToList.StringToList(phs);
                                for (Object path : photopaths) {
                                    PhotoInfo photoinfo = new PhotoInfo();
                                    photoinfo.setPhotoPath(path.toString());
                                    photoinfo.setPhotoId(1);
                                    ps.add(photoinfo);
                                }
                            } else if (!phs.trim().isEmpty()) {
                                ps = ToolGson.fromJson(phs, new TypeToken<ArrayList<PhotoInfo>>() {
                                }.getType());
                            }
                            if (ps == null) ps = new ArrayList<>();
                            imageGalleryItem.photoInfo = ps;
                        }
                        if (isUpdate || imageGalleryItem.photoInfo.isEmpty()) {
                            if (isUpdate){
                                if (imageGalleryItem.info.key.equals("expiredCertificateUndertaking"))//过期证件承诺书
                                    continue;
                                if (imageGalleryItem.info.key.equals("htaBusinessLicense"))//卫生烟草酒类等经营许可证
                                    continue;
                                if (imageGalleryItem.info.key.equals("franchiseeCommitment"))//加盟商承诺书
                                    continue;
                                if (imageGalleryItem.info.key.equals("declarationLetter"))//声明函(签字,带手印)
                                    continue;
                            }
                            if (data.get("relationShip").toString().equals("key1")) //实际经营者为法人本人
                                if (imageGalleryItem.info.key.equals("legalIdCardPhoto") || imageGalleryItem.info.key.equals("proxyBook") || imageGalleryItem.info.key.equals("authorizationProvePhoto"))
                                    continue;
                            if (imageGalleryItem.info.key.equals("businessLicense")) {
                                if (data.get("originalScript").equals("2"))  //营业执照回执
                                    imageGalleryItem.info.name = "营业执照回执";
                                if (!data.get("originalScript").equals(res.optString("originalScript", "")))
                                    imageGalleryItem.photoInfo.clear();
                            }
                            if (data.get("isFood").toString().equals("2"))  // 暂无食品流通许可证
                                if (imageGalleryItem.info.key.equals("foodCirculationPermit"))
                                    continue;
                            if (data.get("isTobacco").toString().equals("2") || data.get("isNotTobacco").toString().equals("2"))  // 不售卖烟草
                                if (imageGalleryItem.info.key.equals("tobaccoPhoto"))
                                    continue;
                            if (data.get("isFood").toString().equals("1") && (data.get("isTobacco").toString().equals("1") && data.get("isNotTobacco").toString().equals("1") || data.get("isTobacco").toString().equals("2")))  // 责任函
                                if (imageGalleryItem.info.key.equals("certificateResponsibility"))
                                    continue;
                            if (imageGalleryItem.info.key.equals("rentContract")) {
                                if (data.get("storeSource").toString().equals("2")) {   // 租金缴费凭证
                                    imageGalleryItem.info.name = "房屋所有证";
                                    imageGalleryItem.info.des = "";
                                }else{
                                    imageGalleryItem.info.name = "房租合同";
                                    imageGalleryItem.info.des = "";
                                }
                                if (!data.get("storeSource").equals(res.optString("storeSource", "")))
                                    imageGalleryItem.photoInfo.clear();
                            }
                            if (data.get("storeSource").toString().equals("2")) {   // 租金缴费凭证
                                if (imageGalleryItem.info.key.equals("rentPaymentVoucher"))
                                    continue;
                            }
                        }else{
                            if (imageGalleryItem.info.key.equals("businessLicense")) {
                                if (data.get("originalScript").equals("2"))  //营业执照回执
                                    imageGalleryItem.info.name = "营业执照回执";
                            }
                            if (imageGalleryItem.info.key.equals("rentContract")) {
                                if (data.get("storeSource").toString().equals("2")) {   // 租金缴费凭证
                                    imageGalleryItem.info.name = "房屋所有证";
                                    imageGalleryItem.info.des = "";
                                }else{
                                    imageGalleryItem.info.name = "房租合同";
                                    imageGalleryItem.info.des = "";
                                }
                            }
                        }
                        imageGalleryItems.add(imageGalleryItem);
                        if ("corporateIdentityCard".equals(imageGalleryItem.info.key))
                            oldPhotos = imageGalleryItem.photoInfo;
                        if ("legalIdCardPhoto".equals(imageGalleryItem.info.key))
                            newPhotos = imageGalleryItem.photoInfo;
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }

        if (oldPhotos != null && oldPhotos.size() > 0 && (newPhotos == null || newPhotos.size() < 1)) {
            newPhotos.clear();
            newPhotos.addAll(oldPhotos);
        }
    }

    public static boolean isImagesSatifyied(List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems) {
        for (ImageGalleryActivity.ImageGalleryItem imageGalleryItem : imageGalleryItems) {
            if (!imageGalleryItem.check()) return false;
        }
        return true;
    }

    public static void disableAllView(View root, boolean isEnable) {
        if (root == null) return;
        if (root instanceof ViewGroup) {
            ViewGroup view = (ViewGroup) root;
            view.setEnabled(isEnable);
            for (int i = 0; i < view.getChildCount(); i++) {
                View viewchild = view.getChildAt(i);
                disableAllView(viewchild, isEnable);
            }
        } else {
            if (root.getTag() == null || !root.getTag().equals("onClick"))
                root.setEnabled(isEnable);
        }
    }
}
