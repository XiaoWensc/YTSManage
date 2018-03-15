package asuper.yt.cn.supermarket.modules.setting;

import android.view.View;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.modules.myprofile.MyProfileFragment;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/12/6.
 */

public class AccountActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_account;
    }

    @Override
    protected void findView(View root) {
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_context,new MyProfileFragment()).commit();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }


}
