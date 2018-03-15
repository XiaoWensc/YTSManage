package asuper.yt.cn.supermarket.modules.coauditing;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.DensityUtil;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolString;

/**
 * Created by liaoqinsen on 2017/6/1 0001.
 */

public class AuditingMessageActivity extends BaseActivity {

    private int type = 0;

    private LinearLayout auditing_form;

    private EditText auditing_msg;

    private TextView auditing_msg_limit;

    private Button commit;

    public String taskDefId;
    public String processType;
    public JSONObject auditingForm;
    public String intentionId;
    public Mission mission;

    private HashMap<String, String> format, lables;

    public static final int REQUEST_GET_MESSAGE = 0x0801;
    public static final int REQUEST_AGREE = 0x0802;
    public static final int REQUEST_REJECT = 0x0803;


    @BindAction(REQUEST_GET_MESSAGE)
    public void getDetailResult(JSONObject res) {
        if (res == null) return;
        auditingForm = res;
        commit.setVisibility(View.VISIBLE);
        genForm();
    }

    @BindAction(REQUEST_AGREE)
    public void agreeResult(boolean success) {
        if (success) {
            ToastUtil.success("提交成功");
            setResult(100);
            finish();
        } else ToastUtil.error("提交失败");
    }

    @BindAction(REQUEST_REJECT)
    public void rejectResult(boolean success) {
        if (success) {
            ToastUtil.success("驳回成功");
            setResult(100);
            finish();
        } else ToastUtil.error("驳回失败");
    }

    public void initView(View view) {
        type = getIntent().getExtras().getInt("type");
        taskDefId = getIntent().getExtras().getString("taskDefId");
        processType = getIntent().getExtras().getString("processType");
        intentionId = getIntent().getExtras().getString("intentionId");
        mission = EventBus.getDefault().getStickyEvent(Mission.class);

        ActionBarManager.initBackToolbar(getContext(), type == 0 ? "审核通过" : "审核拒绝");
        auditing_form = (LinearLayout) view.findViewById(R.id.auditing_form);
        auditing_msg = (EditText) view.findViewById(R.id.auditing_msg);
        if (type == 0) {
            auditing_msg.setText("同意");
        }
        auditing_msg_limit = (TextView) view.findViewById(R.id.auditing_msg_limit);
        commit = (Button) view.findViewById(R.id.btnCommit);
        commit.setText(type == 0 ? "提  交" : "驳  回");
        commit.setVisibility(type == 0 ? View.INVISIBLE : View.VISIBLE);
        auditing_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                auditing_msg_limit.setText(auditing_msg.getText().toString().length() + "/256");
            }
        });

        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() + dest.toString().length() > 256) {
                    return "";
                }
                return null;
            }
        };

        auditing_msg.setFilters(new InputFilter[]{inputFilter, new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.length() > 0 && ToolString.isEmojiCharacter(source.charAt(source.length() - 1)))
                    return "";
                return null;
            }
        }});

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        commit.setEnabled(false);
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        getFormData(map);
                        if (!checkData(map)) {
                            commit.setEnabled(true);
                            return;
                        }
                        if (auditing_msg.getText().toString().trim().isEmpty()) {
                            ToastUtil.info("请输入审批意见");
                            commit.setEnabled(true);
                            return;
                        }
                        map.put("auditMessage", auditing_msg.getText().toString());
                        map.put("taskId", mission.taskId);
                        map.put("processType", processType);
                        map.put("processInstanceId", mission.processInstanceId);
                        map.put("taskDefId", mission.taskDefId);
                        map.put("taskDefName", mission.taskDefName);
                        map.put("formId", mission.formId);
                        map.put("intentionId", mission.intentionId);
                        map.put("originatorId", mission.originatorId);
                        map.put("originatorName", mission.originatorName);
                        map.put("telephone", mission.telephone);
                        map.put("companyId", mission.companyId);
                        dispatch(REQUEST_AGREE, map);
                        break;
                    case 1:
                        commit.setEnabled(false);
                        if (auditing_msg.getText().toString().trim().isEmpty()) {
                            ToastUtil.info("请输入审批意见");
                            commit.setEnabled(true);
                            return;
                        }
                        HashMap<String, Object> params = new HashMap<String, Object>();
                        params.put("operate", 3);
                        params.put("taskId", mission.taskId);
                        params.put("rejectMessage", auditing_msg.getText().toString());
                        params.put("processType", processType);
                        params.put("processInstanceId", mission.processInstanceId);
                        params.put("taskDefId", mission.taskDefId);
                        params.put("taskDefName", mission.taskDefName);
                        params.put("formId", mission.formId);
                        params.put("intentionId", intentionId);
                        params.put("originatorId", mission.originatorId);
                        params.put("originatorName", mission.originatorName);
                        params.put("telephone", mission.telephone);
                        params.put("companyId", mission.companyId);
                        params.put("companyId", mission.companyId);
                        dispatch(REQUEST_REJECT, params);
                        break;
                }
            }
        });
        doBusiness();
    }

    private void getFormData(HashMap<String, Object> map) {
        getFormData(map, auditing_form);
    }

    private void getFormData(HashMap<String, Object> map, View v) {
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                getFormData(map, vg.getChildAt(i));
            }
            return;
        }

        if (v.getTag() != null && v instanceof EditText) {
            map.put(v.getTag().toString(), ((EditText) v).getText().toString());
        }
    }

    private boolean checkData(HashMap<String, Object> map) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            String value = map.get(key).toString();
            if (value == null) {
                ToastUtil.error(String.format("%s数据错误", lables.get(key)));
                return false;
            }
            String regex = format.get(key);
            if (regex == null || regex.trim().isEmpty()) continue;
            if (value.matches(format.get(key))) {
                continue;
            } else {
                ToastUtil.error(String.format("%s格式错误", lables.get(key)));
                return false;
            }
        }
        return true;
    }

    public void doBusiness() {
        if (type == 0) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("taskDefId", taskDefId);
            params.put("processType", processType);
            dispatch(REQUEST_GET_MESSAGE, params);
        }
    }


    public void genForm() {
        format = new HashMap<>();
        lables = new HashMap<>();
        String form = auditingForm.optString("formjson");
        if (form == null) return;
        try {
            List<Form> forms = ToolGson.fromJson(form, new TypeToken<List<Form>>() {
            }.getType());
            for (Form f : forms) {
                View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_auditing_msg_item, null);
                int margin = DensityUtil.dip2px(getContext(), 20);
                v.setPadding(margin, 0, margin, 0);
                TextView title = (TextView) v.findViewById(R.id.auditing_msg_item_title);
                EditText value = (EditText) v.findViewById(R.id.auditing_msg_item_value);
                title.setText(f.label);
                value.setHint(f.placeholder);
                if (f.defaultValue != null && !f.defaultValue.trim().isEmpty()) {
                    value.setText(f.defaultValue);
                }
                if (f.name != null) {
                    value.setTag("businessData[" + f.name + "]");
                    format.put("businessData[" + f.name + "]", f.validate);
                    lables.put("businessData[" + f.name + "]", f.label);
                }
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, DensityUtil.dip2px(getContext(), 50));
                if (auditing_form.getChildCount() >= 2) {
                    auditing_form.addView(v, auditing_form.getChildCount() - 2, layoutParams);
                }
            }
        } catch (Exception e) {
            return;
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_auditing_msg;
    }

    @Override
    protected void findView(View root) {
        initView(root);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new AuditingMessageStore(dependencyDelegate);
    }

    public class Form {
        public String label;
        public String name;
        public String type;
        public String defaultValue;
        public String validate;
        public String placeholder;
    }
}
