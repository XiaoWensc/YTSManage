package asuper.yt.cn.supermarket.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import asuper.yt.cn.supermarket.R;

/**
 * Created by liaoqinsen on 2017/9/19 0019.
 */

public class SlantTextView extends View {

    private String text="测试文字";
    private int color = Color.RED;
    private Paint paint;

    public SlantTextView(Context context) {
        super(context);
    }

    public SlantTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setText(String text){
        this.text = text;
        invalidate();
    }

    public void setColor(int color){
        this.color = color;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(paint == null){
            paint = new Paint();
            paint.setAntiAlias(true);
        }
        canvas.rotate(-45,canvas.getWidth()/2,canvas.getHeight()/2);
        paint.setColor(getResources().getColor(R.color.yt_red_light));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        canvas.drawRect(-canvas.getWidth()*0.25f,canvas.getHeight()/2, canvas.getWidth()*1.25f,canvas.getHeight()/2+canvas.getWidth()/4,paint);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(canvas.getWidth()/8);
        canvas.drawText(text,(canvas.getWidth())/2,canvas.getHeight()/2+canvas.getWidth()/6,paint);
    }

}
