package asuper.yt.cn.supermarket.modules.myclew;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.newclient.AddNewClientActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/6/21 0021.
 */

public class NewMissionDetailActivity extends BaseActivity {

    private String taskId;
    private NewMission newMission;
    private JSONObject resultObject;

    public void doBusiness() {
        Map<String, Object> params = new HashMap<>();
        params.put("tid", taskId);
        params.put("employeeId", Config.UserInfo.USER_ID);
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_NEW_MISSSION_DETAIL), params, new ToolOkHTTP.OKHttpCallBack() {

            @Override
            public void onSuccess(JSONObject response) {
                resultObject = response.optJSONObject("resultObject");
                if (resultObject == null) {
                    return;
                }
                try {
                    newMission = ToolGson.fromJson(resultObject.toString(), NewMission.class);
                } catch (Exception e) {

                }
                setViewData();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 101) {
            setResult(101);
            finish();
        }
    }

    private void setViewData() {
        if (resultObject == null) return;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(newMission == null ? System.currentTimeMillis() : newMission.franchiseeTime));
        ((TextView) findViewById(R.id.newmission_detail_date)).setText(date);
        setViewData(root.findViewById(R.id.new_mission_detail_container), resultObject);
        if (newMission == null) return;
        ((TextView) findViewById(R.id.newmission_detail_city)).setText(String.format("%1$s%2$s", newMission.shopCity, newMission.shopCountry));
        switch (newMission.applySteup) {
            case "0":
                ((TextView) findViewById(R.id.newmission_detail_intention)).setText("弱");
                break;
            case "1":
                ((TextView) findViewById(R.id.newmission_detail_intention)).setText("中");
                break;
            case "2":
                ((TextView) findViewById(R.id.newmission_detail_intention)).setText("强");
                break;
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
            String key = v.getTag().toString();
            String data = object.optString(key, "");
            if ("null".equals(data)) {
                ((TextView) v).setText("");
            } else {
                ((TextView) v).setText(data);
            }
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_newmission_detail;
    }

    @Override
    protected void findView(View root) {
        taskId = getIntent().getExtras().getString("id");
        ActionBarManager.initBackToolbar(this, "任务详情");
        root.findViewById(R.id.new_mission_detail_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newMission != null) {
                    try {
                        Map<String, Object> params = new HashMap<>();
                        params.put("tid", newMission.id);
                        params.put("user_id", Config.UserInfo.USER_ID);
                        List<ClientInfoDetail> clientInfoDetails = ToolDbOperation.getClientDao().queryForFieldValues(params);
                        if (clientInfoDetails != null && clientInfoDetails.size() > 0) {
                            getOperation().addParameter("client", clientInfoDetails.get(0));
                            getOperation().addParameter("isNew", false);
                            ToolLog.i(clientInfoDetails.get(0).toString());
                        }else{
                            EventBus.getDefault().postSticky(newMission);
                            getOperation().addParameter("isNew", true);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                getOperation().forwardForResult(AddNewClientActivity.class, 100);
            }
        });

        doBusiness();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }
}
