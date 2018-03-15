package asuper.yt.cn.supermarket.modules.myclient.join;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.MerchantJoinScoretableVO;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/14 0014.
 */

public class JoinStore extends UploadStore {

    private boolean fromLocal = false;
    private JSONObject pageData;
    private JSONArray attaRes;
    private int shopId;
    private boolean isUpdate;
    private boolean isNew;
    protected static final int POST_JOIN_IF_PHONE = 0x001548645;

    public JoinStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void DoAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        switch (type) {
            case JoinFragment.REQUEST_GET_JOIN_ATTACHMENT:
                getAttachment(type, data, callBack);
                break;
            case JoinFragment.REQUEST_GET_JOIN_DETAIL:
                getDetail(type, data, callBack);
                break;
            case JoinFragment.REQUEST_GET_JOIN_NODE_INFO:
                getNodeInfo(type, data, callBack);
                break;
            case JoinFragment.REQUEST_GET_JOIN_VERIFY:
                getVerify(type, data, callBack);
                break;
            case JoinFragment.REQUEST_JOIN_COMMIT:
                commit(type, data, callBack);
                break;
            case JoinFragment.REQUEST_JOIN_SAVE:
                save(type, data, callBack);
                break;
            case POST_JOIN_IF_PHONE:
                ifPhone(type, data, callBack);
                break;
        }
    }

    private void ifPhone(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL("app/ratingScale/judgeOneLeader.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Map<String, String> map = new HashMap<>();
                map.put("suc", response.optString("success", "false"));
                map.put("msg", response.optString("errorMessage", null));
                callBack.onResult(type, map);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }
        });
    }

    private void getDetail(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        shopId = (int) data.get("shopId");
        isUpdate = (boolean) data.get("isUpdate");
        isNew = (boolean) data.get("isNew");
        if (isNew || isUpdate) {
            MerchantJoinScoretableVO joinVO = null;
            try {
                joinVO = ToolDbOperation.getJoinDao().queryForId(shopId + "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (joinVO != null) {
                try {
                    pageData = new JSONObject(joinVO.getSelectListDataJson());
                    if (pageData.optInt("shopId", -1) < 0) {
                        pageData = new JSONObject(ToolGson.toJson(joinVO));
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

                if (pageData != null) {
                    fromLocal = true;

                    //// TODO: 2017/9/14 0014 isover
//                    state.data.isOver = state.data.data.optString("isOver", null);
//                    state.data.groupId = state.data.data.optInt("groupId");
                }
            }

        }
        ToolOkHTTP.post(Config.getURL("app/ratingScale/queryRatingScaleObjByIdNew2.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                JSONObject jsonObject = response.optJSONObject("resultObject");
                if (jsonObject != null) {
                    if (fromLocal && pageData != null) {
                        try {
                            pageData.put("selectListData", jsonObject.optJSONObject("selectListData"));
                            pageData.put("fileRule", jsonObject.optJSONArray("fileRule"));
                            pageData.put("isOverdueRevert",jsonObject.optInt("isOverdueRevert",0));
                            pageData.put("originalScript",jsonObject.optString("originalScript"));
                            if (jsonObject.optJSONArray("qiye") != null) {
                                pageData.put("businessLicense", jsonObject.optJSONArray("qiye"));
                                pageData.put("otherPhoto", jsonObject.optJSONArray("qita"));
                                pageData.put("corporateIdentityCard", jsonObject.optJSONArray("fcode"));
                                pageData.put("rentContract", jsonObject.optJSONArray("zulin"));
                                pageData.put("accordanceShopPhoto", jsonObject.optJSONArray("xingxpho"));
                                pageData.put("monthlySalesCertificate", jsonObject.optJSONArray("liushui"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        pageData = jsonObject;
                    }
                }
                imageGalleryItemList = ToolAttachment.dealWithResult(pageData, shopId + "_1");
                Map<String, Object> map = new HashMap<>();
                map.put("pageData", pageData);
                map.put("items", imageGalleryItemList);
                map.put("local", fromLocal);
                callBack.onResult(type, map);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }

        });
    }

    private void getAttachment(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        if (pageData==null) return;
        if (attaRes == null) {
            Map<String, Object> map = new HashMap<>();
            map.put("relationShip", "key2");
            map.put("originalScript", "1");
            ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_JOIN_ATTACHMENT), map, new ToolOkHTTP.OKHttpCallBack() {
                @Override
                public void onSuccess(JSONObject response) {
                    attaRes = response.optJSONArray("resultObject");
                    ToolAttachment.reGenerateAttachmentInfo(pageData, imageGalleryItemList, attaRes, shopId + "_1", data,isUpdate);
                    callBack.onResult(type, imageGalleryItemList);
                }

                @Override
                public void onFailure() {
                    callBack.onResult(type, null);
                }
            });
        } else {
            ToolAttachment.reGenerateAttachmentInfo(pageData, imageGalleryItemList, attaRes, shopId + "_1", data,isUpdate);
            callBack.onResult(type, imageGalleryItemList);
        }
    }

    private void getNodeInfo(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDIT_MESSAGE), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String res = response.toString();
                NodeList nodeInfos = ToolGson.fromJson(res, new TypeToken<NodeList>() {
                }.getType());
                callBack.onResult(type, nodeInfos);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }
        });
    }

    private void getVerify(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_CHECK_PHONE), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                callBack.onResult(type, true);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, false);
            }
        });
    }

    private void commit(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        ToolOkHTTP.post(Config.getURL((isUpdate ? "app/ratingScale/updateRatingScaleNew.htm" : "app/ratingScale/addRatingScaleNew.htm")), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                MerchantJoinScoretableVO merchantJoinScoretableVO = new MerchantJoinScoretableVO();
                merchantJoinScoretableVO.setShopId(shopId);
                ToolDbOperation.deleteSync(merchantJoinScoretableVO, MerchantJoinScoretableVO.class);
                callBack.onResult(type, true);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, false);
            }
        });
    }


    private void save(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        if (data == null) {
            callBack.onResult(type, false);
            return;
        }
        Set<String> ketSet = data.keySet();
        Map<String, String> formData = new HashMap<>();
        for (String key : ketSet) {
            Object obj = data.get(key);
            if (obj != null) formData.put(key, obj.toString());
        }
        callBack.onResult(type, internalSave(formData));
    }


    private boolean internalSave(Map<String, String> formData) {
        ToolLog.i("formData:   " + ToolGson.toJson(formData));
        MerchantJoinScoretableVO joinVO = ToolGson.fromJson(ToolGson.toJson(formData), MerchantJoinScoretableVO.class);
        joinVO.setShopId(shopId);
        joinVO.setUser_id(Config.UserInfo.USER_ID);

        try {
            JSONObject save = new JSONObject(ToolGson.toJson(joinVO));
            for (int i = 0; i < imageGalleryItemList.size(); i++) {
                save.put(imageGalleryItemList.get(i).info.key, ToolGson.toJson(imageGalleryItemList.get(i).photoInfo));
            }
            joinVO.setSelectListDataJson(save.toString());
            ToolDbOperation.getJoinDao().createOrUpdate(joinVO);

            CommonRequest.notifyServerWhenSaveLocal(joinVO.getShopId() + "");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
