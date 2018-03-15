package asuper.yt.cn.supermarket.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import asuper.yt.cn.supermarket.R;

/**
 * Created by liaoqinsen on 2017/9/12 0012.
 */

public class LoadMoreFooterView extends LinearLayout implements SwipeTrigger, SwipeLoadMoreTrigger {
    public LoadMoreFooterView(Context context) {
        super(context);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_refresh_header,this);
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled <= -getHeight()) {
//                setText("RELEASE TO LOAD MORE");
            } else {
//                setText("SWIPE TO LOAD MORE");
            }
        } else {
//            setText("LOAD MORE RETURNING");
        }
    }

    @Override
    public void onRelease() {
//        setText("LOADING MORE");
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
