package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import supermarket.cn.yt.asuper.ytlibrary.R;

/**
 * Created by liaoqinsen on 2017/9/11 0011.
 */

public class ProgressPopupWindow {


    private Context mContext;
    private LayoutInflater mInflater;
    private TextView info;
    private Dialog mPopupWindow;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    Window window;

    /**
     * @param context
     */
    public ProgressPopupWindow(Activity context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        windowManager = context.getWindowManager();
        window = context.getWindow();
        params = context.getWindow().getAttributes();
        init();
    }

    public void init() {
        View view = mInflater.inflate(R.layout.layout_progress, null);
        params.dimAmount = 0.5f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        info = (TextView) view.findViewById(R.id.progress_info);

        mPopupWindow = new Dialog(mContext, R.style.time_dialog_tans);
        mPopupWindow.setCancelable(false);
        mPopupWindow.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPopupWindow.setContentView(view);
        Window window = mPopupWindow.getWindow();
        window.setGravity(Gravity.CENTER);
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        // 动画效果 从底部弹起
    }

    /**
     * 显示底部对话框
     */
    public void show() {
        mPopupWindow.show();
    }


    public void setInfo(String text) {
        info.setText(text);
        if(text != null && !text.trim().isEmpty()) {
            info.setVisibility(View.VISIBLE);
        }else{
            info.setVisibility(View.GONE);
        }
    }


    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

}
