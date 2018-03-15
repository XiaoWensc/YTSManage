package asuper.yt.cn.supermarket.modules.myclew;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class MyClewStore extends Store {

    List<ClientInfoDetail> clientelesOld;

    public MyClewStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case MyClewActivity.REQUEST_GET_MISSION:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_NEW_MISSION_LIST), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (response.optBoolean("success")) {
                            JSONObject res = response.optJSONObject("resultObject");
                            if (res == null) {
                                onFailure();
                                return;
                            }
                            List<NewMission> newMissionList = new ArrayList<NewMission>();
                            try {
                                newMissionList = ToolGson.fromJson(res.optString("data"), new TypeToken<List<NewMission>>() {
                                }.getType());
                            } catch (Exception e) {
                                newMissionList = new ArrayList<NewMission>();
                            }
                            callBack.onResult(type, newMissionList);

                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case MyClewActivity.REQUEST_GET_UNCOMPLETE:
                try {
                    clientelesOld = ToolDbOperation.getClientDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                    callBack.onResult(type, clientelesOld);
                } catch (Exception e) {
                    e.printStackTrace();
                    callBack.onResult(type, null);
                }
                break;
            case MyClewActivity.REQUEST_GET_COUNT:
                int unCompleteCount = 0;
                try {
                    clientelesOld = ToolDbOperation.getClientDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                    unCompleteCount = clientelesOld.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int finalUnCompleteCount = unCompleteCount;
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_NEW_MISSION_COUNT), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type, new int[]{response.optInt("resultObject", 0), finalUnCompleteCount});
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, new int[]{0, finalUnCompleteCount});
                    }

                });
                break;
        }
    }
}

