package asuper.yt.cn.supermarket.modules.plan;

import android.content.Context;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.modules.index.IndexLvAdapter;

/**
 * Created by zengxiaowen on 2017/9/26.
 */

public class PlanLvAdapter extends IndexLvAdapter {

    public PlanLvAdapter(Context context) {
        super(context);
    }

    @Override
    public int OnEmptyView() {
        return R.layout.layout_empty_view_plan;
    }
}
