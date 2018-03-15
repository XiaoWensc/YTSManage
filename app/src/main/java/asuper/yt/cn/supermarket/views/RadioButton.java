package asuper.yt.cn.supermarket.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import asuper.yt.cn.supermarket.R;

/**
 * 自定义RadioButton,增加属性key/value
 *
 * @author zxw
 * @version 1.0
 */
public class RadioButton extends android.support.v7.widget.AppCompatRadioButton {

    private String mKey;
    private String mValue;

    public RadioButton(Context context) {
        this(context, null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.radioButtonStyle);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //获取自定义属性和默认值
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        mKey = mTypedArray.getString(R.styleable.TextView_key);
        mValue = mTypedArray.getString(R.styleable.TextView_value);
        mTypedArray.recycle();
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }
}
