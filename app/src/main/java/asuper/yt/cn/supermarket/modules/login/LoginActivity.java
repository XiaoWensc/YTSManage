package asuper.yt.cn.supermarket.modules.login;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.yatang.oles.base.utils.DES;
import com.yatang.oles.base.utils.Md5Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.setting.SettingActivity;
import chanson.androidflux.Action;
import chanson.androidflux.BindAction;
import chanson.androidflux.Dispatcher;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressButton;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText verifyCode;
    private TextView getVerify;
    private View verifyLine;
    private ProgressButton loginButton;

    public static final int REQUEST_LOGIN = 0;
    public static final int REQUEST_VERIFY = 1;
    public static final int REQUEST_GET_VERIFY = 2;


    private TextView ipconfig;
    private View logo;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what > 0){
                getVerify.setEnabled(false);
                getVerify.setClickable(false);
                getVerify.setTextColor(getResources().getColor(R.color.gray_light));
                getVerify.setText(msg.what+"秒后再次获取");
            }else{
                getVerify.setEnabled(true);
                getVerify.setClickable(true);
                getVerify.setTextColor(getResources().getColor(R.color.neutral_gray));
                getVerify.setText("点击获取验证码");
            }
        }
    };

    private static Thread countDownThread;

    private void attemptLogin(boolean needVerify) {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("请输入密码");
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError("密码格式错误");
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("请输入账号");
            focusView = mEmailView;
            cancel = true;
        }else if(needVerify && ((View)verifyCode.getParent()).getVisibility() == View.VISIBLE && TextUtils.isEmpty(verifyCode.getText().toString())){
            verifyCode.setError("请输入验证码");
            focusView = verifyCode;
            cancel = true;
        }

        if(needVerify && ((View)verifyCode.getParent()).getVisibility() == View.VISIBLE && verifyCode.getText().toString().length() < 6){
            verifyCode.setError("验证码格式错误");
            focusView = verifyCode;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            loginButton.cancel();
        } else {

            Map<String,Object> action = new HashMap<>();
            action.put("login", email);
            action.put("verification", verifyCode.getText().toString());
            try {
                action.put("password", DES.encryptDES(password, Config.PASSWD_KEY));
            } catch (Exception e) {
                e.printStackTrace();
            }
            dispatch(REQUEST_LOGIN,action);
        }
    }


    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }



    @Override
    protected int getContentId() {
        return R.layout.activity_login;
    }

    @Override
    protected void findView(View root) {
        initView(root);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new LoginStore(dependencyDelegate);
    }

    @Override
    protected boolean enableSliding() {
        return false;
    }

    @BindAction(REQUEST_LOGIN)
    public void login(JSONObject response){
        if(response == null){
            onFailure();
            return;
        }
        loginButton.success();
        if (response.optBoolean("success")) {
            Map<String, String> userMap = new HashMap<>();
            userMap.put("verification", verifyCode.getText().toString());

            if (response.optJSONObject("resultObject") == null) {
                ToastUtil.error("登录异常");
                return;
            }

            Config.UserInfo.USER_ID = response.optJSONObject("resultObject").optString("userId");
            Config.UserInfo.COMPANYID = response.optJSONObject("resultObject").optString("companyId");
            Config.UserInfo.FINACIAL_ACCOUNT = response.optJSONObject("resultObject").optString("mFinanceAccount");
            Config.UserInfo.USER_NAME = mEmailView.getText().toString();
            Config.UserInfo.PASSWORD = mPasswordView.getText().toString();
            Config.UserInfo.PHONE = response.optJSONObject("resultObject").optString("phoneNum");
            Config.UserInfo.NAME = response.optJSONObject("resultObject").optString("realName");
            Config.UserInfo.COMPANYID_HOME = response.optJSONObject("resultObject").optString("companyName");
            Config.UserInfo.save();
            if (ActivityManager.getActivitys().size()>1)
                finish();
            else {
                getOperation().forward(MainActivity.class);
                finish();
            }
        } else {
            ToastUtil.error(response.optString("errMsg"));
        }
    }

    @BindAction(REQUEST_VERIFY)
    public void checkVerify(JSONObject response){
        onFailure();
        if(response == null){
            return;
        }
        loginButton.success();
        try {
            boolean res = response.optBoolean("resultObject");
            Config.UserInfo.NEED_VERIFY = res?"yes":"no";
            if(res && ((View)verifyCode.getParent()).getVisibility() != View.VISIBLE){
                ((View)verifyCode.getParent()).setVisibility(res?View.VISIBLE:View.GONE);
                verifyLine.setVisibility(res?View.VISIBLE:View.GONE);
                return;
            }
            if(response.optBoolean("needLogin",true)) {
                attemptLogin(res);
            }
            ((View)verifyCode.getParent()).setVisibility(res?View.VISIBLE:View.GONE);
            verifyLine.setVisibility(res?View.VISIBLE:View.GONE);
            return;
        }catch (Exception e){
            onFailure();
        }
    }

    @BindAction(REQUEST_GET_VERIFY)
    public void getVerify(JSONObject response){
        if(response == null){
            onFailure();
            return;
        }
        boolean isChecked = response == null ?false:response.optBoolean("isChecked");
        if(isChecked){
            if(countDownThread == null || !countDownThread.isAlive() || countDownThread.isInterrupted()){
                countDownThread = new Thread(new Runnable() {

                    private long current = System.currentTimeMillis();

                    @Override
                    public void run() {
                        int remain = 60 - (int)((System.currentTimeMillis()-current)/1000);
                        while (remain >= 0){
                            handler.sendEmptyMessage(remain);
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            remain = 60 - (int)((System.currentTimeMillis()-current)/1000);
                        }
                        handler.sendEmptyMessage(remain);
                    }
                });
                countDownThread.start();
            }else{
                ToastUtil.info("获取验证码失败");
            }
        }
    }

    public void onFailure(){
        loginButton.cancel();
        mEmailView.setEnabled(true);
        mPasswordView.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void initView(View view) {
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(Config.UserInfo.USER_NAME);
        verifyCode = (EditText) findViewById(R.id.login_verify);
        getVerify = (TextView) findViewById(R.id.login_get_verify);
        verifyLine = findViewById(R.id.login_verify_line);
        getVerify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(countDownThread != null && countDownThread.isAlive()){
                    ToastUtil.info("获取验证码过于频繁，请稍候再试");
                    return;
                }
                Map<String,Object> map = new HashMap<>();
                map.put("loginName",mEmailView.getText().toString());
                dispatch(REQUEST_GET_VERIFY,map);

            }
        });
        if(!BuildConfig.DEBUG) {
            logo = findViewById(R.id.login_logo);
            mEmailView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(mEmailView.getText().toString().startsWith("debug://ytxc_debug")){
                        mEmailView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                    }else{
                        mEmailView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
                    }
                }
            });
            logo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Md5Util.md5(mEmailView.getText().toString().trim()).equals(BuildConfig.DEBUG_MODE_COMMAND) && Md5Util.md5(mPasswordView.getText().toString().trim()).equals(BuildConfig.DEBUG_MODE_PWD)) {
                        ipconfig.setVisibility(View.VISIBLE);
                        YTApplication.debugEnable = true;
                    } else {
                        ipconfig.setVisibility(View.GONE);
                        YTApplication.debugEnable = false;
                    }
                    return true;
                }
            });
        }
