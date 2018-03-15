package asuper.yt.cn.supermarket.modules.operate.zbar;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/12/11.
 */

public class OperResultActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_oper_result;
    }

    @Override
    protected void findView(View root) {

//透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        boolean enbol = getIntent().getBooleanExtra("suc",false);
        ViewGroup group = root.findViewById(R.id.root);
        initView(group,enbol);
        root.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initView(ViewGroup group, boolean enbol) {
        for (int i=0;i<group.getChildCount();i++){
            if (group.getChildAt(i) instanceof ViewGroup) initView((ViewGroup) group.getChildAt(i),enbol);
            else if (group.getChildAt(i) instanceof TextView) {
                group.getChildAt(i).setEnabled(enbol);
                if (group.getChildAt(i).getTag()!=null){
                    switch (group.getChildAt(i).getTag().toString()){
                        case "msg1":
                            ((TextView) group.getChildAt(i)).setText(enbol?"签到成功":"签到失败");
                            break;
                        case "msg2":
                            ((TextView) group.getChildAt(i)).setText(enbol?"该客户已签到成功":"该客户签到失败");
                            break;
                    }
                }
            }
        }
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
