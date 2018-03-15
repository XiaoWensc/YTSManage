package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import supermarket.cn.yt.asuper.ytlibrary.R;

/**
 * 自定义Toast提示
 * Created by zengxiaowen on 2017/4/8.
 */
public class SimpleHUDToast {

    private TextView mTextView;
    private ImageView mImageView;
    private Toast toastStart;
    private static int mScreenHeight;

    public static SimpleHUDToast init(Context mContext) {
        SimpleHUDToast toast = new SimpleHUDToast();
        mScreenHeight = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        toast.showToast(mContext);
        return toast;
    }

    private void showToast(Context mContext) {
        //加载Toast布局
        View toastRoot = LayoutInflater.from(mContext).inflate(R.layout.layout_simplehud, null);
        //初始化布局控件
        mTextView = (TextView) toastRoot.findViewById(R.id.simplehud_message);
        mImageView = (ImageView) toastRoot.findViewById(R.id.simplehud_image);
        //Toast的初始化
        toastStart = new Toast(mContext);
        //Toast的Y坐标是屏幕高度的1/3，不会出现不适配的问题
        toastStart.setGravity(Gravity.CENTER, 0, 0);
        toastStart.setDuration(Toast.LENGTH_SHORT);
        toastStart.setView(toastRoot);
    }

    private void setToast(String msg, int resId) {
        mTextView.setText(msg);
        mImageView.setImageResource(resId);
    }

    public void showInfoToast(String msg) {
        setToast(msg, R.drawable.simplehud_info);
        toastStart.show();
    }

    public void showErrorToast(String msg) {
        setToast(msg, R.drawable.simplehud_error);
        toastStart.show();
    }

    public void showSuccessToast(String msg) {
        setToast(msg, R.drawable.simplehud_success);
        toastStart.show();
    }
}
