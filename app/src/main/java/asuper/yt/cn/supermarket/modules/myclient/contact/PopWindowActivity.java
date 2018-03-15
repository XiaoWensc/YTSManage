package asuper.yt.cn.supermarket.modules.myclient.contact;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/11/11.
 */

public class PopWindowActivity extends BaseActivity {

    View view;

    @Override
    protected int getContentId() {
        return R.layout.activity_popwin_client;
    }

    @Override
    protected void findView(View root) {
        (view = root.findViewById(R.id.layout)).setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_client_pop);
        view.startAnimation(animation);
        Animation ani2 = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim_client_pop2);
        root.findViewById(R.id.back).startAnimation(ani2);

    }

    public void onBack(View v){
        finish();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
