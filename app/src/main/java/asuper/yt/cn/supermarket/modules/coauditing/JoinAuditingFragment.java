package asuper.yt.cn.supermarket.modules.coauditing;

import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolViewGroup;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by liaoqinsen on 2017/5/31 0031.
 */

public class JoinAuditingFragment extends BaseFragment {

    private String intentionId;
    private String processType;
    private String taskDefId;
    private String originatorName;
    private LinearLayout join_auditing_root;

    private LinearLayout join_auditing_comments;
    private AppCompatCheckBox join_auditing_comments_title;
    private View join_auditing_attachment;
    private TextView join_auditing_total_rent;
    List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems;

    private int type = 0;

    private MymissionActivity activity;

    private View dealWith;


    public JSONObject auditingMsg;
    public JSONObject auditingInfo;
    public boolean getDataSuccess = false;

    public boolean canAuditing;

    public static final int REQUEST_GET_DETAIL = 0X0901;
    public static final int REQUEST_GET_MESSAGE = 0X0902;

    public static JoinAuditingFragment newInstance(String originatorName, boolean canAuditing, int type, String intentionId, String processType, String taskDefId) {
        JoinAuditingFragment fragment = new JoinAuditingFragment();
        fragment.intentionId = intentionId;
        fragment.processType = processType;
        fragment.taskDefId = taskDefId;
        fragment.originatorName = originatorName;
        fragment.type = type;
        fragment.canAuditing = canAuditing;
        return fragment;
    }


    @BindAction(REQUEST_GET_DETAIL)
    public void getDetailResult(JSONObject jsonObject) {
        auditingInfo = jsonObject;
        setViewData();
        HashMap<String, Object> params = new HashMap<>();
        params.put("intentionId", intentionId);
        params.put("processType", processType);
        dispatch(REQUEST_GET_MESSAGE, params);
        if (getActivity() != null) {
            if (dealWith != null) dealWith.setVisibility(canAuditing ? View.VISIBLE : View.GONE);
        }
    }

    @BindAction(REQUEST_GET_MESSAGE)
    public void getMessageResult(JSONObject jsonObject) {
        auditingMsg = jsonObject;
        genAuditingMsg();


    }

    public void setMymissionActivity(MymissionActivity mymissionActivity) {
        this.activity = mymissionActivity;
    }

