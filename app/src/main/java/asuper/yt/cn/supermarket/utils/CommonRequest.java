package asuper.yt.cn.supermarket.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asuper.yt.cn.supermarket.base.Config;


/**
 * Created by liaoqinsen on 2017/4/12 0012.
 */

public class CommonRequest {

    public static final String VERIFY_CODE_TYPE_MODIFY_PHONE = "modifyPhone";
    public static final String VERIFY_CODE_TYPE_TRANSMIT = "dataTransfer";
    public static final String VERIFY_CODE_TYPE_JOIN = "validatePhone";
    private static final HashMap<String, Long> phonesRemainInterval = new HashMap<>();
    private static final HashMap<String, Long> phonesInterval = new HashMap<>();

    private static boolean isStop = false;

    private static ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static Handler handler = new Handler(Looper.getMainLooper());

    public static void checkPhoneVerified(Context context,String intentid,String phoneNumer,final CheckVerifiedCallBack callBack){
        HashMap<String,Object> params = new HashMap<>();
        params.put("intentionId",intentid);
        params.put("phoneNumber",phoneNumer);
        checkPhoneVerified(context, Config.getURL(Config.URL.URL_CHECK_PHONE_VERIFIED),params,callBack);
    }

    public static void checkPhoneVerified(Context context, String url, Map<String,Object> params, final CheckVerifiedCallBack callBack){
        ToolOkHTTP.post(url, params, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if(callBack == null) return;
                callBack.onResult(response.optBoolean("resultObject"));
            }

            @Override
            public void onFailure() {
                if(callBack == null) return;
                callBack.onResult(false);
            }
        });
    }

    public static void getVerifyCode(final String phoneNumber, String businessType, final VerifyCodeCallBack callBack) {
        if (phonesRemainInterval != null && phonesRemainInterval.get(phoneNumber) != null && phonesInterval.get(phoneNumber) != null) {
            long startTime = phonesRemainInterval.get(phoneNumber);
            long total = phonesInterval.get(phoneNumber);
            if (System.currentTimeMillis() - startTime < total) {
                callBack.onResult(-1, "请" + (startTime + total - System.currentTimeMillis()) / 1000 + "秒后再尝试获取验证码");
                return;
            } else {
                phonesRemainInterval.remove(phoneNumber);
                phonesInterval.remove(phoneNumber);
            }
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("phoneNumber", phoneNumber);
        map.put("businessType", businessType);
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_VERIFY_CODE), map,new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optBoolean("success")) {
                    JSONObject res = response.optJSONObject("resultObject");
                    if (res != null) {
                        String interval = res.optString("intervalTime");
                        if (interval != null) {
                            int inter = new BigDecimal(interval).intValue();
                            boolean canStartCountdown = false;
                            if (callBack != null) canStartCountdown = callBack.onResult(inter, "");
                            if (canStartCountdown) {
                                startCountDown(inter, callBack, phoneNumber);
                            }
                            return;
                        }
                    }
                }
                if (callBack != null) {
                    callBack.onResult(-1, "获取验证码失败");
                }
            }

            @Override
            public void onFailure() {
                if (callBack != null) {
                    callBack.onResult(-1, "");
                }
            }
        });
    }

    public static void notifyServerWhenSaveLocal(String intentionId){
        Map<String,Object> data = new HashMap<>();
        data.put("intentionId",intentionId);
        ToolOkHTTP.post(Config.getURL("/oles/app/myClient/modifyOperationTime.htm"),data, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

            }

            @Override
            public void onFailure() {

            }
        });
    }

    public static void stopCountDown() {
        isStop = true;
    }

    private static void startCountDown(int interval, VerifyCodeCallBack callBack, String phone) {
        executorService.execute(new VerifyCodeCountDownRunnable(System.currentTimeMillis(), interval, callBack, phone));
    }

    public interface VerifyCodeCallBack extends VerifyCountDown {
        boolean onResult(int interval, String msg);
    }

    public interface CheckVerifiedCallBack{
        void onResult(boolean isChecked);
    }

    public interface VerifyCountDown {
        void onCountDown(int remain);
    }

    public static class VerifyCodeCountDownRunnable implements Runnable {

        private long passedTime = 0;

        private long startTime = 0;

        private VerifyCodeCallBack callBack;

        private long total;

        private String phoneNumber;

        public VerifyCodeCountDownRunnable(long startTime, int total, VerifyCodeCallBack callBack, String phoneNumber) {
            this.total = total * 1000;
            this.startTime = startTime;
            this.callBack = callBack;
            this.phoneNumber = phoneNumber;
        }

        @Override
        public void run() {
            isStop = false;
            phonesInterval.put(phoneNumber, total);
            phonesRemainInterval.put(phoneNumber, startTime);
            while (!isStop) {
                try {
                    long currentPassed = System.currentTimeMillis() - startTime;
                    if (currentPassed - passedTime >= 1000) {
                        passedTime = currentPassed;
                    }
                    if (total - passedTime <= 0) {
                        if (!isStop) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (isStop) return;
                                    callBack.onCountDown(-1);
                                }
                            });
                        }
                        phonesRemainInterval.remove(phoneNumber);
                        phonesInterval.remove(phoneNumber);
                        break;
                    }
                    if (!isStop) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (isStop) return;
                                callBack.onCountDown((int) ((total - passedTime) / 1000));
                            }
                        });
                    }
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