//        populateAutoComplete();

        initIpConfig(view);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    checkNeedVerify(true,mEmailView.getText().toString());
                    return true;
                }
                return false;
            }
        });

        loginButton = (ProgressButton) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailView.setEnabled(false);
                mPasswordView.setEnabled(false);
                checkNeedVerify(true,mEmailView.getText().toString());
            }
        });
        mEmailView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    checkNeedVerify(false,mEmailView.getText().toString());
                }
            }
        });

    }

    private void checkNeedVerify(boolean login,String loginName){
        Action action = new Action(REQUEST_VERIFY) {
        };
        action.put("loginName",loginName);
        action.put("needLogin",login);
        Dispatcher.dispatch(action,store);
    }

    private void initIpConfig(View v) {
        ipconfig = (TextView) v.findViewById(R.id.login_debug_ipconfig);
        ipconfig.setVisibility(BuildConfig.DEBUG?View.VISIBLE:View.GONE);
        ipconfig.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getOperation().forward(SettingActivity.class);
//                    ToastUtil.info("下发补丁成功！！");
                }
        });
    }

    @Override
    public void onBackPressed() {
        ToolAlert.dialog(getContext(), getContext().getResources().getDrawable(R.mipmap.logo_xiaochao), "提示", "退出程序!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityManager.finishAll();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
    }


}

