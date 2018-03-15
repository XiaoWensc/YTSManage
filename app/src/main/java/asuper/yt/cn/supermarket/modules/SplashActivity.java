package asuper.yt.cn.supermarket.modules;

import android.content.Intent;
import android.view.View;

import com.yatang.oles.base.utils.DES;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import asuper.yt.cn.supermarket.modules.login.LoginStore;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.utils.ToolNetwork;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/5/9 0009.
 */

public class SplashActivity extends BaseActivity {

    @Override
    protected int getContentId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void findView(View root) {
    }

    @BindAction(LoginActivity.REQUEST_LOGIN)
    public void loginResult(JSONObject response) {
        if (response == null) {
            checkUpdate();
            return;
        }
        Config.UserInfo.USER_ID = response.optJSONObject("resultObject").optString("userId");
        Config.UserInfo.COMPANYID = response.optJSONObject("resultObject").optString("companyId");
        Config.UserInfo.FINACIAL_ACCOUNT = response.optJSONObject("resultObject").optString("mFinanceAccount");
        Config.UserInfo.PHONE = response.optJSONObject("resultObject").optString("phoneNum");
        Config.UserInfo.NAME = response.optJSONObject("resultObject").optString("realName");
        Config.UserInfo.COMPANYID_HOME = response.optJSONObject("resultObject").optString("companyName");
        Config.UserInfo.NEED_VERIFY = "no";
        Config.UserInfo.save();
        getOperation().forward(MainActivity.class);
        finish();

    }

    @Override
    protected boolean enableSliding() {
        return false;
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new LoginStore(dependencyDelegate);
    }

    private void checkUpdate() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("systemType", "android");
//        hashMap.put("version", Common.getVersionName(this));
//        hashMap.put("versionCode",Common.getVersionCode(this));

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ToolNetwork.getInstance().validateNetWork(this)) {

            Config.UserInfo.restore();
            if (Config.UserInfo.NEED_VERIFY != null && Config.UserInfo.USER_NAME != null && Config.UserInfo.PASSWORD != null) {
                if ("yes".equals(Config.UserInfo.NEED_VERIFY)) {
                    checkUpdate();
                } else {
                    Map<String, Object> action = new HashMap<>();
                    action.put("login", Config.UserInfo.USER_NAME);
                    try {
                        action.put("password", DES.encryptDES(Config.UserInfo.PASSWORD, Config.PASSWD_KEY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dispatch(LoginActivity.REQUEST_LOGIN, action);
                }
            } else {
                checkUpdate();
            }
        }
    }
}
