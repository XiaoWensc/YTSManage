package asuper.yt.cn.supermarket.modules.myclient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.MyExpandVo;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.modules.myclient.entities.MyClient;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.views.SlantTextView;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/9/7 0007.
 */


// TODO: 2017/9/12 0012 客户列表需要返回group_id,step和over供MyClientDetailActivity查询tab信息时用
// TODO: 2017/9/12 0013 客户列表筛选条件需要服务器给出
public class MyClientListActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView recyclerView;

    private int pageNum, pageSize = 10;

    public static final int REQUEST_GET_CLIENTELE = 0X2001;
    public static final int REQUEST_GET_FILTER = 0X2002;

    private ClienteleAdapter adapter;

    private boolean requestFirstPage = true;

    private SwipeToLoadLayout smoothRefreshLayout;
    private AppBarLayout appBarLayout;
    private RadioButton all;
    private android.widget.RadioButton filter;


    private List<MyExpandVo.MyExpandSub> myExpandSubs;
    private SparseArray<Boolean> checkedList;
    private RecyclerView filterList;
    private View cancel, confirm, loader, filterContainer, confimChoice, back, search;

    private boolean isSelectable = false;
    private String filterStr = "";
    private SparseArray<String> ids = new SparseArray<>();

    @Override
    protected int getContentId() {
        return R.layout.activity_my_clientele;
    }

    @Override
    protected void findView(View root) {
        showProgress();
        if (getIntent() != null && getIntent().getExtras() != null) {
            isSelectable = getIntent().getExtras().getBoolean("isSelectable");
        }
        adapter = new ClienteleAdapter(isSelectable);
        smoothRefreshLayout = (SwipeToLoadLayout) root.findViewById(R.id.my_client_list_swipe);
        appBarLayout = (AppBarLayout) root.findViewById(R.id.my_client_list_appbar);
        filter = (RadioButton) root.findViewById(R.id.my_client_list_filter);
        confimChoice = root.findViewById(R.id.my_client_list_confirm_choice);
        confimChoice.setVisibility(isSelectable ? View.VISIBLE : View.GONE);
        confimChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clit clit = adapter.getSelectVO();
                if (clit == null) ToastUtil.info("请选择一个客户");
                else {
                    EventBus.getDefault().post(clit.id + "," + clit.applyShopName);
                    finish();
                }
            }
        });
        all = (RadioButton) root.findViewById(R.id.my_client_list_all);
        filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (filterList.getAdapter() == null || filterList.getAdapter().getItemCount() < 1) {
                    dispatch(REQUEST_GET_FILTER, new HashMap<String, Object>());
                }
                filterContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }

        });
        all.setChecked(true);
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.setChecked(false);
                filterContainer.setVisibility(View.GONE);
                filterStr = null;
                getClientele(true);
                checkedList.clear();
                filterList.getAdapter().notifyDataSetChanged();
            }
        });
        smoothRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                getClientele(true);
            }
        });
        smoothRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getClientele(false);
            }
        });
        recyclerView = (RecyclerView) root.findViewById(R.id.swipe_target);
        recyclerView.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -2);
            }
        });
        recyclerView.setAdapter(adapter);


        back = root.findViewById(R.id.my_client_list_back);
        search = root.findViewById(R.id.my_client_list_search_icon);

        back.setOnClickListener(this);
        search.setOnClickListener(this);

        initFilterList(root);
    }

    private void initFilterList(View view) {
        cancel = view.findViewById(R.id.popup_client_filter_cancel);
        confirm = view.findViewById(R.id.popup_client_filter_confirm);
        loader = view.findViewById(R.id.popup_client_filter_loader);
        filterContainer = view.findViewById(R.id.my_client_list_filter_container);
        checkedList = new SparseArray<>();
        if (myExpandSubs != null) loader.setVisibility(View.GONE);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter.setChecked(false);
                filterContainer.setVisibility(View.GONE);
                checkedList.clear();
                filterList.getAdapter().notifyDataSetChanged();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterContainer.setVisibility(View.GONE);
                filter.setChecked(false);
                if (myExpandSubs == null) return;
                String filter = "";
                for (int i = 0; i < myExpandSubs.size(); i++) {
                    Boolean isChecked = checkedList.get(i);
                    if (isChecked != null && isChecked) {
                        filter += myExpandSubs.get(i).step + ",";
                    }
                }
                filter = filter.substring(0, filter.length());
                filterStr = filter;
                showProgress();
                getClientele(true);
            }
        });
        filterList = (RecyclerView) view.findViewById(R.id.popup_client_filter_list);
        filterList.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -2);
            }
        });
        filterList.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL));
        filterList.setAdapter(new Adapter());
    }

    private void getClientele(boolean isRefresh) {
        if (isRefresh) {
            requestFirstPage = true;
            pageNum = 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("applySteup", filterStr);
        dispatch(REQUEST_GET_CLIENTELE, map);
        dispatch(REQUEST_GET_FILTER, new HashMap<String, Object>());
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new MyClientListStore(dependencyDelegate);
    }

    @BindAction(REQUEST_GET_CLIENTELE)
    public void getClienteles(Map<String, Object> res) {
        dismissProgress();
        smoothRefreshLayout.setRefreshing(false);
        smoothRefreshLayout.setLoadingMore(false);
        if (res == null) return;
        MyClient clienteles = (MyClient) res.get("client");
        SparseArray<String> i = (SparseArray<String>) res.get("ids");
        if (i != null) ids = i;
        if (clienteles == null || clienteles.getRows() == null) return;
        if (requestFirstPage) adapter.clear();
        requestFirstPage = false;
        adapter.addItems(clienteles.getRows());
        if (clienteles.getRows().size() < pageSize) {
            smoothRefreshLayout.setLoadMoreEnabled(false);
        } else {
            smoothRefreshLayout.setLoadMoreEnabled(true);
            pageNum++;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getClientele(true);
    }

    @BindAction(REQUEST_GET_FILTER)
    public void getFilterResult(List<MyExpandVo.MyExpandSub> myExpandSubs) {
        if (myExpandSubs == null) ToastUtil.error("获取筛选条件失败");
        this.myExpandSubs = myExpandSubs;
        if (filterList.getAdapter() != null) filterList.getAdapter().notifyDataSetChanged();
        loader.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_client_list_back:
                finish();
                break;
            case R.id.my_client_list_search_icon:
                getOperation().addParameter("isSelectable", isSelectable);
                EventBus.getDefault().postSticky(((ClienteleAdapter) recyclerView.getAdapter()).getClits());
                getOperation().forwardForResult(MyClientListSearchActivity.class, 100);
                break;
        }
    }

    public class ClienteleAdapter extends EmptyViewRecyclerAdapter {

        private List<Clit> clits = new ArrayList<>();
        private boolean isSelectable = false;
        private Clit selectVO;

        public Clit getSelectVO() {
            return selectVO;
        }

        public ClienteleAdapter(boolean isSelectable) {
            this.isSelectable = isSelectable;
        }

        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_shop_card, null), isSelectable);
            viewHolder.onItemSelected = new OnItemSelected() {
                @Override
                public void onSelected(Clit clit, CheckBox checkBox) {
                    selectVO = clit;
                    notifyDataSetChanged();
                }
            };
            return viewHolder;
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).setData(clits.get(position), clits.get(position).equals(selectVO));
        }

        @Override
        public int GetItemCount() {
            return clits.size();
        }

        public void addItems(List<Clit> clits) {
            this.clits.addAll(clits);
            notifyDataSetChanged();
        }

        public List<Clit> getClits() {
            return clits;
        }

        public void clear() {
            clits.clear();
            notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            finish();
        } else if (requestCode == 101) {
            showProgress();
            getClientele(true);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, shopName, address, phone, date, status;
        private SlantTextView local;
        private View dateLine;
        private CheckBox checkBox;
        private boolean isSelectable = false;
        public OnItemSelected onItemSelected;

        public ViewHolder(View itemView) {
            this(itemView, false);
        }

        public ViewHolder(View itemView, boolean selectable) {
            super(itemView);
            isSelectable = selectable;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clit p = (Clit) v.getTag();
                    if (!isSelectable) {
                        EventBus.getDefault().postSticky(p);
                        getOperation().addParameter("fromLocal", local.getVisibility() == View.VISIBLE);
                        getOperation().forwardForResult(MyClientDetailActivity.class, 100);
                    } else {
                        if (onItemSelected != null) {
                            onItemSelected.onSelected(p, checkBox);
                        }
                    }

                }
            });
            name = (TextView) itemView.findViewById(R.id.shop_card_owner_name);
            shopName = (TextView) itemView.findViewById(R.id.shop_card_shop_name);
            address = (TextView) itemView.findViewById(R.id.shop_card_address);
            phone = (TextView) itemView.findViewById(R.id.shop_card_owner_phone);
            date = (TextView) itemView.findViewById(R.id.shop_card_date);
            local = (SlantTextView) itemView.findViewById(R.id.shop_card_local);
            status = (TextView) itemView.findViewById(R.id.shop_card_shop_status);
            dateLine = itemView.findViewById(R.id.shop_card_date_line);
            checkBox = (CheckBox) itemView.findViewById(R.id.shop_card_selected);
            checkBox.setEnabled(false);
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            if (isSelectable) {
                checkBox.setVisibility(View.VISIBLE);
            }
        }

        public void setData(Clit clit, boolean isChecked) {
            itemView.setTag(clit);
            if (clit.getApplyStepName().contains("审核中")){
                local.setVisibility(View.INVISIBLE);
            }else {
                String s = ids.get(clit.id);
                local.setVisibility(s != null ? View.VISIBLE : View.INVISIBLE);
                local.setText(s);
            }
            name.setText(clit.applyLegalperson);
            shopName.setText(clit.applyShopName);
            address.setText(clit.applyAddress);
            phone.setText(clit.applyPhoneNumber);
            date.setVisibility(View.GONE);
            dateLine.setVisibility(View.GONE);
            status.setText(clit.getApplyStepName());
            checkBox.setChecked(isChecked);
            if (clit.getApplyStepName() == null || clit.getApplyStepName().trim().isEmpty()) {
                status.setVisibility(View.INVISIBLE);
                return;
            } else {
                status.setVisibility(View.VISIBLE);
            }
            int[] res = ToolGetButtoninfos.getIconAndBackground(clit.applySteup);
            status.setBackgroundResource(res[1]);
            Drawable drawable = shopName.getResources().getDrawable(res[0]);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            status.setCompoundDrawables(drawable, null, null, null);
        }


    }

    private interface OnItemSelected {
        public void onSelected(Clit clit, CheckBox checkBox);
    }

    private class FilterViewHolder extends RecyclerView.ViewHolder {

        public CheckBox title, check;
        public TextView num;

        public FilterViewHolder(View itemView) {
            super(itemView);
            title = (CheckBox) itemView.findViewById(R.id.item_client_filter_title);
            check = (CheckBox) itemView.findViewById(R.id.item_client_filter_check);
            num = (TextView) itemView.findViewById(R.id.item_client_filter_num);
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    title.setChecked(isChecked);
                    checkedList.put((int) buttonView.getTag(), isChecked);
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    check.performClick();
                }
            });
        }

        public void setData(MyExpandVo.MyExpandSub sub) {
            if (sub == null) return;
            title.setText(sub.stepName == null ? "---" : sub.stepName);
            num.setText(sub.num + "");
            if (sub.num < 1) {
                num.setVisibility(View.INVISIBLE);
            } else {
                num.setVisibility(View.VISIBLE);
            }
            Boolean isChecked = checkedList.get((int) check.getTag());
            check.setChecked(isChecked == null ? false : isChecked);
        }
    }

    private class Adapter extends RecyclerView.Adapter<FilterViewHolder> {
        @Override
        public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MyClientListActivity.this).inflate(R.layout.item_client_filter, null);
            return new FilterViewHolder(v);
        }

        @Override
        public void onBindViewHolder(FilterViewHolder holder, int position) {
            holder.check.setTag(position);
            holder.setData(myExpandSubs.get(position));
        }

        @Override
        public int getItemCount() {
            return myExpandSubs == null ? 0 : myExpandSubs.size();
        }
    }
}
