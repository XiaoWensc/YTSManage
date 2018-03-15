package asuper.yt.cn.supermarket.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import asuper.yt.cn.supermarket.R;
import chanson.androidflux.Action;
import chanson.androidflux.Dispatcher;
import chanson.androidflux.Reducer;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressPopupWindow;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public abstract class BaseActivity extends AppCompatActivity implements Reducer{

    protected View root;
    private Operation operation;
    private StoreDependencyDelegate storeDependencyDelegate;
    protected  Store store;
    private BaseActivity context;
    private ProgressPopupWindow progressPopupWindow;

    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowblePermissionRunnables = new HashMap<>();
    public static final int DEFUALT_REQUEST_ID = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ActivityManager.in(this);
        if (enableSliding()){
            SlidingLayout rootView = new SlidingLayout(this);
            rootView.bindActivity(this);
        }
        storeDependencyDelegate = new StoreDependencyDelegate(this);
        store = bindStore(storeDependencyDelegate);
        beforeCreate();
        super.onCreate(savedInstanceState);
        context = this;
        operation = new Operation(this);
        root = LayoutInflater.from(this).inflate(getContentId(),null);
        setContentView(root);
        findView(root);

        StatusBarCompat.compat(this, statusBarColor());
    }

    protected  int statusBarColor(){
        return R.color.colorPrimary;
    }

    protected boolean enableSliding() {
        return true;
    }

    protected Action dispatch(int id, Map<String,Object> data){
        Action action = new Action(id) {
        };
        if(data != null) action.putAll(data);
        Dispatcher.dispatch(action,store);
        return action;
    }

    protected void beforeCreate(){}

    protected abstract int getContentId();
    protected abstract void findView(View root);
    protected abstract Store bindStore(StoreDependencyDelegate dependencyDelegate);

    public boolean isAvaliable() {
        return !isFinishing() && !isDestroyed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.out(this);
        storeDependencyDelegate.destroy();
    }

    @Override
    public boolean avaliable() {
        return !isFinishing() && !isDestroyed();
    }

    public Operation getOperation() {
        return operation;
    }

    /**
     * 获取当前Activity
     *
     * @return
     */
    public BaseActivity getContext() {
        return context;
    }


    /**
     * @param requestId            请求授权的Id，唯一即可
     * @param permission           请求的授权
     * @param allowableRunnable    同意授权后的操作
     * @param disallowableRunnable 禁止授权后的操作
     **/
    public void requestPermission(int requestId, String[] permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }
        allowablePermissionRunnables.put(requestId, allowableRunnable);

        if (disallowableRunnable != null) {
            disallowblePermissionRunnables.put(requestId, disallowableRunnable);

        }
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> pers = new ArrayList<>();
            //检查是否拥有权限
            for (int i = 0; i < permission.length; i++) {
                int checkPermission = ContextCompat.checkSelfPermission(YTApplication.get(), permission[i]);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    //弹出对话框请求授权
                    pers.add(permission[i]);
                }
            }
            if (pers.size() > 0) {
                String[] permissions = new String[pers.size()];
                for (int i = 0; i < pers.size(); i++) {
                    permissions[i] = pers.get(i);
                }
                ActivityCompat.requestPermissions(this, permissions, requestId);
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Runnable allowRun = allowablePermissionRunnables.get(requestCode);
            allowRun.run();
        } else {
            Runnable disallowRun = disallowblePermissionRunnables.get(requestCode);
            disallowRun.run();
        }
    }
    public void showProgress(){
        showProgress(null);
    }
    public void showProgress(String info){
        if(progressPopupWindow == null) progressPopupWindow = new ProgressPopupWindow(this);
        progressPopupWindow.setInfo(info);
        progressPopupWindow.show();
    }

    public void dismissProgress(){
        if(progressPopupWindow != null) progressPopupWindow.dismiss();
    }

    protected BeforeFinishInterceptor interceptor;

    @Override
    public void finish() {
        if (interceptor != null && interceptor.needIntercept()) {
            if (interceptor.doBeforeFinish()) {
                super.finish();
            }
        } else {
            super.finish();
        }
    }

    public interface BeforeFinishInterceptor {
        boolean needIntercept();

        boolean doBeforeFinish();
    }
}
