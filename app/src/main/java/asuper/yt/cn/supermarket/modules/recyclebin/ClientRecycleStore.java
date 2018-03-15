package asuper.yt.cn.supermarket.modules.recyclebin;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.modules.myclient.entities.MyClient;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class ClientRecycleStore extends Store {

    public ClientRecycleStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case ClientRecycleActivity.REQUEST_GET_NOINTENTION:
                ToolOkHTTP.post(Config.getURL("app/recycling/noIntentionList.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Gson gson = new Gson();
                        MyClient ent = gson.fromJson(response.optString("resultObject"), MyClient.class);
                        if (ent != null&&ent.getRows()!=null) {
                            //暂无意向列表所有数据显示暂无意向状态，且背景颜色跟被驳回颜色保持一致（红色）
                            for (Clit clit : ent.getRows()) {
                                clit.applySteup = "step_4";
                                clit.setApplyStepName("暂无意向");
                            }
                            callBack.onResult(type, ent);
                        } else {
                            onFailure();
                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case ClientRecycleActivity.REQUEST_GET_GIVEUP:
                ToolOkHTTP.post(Config.getURL("app/recycling/giveupJoiningList.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Gson gson = new Gson();
                        MyClient ent = gson.fromJson(response.optString("resultObject"), MyClient.class);
                       if(ent != null){
                           callBack.onResult(type,ent);
                       }else {
                           onFailure();
                       }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case ClientRecycleActivity.REQUEST_GET_NUM:
                data = new HashMap<>();
                data.put("applyAssignOwner",Config.UserInfo.USER_ID);
                ToolOkHTTP.post(Config.getURL("app/recycling/getTotalNumByApplyStatus.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONObject res = response.optJSONObject("resultObject");
                        if(res != null){
                            int[] nums = new int[]{0,0};
                            if(res != null){
                                nums[0] = res.optInt("noIntentionNum",0);
                                nums[1] = res.optInt("giveupJoiningNum",0);
                            }
                            callBack.onResult(type,nums);
                        }else {
                            onFailure();
                        }

                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,new int[]{0,0});
                    }
                });
                break;
        }
    }
}
