package asuper.yt.cn.supermarket.modules.myclient;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/12 0012.
 */

public class MyClientDetailStore extends Store{

    protected static final int POST_URL_CLIENT_RECALL = 0x52220;

    public MyClientDetailStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case MyClientDetailActivity.REQUEST_GET_TAB_INFO:
                Map<String, Object> map = (HashMap<String, Object>) data.get("date");
                final String key = data.get("key").toString();
                ToolOkHTTP.post(Config.getURL("oles/app/myClient/queryClientInfoDetailNew2.htm"), map, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Gson gson = new Gson();
                        Map<String,Object> resp = new HashMap<>();
                        ClientInfoDetail info = gson.fromJson(response.optString("resultObject"), ClientInfoDetail.class);
                        if(info != null){
                            resp.put("date",info);
                            resp.put("key",key);
                            callBack.onResult(type,resp);
                        }else {
                            callBack.onResult(type,null);
                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }

                });
                break;
            case MyClientDetailActivity.REQUEST_GIVE_UP:
                ToolOkHTTP.post(Config.getURL("app/recycling/noIntention.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,true);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,false);
                    }

                });
                break;
            case MyClientDetailActivity.REQUEST_RECOVER:
                ToolOkHTTP.post(Config.getURL("app/recycling/revert.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,true);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,false);
                    }

                });
                break;
            case POST_URL_CLIENT_RECALL:

                ToolOkHTTP.post(data.get("url").toString(), (HashMap<String, Object>) data.get("map"), new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,true);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,false);
                    }

                });
                break;
        }
    }
}
