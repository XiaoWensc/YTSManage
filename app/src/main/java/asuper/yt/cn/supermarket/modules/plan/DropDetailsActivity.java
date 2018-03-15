package asuper.yt.cn.supermarket.modules.plan;

import android.view.View;
import android.widget.TextView;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/9/12.
 */

public class DropDetailsActivity extends BaseActivity {

    @Override
    protected int getContentId() {
        return R.layout.activity_drop_details;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"拜访详情");
        Plan.PlanItem item = (Plan.PlanItem) getIntent().getSerializableExtra("plan");
        initView(root,item);
    }

    private void initView(View root, Plan.PlanItem item) {
        ((TextView) root.findViewById(R.id.tvTitle)).setText(item.getShopName());
        ((TextView) root.findViewById(R.id.tvUserName)).setText(Config.UserInfo.NAME+"  |  "+(item.getVisitType().equals("home_visit")?"上门拜访":"电话拜访"));
        ((TextView) root.findViewById(R.id.tvTime)).setText(item.getCreateTime().split("\\.")[0]);
        ((TextView) root.findViewById(R.id.tvDes)).setText(item.getVisitContent());
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
