package asuper.yt.cn.supermarket.modules.main;

import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.dynamic.Dynamic;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/6 0006.
 */

public class MainStore extends Store {

    public static final int REQUEST_GET_DYNAMIC_LISTSIZE = 0x3004;  // 获取通知代办列表条数

    public MainStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, final HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case MainActivity.REQUEST_GET_MAIN_BUTTON:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_MAIN_BUTTON), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
//            case MainActivity.REQUEST_GET_DYNAMIC_NUM:
//                ToolOkHTTP.post(Config.getURL("app/shopDynamic/list.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
//
//                    @Override
//                    public void onSuccess(JSONObject response) {
//                        Dynamic dyn = ToolGson.fromJson(response.toString(), Dynamic.class);
//                        int num = 0;
//                        if(dyn != null && dyn.getResultObject() != null){
//                            Set<String> keys = dyn.getResultObject().keySet();
//                            for(String key:keys){
//                                List items = dyn.getResultObject().get(key).getRows();
//                                num += items == null?0:items.size();
//                            }
//                        }
//                        callBack.onResult(type,num);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        callBack.onResult(type,0);
//                    }
//                });
//                break;

            case REQUEST_GET_DYNAMIC_LISTSIZE:
                ToolOkHTTP.post(Config.getURL("app/shopDynamic/getCount.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONObject object = response.optJSONObject("resultObject");
                        int[] toNot = new int[3];
                        toNot[0] = object.optInt("total",0);
                        toNot[1] = object.optInt("notice",0);
                        toNot[2] = object.optInt("todo",0);
                        callBack.onResult(type,toNot);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
        }
    }
}
