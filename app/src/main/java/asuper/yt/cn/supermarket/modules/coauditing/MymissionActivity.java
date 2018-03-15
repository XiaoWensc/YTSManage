package asuper.yt.cn.supermarket.modules.coauditing;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailFragment;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/5/31 0031.
 */

public class MymissionActivity extends BaseActivity {

    private TabLayout tableLayout;
    private ViewPager viewPager;

    private PopupWindow popupWindow;

    private Toolbar toolbar;

    public ClientInfoDetail info;
    public List<BaseFragment> fragmentV4s;

    public String intentionId;
    public String processType;
    public String taskDefId;
    public boolean canAuditing;

    public Mission mission;



            public void onComplete(String msg, boolean isSuccess) {
                if(isSuccess){
                    viewPager.getAdapter().notifyDataSetChanged();
                    viewPager.setCurrentItem(viewPager.getAdapter().getCount()-1,false);
                }else if(msg != null){
                    ToastUtil.error(msg);
                }
            }

    public void initView(View view) {
        fragmentV4s = new ArrayList<>();
        intentionId = getIntent().getExtras().getString("intentionId");
        processType = getIntent().getExtras().getString("processType");
        taskDefId = getIntent().getExtras().getString("taskDefId");
        canAuditing = getIntent().getExtras().getBoolean("canAuditing");
        mission = EventBus.getDefault().getStickyEvent(Mission.class);

        tableLayout = (TabLayout) view.findViewById(R.id.mymission_tab);
        viewPager = (ViewPager) view.findViewById(R.id.mymission_pager);
        MyMissionAdapter myMissionAdapter = new MyMissionAdapter(getSupportFragmentManager());
        viewPager.setAdapter(myMissionAdapter);
        tableLayout.setupWithViewPager(viewPager);

        View v = LayoutInflater.from(this).inflate(R.layout.layout_popup_auditing_deal,null);
        v.findViewById(R.id.popup_auditing_pass).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOperation().addParameter("type",0);
                getOperation().addParameter("taskDefId",taskDefId);
                getOperation().addParameter("processType",processType);
                getOperation().addParameter("intentionId",intentionId);
                getOperation().forwardForResult(AuditingMessageActivity.class,100);
            }
        });
        v.findViewById(R.id.popup_auditing_reject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOperation().addParameter("type",1);
                getOperation().addParameter("taskDefId",taskDefId);
                getOperation().addParameter("processType",processType);
                getOperation().addParameter("intentionId",intentionId);
                getOperation().forwardForResult(AuditingMessageActivity.class,100);
            }
        });

        v.findViewById(R.id.popup_auditing_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        toolbar = ActionBarManager.initBackToolbar(this,getIntent().getExtras().getString("title"));
        popupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT,  ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setAnimationStyle(R.style.pop_animation);
        popupWindow.setOutsideTouchable(true);
    }

    public void doBusiness() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("intentionId",intentionId);
        params.put("processType",processType);
        requestData(params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            setResult(100);
            finish();
        }
    }

    public void showDeal(){
        popupWindow.showAtLocation(viewPager,Gravity.BOTTOM,0,0);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_mymission;
    }

    @Override
    protected void findView(View root) {
        initView(root);
        doBusiness();
    }

    public void requestData(final HashMap<String, Object> map) {
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_AUDITING_BUTTON), map, new ToolOkHTTP.OKHttpCallBack() {

            @Override
            public void onSuccess(JSONObject response) {
                JSONObject res = response.optJSONObject("resultObject");
                if (res == null) onComplete("服务器返回数据有误", false);
                else {
                    info = ToolGson.fromJson(res.toString(), ClientInfoDetail.class);
                    List<ButtonInfos> buts = info.getButtonInfos();
                    fragmentV4s.clear();
                    fragmentV4s.add(ClientDetailFragment.newInstance(info));
                    for (int i = 0; i < buts.size(); i++) {
                        if (i == buts.size() - 1) {
                            processType = buts.get(i).processType;
                        }
                        fragmentV4s.add(JoinAuditingFragment.newInstance(mission == null ? "" : mission.originatorName, canAuditing, i, buts.get(i).getParameterId(), buts.get(i).processType, taskDefId));
                        if (i == buts.size() - 1) {
                            ((JoinAuditingFragment) fragmentV4s.get(i + 1)).setMymissionActivity(MymissionActivity.this);
                        }
                    }
                    onComplete(null, true);
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    private class MyMissionAdapter extends FragmentPagerAdapter{

        public MyMissionAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentV4s.get(position);
        }

        @Override
        public int getCount() {
            return fragmentV4s.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return position > 0 ? info.getButtonInfos().get(position-1).getButtonName():"基本信息表";
        }
    }
}
