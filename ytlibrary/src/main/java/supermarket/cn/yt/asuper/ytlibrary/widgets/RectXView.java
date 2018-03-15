package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import supermarket.cn.yt.asuper.ytlibrary.R;

/**
 * Created by zengxiaowen on 2017/7/6.
 */

public class RectXView extends View {

    private Paint paint;
    private boolean isLine = true;

    public RectXView(Context context) {
        super(context);
    }

    public RectXView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RectXView);
        isLine = array.getBoolean(R.styleable.RectXView_isLine,true);
    }

    public RectXView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RectXView);
        isLine = array.getBoolean(R.styleable.RectXView_isLine,true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();

        paint.setAntiAlias(true);                       //设置画笔为无锯齿
        paint.setColor(Color.WHITE);                    //设置画笔颜色
//        canvas.drawColor(Color.WHITE);                  //白色背景
        paint.setStrokeWidth((float) 1.0);              //线宽
        paint.setStyle(Paint.Style.FILL);

        if (isLine) {
            RectF oval = new RectF(0, 1,
                    getWidth(), getWidth());

            canvas.drawArc(oval, 210, 120, true, paint);


            paint.setColor(Color.parseColor("#d5d5d5"));                    //设置画笔颜色
            paint.setStyle(Paint.Style.STROKE);
            RectF oval1 = new RectF(0, 1,
                    getWidth(), getWidth());

            canvas.drawArc(oval1, 200, 140, false, paint);
        }
        paint.setColor(Color.parseColor("#1b82d1"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(getWidth()/2,getHeight()/2,getWidth()/2-15,paint);

        //画图片，就是贴图
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_main_add);
        canvas.drawBitmap(bitmap, getWidth()/2-bitmap.getWidth()/2,getHeight()/2-bitmap.getHeight()/2, paint);

    }
}
