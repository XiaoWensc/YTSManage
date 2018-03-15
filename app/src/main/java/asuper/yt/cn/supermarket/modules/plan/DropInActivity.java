package asuper.yt.cn.supermarket.modules.plan;

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
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * 拜访activity
 * Created by zengxiaowen on 2017/9/7.
 */

public class DropInActivity extends BaseActivity{

    private EditText edtDes;
    private TextView textNum;

    @Override
    protected int getContentId() {
        return R.layout.activity_dropin;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"拜访");
        textNum = (TextView) root.findViewById(R.id.textNum);
        (edtDes = (EditText) root.findViewById(R.id.edtDes)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()<=100){
                    textNum.setText(s.length()+"/100");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDes.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100), ToolInputFilter.getEmojiFilter()});
        root.findViewById(R.id.btnEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> map = new HashMap<>();
                map.put("id",getIntent().getIntExtra("id",0));
                map.put("visitContent",edtDes.getText().toString());
                dispatch(PlanStore.REQUEST_GET_PLAN_UPDATE,map);
            }
        });
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_UPDATE)
    public void setPlanUpdate(JSONObject jsonObject){
        if (jsonObject==null) return;
        if(jsonObject.optBoolean("success",false)){
            setResult(PlanStore.REQUEST_GET_PLAN_UPDATE);
            finish();
        }
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PlanStore(dependencyDelegate);
    }
}
