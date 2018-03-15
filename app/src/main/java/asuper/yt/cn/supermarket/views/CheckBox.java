package asuper.yt.cn.supermarket.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import asuper.yt.cn.supermarket.R;

/**
 * Created by zengxiaowen on 2018/1/4.
 */

public class CheckBox extends android.support.v7.widget.AppCompatCheckBox {

    private String mKey;
    private String mValue_t;
    private String mValue_f;

    public CheckBox(Context context) {
        this(context, null);
    }

    public CheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.checkboxStyle);
    }

    public CheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取自定义属性和默认值
        @SuppressLint("CustomViewStyleable") TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        mKey = mTypedArray.getString(R.styleable.TextView_key);
        mValue_t = mTypedArray.getString(R.styleable.TextView_value);
        mValue_f = mTypedArray.getString(R.styleable.TextView_value_f);
        mTypedArray.recycle();
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getValue() {
        if (isChecked()) return mValue_t;
        else return mValue_f;
    }

    public void setValue(String value) {
        if (value==null||value.isEmpty()) return;
        setChecked(value.equals(mValue_t));
    }
}
