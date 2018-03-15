package asuper.yt.cn.supermarket.modules.myprofile;

import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/18 0018.
 */

public class AccountSecondaryStore extends Store {

    public AccountSecondaryStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        Object objUrl = data.get("url");
        if (objUrl == null) {
            return;
        }
        String url = objUrl.toString();
        data.remove("url");
        ToolOkHTTP.post(Config.getURL(url), data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                callBack.onResult(type,true);
            }

            @Override
            public void onFailure() {
                callBack.onResult(type,false);
            }
        });
    }
}
