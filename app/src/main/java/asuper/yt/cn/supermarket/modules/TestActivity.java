package asuper.yt.cn.supermarket.modules;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import asuper.yt.cn.supermarket.base.BaseActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import asuper.yt.cn.supermarket.R;

/**
 * Created by zengxiaowen on 2017/12/27.
 */

public class TestActivity extends BaseActivity {

    private View foot;

    @Override
    protected int getContentId() {
        return R.layout.fragment_join_new;
    }

    @Override
    protected void findView(View root) {
        foot = root.findViewById(R.id.frame_root);
        setAllEnabled(foot,false);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }


    private void setAllEnabled(View root,boolean enabled){
        if (root instanceof ViewGroup){
            ViewGroup view = (ViewGroup) root;
            for (int i=0;i<view.getChildCount();i++){
                View viewchild = view.getChildAt(i);
                setAllEnabled(viewchild,enabled);
            }
        } else if (root instanceof CompoundButton){
            if (root.getTag()!=null&&!root.getTag().toString().equals("onClick")) root.setEnabled(enabled);
        }
        else{
            if (root.getTag()!=null&&!root.getTag().toString().equals("onClick")) root.setEnabled(enabled);
        }
    }
}
