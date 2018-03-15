package asuper.yt.cn.supermarket.modules.login;

import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public class LoginStore extends Store {

    public LoginStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, final HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case LoginActivity.REQUEST_LOGIN:
                data.put("deviceId", Config.UserInfo.DEVICEID);
                ToolOkHTTP.post(Config.getURL("app/sys/login.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type, response);
                    }


                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case LoginActivity.REQUEST_VERIFY:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_CHECK_LOGIN_VERIFY), data, new ToolOkHTTP.OKHttpCallBack() {


                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            response.put("needLogin",data.get("needLogin"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.onResult(type, response);
                    }


                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case LoginActivity.REQUEST_GET_VERIFY:
                CommonRequest.checkPhoneVerified(YTApplication.get(), Config.getURL(Config.URL.URL_GET_LOGIN_VERIFY), data, new CommonRequest.CheckVerifiedCallBack() {
                    @Override
                    public void onResult(boolean isChecked) {
                       JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("isChecked",isChecked);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        callBack.onResult(type,jsonObject);
                    }
                });
                break;
        }
    }
}
