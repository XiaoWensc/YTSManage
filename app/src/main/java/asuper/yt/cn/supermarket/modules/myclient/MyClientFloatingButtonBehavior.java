package asuper.yt.cn.supermarket.modules.myclient;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liaoqinsen on 2017/9/14 0014.
 */

public class MyClientFloatingButtonBehavior extends CoordinatorLayout.Behavior {

    public MyClientFloatingButtonBehavior() {
        super();
    }

    public MyClientFloatingButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return true;//返回true代表我们关心这个滚动事件
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
        if (dy < 0) {//向下滚动
            ViewCompat.animate(child).translationX(0).start();
        } else {//向上滚动
            ViewCompat.animate(child).translationX(child.getMeasuredWidth()+child.getRight()).start();
        }
    }
}
