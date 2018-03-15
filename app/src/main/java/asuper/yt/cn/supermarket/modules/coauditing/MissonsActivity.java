package asuper.yt.cn.supermarket.modules.coauditing;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/5/31 0031.
 */

public class MissonsActivity extends BaseActivity {
    RecyclerView recyclerView;
    SwipeToLoadLayout swipeRefreshLayout;
    public List<Mission> missions;
    public MainButtonSubVO vo;
    public int start = 0;

    private boolean canAuditing;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 100){
            dismissProgress();
            start = 0;
            swipeRefreshLayout.setRefreshing(false);
            requestData();
            EventBus.getDefault().removeStickyEvent(Mission.class);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected int getContentId() {
        return R.layout.activity_coauditing_missions;
    }

    @Override
    protected void findView(View view) {
        vo = EventBus.getDefault().getStickyEvent(MainButtonSubVO.class);
        EventBus.getDefault().removeStickyEvent(MainButtonSubVO.class);
        String title = getIntent().getExtras().getString("title");
        ActionBarManager.initBackToolbar(this,title==null?"我的任务":title);
        canAuditing = getIntent().getExtras().getBoolean("canAuditing");
        missions = new ArrayList<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        swipeRefreshLayout = (SwipeToLoadLayout) view.findViewById(R.id.coauditing_missions_swipe);
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                start = 0;
                requestData();
            }
        });
        swipeRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                requestData();
            }

        });
        recyclerView.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }

            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        });
        recyclerView.setAdapter(new MissionsAdapter());
        showProgress();
        requestData();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    
    public void requestData() {
        HashMap<String,Object> params = new HashMap<>();
        if(vo == null){
            ToastUtil.error("数据出错了，请重试");
            return;
        }
        if("notify".equals(vo.action)){
            params.put("start", start);
            params.put("size", 10);
        }else {
            params.put("complete", "history".equals(vo.action)? true : false);
            params.put("start", start);
            params.put("length", 10);
            params.put("userId",Config.UserInfo.USER_ID);
        }
        ToolOkHTTP.post(Config.getURL("notify".equals(vo.action)? Config.URL.URL_GET_MISSIONS_NOTIFY:Config.URL.URL_MISSION_LIST), params, new ToolOkHTTP.OKHttpCallBack() {

            @Override
            public void onSuccess(JSONObject response) {
                dismissProgress();
                JSONArray res = response.optJSONArray("data");
                if(res == null) ToastUtil.error("服务器返回参数有误");
                else {
                    try {
                        List<Mission> missions = ToolGson.fromJson(res.toString(), new TypeToken<List<Mission>>() {
                        }.getType());
                        if(start <= 0 || missions.size() > response.optInt("total",Integer.MAX_VALUE)) MissonsActivity.this.missions.clear();
                        MissonsActivity.this.missions.addAll(missions);

                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setLoadingMore(false);
                        recyclerView.getAdapter().notifyDataSetChanged();
                        swipeRefreshLayout.setLoadMoreEnabled(MissonsActivity.this.missions.size() < response.optInt("total",0));
                        start+=missions.size();
                    }catch (Exception e){
                        ToastUtil.error("服务器返回参数有误");
                    }
                }
            }

            @Override
            public void onFailure() {
                dismissProgress();
                ToastUtil.error("请求失败");
            }

        });
    }


    private class MissionsAdapter extends RecyclerView.Adapter<MissionsViewHolder>{

        @Override
        public MissionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_coauditing_missions,null);
            return new MissionsViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MissionsViewHolder holder, int position) {
            Mission mission = missions.get(position);
            if("notify".equals(vo.action)){
                holder.shopName.setText(mission.processName);
                holder.name.setText(mission.originatorName);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                holder.date.setText(simpleDateFormat.format(new Date(mission.createTime)));
                holder.type.setText(mission.typeName);
            }else {
                holder.shopName.setText(mission.name);
                holder.name.setText(mission.originatorName);
                holder.date.setText(mission.startTime);
                holder.type.setText(mission.typeName);
            }
            holder.itemView.setTag(missions.get(position));
            switch (mission.status){
                case "normal":
                    holder.status.setText("审批中");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    break;
                case "stop":
                    holder.status.setText("终止");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    break;
                case "finish":
                    holder.status.setText("完成");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.yt_green_light));
                    break;
                case "reject":
                    holder.status.setText("驳回");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    break;
                case "revoke":
                    holder.status.setText("撤回");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    break;
                case "0":
                    holder.status.setText("未查看");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                    break;
                case "1":
                    holder.status.setText("已查看");
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.yt_gray));
                    break;
                default:
                    holder.status.setTextColor(getContext().getResources().getColor(R.color.yt_gray));
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return missions == null?0:missions.size();
        }
    }

    private class MissionsViewHolder extends RecyclerView.ViewHolder{

        public TextView shopName,name,type,status,date;

        public MissionsViewHolder(View itemView) {
            super(itemView);
            shopName = (TextView) itemView.findViewById(R.id.missions_item_shopname);
            name = (TextView) itemView.findViewById(R.id.missions_item_name);
            type = (TextView) itemView.findViewById(R.id.missions_item_type);
            status = (TextView) itemView.findViewById(R.id.missions_item_status);
            date = (TextView) itemView.findViewById(R.id.missions_item_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getTag() == null || !(v.getTag() instanceof  Mission)) return;
                    Mission mission = (Mission) v.getTag();
                    if("7".equals(mission.processType)){
                       ToastUtil.info("暂不支持绩效审核");
                        return;
                    }
                    EventBus.getDefault().postSticky(mission);
                    getOperation().addParameter("intentionId",mission.intentionId);
                    getOperation().addParameter("processType",mission.processType);
                    getOperation().addParameter("taskDefId",mission.taskDefId);
                    getOperation().addParameter("title",mission.typeName);
                    getOperation().addParameter("canAuditing",canAuditing);
                    getOperation().forwardForResult(MymissionActivity.class,100);
                    if(vo.id ==18){
                        HashMap<String,Object> params = new HashMap<String, Object>();
                        params.put("noticeId",mission.id);
                        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_MISSIONS_NOTIFY_UPDATE),params,null);
                    }
                }
            });
        }
    }
}
