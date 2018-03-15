package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zengxiaowen on 2017/7/6.
 */

public class TrigonView extends View {

    private Context mContext;
    //定义一个paint
    private Paint mPaint;

    public TrigonView(Context context) {
        this(context, null);
    }
    public TrigonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TrigonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawTrigon(canvas);

        Paint p = new Paint();
        p.setColor(Color.RED);// 设置红色

        Path path = new Path();
        path.moveTo(80, 200);// 此点为多边形的起点
        path.lineTo(120, 250);
        path.lineTo(80, 250);
        path.close(); // 使这些点构成封闭的多边形
        canvas.drawPath(path, p);

    }

    private void drawTrigon(Canvas canvas) {

        int width = getWidth();
        int height = getHeight();

        mPaint =new Paint();
        // 绘制画布背景
//        canvas.drawColor(Color.GRAY);
        //设置画笔颜色
        mPaint.setColor(Color.WHITE);
        //三角形
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth()/2, getHeight());
        path.close();
        canvas.drawPath(path, mPaint);
    }
}
