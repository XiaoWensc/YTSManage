package asuper.yt.cn.supermarket.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.dynamic.ShopDynamicActivity;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.newclient.AddNewClientActivity;
import asuper.yt.cn.supermarket.utils.ToolLog;
import cn.jpush.android.api.JPushInterface;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

import static android.content.Context.NOTIFICATION_SERVICE;


/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
@SuppressLint("NewApi")
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        ToolLog.d(printBundle(bundle));
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Config.UserInfo.DEVICEID = regId;
            Config.UserInfo.save();
            ToolLog.d("[MyReceiver] 接收Registration Id : " + regId);
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                showNotification(context, 2, title, msg, extra);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ToolLog.d("收到了自定义消息。消息内容是：" + extra);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            // 在这里可以做些统计，或者做些其他工作
            String msg = bundle.getString(JPushInterface.EXTRA_EXTRA);
            ToolLog.d("收到了通知: " + msg);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            ToolLog.d("用户点击打开了通知");
            // 在这里可以自己写代码去定义用户点击后的行为
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try {
                postActivity(context, extra);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            ToolLog.d("Unhandled intent - " + intent.getAction());
        }
    }


    /**
     * 用户点击了通知
     */
    private void postActivity(Context context, String extra) throws JSONException {
        ToolLog.i("" + extra);
        JSONObject object = new JSONObject(extra);
        JPushExt ext = ToolGson.fromJson(object.optString("other"), JPushExt.class);
        if (!ext.userId.equals(Config.UserInfo.USER_ID)) {
            context.startActivity(new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }else {
            switch (ext.getActivity()) {
                case "noticeDetails":
                    if (ActivityManager.activityTop() instanceof ShopDynamicActivity)
                        ((ShopDynamicActivity) ActivityManager.activityTop()).updateAll(ext.getShopId(),ext.getNoticeType(),1);
                    else {
                        Intent intent = new Intent(context, ShopDynamicActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("shopId", ext.getShopId());
                        intent.putExtra("frag", ext.getNoticeType());
                        context.startActivity(intent);
                    }
                    break;
            }
        }
    }

    /**
     * 打印所有的 intent extra 数据
     */
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    ToolLog.d("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    private void showNotification(Context context, int id, String title, String text, String extra) throws JSONException {

        int shopId = new JSONObject(extra).getInt("shopId");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.logo_xiaochao);
        builder.setContentTitle(title);
        builder.setContentText(text);
        builder.setOnlyAlertOnce(true);
        builder.setWhen(System.currentTimeMillis()); //发送时间
        builder.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
        builder.setAutoCancel(true);//打开程序后图标消失
        // 需要VIBRATE权限
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setPriority(Notification.PRIORITY_DEFAULT);

        // Creates an explicit intent for an Activity in your app
        //自定义打开的界面
        Intent resultIntent = new Intent(context, ShopDynamicActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("shopId", shopId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

}
