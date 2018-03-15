package asuper.yt.cn.supermarket.base;

import asuper.yt.cn.supermarket.BuildConfig;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolPreference;

/**
 * Created by liaoqinsen on 2017/9/4 0004.
 */

public class Config {
    /*** 数据库名称  */
    public static final String DB_NAME = "ytSuper.db";

    /*** 数据库版本号 */
    public static final int DB_VERSION = 30;

    //线上
    public static String HOST = BuildConfig.CLIENT_HOST;
    public static String IMG_HOST = BuildConfig.IMG_HOST;
    public static String JPshTag = BuildConfig.JPUSH_TAG;

    public static String DEV_HOST = "http://172.30.11.20:30201/";
    public static String DEV_HOST_IMG = "http://uat.image.com/";
    public static String DEV_HOST_TAG = "oles_tag_uat";

    public static String TEST_HOST = "http://sit.olesapp.com.cn:8081/";
    public static String TEST_HOST_IMG = "http://sit.image.com/";
    public static String TEST_HOST_TAG = "oles_tag_test";

    public static String SIT3_HOST = "http://sit3.olesapp.com:8082/";
    public static String SIT3_HOST_IMG = "http://sit.image.com/";
    public static String SIT3_HOST_TAG = "oles_tag_test";

    public static String PRO_HOST = "https://olesapp.yatang.com.cn/";
    public static String PRO_HOST_IMG = "http://olesimg.yatang.com.cn/";
    public static String PRO_HOST_TAG = "oles_tag_release";

    /***  -----    接口API end*/

    /*** 密码加密key */
    public static final String PASSWD_KEY = "@DYtOles";

    /*** sign生成key */
    public static final String SIGN_KEY = "@MYtOles";


    public static boolean isAutoFillAttachment = false;

    public static String autoFillAttachmentPath = null;

    public static class URL{
        public static final String URL_GET_VERIFYCODE = "app/sys/sendVerificationCode.htm";
        public static String URL_TRANSMIT = "app/usercenter/transferData.htm";
        public static String URL_MODIFY_NAME = "app/sys/updateEmployeeInfo.htm";
        public static String URL_MODIFY_PHONE = "app/sys/modifyEmployeePhoneNumber.htm";
        public static String URL_SUBSIDY = "/app/subsidy/pagelist.htm";
        public static String URL_MODIFY_PWD = "app/sys/modifyUserPassword.htm";
        public static String URL_GET_VERIFY_CODE = "app/sys/sendVerificationCode.htm";
        public static String URL_GET_TRANSMITINFO = "app/usercenter/communicationList.htm";
        public static String URL_CHECK_PHONE = "app/sys/validatePhoneNumber.htm";
        public static String URL_GET_SUBSDIY = "oles/app/rental/getRentalInfo.htm";
        public static String URL_ADD_SUBSDIY = "oles/app/rental/addRental.htm";
        public static String URL_UPDATE_SUBSDIY = "oles/app/rental/updateRental.htm";
        public static String URL_GET_AUDIT_MESSAGE = "app/audit/messages.htm";
        public static String URL_JOIN_CANCEL = "app/ratingScale/revoke.htm";
        public static String URL_CONTRACT_CANCEL = "oles/app/agreementApproveAction/revoke.htm";
        public static String URL_SUBSIDY_CANCEL = "oles/app/rental/revoke.htm";
        public static String URL_CHECK_UPDATE = "app/upgrade/check.htm";
        public static String URL_CHECK_PHONE_VERIFIED = "app/ratingScale/phoneIsCheck.htm";
        public static String URL_MISSION_LIST = "app/audit/list.htm";
        public static String URL_MAIN_BUTTON= "oles/app/myClient/loadButton2.htm";
        public static String URL_MAIN_SUB_BUTTON = "oles/app/myClient/loadSubButton.htm";
        public static String URL_GET_AUDITING_BUTTON = "app/audit/list/detail/buttons.htm";
        public static String URL_GET_AUDITING_INFO = "app/audit/list/detail.htm";
        public static String URL_GET_AUDITING_MESSAGE = "app/audit/list/detail/auditMessages.htm";
        public static String URL_GET_AUDITING_FORM = "app/audit/roleForm.htm";
        public static String URL_GET_AUDITING_AGREE= "app/audit/pass.htm";
        public static String URL_GET_AUDITING_REJECT = "app/audit/reject.htm";
        public static String URL_GET_MISSIONS_NOTIFY = "app/audit/notice/list.htm";
        public static String URL_GET_MISSIONS_NOTIFY_UPDATE = "app/audit/notice/updateStatus.htm";
        public static String URL_ORDER= "oles/app/myClient/ashOrder.htm";
        public static String URL_NEW_MISSION_COUNT= "oles/app/myClient/myMissionCount.htm";
        public static String URL_NEW_MISSION_LIST= "oles/app/myClient/myMissionList.htm";
        public static String URL_NEW_MISSSION_DETAIL= "oles/app/myClient/myMissionDetail.htm";
        public static String URL_GET_ADD= "app/region/queryRegion.htm";
        public static String URL_GET_JOIN_ATTACHMENT= "app/ratingScale/queryRatingScaleAnnexByLegal2.htm";
        public static String URL_CHECK_LOGIN_VERIFY =  "app/sys/isSendSms.htm";
        public static String URL_GET_LOGIN_VERIFY =  "app/sys/sendSms.htm";
        public static String URL_GET_WEEKVISITCOUNT =  "app/myPlan/weekVisitCount.htm"; // 每日拜访数
        public static String URL_GET_NEWCUSTOMERCOUNT =  "app/myPlan/newCustomerCount.htm"; // 每日新增客户数
        public static String URL_GET_INSERT =  "app/myPlan/insert.htm"; // 添加计划
    }

