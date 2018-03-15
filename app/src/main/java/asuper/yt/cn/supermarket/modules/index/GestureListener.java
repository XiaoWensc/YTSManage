package asuper.yt.cn.supermarket.modules.index;

import android.view.GestureDetector;
import android.view.MotionEvent;
/**
 * Created by zengxiaowen on 2017/9/25.
 */

public class GestureListener implements GestureDetector.OnGestureListener {

    private OnTuoListener listener;

    public GestureListener(OnTuoListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (e1.getX() - e2.getX() > 80 && listener != null) {
            // 向左滑
            listener.onLift();
            return true;
        } else if (e1.getX() - e2.getX() < -80 && listener != null) {
            listener.onRight();
            return true;
        }
        return false;
    }

    public interface OnTuoListener {
        void onLift();

        void onRight();
    }
}