    private void genAuditingMsg() {
        Iterator<String> keys = auditingMsg.keys();
        join_auditing_comments_title.setVisibility(keys.hasNext() ? View.VISIBLE : View.GONE);
        if (keys.hasNext()) {
            join_auditing_comments_title.setOnCheckedChangeListener((buttonView, isChecked) -> join_auditing_comments.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        }
        while (keys.hasNext()) {
            String k = keys.next();
            JSONArray ob = auditingMsg.optJSONArray(k);
            View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_nodeinfo, null);
            ((TextView) v.findViewById(R.id.auditorName)).setText(k);
            ((TextView) v.findViewById(R.id.auditorName)).setTextColor(getResources().getColor(R.color.colorPrimary));
            join_auditing_comments.addView(v);
            if (ob == null || ob.length() < 1) continue;
            for (int i = 0; i < ob.length(); i++) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = ob.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject == null) continue;
                String msg = jsonObject.optString("auditMessage");
                String auhtor = jsonObject.optString("auditorName");
                View v1 = LayoutInflater.from(getContext()).inflate(R.layout.layout_item_nodeinfo, null);
                ((TextView) v1.findViewById(R.id.auditMessage)).setText(msg);
                ((TextView) v1.findViewById(R.id.auditorName)).setText(auhtor);
                join_auditing_comments.addView(v1);
            }

        }
    }


    public void initView(View view) {
        join_auditing_comments = view.findViewById(R.id.join_auditing_comments);
        join_auditing_root = view.findViewById(R.id.join_auditing_root);
        join_auditing_comments_title = view.findViewById(R.id.join_auditing_comments_title);
        join_auditing_attachment = view.findViewById(R.id.join_auditing_attachment);
        dealWith = view.findViewById(R.id.mymission_deal);
        if (dealWith != null) {
            dealWith.setOnClickListener(v -> {
                if (getActivity() != null) {
                    ((MymissionActivity) getActivity()).showDeal();
                }
            });
        }
        join_auditing_total_rent = (TextView) view.findViewById(R.id.join_auditing_total_rent);

        join_auditing_attachment.setOnClickListener(v -> {
            genFiles();
        });
        doBusiness();
    }

    public void doBusiness() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("intentionId", intentionId);
        params.put("processType", processType);
        dispatch(REQUEST_GET_DETAIL, params);
    }

    private void setViewData() {
        if (auditingInfo == null) return;
        JSONObject formData = auditingInfo.optJSONObject("formData");
        String dataJson = formData.optString("dataJson");
        if (dataJson != null) {
            try {
                JSONObject dataJ = new JSONObject(dataJson);
                Iterator<String> keys = dataJ.keys();
                while (keys.hasNext()) {
                    String k = keys.next();
                    formData.put(k, dataJ.get(k));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            formData.put("companyName", auditingInfo.optString("companyName"));
            formData.put("originatorName", originatorName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        extraSetViewData(formData);
        setViewData(join_auditing_root, formData);
    }

    private void extraSetViewData(JSONObject jsonObject) {
        if (join_auditing_total_rent != null) {
            String monthlyRent = jsonObject.optString("shopMonthRent");
            BigDecimal bigDecimal = new BigDecimal(1);
            try {
                bigDecimal = bigDecimal.multiply(new BigDecimal(monthlyRent)).multiply(new BigDecimal(12)).multiply(new BigDecimal(0.8f));
                join_auditing_total_rent.setText(bigDecimal.intValue() + "");
            } catch (Exception e1) {
                join_auditing_total_rent.setText("0");
            }
        }
    }

    private void setViewData(View v, JSONObject object) {
        if (object == null) return;
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setViewData(vg.getChildAt(i), object);
            }
            return;
        }

        if (v.getTag() != null && v instanceof TextView) {
            v.setEnabled(canAuditing);
            try {
                object.getBoolean(v.getTag().toString());
                ((TextView) v).setText(object.getBoolean(v.getTag().toString()) ? "是" : "否");
            } catch (JSONException e) {
                String key = v.getTag().toString();
                if ("dataType".equals(key)) {
                    String data = object.optString("dataType");
                    if (data != null&&!data.equals("null")) {
                        switch (data) {
                            case "1":
                                ((TextView) v).setText("绩效补贴");
                                break;
                            case "2":
                                ((TextView) v).setText("门头补贴");
                                break;
                            case "3":
                                ((TextView) v).setText("租金补贴");
                                break;
                        }
                    }
                }
                else if ("relationShip".equals(key)){
                    String data = object.optString("relationShip");
                    if (data != null&&!data.equals("null")) {
                        if (data.equals("key1")){
                            ((TextView) v).setText("法人本人");
                        }else{
                            ((TextView) v).setText("非法人本人");
                        }
                    }else{
                        ((TextView) v).setText("法人本人");
                    }
                }
                else if ("businessLicenseName".equals(key)){
                    String data = object.optString("originalScript");
                    if (data != null&&!data.equals("null")) {
                        if (data.equals("2")){
                            ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                            ((TextView) v).setText(object.optString("businessLicenseName"));
                        }else{
                            ((TextView) v).setText(object.optString("businessLicenseName"));
                        }
                    }else{
                        ((TextView) v).setText(object.optString("businessLicenseName"));
                    }
                }
                else if ("isFood".equals(key)){   // 食品流通证
                    String data = object.optString("isFood");
                    if (data != null&&!data.equals("null")) {
                        if (data.equals("2")) {
                            ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                            ((TextView) v).setText("暂无");
                        } else {
                            ((TextView) v).setText("");
                        }
                    }else{
                        ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                        ((TextView) v).setText("未选择");
                    }
                }
                else if ("isNotTobacco".equals(key)) { // 烟草许可证
                    String data = object.optString("isTobacco");
                    if (data != null&&!data.equals("null")) {
                        if (data.equals("2")) {   // 不售卖烟草
                            ((View) v.getParent().getParent()).setVisibility(View.GONE);
                        } else {
                            data = object.optString("isNotTobacco");
                            if (data != null && data.equals("2")) { //暂无烟草许可证
                                ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                                ((TextView) v).setText("暂无");
                            } else {
                                ((TextView) v).setText("");
                            }
                        }
                    }else{
                        ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                        ((TextView) v).setText("未选择");
                    }
                }
                else if ("storeSource".equals(key)) {
                    String data = object.optString("storeSource");
                    if (data != null&&!data.equals("null")) {
                        switch (data) {
                            case "1":
                                ((TextView) v).setText("租赁房屋");
                                break;
                            case "2":
                                ((TextView) v).setText("自有房屋");
                                ToolViewGroup.getNextView((View) v.getParent()).setVisibility(View.GONE);
                                break;
                        }
                    }
                } else if (key.equals("gmtCreate")) { //返回的时间戳
                    String data = object.optString(key, "");
                    String time = transformsTimestamp(data);
                    setTextValues((TextView) v, time);
                } else {
                    String data = object.optString(key, "");
                    setTextValues((TextView) v, data);
                }
            }
        }
    }

    private String transformsTimestamp(String value) {
        String timeString = "";
        try {
            //时间戳转化为Sting
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long time = new Long(value);
            timeString = format.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return timeString;
    }


    private void setTextValues(TextView v, String data) {
        if ("null".equals(data)) {
            v.setText("");
        } else {
            v.setText(data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @BindAction(JoinAuditingStore.POST_JOIN_AUDITING_SHIMG)
    public void AuditingImag(List<ImageGalleryActivity.ImageGalleryItem> imageGalleryItems) {
        getOperation().addParameter("canUpdate", false);
        ToolLog.i(imageGalleryItems);
        ToolAttachment.gotoUpload(false, imageGalleryItems, auditingInfo.optString("formData"));
    }

    private void genFiles() {
        if (imageGalleryItems != null && imageGalleryItems.size() > 0) return;
        if (auditingInfo == null) return;
        JSONObject files = auditingInfo.optJSONObject("files");
        if (files == null) return;
        Map<String, Object> map = new HashMap<>();
        map.put("files", files);
        dispatch(JoinAuditingStore.POST_JOIN_AUDITING_SHIMG, map);
    }

    @Override
    protected int getContentId() {
        switch (type) {
            case 0:
                return R.layout.fragment_join_auditing;
            case 1:
                return R.layout.fragment_contract_auditing;
            case 2:
                return R.layout.fragment_subsidy_auditing;
            default:
                return R.layout.fragment_join_auditing;
        }
    }

    @Override
    protected void findView(View root) {
        initView(root);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new JoinAuditingStore(dependencyDelegate);
    }
}
