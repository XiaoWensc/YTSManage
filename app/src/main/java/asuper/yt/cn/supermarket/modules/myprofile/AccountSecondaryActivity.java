package asuper.yt.cn.supermarket.modules.myprofile;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressButton;

/**
 * Created by Chanson on 2017/4/5.
 */

public class AccountSecondaryActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_MODIFY_NONE = 0x00;

    public static final String RESULT_FINAL = "final";

    private int type = TYPE_MODIFY_NONE;

    private View modifyNameContainer, modifyPWDContainer, modifyPhoneContainer, transmitContainer, verifyCodeContainer, modify_check_password_container;

    private EditText name, finacialAccount, oldPwd, newPwd, repeatPwd, verifyCode, phone, check_pwd;
    private TextView transmitPeople, transmitPhone;

    private TextView getVerifyCode,getVerifyCodeCountDown;

    private ProgressButton commit;

    private String verifyType;

    private TransmitVO transmitVO;

    private int transmitId = -1;
    private String transmitName = null;
    private OnRequestComplete onRequestComplete;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_get_verify_code:
                getVerifyCode();
                break;
            case R.id.modify_commit:
                commit();
                break;
            case R.id.modify_transmit:
//                getOperation().forward(TransmitPeopleInfosActivity.class);
                break;
        }
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_secondary_account;
    }

    @Override
    protected void findView(View root) {
        type = getIntent().getExtras().getInt("type");
        (commit = (ProgressButton) root.findViewById(R.id.modify_commit)).setOnClickListener(this);
        switch (type) {
            case R.id.nav_name:
                initModifyName();
                break;
            case R.id.nav_phone:
                initModifyPhone();
                break;
            case R.id.nav_over:
                initTransmit();
                break;
            case R.id.nav_itbt:
                initModifyName();
                break;
            case R.id.nav_passd:
                initModifyPWD();
                break;
            default:
                break;
        }

    }

    private void initModifyName() {
        ActionBarManager.initBackToolbar(this, "修改姓名/金融账号");
        (modifyNameContainer = findViewById(R.id.modify_name_container)).setVisibility(View.VISIBLE);
        name = (EditText) modifyNameContainer.findViewById(R.id.modify_name);
        name.setText(Config.UserInfo.NAME);
        finacialAccount = (EditText) modifyNameContainer.findViewById(R.id.modify_finacial_account);
        finacialAccount.setText(Config.UserInfo.FINACIAL_ACCOUNT == null ? "" : Config.UserInfo.FINACIAL_ACCOUNT);
    }

    private void initModifyPhone() {
        ActionBarManager.initBackToolbar(this, "修改手机");
        (modifyPhoneContainer = findViewById(R.id.modify_phone_container)).setVisibility(View.VISIBLE);
        (verifyCodeContainer = findViewById(R.id.modify_verifycode_container)).setVisibility(View.VISIBLE);
        modify_check_password_container = findViewById(R.id.modify_check_password_container);
        check_pwd = (EditText) modify_check_password_container.findViewById(R.id.modify_check_password);
        verifyType = CommonRequest.VERIFY_CODE_TYPE_MODIFY_PHONE;
        phone = (EditText) modifyPhoneContainer.findViewById(R.id.modify_new_phone);
        verifyCode = (EditText) verifyCodeContainer.findViewById(R.id.modify_verify_code);
        getVerifyCode = (TextView) verifyCodeContainer.findViewById(R.id.modify_get_verify_code);
        getVerifyCodeCountDown = (TextView) verifyCodeContainer.findViewById(R.id.modify_get_verify_code_countdown);
        getVerifyCode.setOnClickListener(this);
    }

    private void initModifyPWD() {
        ActionBarManager.initBackToolbar(this, "修改密码");
        (modifyPWDContainer = findViewById(R.id.modify_pwd_container)).setVisibility(View.VISIBLE);

        oldPwd = (EditText) modifyPWDContainer.findViewById(R.id.modify_old_pwd);
        newPwd = (EditText) modifyPWDContainer.findViewById(R.id.modify_new_pwd);
        repeatPwd = (EditText) modifyPWDContainer.findViewById(R.id.modify_new_pwd_confirm);
    }

    private void initTransmit() {
        ActionBarManager.initBackToolbar(this, "小超移交");
        (verifyCodeContainer = findViewById(R.id.modify_verifycode_container)).setVisibility(View.VISIBLE);
        (transmitContainer = findViewById(R.id.modify_transmit_container)).setVisibility(View.VISIBLE);
        modify_check_password_container = findViewById(R.id.modify_check_password_container);
        check_pwd = (EditText) modify_check_password_container.findViewById(R.id.modify_check_password);

        verifyType = CommonRequest.VERIFY_CODE_TYPE_TRANSMIT;
        transmitPeople = (TextView) findViewById(R.id.modify_transmit);
        transmitPhone = (TextView) findViewById(R.id.modify_transmit_phone);
        transmitPhone.setText(Config.UserInfo.PHONE);
        verifyCode = (EditText) verifyCodeContainer.findViewById(R.id.modify_verify_code);
        getVerifyCode = (TextView) verifyCodeContainer.findViewById(R.id.modify_get_verify_code);
        getVerifyCodeCountDown = (TextView) verifyCodeContainer.findViewById(R.id.modify_get_verify_code_countdown);
        getVerifyCode.setOnClickListener(this);
        transmitPeople.setOnClickListener(this);
//        getTransmitInfo(false,false);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new AccountSecondaryStore(dependencyDelegate);
    }
    private void commit() {
        if (modify_check_password_container != null && modify_check_password_container.getVisibility() != View.VISIBLE) {
            modify_check_password_container.setVisibility(View.VISIBLE);
            if (check_pwd.getText().toString().isEmpty()) {
                check_pwd.setError("请输入密码后再次点击提交");
                check_pwd.requestFocus();
                commit.cancel();
                return;
            }
            check_pwd.requestFocus();
        }
        switch (type) {
            case R.id.nav_name:
                modifyNameAndFinacialAccount();
                break;
            case R.id.nav_phone:
                modifyPhoneNumber();
                break;
            case R.id.nav_over:
                transmit();
                break;
            case R.id.nav_itbt:
                modifyNameAndFinacialAccount();
                break;
            case R.id.nav_passd:
                modifyPWD();
                break;
            default:
                break;
        }
    }

    private void getVerifyCode() {
        String phoneNumber = null;
        if (CommonRequest.VERIFY_CODE_TYPE_MODIFY_PHONE.equals(verifyType)) {
            phoneNumber = phone.getText().toString();
        } else if (CommonRequest.VERIFY_CODE_TYPE_TRANSMIT.equals(verifyType)) {
            phoneNumber = Config.UserInfo.PHONE;
        }

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            phone.setError("请输入手机号");
            return;
        }
        if (phoneNumber != null && phoneNumber.length() < 11) {
            phone.setError("手机号格式错误");
            return;
        }
        getVerifyCode.setText("正在获取验证码");
        getVerifyCode.setClickable(false);
        CommonRequest.getVerifyCode(phoneNumber, verifyType, new CommonRequest.VerifyCodeCallBack() {
            @Override
            public boolean onResult(int interval, String msg) {
                if (interval < 0) {
                    ToastUtil.error(msg);
                    getVerifyCode.setVisibility(View.VISIBLE);
                    getVerifyCodeCountDown.setVisibility(View.GONE);
                    getVerifyCode.setClickable(true);
                    getVerifyCode.setText("获取验证码");
                    return false;
                }
                getVerifyCode.setClickable(false);
                return true;
            }

            @Override
            public void onCountDown(int remain) {
                if (remain > 0) {
                    getVerifyCode.setVisibility(View.GONE);
                    getVerifyCodeCountDown.setVisibility(View.VISIBLE);
                    getVerifyCode.setClickable(false);
                    getVerifyCodeCountDown.setText(remain + "S");
                } else {

                    getVerifyCode.setVisibility(View.VISIBLE);
                    getVerifyCodeCountDown.setVisibility(View.GONE);
                    getVerifyCode.setClickable(true);
                    getVerifyCode.setText("获取验证码");
                }
            }
        });
    }

    private void modifyNameAndFinacialAccount() {
        if (name.getText().toString().isEmpty()) {
            name.setError("姓名不能为空");
            return;
        }
        if (finacialAccount.getText().toString().isEmpty()) {
            finacialAccount.setError("金融账号不能为空");
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("url", Config.URL.URL_MODIFY_NAME);
        map.put("employeeLoginName", Config.UserInfo.USER_NAME);
        map.put("employeeName", name.getText().toString());
        map.put("financeAccount", finacialAccount.getText().toString());

        onRequestComplete = new OnRequestComplete() {
            @Override
            public void write() {
                Config.UserInfo.NAME = name.getText().toString();
                Config.UserInfo.FINACIAL_ACCOUNT = finacialAccount.getText().toString();
                Config.UserInfo.save();
            }
        };
        requestData(map);
    }

    private void modifyPhoneNumber() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("url", Config.URL.URL_MODIFY_PHONE);
        map.put("employeeLoginName", Config.UserInfo.USER_NAME);
        map.put("verificationCode", verifyCode.getText().toString());
        map.put("businessType", verifyType);
        map.put("password", check_pwd.getText().toString());
        map.put("phoneNumber", phone.getText().toString());
        if (phone.getText().toString().length() != 11) {
            phone.setError("手机号格式错误");
            return;
        }
        onRequestComplete = new OnRequestComplete() {
            @Override
            public void write() {
                Config.UserInfo.PHONE = phone.getText().toString();
            }
        };

        requestData(map);
    }

    private void transmit() {
        if (transmitId < 0 || transmitName == null || transmitName.isEmpty()) {
            ToastUtil.error("请选择移交人员");
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("url", Config.URL.URL_TRANSMIT);
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("transferId", transmitId);
        map.put("employeeName", Config.UserInfo.NAME);
        map.put("transferName", transmitName);
        map.put("password", check_pwd.getText().toString());
        map.put("businessType", verifyType);
        map.put("Telephone", Config.UserInfo.PHONE);
        map.put("validate", verifyCode.getText().toString());
        onRequestComplete = new OnRequestComplete() {
            @Override
            public void write() {
                YTApplication.logOut(AccountSecondaryActivity.this);
            }
        };
        requestData(map);
    }

    private void modifyPWD() {
        if (oldPwd.getText().toString().isEmpty()) {
            oldPwd.setError("请输入密码");
            return;
        }
        if (newPwd.getText().toString().isEmpty()) {
            newPwd.setError("请输入新密码");
            return;
        }

        if (newPwd.getText().toString().length() < 6) {
            newPwd.setError("密码必须大于6位");
            return;
        }
        if (repeatPwd.getText().toString().isEmpty()) {
            repeatPwd.setError("请再次输入新密码");
            return;
        }
        if (!newPwd.getText().toString().equals(repeatPwd.getText().toString())) {
            ToastUtil.error("两次密码输入不一致，请重新输入");
            return;
        }
        onRequestComplete = new OnRequestComplete() {
            @Override
            public void write() {
                YTApplication.logOut(AccountSecondaryActivity.this);
            }
        };

        HashMap<String, Object> map = new HashMap<>();
        map.put("url", Config.URL.URL_MODIFY_PWD);
        map.put("employeeLoginName", Config.UserInfo.USER_NAME);
        map.put("oldPassword", oldPwd.getText().toString());
        map.put("newPassword", newPwd.getText().toString());
        map.put("confirmPassword", repeatPwd.getText().toString());
        requestData(map);
    }

    private void requestData(Map<String,Object> data){
        dispatch(0x0901,data);
    }

    @BindAction(0x0901)
    public void onCommonResult(boolean success){
        if(success) {
            ToastUtil.success("修改成功");
            if (onRequestComplete != null) onRequestComplete.write();
            commit.cancel();
            finish();
        }else{
            ToastUtil.success("修改失败");
            commit.cancel();
        }
    }

    private interface OnRequestComplete {
        void write();
    }

}
