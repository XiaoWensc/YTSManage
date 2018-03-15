package asuper.yt.cn.supermarket.modules.contacts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MyLetterListView extends View {

    private Context context;
    OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;

    private List<String> listString = new ArrayList<>();

    public List<String> getListString() {
        return listString;
    }

    public void setListString(List<String> listString) {
        this.listString = listString;
        invalidate();
    }

    public MyLetterListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public MyLetterListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MyLetterListView(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#40000000"));
        }
        int height = getHeight();
        int width = getWidth();
        int singleHeight = listString.size() == 0 ? 0 : height / listString.size();
        for (int i = 0; i < listString.size(); i++) {
            paint.setColor(Color.parseColor("#8c8c8c"));
                paint.setTextSize(dip2px(context,12));
            // paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            /*if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}*/
            float xPos = width / 2 - paint.measureText(listString.get(i)) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(listString.get(i), xPos, yPos, paint);
            paint.reset();
        }
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final int c = (int) (y / getHeight() * listString.size());
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c && onTouchingLetterChangedListener != null) {
                    if (c >= 0 && c < listString.size()) {
                        onTouchingLetterChangedListener.onTouchingLetterChanged(listString.get(c));
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c && onTouchingLetterChangedListener != null) {
                    if (c >= 0 && c < listString.size()) {
                        onTouchingLetterChangedListener.onTouchingLetterChanged(listString.get(c));
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

}
