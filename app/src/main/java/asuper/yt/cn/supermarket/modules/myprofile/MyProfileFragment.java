package asuper.yt.cn.supermarket.modules.myprofile;

import android.Manifest;
import android.view.View;
import android.widget.TextView;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailDialog;
import asuper.yt.cn.supermarket.modules.operate.zbar.ZBarActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/9/7 0007.
 */

public class MyProfileFragment extends BaseFragment implements MainActivity.MainFragment,View.OnClickListener {

    private ClientDetailDialog clientDetailDialog;

    @Override
    protected int getContentId() {
        return R.layout.fragment_my_profile;
    }

    @Override
    protected void findView(View root) {
        clientDetailDialog = new ClientDetailDialog(getContext());
        root.findViewById(R.id.profile_modify).setOnClickListener(this);
        root.findViewById(R.id.profile_setting).setOnClickListener(this);
        root.findViewById(R.id.profile_logout).setOnClickListener(this);
        ((TextView) root.findViewById(R.id.profile_company_name)).setText(Config.UserInfo.COMPANYID_HOME);
        ((TextView) root.findViewById(R.id.profile_name)).setText(Config.UserInfo.NAME);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_modify:
                getOperation().forward(EditAccountActivity.class);
                break;
            case R.id.profile_setting:
                getOperation().forward(AboutActivity.class);
                break;
            case R.id.profile_logout:
                clientDetailDialog.show("退出登录", "是否确认退出登录?", "", new ClientDetailDialog.OnClientDialogConfirmListener() {
                    @Override
                    public void onConfirm() {
                        YTApplication.logOut(getContext());
                    }
                });
                break;
        }
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }
}
