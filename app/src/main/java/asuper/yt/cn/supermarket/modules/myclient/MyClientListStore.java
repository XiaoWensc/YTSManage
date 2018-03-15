package asuper.yt.cn.supermarket.modules.myclient;

import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.MerchantJoinScoretableVO;
import asuper.yt.cn.supermarket.entities.MyExpandVo;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;
import asuper.yt.cn.supermarket.modules.myclient.entities.MyClient;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/7 0007.
 */

public class MyClientListStore extends Store {

    SparseArray<String> ids;

    public MyClientListStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, final HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case MyClientListActivity.REQUEST_GET_CLIENTELE:
                if(data != null) data.put("applyStatus","1");
                if(ids == null || (data.get("pageNum") != null && ((int)data.get("pageNum")) == 1)) {
                    ids = new SparseArray<>();
                    try {
                        List<MerchantJoinScoretableVO> joinList = ToolDbOperation.getJoinDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                        List<Contract> contractList = ToolDbOperation.getRactDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                        List<SubsidyLocalVO> subsidyLocalVOs = ToolDbOperation.getSubsidyDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                        for (MerchantJoinScoretableVO item : joinList) {
                            if (item == null) continue;
                            ids.put(item.getShopId(), "加盟未提交");
                        }
                        for (Contract item : contractList) {
                            if (item == null) continue;
                            try {
                                ids.put(new BigDecimal(item.getIntentionId()).intValue(), "合同未提交");
                            } catch (Exception e) {

                            }
                        }
                        for (SubsidyLocalVO item : subsidyLocalVOs) {
                            if (item == null) continue;
                            try {
                                ids.put(new BigDecimal(item.intentionId).intValue(), "补贴未提交");
                            } catch (Exception e) {

                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                ToolOkHTTP.post(Config.getURL("oles/app/myClient/findShopInfoList.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Gson gson = new Gson();
                        MyClient ent = gson.fromJson(response.optString("resultObject"), MyClient.class);
                        Map<String,Object> res = new HashMap<>();
                        res.put("client",ent);
                        res.put("ids",ids);
                        callBack.onResult(type, res);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case MyClientListActivity.REQUEST_GET_FILTER:
                ToolOkHTTP.post(Config.getURL("oles/app/myClient/getStepCount.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response.optBoolean("success")) {
                            Gson gson = new Gson();
                            List<MyExpandVo.MyExpandSub> myExpandSubs = gson.fromJson(response.optString("resultObject"), new TypeToken<List<MyExpandVo.MyExpandSub>>() {
                            }.getType());

                            callBack.onResult(type, myExpandSubs);
                        } else {
                            onFailure();
                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
        }
    }
}
