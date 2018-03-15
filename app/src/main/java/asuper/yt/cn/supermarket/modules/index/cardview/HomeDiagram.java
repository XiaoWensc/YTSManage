package asuper.yt.cn.supermarket.modules.index.cardview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.view.View;
import java.util.Collections;
import java.util.List;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.YTApplication;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolUnit;

/**
 * 线框图
 * Created by zengxiaowen on 2017/7/17.
 */
public class HomeDiagram extends View {

    private List<Integer> milliliter; // 收入
    private float interval_left_right;
    private float interval;
    private Paint paint_brokenLine ,framPanint2;
    private Paint paint_text; //

    private Bitmap bitmap_point;
    private Path path2;



    private int top = ToolUnit.dipTopx(getContext(),20); //上间距
    private int bottom = ToolUnit.dipTopx(getContext(),30); // 下间距
    private float base = 0; // 单位长度
    private int htight; // 折线绘制高度

    public HomeDiagram(Context context, List<Integer> milliliter) {
        super(context);
        init(milliliter);
    }

    /**
     * @param milliliter 收入
     */
    public void init(List<Integer> milliliter) {
        if (null == milliliter || milliliter.size() == 0)
            return;
        this.milliliter = milliliter;
        paint_brokenLine = new Paint();
        paint_text = new Paint();
        framPanint2 = new Paint();
        path2 = new Path();

        framPanint2.setAntiAlias(true);
        framPanint2.setStrokeWidth(2f);

    }

    protected void onDraw(Canvas c) {
        if (null == milliliter || milliliter.size() == 0)
            return;
        htight = getHeight() - top - bottom;
        if (Collections.max(milliliter) != 0) {
            base = (float) (htight / Collections.max(milliliter));
        }
        // 绘制折线
        drawLine(c, milliliter);

        //绘制阴影
        if (Collections.max(milliliter)>0) {
            drawShader(c, milliliter);
        }

        // 绘制图片
        drawBitMap(c,milliliter);
    }

    /**
     * 绘制图片
     * @param c
     * @param milliliter
     */
    private void drawBitMap(Canvas c, List<Integer> milliliter) {
        bitmap_point = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_spline);
        for (int i =0; i<milliliter.size();i++){
            float x1 = interval_left_right * i + interval / 2;
            float y1 = milliliter.get(i) < 0 ?  (htight + top)  : (htight - (base * milliliter.get(i)) + top);
            String numTxt = milliliter.get(i)+"";
            c.drawText(numTxt, x1 - numTxt.length() * 15 / 2, y1 - ToolUnit.dipTopx(getContext(),5), paint_text);
            c.drawBitmap(bitmap_point,
                    x1 - bitmap_point.getWidth() / 2,
                    y1 - bitmap_point.getHeight() / 2, null);
        }
    }

    /**
     * 设置Paint
     */
    private void initLinePaint() {
        paint_brokenLine.setStrokeWidth(ToolUnit.dipTopx(getContext(),2));
        paint_brokenLine.setColor(Color.parseColor("#79B6FC"));
        paint_brokenLine.setAntiAlias(true);

        paint_text.setTextSize(ToolUnit.dipTopx(getContext(),10));
        paint_text.setColor(Color.parseColor("#6C6B6B"));
    }


    /**
     * 绘制阴影
     *
     * @param c
     * @param milliliter
     */
    private void drawShader(Canvas c, List<Integer> milliliter) {
        Shader mShader = new LinearGradient(interval, htight -(base * Collections.max(milliliter)) + top, interval, htight + top, new int[]{
                Color.parseColor("#FFD7F6FD"),
                Color.parseColor("#FFFAFDFE")},
                null, Shader.TileMode.CLAMP);
        framPanint2.setShader(mShader);
        for (int i = 0; i < milliliter.size() - 1; i++) {
            float x1 = interval_left_right * i + interval / 2;
            float y1 = milliliter.get(i) < 0 ?  (htight + top)  : (htight - (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval / 2;
            float y2 = milliliter.get(i + 1) < 0 ?  (htight + top)  : (htight -(base * milliliter.get(i + 1)) + top);
            if (i == 0) {
                path2.moveTo(x1, y1);
            } else {
                path2.lineTo(x1, y1);
            }
            if (i == milliliter.size() - 2) {
                path2.lineTo(x2, y2);
                path2.lineTo(x2, getHeight());
                path2.lineTo(interval / 2, getHeight());
                path2.close();
                c.drawPath(path2, framPanint2);
            }

        }
    }

    /**
     * 绘制折线
     *
     * @param c
     */
    public void drawLine(Canvas c, List<Integer> milliliter) {
        initLinePaint();
        interval_left_right = YTApplication.mScreenWidth / 7;
        interval = getWidth() / 7-getResources().getDimension(R.dimen.page_start)*2;
        for (int i = 0; i < milliliter.size() - 1; i++) {
            float x1 = interval_left_right * i + interval / 2;
            float y1 = milliliter.get(i) < 0 ? (htight + top) : (htight -  (base * milliliter.get(i)) + top);
            float x2 = interval_left_right * (i + 1) + interval / 2;
            float y2 = milliliter.get(i + 1) < 0 ? (htight + top) : (htight -  (base * milliliter.get(i + 1)) + top);
            c.drawLine(x1, y1, x2, y2, paint_brokenLine);
        } }
}
