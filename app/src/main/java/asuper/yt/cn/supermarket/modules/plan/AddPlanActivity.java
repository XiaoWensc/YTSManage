package asuper.yt.cn.supermarket.modules.plan;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Intent;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientListActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.views.CustomDatePicker;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * 添加计划activity
 * Created by zengxiaowen on 2017/9/11.
 */

public class AddPlanActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTime;
    private CustomDatePicker customDatePicker;
    private TextView tvIntentionId; //关联客户
    private TextView tvShopName;
    private RadioGroup radioType;
    private EditText edRemark;  //备注
    private TextView textNum;

    private String intentionId = ""; //意向ID

    private String toDayTime = "";

    @Override
    protected int getContentId() {
        return R.layout.activity_addplan;
    }

    @Override
    protected void findView(View root) {
        EventBus.getDefault().register(this);
        ActionBarManager.initBackToolbar(this, "添加计划");
        toDayTime = getIntent().getStringExtra("time");
        initView(root);
        if (getIntent() != null && getIntent().getExtras() != null) {
            String data = getIntent().getStringExtra("data");
            if (data != null) {
                String[] datas = data.split(",");
                intentionId = datas[0];
                if (datas.length > 1) {
                    tvShopName.setText(datas[1]);
                } else {
                    tvIntentionId.setVisibility(View.GONE);
                    tvShopName.setVisibility(View.GONE);
                }
            }
        }
    }

    private void initView(View root) {
        (tvTime = (TextView) root.findViewById(R.id.tvTime)).setOnClickListener(this);
        root.findViewById(R.id.btnAddPlan).setOnClickListener(this);
        (tvIntentionId = (TextView) root.findViewById(R.id.tvIntentionId)).setOnClickListener(this);
        (tvShopName = (TextView) root.findViewById(R.id.tvShopName)).setOnClickListener(this);
        tvShopName.setVisibility(View.GONE);
        radioType = (RadioGroup) root.findViewById(R.id.radioType);
        edRemark = (EditText) root.findViewById(R.id.edRemark);
        edRemark.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
        textNum = (TextView) root.findViewById(R.id.textNum);
        edRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 改变前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 改变后
                if (s.length() <= 100) {
                    textNum.setText(s.length() + "/100");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initDatePicker();
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());
        if (toDayTime == null || toDayTime.isEmpty() || isTimeCompare(toDayTime)) {
            tvTime.setText(now.split(" ")[0]);
        } else {
            tvTime.setText(toDayTime);
        }
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                tvTime.setText(time);
            }
        }, now, "2100-01-01 00:00", false); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(false); // 不显示时和分
        customDatePicker.setIsLoop(false); // 不允许循环滚动

    }

    /**
     * 时间对比
     *
     * @param dayTime
     * @return
     */
    private boolean isTimeCompare(String dayTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        try {
            Date toDay = new Date();
            Date datTim = sdf.parse(dayTime);
            if (datTim.getTime()<toDay.getTime()){
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PlanStore(dependencyDelegate);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String data) {
        if (data != null) {
            String[] datas = data.split(",");
            intentionId = datas[0];
            if (datas.length > 1) {
                tvShopName.setText(datas[1]);
                tvShopName.setVisibility(View.VISIBLE);
            } else {
                tvIntentionId.setVisibility(View.GONE);
                tvShopName.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PlanStore.REQUEST_GET_PLAN_INSERT:

                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvTime:
                if (toDayTime == null || toDayTime.isEmpty()) {
                    customDatePicker.show(tvTime.getText().toString());
                } else {
                    customDatePicker.show(toDayTime);
                }
                break;
            case R.id.btnAddPlan:
                Map<String, Object> map = new HashMap<>();
                map.put("createTime", tvTime.getText().toString());
                map.put("intentionId", intentionId);
                map.put("visitType", radioType.getCheckedRadioButtonId() == R.id.radHome ? "home_visit" : "phone_visit");
                map.put("remark", edRemark.getText().toString());
                map.put("visitName", ((RadioButton) findViewById(radioType.getCheckedRadioButtonId())).getText().toString());
                dispatch(PlanStore.REQUEST_GET_PLAN_INSERT, map);
                break;
            case R.id.tvIntentionId:
                getOperation().addParameter("isSelectable", true);
                getOperation().forward(MyClientListActivity.class);
                break;
            case R.id.tvShopName:
                intentionId = "";
                tvShopName.setVisibility(View.GONE);
                break;
        }
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_INSERT)
    public void addPlan(JSONObject json) {
        if (json == null) return;
        if (json.optBoolean("success", false)) {
            ToastUtil.success("添加计划成功");
            setResult(PlanStore.REQUEST_GET_PLAN_INSERT);
            setResult(202);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
