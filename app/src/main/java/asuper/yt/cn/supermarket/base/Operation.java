package asuper.yt.cn.supermarket.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.j256.ormlite.stmt.query.In;

import java.io.Serializable;
import java.util.ArrayList;

import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;

/**
 * 基本的操作共通抽取
 *
 * @author 曾晓文
 * @version 1.0
 */
public class Operation {

    /**
     * 激活Activity组件意图
     **/
    private Intent mIntent = new Intent();
    /*** 上下文 **/
    private Context mContext = null;
    /*** 整个应用Applicaiton **/
//	private MApplication mApplication = null;

    public static final int REQUESTCODE = 101;

    public Operation(Context mContext) {
        this.mContext = mContext;
//		mApplication = (MApplication) this.mContext.getApplicationContext();
    }

    /**
     * 跳转Activity
     *
     * @param activity 需要跳转至的Activity
     */
    public void forward(Class<? extends Activity> activity) {
        mIntent.setClass(mContext, activity);
        if (!(mContext instanceof Activity)) {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        }
        mContext.startActivity(mIntent);
//		mContext.overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }

    /**
     * 跳转ActivityForResult
     *
     * @param activity    需要跳转至的Activity
     * @param requestCode 请求Code
     */
    public void forwardForResult(Class<? extends Activity> activity, int requestCode) {
        mIntent.setClass(mContext, activity);
        if (mContext instanceof Activity) {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        } else {
            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ((Activity) mContext).startActivityForResult(mIntent, requestCode);
    }


    /**
     * 设置传递参数
     *
     * @param key
     * @param value
     */
    public void addParameter(String key, ArrayList<String> value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Parcelable value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Bundle value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Serializable value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, int value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, Boolean value) {
        mIntent.putExtra(key, value);
    }

    /**
     * 设置传递参数
     *
     * @param key   参数key
     * @param value 数据传输对象
     */
    public void addParameter(String key, String value) {
        mIntent.putExtra(key, value);
    }


    /**
     * 设置全局Application传递参数
     *
     * @param strKey 参数key
     * @param value  数据传输对象
     */
    public void addGloableAttribute(String strKey, Object value) {
//		mApplication.assignData(strKey, value);
    }

    /**
     * 获取跳转时设置的参数
     *
     * @param strKey
     * @return
     */
    public Object getGloableAttribute(String strKey) {
//		return mApplication.gainData(strKey);
        return null;
    }

    /**
     * 弹出等待对话框
     *
     * @param message 提示信息
     */
    public void showLoading(String message) {
        ToolAlert.loading(mContext, message);
    }

    /**
     * 弹出等待对话框
     *
     * @param message  提示信息
     * @param listener 按键监听器
     */
    public void showLoading(String message, ToolAlert.ILoadingOnKeyListener listener) {
        ToolAlert.loading(mContext, message, listener);
    }

    /**
     * 更新等待对话框显示文本
     *
     * @param message 需要更新的文本内容
     */
    public void updateLoadingText(String message) {
        ToolAlert.updateProgressText(message);
    }

    /**
     * 关闭等待对话框
     */
    public void closeLoading() {
        ToolAlert.closeLoading();
    }

}
