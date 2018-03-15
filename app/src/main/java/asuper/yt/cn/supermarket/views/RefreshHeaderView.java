package asuper.yt.cn.supermarket.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import asuper.yt.cn.supermarket.R;

/**
 * Created by liaoqinsen on 2017/9/12 0012.
 */

public class RefreshHeaderView extends LinearLayout implements SwipeRefreshTrigger, SwipeTrigger {

    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresh_header,this);
    }

    @Override
    public void onRefresh() {
//        setText("REFRESHING");
    }

    @Override
    public void onPrepare() {
//        setText("");
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled >= getHeight()) {
//                setText("RELEASE TO REFRESH");
            } else {
//                setText("SWIPE TO REFRESH");
            }
        } else {
//            setText("REFRESH RETURNING");
        }
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void onComplete() {
//        setText("COMPLETE");
    }

    @Override
    public void onReset() {
//        setText("");
    }
}