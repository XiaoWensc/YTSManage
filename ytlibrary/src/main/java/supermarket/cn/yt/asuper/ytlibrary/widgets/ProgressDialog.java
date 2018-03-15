package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import supermarket.cn.yt.asuper.ytlibrary.R;


/**
 * 自定义对话框
 *
 * @author 曾晓文
 * @version 1.0
 */
public class ProgressDialog extends Dialog {

    /**
     * 消息TextView
     */
    private TextView tvMsg;

    public ProgressDialog(Context context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public ProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.layout_progress_dialog);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        tvMsg = (TextView) this.findViewById(R.id.tv_msg);
        setMessage(strMessage);
    }

    /**
     * 设置进度条消息
     *
     * @param strMessage 消息文本
     */
    public void setMessage(String strMessage) {
        if (tvMsg != null) {
            tvMsg.setText(strMessage);
        }
    }
}