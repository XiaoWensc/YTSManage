package asuper.yt.cn.supermarket.modules.myclient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import asuper.yt.cn.supermarket.R;

/**
 * Created by liaoqinsen on 2017/9/19 0019.
 */

public class ClientDetailDialog {

    private Context mContext;
    private LayoutInflater mInflater;
    private TextView title,remind,content,confirm,cancel;
    private Dialog mPopupWindow;
    WindowManager.LayoutParams params;
    WindowManager windowManager;
    OnClientDialogConfirmListener onClientDialogConfirmListener;
    Window window;

    /**
     * @param context
     */
    public ClientDetailDialog(Activity context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        windowManager = context.getWindowManager();
        window = context.getWindow();
        params = context.getWindow().getAttributes();
        init();
    }

    public void init() {
        View view = mInflater.inflate(R.layout.dialog_common, null);
        params.dimAmount = 0.5f;
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        title = (TextView) view.findViewById(R.id.dialog_client_title);
        remind = (TextView) view.findViewById(R.id.dialog_client_remind);
        content = (TextView) view.findViewById(R.id.dialog_client_content);
        cancel = (TextView) view.findViewById(R.id.dialog_client_cancel);
        confirm = (TextView) view.findViewById(R.id.dialog_client_confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if(onClientDialogConfirmListener != null) onClientDialogConfirmListener.onConfirm();
            }
        });

        mPopupWindow = new Dialog(mContext, supermarket.cn.yt.asuper.ytlibrary.R.style.time_dialog_tans);
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




    public void dismiss() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void show(String title,String remind,String content,OnClientDialogConfirmListener listener) {
        this.title.setText(title);
        this.remind.setText(remind);
        onClientDialogConfirmListener = listener;
        this.content.setText(content);
        if(content == null || content.trim().isEmpty()){
            this.content.setVisibility(View.GONE);
        }
        show();
    }

    public static interface OnClientDialogConfirmListener{
        public void onConfirm();
    }
}
