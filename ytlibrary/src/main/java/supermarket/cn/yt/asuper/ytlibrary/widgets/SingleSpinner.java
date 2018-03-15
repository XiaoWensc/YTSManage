package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;

import supermarket.cn.yt.asuper.ytlibrary.R;


public class SingleSpinner extends android.support.v7.widget.AppCompatSpinner {

    private String mKey;

    public SingleSpinner(Context context) {
        super(context);
    }

    public SingleSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SingleSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        //获取自定义属性和默认值
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        mKey = mTypedArray.getString(R.styleable.TextView_key);
        mTypedArray.recycle();
        setGravity(Gravity.CENTER_VERTICAL);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }

    /**
     * 获取选中的选项
     */
    public Option getSelectedItem() {
        return (Option) super.getSelectedItem();
    }

    /**
     * 获取选中的选项索引
     */
    public int getSelectedIndex() {
        return super.getSelectedItemPosition();
    }

    /**
     * 获取选中的选项编码value
     */
    public String getSelectedValue() {
        return getSelectedItem().getValue();
    }

    /**
     * 获取选中的选项编码显示文本
     */
    public String getSelectedLabel() {
        return getSelectedItem().getLabel();
    }

}
