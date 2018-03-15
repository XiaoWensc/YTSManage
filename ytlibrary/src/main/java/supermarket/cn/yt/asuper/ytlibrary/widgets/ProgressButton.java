package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tuyenmonkey.mkloader.MKLoader;

import supermarket.cn.yt.asuper.ytlibrary.R;
import supermarket.cn.yt.asuper.ytlibrary.utils.Common;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolUnit;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public class ProgressButton extends FrameLayout implements View.OnClickListener {

    private TextView textView;
    private MKLoader progressView;
    private int progressWidth, progressHeight;
    private int textColor, textSize;
    private String text;

    private OnClickListener onClickListener;

    public ProgressButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
        progressWidth = (int) typedArray.getDimension(R.styleable.ProgressButton_pb_progress_width, 40);
        progressHeight = (int) typedArray.getDimension(R.styleable.ProgressButton_pb_progress_height, 40);
        textColor = typedArray.getColor(R.styleable.ProgressButton_pb_progress_text_color, Color.WHITE);
        textSize = typedArray.getInteger(R.styleable.ProgressButton_pb_progress_text_size, 5);
        text = typedArray.getString(R.styleable.ProgressButton_pb_progress_text);

        textView = new TextView(context);
        textView.setLayoutParams(new FrameLayout.LayoutParams(-2, -2, Gravity.CENTER));
        textView.setTextSize(ToolUnit.dipTopx(context,textSize));
        textView.setTextColor(textColor);
        textView.setText(text);
        addView(textView, 0);

        progressView = (MKLoader) LayoutInflater.from(context).inflate(R.layout.layout_loadview,null);
        progressView.setLayoutParams(new FrameLayout.LayoutParams(progressWidth, progressHeight, Gravity.CENTER));
        progressView.setBackgroundColor(Color.TRANSPARENT);
        progressView.setVisibility(INVISIBLE);
        addView(progressView, 1);

        super.setOnClickListener(this);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        onClickListener =  l;
    }

    public void setTextView(TextView textView) {
        removeView(this.textView);
        this.textView = textView;
        addView(textView, 0);
    }

    @Override
    public void onClick(View v) {
        if (textView.getVisibility() == VISIBLE) {
            textView.setVisibility(INVISIBLE);
            progressView.setVisibility(VISIBLE);
            if(onClickListener != null){
                onClickListener.onClick(this);
            }
        }
    }

    public void cancel(){
        textView.setVisibility(VISIBLE);
        progressView.setVisibility(INVISIBLE);
    }

    public void success(){
        textView.setVisibility(VISIBLE);
        progressView.setVisibility(INVISIBLE);
    }
}
