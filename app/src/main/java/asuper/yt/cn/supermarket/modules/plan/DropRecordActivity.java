package asuper.yt.cn.supermarket.modules.plan;

import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolUnit;

/**
 * Created by zengxiaowen on 2017/9/12.
 */

public class DropRecordActivity extends BaseActivity {

    private SwipeToLoadLayout refreshLayout;
    private RecyclerView swipe_target;
    private DropRecordAdapter adapter;
    private DropRecord plan;

    private PopupWindow popupView;
    private View anchor;
    private RadioGroup rbGroup;

    @Override
    protected int getContentId() {
        return R.layout.activity_droprecord;
    }

    @Override
    protected void findView(View root) {
        showProgress();
        ActionBarManager.initBackToolbar(this, "拜访历史");
        initView(root);

        postClent(1);
    }

    private void postClent(int pageNum) {
        Map<String, Object> map = new HashMap<>();
        map.put("visitType", getType());//
        map.put("pageNum", pageNum);
        map.put("pageSize", 20);
        dispatch(PlanStore.REQUEST_GET_PLAN_HISTORY, map);
    }

    private String getType() {
        int id = rbGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.btnAll:// 全部
                return "";
            case R.id.btnHome: //上门拜访
                return "home_visit";
            case R.id.btnPhone: //电话拜访
                return "phone_visit";
            default:
                return "";
        }
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_HISTORY)
    public void getHistoryList(JSONObject json) {
        dismissProgress();
        if (refreshLayout.isRefreshing())
            refreshLayout.setRefreshing(false);
        if (refreshLayout.isLoadingMore())
            refreshLayout.setLoadingMore(false);
        if (json == null)
            return;
        plan = ToolGson.fromJson(json.optString("resultObject"), DropRecord.class);
        if (plan.getPageNum() == 1) {
            adapter.clear();
        }
        adapter.addItems(plan.getRows());
    }

    private void initView(View root) {
        anchor = root.findViewById(R.id.top);
        refreshLayout = (SwipeToLoadLayout) root.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                postClent(1);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                postClent(plan.getPageNum() + 1);
            }
        });
        swipe_target = (RecyclerView) root.findViewById(R.id.swipe_target);
        swipe_target.setLayoutManager(new SafetyLinearLayoutManager(this) {
        });
//        swipe_target.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
//            @Override
//            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
//                return new RecyclerView.LayoutParams(-1, -1);
//            }
//        });
        adapter = new DropRecordAdapter();
        swipe_target.setAdapter(adapter);

        initPopwinView();
    }

    private void initPopwinView() {
        popupView = ToolAlert.popwindow(this, R.layout.popwindow_saix);
        (rbGroup = (RadioGroup) popupView.getContentView().findViewById(R.id.ra_group)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                popupView.dismiss();
                postClent(1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        ActionBarManager.initActionBarSubmitButton(menu, ActionBarManager.TOOLBARBTN.SAIX);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        popupView.showAtLocation(swipe_target, Gravity.RIGHT|Gravity.TOP,0, ToolUnit.dipTopx(this,55));

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PlanStore(dependencyDelegate);
    }

    private class DropRecordAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Plan.PlanItem> plans = new ArrayList<>();

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nodate, parent, false);
                return new ViewHolder(view, viewType);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_droprecord_title, parent, false);
                return new ViewHolder(view, viewType);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (getItemViewType(position) > 0) {
                holder.itemView.setTag(plans.get(position));
                holder.setDateView(plans.get(position), position == 0 ? View.VISIBLE : (plans.get(position).getCreateTime().split(" ")[0].equals(plans.get(position - 1).getCreateTime().split(" ")[0]) ? View.GONE : View.VISIBLE));
            }
        }

        @Override
        public int getItemViewType(int position) {
            return plans.size();
        }

        @Override
        public int getItemCount() {
            return plans.size() == 0 ? 1 : plans.size();
        }

        public void addItems(List<Plan.PlanItem> plans) {
            this.plans.addAll(plans);
            notifyDataSetChanged();
        }

        public void clear() {
            this.plans.clear();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvThem, tvName, tvTime;
        private ImageView igTy;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType > 0) {
                tvThem = (TextView) itemView.findViewById(R.id.tvThem);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                igTy = (ImageView) itemView.findViewById(R.id.igTy);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOperation().addParameter("plan", (Plan.PlanItem) v.getTag());
                        getOperation().forward(DropDetailsActivity.class);
                    }
                });
            }
        }

        public void setDateView(Plan.PlanItem item, int view) {
            String time[] = item.getCreateTime().split(" ");  //  年月日   时分秒
            String year[] = time[0].split("-");
            tvThem.setText(year[0] + "年" + year[1] + "月" + year[2] + "日");
            tvThem.setVisibility(view);
            igTy.setImageResource(item.getVisitType().equals("phone_visit") ? R.mipmap.ic_phone : R.mipmap.ic_zuji);
            tvName.setText((item.getVisitType().equals("phone_visit") ? "电话拜访" : "上门拜访") + "-" + item.getShopName());
            String hous[] = time[1].split(":");
            tvTime.setText(hous[0] + ":" + hous[1]);
        }
    }


}
