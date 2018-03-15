package asuper.yt.cn.supermarket.modules.myclient;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.widgets.ProgressButton;

/**
 * Created by liaoqinsen on 2017/9/18 0018.
 */

public class  TempVisitActivity extends BaseActivity {

    private String intentionId;
    private RadioButton phone,home;
    private EditText content;
    private TextView length;
    private ProgressButton button;

    @Override
    protected int getContentId() {
        return R.layout.activity_temp_visit;
    }

    @Override
    protected void findView(View root) {
        intentionId = getIntent().getStringExtra("id");
        ActionBarManager.initBackToolbar(getContext(),"临时拜访");
        phone = (RadioButton) root.findViewById(R.id.temp_visit_type_phone);
        home = (RadioButton) root.findViewById(R.id.temp_visit_type_home);
        content = (EditText) root.findViewById(R.id.temp_visit_content);
        length = (TextView) root.findViewById(R.id.temp_visit_content_length);
        content.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter(),new InputFilter.LengthFilter(100)});
        content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                length.setText(content.getText().toString().length()+"/100");
            }
        });
        button = (ProgressButton) root.findViewById(R.id.temp_visit_over);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone.setEnabled(false);
                home.setEnabled(false);
                content.setEnabled(false);
                Map<String,Object> data = new HashMap<>();
                data.put("intentionId",intentionId);
                data.put("visitType",phone.isChecked()?"phone_visit":"home_visit");
                data.put("visitName",phone.isChecked()?"电话拜访":"上门拜访");
                data.put("visitContent",content.getText().toString());
                ToolOkHTTP.post(Config.getURL("app/myPlan/temporaryVisit.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        ToastUtil.success("提交成功");
                        setResult(202);
                        finish();
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.success("提交失败");
                        phone.setEnabled(true);
                        home.setEnabled(true);
                        content.setEnabled(true);
                        button.cancel();
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
