package asuper.yt.cn.supermarket.base;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import chanson.androidflux.Action;
import chanson.androidflux.Dispatcher;
import chanson.androidflux.Reducer;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/9/6.
 */

public abstract class BaseFragment extends Fragment implements Reducer{

    /**
     * 当前Fragment渲染的视图View
     **/
    private View mContextView = null;
    /**
     * 共通操作
     **/
    private Operation mBaseOperation = null;

    private StoreDependencyDelegate storeDependencyDelegate;
    protected Store store;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        beforeCreate();
        super.onCreate(savedInstanceState);
    }


    protected void beforeCreate(){}

    protected void showProgress(){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ((BaseActivity)getActivity()).showProgress();
        }
    }

    protected void showProgress(String msg){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ((BaseActivity)getActivity()).showProgress(msg);
        }
    }



    protected void dissmissProgress(){
        if(getActivity() != null && getActivity() instanceof BaseActivity){
            ((BaseActivity)getActivity()).dismissProgress();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        storeDependencyDelegate = new StoreDependencyDelegate(this);
        store = bindStore(storeDependencyDelegate);
        mBaseOperation = new Operation(getActivity());
        mContextView = LayoutInflater.from(getContext()).inflate(getContentId(),null);
        findView(mContextView);

        return mContextView;
    }

    protected abstract int getContentId();
    protected abstract void findView(View root);
    protected abstract Store bindStore(StoreDependencyDelegate dependencyDelegate);
    protected Action dispatch(int id, Map<String,Object> data){
        Action action = new Action(id) {
        };
        if(data != null) action.putAll(data);
        Dispatcher.dispatch(action,store);
        return action;
    }

    /**
     * 获取共通操作机能
     */
    public Operation getOperation() {
        return this.mBaseOperation;
    }

    /**
     * 获取当前Fragment依附在的Activity
     *
     * @return
     */
    public BaseActivity getContext() {
        return (BaseActivity) getActivity();
    }


    @Override
    public boolean avaliable() {
        return !isRemoving() && !isDetached();
    }
}
