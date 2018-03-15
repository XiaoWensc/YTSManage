package asuper.yt.cn.supermarket.modules.myclient;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressButton;

/**
 * Created by liaoqinsen on 2017/9/19 0019.
 */

public class GiveupActivity extends BaseActivity {

    private EditText content;
    private TextView label;
    private ProgressButton btn;
    private int id;

    @Override
    protected int getContentId() {
        return R.layout.activity_giveup;
    }

    @Override
    protected void findView(View root) {
        id = getIntent().getExtras().getInt("id");
        content = (EditText) root.findViewById(R.id.giveup_content);
        btn = (ProgressButton) root.findViewById(R.id.giveup_content_confirm);
        label = (TextView) root.findViewById(R.id.giveup_content_length);
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                label.setText(content.getText().toString().length()+"/100");
            }
        });
        content.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter(),new InputFilter.LengthFilter(100)});
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(content.getText().toString().trim().isEmpty()){
                    ToastUtil.info("请输入放弃原因");
                    btn.cancel();
                    return;
                }
                content.setEnabled(false);
                Map<String,Object> data = new HashMap<>();
                data.put("intentionID",id);
                data.put("giveupReason",content.getText().toString());
                ToolOkHTTP.post(Config.getURL("app/recycling/giveupJoining.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        ToastUtil.success("放弃加盟成功");
                        setResult(100);
                        finish();
                    }

                    @Override
                    public void onFailure() {
//                        ToastUtil.success("放弃加盟失败");
                        content.setEnabled(true);
                        btn.cancel();
                    }

                });
            }
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
