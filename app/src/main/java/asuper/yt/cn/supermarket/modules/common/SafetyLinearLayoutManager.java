package asuper.yt.cn.supermarket.modules.common;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by liaoqinsen on 2017/9/30 0030.
 * 防止RecycleView因为数据改变前clear数据而崩溃的问题
 */

public abstract class SafetyLinearLayoutManager extends LinearLayoutManager {

    public SafetyLinearLayoutManager(Context context) {
        super(context);
    }

    public SafetyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
