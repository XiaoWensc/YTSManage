package asuper.yt.cn.supermarket.modules.myprofile;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.tencent.bugly.beta.Beta;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.Common;

/**
 * 设置
 * Created by zengxiaowen on 2017/4/18.
 */

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private TextView varName, varCode;


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.isUpdate:
                Beta.checkUpgrade();
                break;
        }
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void findView(View view) {

        varName = (TextView) view.findViewById(R.id.varName);
        varCode = (TextView) view.findViewById(R.id.varCode);

        view.findViewById(R.id.isUpdate).setOnClickListener(this);

        ActionBarManager.initBackToolbar(this, "设置");

        varCode.setText(Common.getVersionCode(getContext())+"");
        varName.setText(Common.getVersionName(getContext()));
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
