package asuper.yt.cn.supermarket.modules.coauditing;

import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/18 0018.
 */

public class AuditingMessageStore extends Store {

    public AuditingMessageStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case AuditingMessageActivity.REQUEST_GET_MESSAGE:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_FORM), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response.optJSONObject("resultObject"));
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case AuditingMessageActivity.REQUEST_AGREE:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_AGREE), data, new ToolOkHTTP.OKHttpCallBack() {

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
            case AuditingMessageActivity.REQUEST_REJECT:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_REJECT), data, new ToolOkHTTP.OKHttpCallBack() {

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
