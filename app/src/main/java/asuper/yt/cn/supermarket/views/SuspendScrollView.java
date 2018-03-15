package asuper.yt.cn.supermarket.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 顶部悬浮
 * Created by zengxiaowen on 2017/9/8.
 */

public class SuspendScrollView extends ScrollView {
    //回调接口的对象
    private OnScrollListener onScrollListener;
    public SuspendScrollView(Context context) {
        super(context);
    }

    public SuspendScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuspendScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);

    }

    /**在滑动的时候调用我们自己写的回调方法，来获取滑动距离*/
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(onScrollListener!=null){
            onScrollListener.onScroll(t);
        }
    }
    /**滑动回调监听的接口*/
    public interface OnScrollListener{
        /**回调方法，返回MyScrollView在Y轴方向的滑动距离*/
        public void onScroll(int scrollY);
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }
}
