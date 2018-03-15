package asuper.yt.cn.supermarket.modules.myclient.contact;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.AppendixOne;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/14 0014.
 */

public class ContactStore extends UploadStore {

    private boolean fromLocal = false;
    private JSONObject pageData;
    private int shopId;
    private boolean isUpdate;
    private boolean isNew;

    public ContactStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    protected void DoAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case ContractFragment.REQUEST_GET_CONTRACT_DETAIL:
                getDetail(type,data,callBack);
                break;
            case ContractFragment.REQUEST_CONTRACT_SAVE:
                save(type,data,callBack);
                break;
            case ContractFragment.REQUEST_GET_CONTRACT_NODE_INFO:
                getNodeInfo(type,data,callBack);
                break;
            case ContractFragment.REQUEST_CONTRACT_COMMIT:
                commit(type,data,callBack);
                break;
        }
    }

    private void getNodeInfo(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack){
        if (data.get("applyId")==null) return;
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDIT_MESSAGE), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String res = response.toString();
                NodeList nodeInfos = ToolGson.fromJson(res, new TypeToken<NodeList>() {}.getType());

                callBack.onResult(type, nodeInfos);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type, null);
            }
        });
    }


    private void getDetail(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        shopId = (int) data.get("intentionId");
        isUpdate = (boolean)data.get("isUpdate");
        isNew = (boolean)data.get("isNew");
        if(isNew || isUpdate){
            try {
                Contract ract = ToolDbOperation.getRactDao().queryForId(shopId+"");
                if(ract != null){
                    pageData = new JSONObject(ract.getBaseSelectInfoJson());
                    if(pageData.optInt("intentionId",-1) < 0){
                        pageData = new JSONObject(ToolGson.toJson(ract));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(pageData != null) fromLocal = true;
        ToolOkHTTP.post(Config.getURL("oles/app/agreementApproveAction/queryAgreementApproveDetailNew.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                final JSONObject res = response.optJSONObject("resultObject");
                if(res != null){
                    if(fromLocal && pageData != null){
                        try {
                            pageData.put("baseSelectInfo", res.optJSONObject("baseSelectInfo"));
                            pageData.put("fileRule", res.optJSONArray("fileRule"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        pageData = res;
                    }


                float doorAllowance = 0;
                float rentAllowance = 0;
                try {
                    doorAllowance = new BigDecimal(pageData.optString("doorAllowanceAmountMax")).floatValue();
                    rentAllowance = new BigDecimal(pageData.optString("rentAllowanceAmountMax")).floatValue();
                } catch (Exception e) {

                }

                if (doorAllowance <= 0 || rentAllowance <= 0) {
                    try {
                        pageData.put("doorAllowanceAmount",res.optString("doorAllowanceAmount"));
                        pageData.put("rentAllowanceAmount",res.optString("rentAllowanceAmount"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    pageData.put("doorAllowanceAmountMax",res.optString("doorAllowanceAmountMax"));
                    pageData.put("rentAllowanceAmountMax",res.optString("rentAllowanceAmountMax"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                imageGalleryItemList = ToolAttachment.dealWithResult(pageData,shopId+"_2");
                Map<String,Object> map = new HashMap<>();
                map.put("pageData",pageData);
                map.put("items",imageGalleryItemList);
                map.put("local",fromLocal);
                callBack.onResult(type,map);
                } else {
                    onFailure();
                }
            }

            @Override
            public void onFailure() {
                callBack.onResult(type,null);
            }
        });
    }


    private void commit(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack){
        ToolOkHTTP.post(Config.getURL(isUpdate? "oles/app/agreementApproveAction/updateAgreementApprove.htm": "oles/app/agreementApproveAction/saveAgreementApprove.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Contract localVO = new Contract();
                localVO.setUser_id(Config.UserInfo.USER_ID);
                localVO.setIntentionId(shopId+"");
                ToolDbOperation.deleteSync(localVO, Contract.class);
                callBack.onResult(type,true);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type,false);
            }
        });
    }

    private void save(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack){
        if(data == null){
            callBack.onResult(type,false);
            return;
        }
        Set<String> ketSet = data.keySet();
        Map<String,String> formData = new HashMap<>();
        for(String key:ketSet){
            Object obj = data.get(key);
            if(obj != null) formData.put(key,obj.toString());
        }
        callBack.onResult(type,saveLocal(formData));
    }
    
    private boolean saveLocal(Map<String,String> formData){
        if (pageData==null) return false;
        Contract ract = ToolGson.fromJson(pageData.toString(),Contract.class);
        ract.setAgreementType(formData.get("agreementType"));
        ract.setPerformStartDateStr(formData.get("performStartDateStr"));
        ract.setPerformEndDateStr(formData.get("performEndDateStr"));
        ract.setAgreementName(formData.get("agreementName"));
        ract.setAgreementSeller(formData.get("agreementSeller"));
        ract.setContactWay(formData.get("contactWay"));
        ract.setShopAddrees(formData.get("shopAddrees"));
        ract.setContractPartyMaster(formData.get("contractPartyMaster"));
        ract.setContractPartyFollow(formData.get("contractPartyFollow"));
        ract.setSubjectContent(formData.get("subjectContent"));
        ract.setSellerIdCard(formData.get("sellerIdCard"));
        ract.setAgreementMoney(formData.get("agreementMoney"));
        ract.attachmentState = formData.get("att");
        ract.rentAllowanceAmount = formData.get("rentAllowanceAmount");
        ract.doorAllowanceAmount = formData.get("doorAllowanceAmount");
        ract.setPayType(formData.get("payType")==null?"":formData.get("payType").toString());
        ract.setIntentionId(shopId+"");
        ract.setUser_id(Config.UserInfo.USER_ID);

        try {
            JSONObject save = new JSONObject(ToolGson.toJson(ract));
            for (int i = 0; i < imageGalleryItemList.size(); i++) {
                save.put(imageGalleryItemList.get(i).info.key, ToolGson.toJson(imageGalleryItemList.get(i).photoInfo));
            }
            ract.setBaseSelectInfoJson(save.toString());
            ToolDbOperation.getRactDao().createOrUpdate(ract);
            CommonRequest.notifyServerWhenSaveLocal(ract.getIntentionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
