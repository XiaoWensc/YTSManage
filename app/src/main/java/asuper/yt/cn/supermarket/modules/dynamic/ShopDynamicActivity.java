package asuper.yt.cn.supermarket.modules.dynamic;

import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseRecyclerAdapter;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolDateTime;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by zengxiaowen on 2017/10/31.
 */

public class ShopDynamicActivity extends BaseActivity {

    private SwipeToLoadLayout refreshLayout;
    private RecyclerView swipe_target;
    private ShopDynamicAdapter adapter;
    private int shopId;
    private int pageNum = 1;
    private Toolbar toolbar;

    private int frag = 0;


    @Override
    protected int getContentId() {
        return R.layout.activity_shop_dynamic;
    }

    @Override
    protected void findView(View root) {
        frag = getIntent().getIntExtra("frag", 0);
        shopId = getIntent().getIntExtra("shopId", 0);
        if (getIntent().getStringExtra("shopName") != null)
            toolbar = ActionBarManager.initBackToolbar(this, getIntent().getStringExtra("shopName"));
        initView();

        setResult(DynamicStore.REQUEST_GET_DYNAMIC_ADD);

        updateAll(shopId,frag,pageNum);
    }

    public void updateAll(int shopId,int frag,int pageNum) {
        this.pageNum = pageNum;
        this.frag = frag;
        this.shopId = shopId;
        Map<String, Object> map = new HashMap<>();
        map.put("shopId", shopId);
        map.put("start", pageNum);
        map.put("length", 10);
        dispatch(DynamicStore.REQUEST_GET_DYNAMIC_TZ_INFO, map);
    }

    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_TZ_INFO)
    public void UpdateList(DynamicStore.Notice notice) {
        if (refreshLayout.isRefreshing()) refreshLayout.setRefreshing(false);
        if (notice == null || notice.rows == null || notice.rows.size() == 0) return;
        if (toolbar == null)
            toolbar = ActionBarManager.initBackToolbar(this, notice.rows.get(0).shopName);
        List<DynamicStore.Notice.Info> infos = new ArrayList<>();
        for (int i = 0; i < notice.rows.size(); i++) {
            if (notice.rows.get(i).noticeType == frag)
                infos.add(notice.rows.get(i));
        }
//        adapter.notifyItemMoved((adapter.getAllList().size()-10),adapter.getAllList().size());
        if (pageNum == 1) {
            adapter.clear();
            swipe_target.scrollToPosition(0);
        }
        adapter.addItemDates(infos);
        adapter.notifyDataSetChanged();
        if (infos.size() < 10)
            refreshLayout.setRefreshEnabled(false);
        pageNum++;
    }

    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_ADD)
    public void addRemove(String str) {
        if (str == null) return;
        if (str.equals("add")) {
            ToastUtil.success("已加入客户回收站");
        }
        finish();
    }

    private void initView() {
        refreshLayout = (SwipeToLoadLayout) findViewById(R.id.my_client_list_swipe);
        swipe_target = (RecyclerView) findViewById(R.id.swipe_target);
        adapter = new ShopDynamicAdapter();
        swipe_target.setAdapter(adapter);

        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        layout.setStackFromEnd(true);//列表再底部开始展示，反转后由上面开始展示
        layout.setReverseLayout(true);//列表翻转
        swipe_target.setLayoutManager(layout);

        refreshLayout.setLoadMoreEnabled(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAll(shopId,frag,pageNum);
            }
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new DynamicStore(dependencyDelegate);
    }

    private class ShopDynamicAdapter extends BaseRecyclerAdapter {

        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_shop_dynamic, parent, false));
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DynamicStore.Notice.Info info = (DynamicStore.Notice.Info) getAllList().get(position);
            holder.itemView.setTag(info);
            ((ViewHolder) holder).setDate();
        }

        private class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tvStatus, tvTime, tvShopName, tvRealName, tvRealPhone, tvShopAddress, tvStrTime, tvEndTime, tvDetail, tvHint, tvYuq;
            private Button btnQue;
            private EditText edYuq;

            public ViewHolder(final View itemView) {
                super(itemView);
                tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
                tvTime = (TextView) itemView.findViewById(R.id.tvTime);
                tvShopName = (TextView) itemView.findViewById(R.id.tvShopName);
                tvRealName = (TextView) itemView.findViewById(R.id.tvRealName);
                tvRealPhone = (TextView) itemView.findViewById(R.id.tvRealPhone);
                tvShopAddress = (TextView) itemView.findViewById(R.id.tvShopAddress);
                tvStrTime = (TextView) itemView.findViewById(R.id.tvStrTime);
                tvEndTime = (TextView) itemView.findViewById(R.id.tvEndTime);
                tvHint = (TextView) itemView.findViewById(R.id.tvHint);
                tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
                tvYuq = (TextView) itemView.findViewById(R.id.tvYuq);
                edYuq = (EditText) itemView.findViewById(R.id.edYuq);
                btnQue = (Button) itemView.findViewById(R.id.btnQue);
                btnQue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String str = v.getTag().toString();
                        DynamicStore.Notice.Info info = (DynamicStore.Notice.Info) itemView.getTag();
                        Map<String, Object> map = new HashMap<>();
                        map.put("shopId", info.shopId);
                        map.put("str", str);
                        if (str.equals("add")) {
                            if (edYuq.getText().toString().trim().isEmpty()) {
                                ToastUtil.info("请输入逾期原因");
                                return;
                            } else {
                                map.put("overdueReasons", edYuq.getText().toString());
                                map.put("overdueStatus", info.currentStatus);
                            }
                        }
                        dispatch(DynamicStore.REQUEST_GET_DYNAMIC_ADD, map);
                    }
                });
            }

            public void setDate() {
                DynamicStore.Notice.Info info = (DynamicStore.Notice.Info) itemView.getTag();
                tvStatus.setText(Html.fromHtml(info.currentStatus));
                tvTime.setText(ToolDateTime.formatDateTime(Long.parseLong(info.gmtCreate), "MM月dd日 HH:mm"));
                tvShopName.setText(Html.fromHtml("店铺名称：" + info.shopName));
                tvRealName.setText(Html.fromHtml("实际经营者：" + info.realManName));
                tvRealPhone.setText(Html.fromHtml("实际经营者电话：" + info.realManPhone));
                tvShopAddress.setText(Html.fromHtml("店铺地址：" + info.shopAddress));
                if (info.completeTime == null || info.completeTime.isEmpty()) {
                    tvStrTime.setVisibility(View.GONE);
                } else {
                    tvStrTime.setVisibility(View.VISIBLE);
                    tvStrTime.setText(info.completeTime);
                }
                if (info.endTime == null || info.endTime.isEmpty()) {
                    tvEndTime.setVisibility(View.GONE);
                } else {
                    tvEndTime.setVisibility(View.VISIBLE);
                    tvEndTime.setText(Html.fromHtml(info.endTime));
                }
//                tvHint.setText(Html.fromHtml("" + info.hint));
                tvHint.setText(Html.fromHtml("提示：" + info.hint));
                tvDetail.setText(Html.fromHtml("详情：" + info.detailContent));
//                tvDetail.setText(Html.fromHtml("距离签约逾期只剩<font style=\"color:#FF0000\">5个工作日</font>"));
                if (info.confirmButton != null && info.confirmButton.equals("show")) {
                    btnQue.setVisibility(View.VISIBLE);
                    tvHint.setVisibility(View.GONE);
                    btnQue.setText("确认并删除");
                    btnQue.setTag("remove");
                } else {
                    btnQue.setVisibility(View.GONE);
                }
                if (info.noticeType == 1) {
                    tvYuq.setVisibility(View.VISIBLE);
                    edYuq.setVisibility(View.VISIBLE);
                    btnQue.setVisibility(View.VISIBLE);
                    btnQue.setText("确认");
                    btnQue.setTag("add");
                } else {
                    tvYuq.setVisibility(View.GONE);
                    edYuq.setVisibility(View.GONE);
                }
            }
        }
    }
}
