package asuper.yt.cn.supermarket.modules.myclient;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.MerchantJoinScoretableVO;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;
import asuper.yt.cn.supermarket.modules.common.ClienteleAdapter;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.modules.myclient.entities.MyClient;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import asuper.yt.cn.supermarket.views.SlantTextView;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/9/18 0018.
 */

public class MyClientListSearchActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private SwipeToLoadLayout swipeToLoadLayout;
    private ClienteleAdapter adapter;

    SparseArray<String> ids;

    private boolean requestFirstPage = true;
    private int pageNum, pageSize = 10;

    private View back,search;
    private EditText searchEdit;
    private String filter;
    private Clit selectVO;

    private boolean isSelectable;
    private View confimChoice;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_client_list_search;
    }

    @Override
    protected void findView(View root) {
        recyclerView = (RecyclerView) root.findViewById(R.id.swipe_target);
        adapter = new ClienteleAdapter();
        ids = new SparseArray<>();
        List<Clit> clits = EventBus.getDefault().getStickyEvent(new ArrayList<Clit>().getClass());
        adapter.addItems(clits);
        swipeToLoadLayout = (SwipeToLoadLayout) root.findViewById(R.id.my_client_list_search_load);
        swipeToLoadLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                filter = searchEdit.getText().toString();
                getClientele(filter,true);
            }
        });
        swipeToLoadLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                filter = searchEdit.getText().toString();
                getClientele(filter,false);
            }
        });
        recyclerView.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -2);
            }
        });
        recyclerView.setAdapter(adapter);

        isSelectable = getIntent().getBooleanExtra("isSelectable",false);
        back = root.findViewById(R.id.my_client_list_back);
        search = root.findViewById(R.id.my_client_list_search);
        searchEdit = (EditText) root.findViewById(R.id.my_client_list_search_edit);

        back.setOnClickListener(this);
        search.setOnClickListener(this);
        searchEdit.setVisibility(View.VISIBLE);
        search.setVisibility(View.VISIBLE);
        searchEdit.requestFocus();

        confimChoice = root.findViewById(R.id.my_client_list_confirm_choice);
        confimChoice.setVisibility(isSelectable ? View.VISIBLE : View.GONE);
        confimChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectVO == null) ToastUtil.info("请选择一个客户");
                else {
                    EventBus.getDefault().post(selectVO.id + "," + selectVO.applyShopName);
                    setResult(100);
                    finish();
                }
            }
        });

    }

    private void getClientele(String filter,boolean isRefresh) {
        if(isRefresh){
            requestFirstPage = true;
            pageNum = 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        map.put("applyStatus",1);
        map.put("search", filter);
        if(ids == null || pageNum == 1) {
            ids = new SparseArray<>();
            try {
                List<MerchantJoinScoretableVO> joinList = ToolDbOperation.getJoinDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                List<Contract> contractList = ToolDbOperation.getRactDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                List<SubsidyLocalVO> subsidyLocalVOs = ToolDbOperation.getSubsidyDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                for (MerchantJoinScoretableVO item : joinList) {
                    if (item == null) continue;
                    ids.put(item.getShopId(), "加盟未提交");
                }
                for (Contract item : contractList) {
                    if (item == null) continue;
                    try {
                        ids.put(new BigDecimal(item.getIntentionId()).intValue(), "合同未提交");
                    } catch (Exception e) {

                    }
                }
                for (SubsidyLocalVO item : subsidyLocalVOs) {
                    if (item == null) continue;
                    try {
                        ids.put(new BigDecimal(item.intentionId).intValue(), "补贴未提交");
                    } catch (Exception e) {

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ToolOkHTTP.post(Config.getURL("oles/app/myClient/findShopInfoList.htm"), map, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Gson gson = new Gson();
                MyClient ent = gson.fromJson(response.optString("resultObject"), MyClient.class);
                getClientelesResult(ent);
        }

            @Override
            public void onFailure() {

                getClientelesResult(null);
            }
        });
    }

    public void getClientelesResult(MyClient clienteles) {
        dismissProgress();
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
        if (clienteles == null || clienteles.getRows() == null) return;
        if (requestFirstPage) adapter.clear();
        requestFirstPage = false;
        adapter.addItems(clienteles.getRows());
        if (clienteles.getRows().size() < pageSize) {
            swipeToLoadLayout.setLoadMoreEnabled(false);
        } else {
            swipeToLoadLayout.setLoadMoreEnabled(true);
            pageNum++;
        }
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_client_list_back:
                finish();
                break;
            case R.id.my_client_list_search:
                filter = searchEdit.getText().toString();
                getClientele(filter,true);
                showProgress();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getClientele(filter,true);
    }

    public class ClienteleAdapter extends EmptyViewRecyclerAdapter {

        private List<Clit> clits = new ArrayList<>();
        private boolean isSelectable = false;

        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_shop_card, null));
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
            if(clits == null) return;
            this.clits.addAll(clits);
            notifyDataSetChanged();
        }

        public void clear() {
            clits.clear();
            notifyDataSetChanged();
        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, shopName, address, phone, date, status;
        private SlantTextView local;
        private View dateLine;
        private CheckBox checkBox;


        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clit p = (Clit) v.getTag();
                    if (!isSelectable) {
                        EventBus.getDefault().postSticky(p);
                        getOperation().addParameter("fromLocal",local.getVisibility() ==View.VISIBLE);
                        getOperation().forwardForResult(MyClientDetailActivity.class,100);
                    } else {
                        selectVO = p;
                        recyclerView.getAdapter().notifyDataSetChanged();
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
            String s = ids.get(clit.id);
            local.setVisibility(s != null ? View.VISIBLE : View.INVISIBLE);
            local.setText(s);
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
}
