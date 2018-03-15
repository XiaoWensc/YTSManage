package asuper.yt.cn.supermarket.modules.operate.zbar;

import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by zengxiaowen on 2017/12/11.
 */

public class ZBarStore extends Store {

    protected static final int REQUEST_POST_ZBAR_GETINFO = 0x6001;  // 查询商户信息
    protected static final int REQUEST_POST_ZBAR_JSONWS = 0x6002;  // 签到

    public ZBarStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case REQUEST_POST_ZBAR_GETINFO:
                ToolOkHTTP.post(Config.getURL("oles/app/whitelist/getInfo.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response.optString("resultObject"));
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_POST_ZBAR_JSONWS:
                ToolOkHTTP.post(Config.getURL("oles/app/whitelist/joinWs.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

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
