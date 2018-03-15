package asuper.yt.cn.supermarket.modules.myprofile;

import android.content.Context;
import android.view.View;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * 个人资料修改
 * Created by zengxiaowen on 2017/4/18.
 */

public class EditAccountActivity extends BaseActivity implements View.OnClickListener {

    private void gotoModify(int id) {
        getOperation().addParameter("type", id);
        getOperation().forward(AccountSecondaryActivity.class);
    }

    @Override
    public void onClick(View v) {
        gotoModify(v.getId());
    }

    @Override
    protected int getContentId() {
        return  R.layout.activity_edit_account;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this, "信息修改");
        root.findViewById(R.id.nav_name).setOnClickListener(this);
        root.findViewById(R.id.nav_itbt).setOnClickListener(this);
        root.findViewById(R.id.nav_phone).setOnClickListener(this);
        root.findViewById(R.id.nav_passd).setOnClickListener(this);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