    public static class UserInfo{
        public static String USER_ID;
        public static String USER_NAME;
        public static String PASSWORD;
        public static String COMPANYID;
        public static String FINACIAL_ACCOUNT;
        public static String PHONE;
        public static String NAME;
        public static String COMPANYID_HOME;
        public static String NEED_VERIFY;
        public static String DEVICEID; //设备ID

        public static final String KEY_USER_ID = "ui_user_id";
        public static final String KEY_USER_NAME = "ui_user_name";
        public static final String KEY_NEED_VERIFY = "ui_verify";
        public static final String KEY_COMPANYID = "ui_company_id";
        public static final String KEY_FINACIAL_ACCOUNT = "ui_finacial_account";
        public static final String KEY_PHONE = "ui_phone";
        public static final String KEY_NAME = "ui_name";
        public static final String KEY_COMPANYID_HOME = "ui_company_home";
        public static final String KEY_PASSWORD = "ui_password";
        public static final String KEY_DEVICEID = "deviceId";

        public static void save(){
            ToolPreference.get().putString(KEY_USER_ID,USER_ID);
            ToolPreference.get().putString(KEY_USER_NAME,USER_NAME);
            ToolPreference.get().putString(KEY_COMPANYID,COMPANYID);
            ToolPreference.get().putString(KEY_FINACIAL_ACCOUNT,FINACIAL_ACCOUNT);
            ToolPreference.get().putString(KEY_PHONE,PHONE);
            ToolPreference.get().putString(KEY_NAME,NAME);
            ToolPreference.get().putString(KEY_COMPANYID_HOME,COMPANYID_HOME);
            ToolPreference.get().putString(KEY_PASSWORD,PASSWORD);
            ToolPreference.get().putString(KEY_NEED_VERIFY,NEED_VERIFY);
            ToolPreference.get().putString(KEY_DEVICEID,DEVICEID);
        }

        public static void restore(){
            USER_ID = ToolPreference.get().getString("ui_user_id");
            USER_NAME = ToolPreference.get().getString("ui_user_name");
            COMPANYID = ToolPreference.get().getString("ui_company_id");
            FINACIAL_ACCOUNT = ToolPreference.get().getString("ui_finacial_account");
            PHONE = ToolPreference.get().getString("ui_phone");
            NAME = ToolPreference.get().getString("ui_name");
            COMPANYID_HOME = ToolPreference.get().getString("ui_company_home");
            PASSWORD = ToolPreference.get().getString(KEY_PASSWORD);
            NEED_VERIFY = ToolPreference.get().getString(KEY_NEED_VERIFY);
            DEVICEID = ToolPreference.get().getString(KEY_DEVICEID);
        }
    }

    public static String getURL(String url){
        return HOST+url;
    }
}
