package asuper.yt.cn.supermarket.modules.dynamic;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseAdapter;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.utils.DateUtils;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.views.CustomListView;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.bingoogolapple.badgeview.BGABadgeImageView;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by zengxiaowen on 2017/10/31.
 */

public class DynamicItemFragment extends BaseFragment {

    private int frag = 0;
    private OnSetListSize onSetListSize;

    private int pageNum = 1;

    public void setOnSetListSize(OnSetListSize onSetListSize) {
        this.onSetListSize = onSetListSize;
    }

    public static DynamicItemFragment newInstance(int frag) {
        DynamicItemFragment fragment = new DynamicItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("frag", frag);
        fragment.setArguments(bundle);
        return fragment;
    }

    private SwipeToLoadLayout refreshLayout;
    private RecyclerView swipe_target;
    private DropRecordAdapter adapter;


    @Override
    protected int getContentId() {
        return R.layout.fragment_item_dynamic;
    }

    @Override
    protected void findView(View root) {
        frag = getArguments().getInt("frag", 0);
        initView(root);
        updateAll(pageNum);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new DynamicStore(dependencyDelegate);
    }

    private void initView(View root) {
        refreshLayout = (SwipeToLoadLayout) root.findViewById(R.id.my_client_list_swipe);
        swipe_target = (RecyclerView) root.findViewById(R.id.swipe_target);

        swipe_target.setLayoutManager(new SafetyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -2);
            }
        });
//        swipe_target.setLayoutManager(new SafetyLinearLayoutManager(getActivity()));
        adapter = new DropRecordAdapter();
        swipe_target.setAdapter(adapter);
        refreshLayout.setLoadMoreEnabled(frag == 0);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAll(1);
                if (onSetListSize != null)
                    onSetListSize.onReferList();
                EventBus.getDefault().post(true);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                updateAll(pageNum);
            }
        });
    }


    public void updateAll(int start) {
        if (start == 1) adapter.clear();
        Map<String, Object> map = new HashMap<>();
        map.put("start", start);
        map.put("length", 20);
        map.put("frag", frag);
        map.put("employeeId", Config.UserInfo.USER_ID);
        dispatch(DynamicStore.REQUEST_GET_DYNAMIC_LIST, map);
    }


    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_LIST)
    public void getDynamicList(Map<String, Object> map) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        if (refreshLayout.isLoadingMore()) {
            refreshLayout.setLoadingMore(false);
        }
        if (map == null || map.get("date") == null) return;
        Object date = map.get("date");
        adapter.addAllItems(date);
        if (date instanceof Dynamic) {
            pageNum = (int) map.get("pageNum");
            Map<String, Dynamic.ResultItem> dynamic = ((Dynamic) date).getResultObject();
            if (dynamic == null) return;
            for (String key : dynamic.keySet()) {
                ToolLog.i(dynamic.get(key).getTotalRow());
                if (adapter.getKeys() >= dynamic.get(key).getTotalRow()) {
                    refreshLayout.setLoadMoreEnabled(false);
                } else {
                    refreshLayout.setLoadMoreEnabled(true);
                }
                return;
            }

        }
    }


    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_ISNOTICE)
    public void getDynamicIsnotice(JSONObject json) {
        updateAll(1);
        if (onSetListSize != null)
            onSetListSize.onReferList();
    }


    private class DropRecordAdapter extends EmptyViewRecyclerAdapter {

        private Map<String, List<Dynamic.Item>> mapItem = new HashMap<>();
        private List<String> keys = new ArrayList<>();

        @Override
        public int OnEmptyView() {
            return 0;
        }

        public int getKeys() {
            int count = 0;
            for (int i = 0; i < keys.size(); i++) {
                count = count + mapItem.get(keys.get(i)).size();
            }
            return count;
        }

        @Override
        public RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setTag(mapItem.get(keys.get(position)));
            ((ViewHolder) holder).setDateView(keys.get(position));
        }

        @Override
        public int GetItemCount() {
            return keys.size();
        }


        public void addAllItems(Object date) {
            if (date instanceof Dynamic) {
                Map<String, Dynamic.ResultItem> map = ((Dynamic) date).getResultObject();
                for (String key : map.keySet()) {
                    if (keys.contains(key)) {
                        mapItem.get(key).addAll(map.get(key).getRows());
                    } else {
                        keys.add(key);
                        mapItem.put(key, map.get(key).getRows());
                    }
                }
            } else if (date instanceof DynmicDaiBan) {
                mapItem.putAll(((DynmicDaiBan) date).getResultObject());
                if (mapItem != null) {
                    keys.addAll(mapItem.keySet());
                    Collections.sort(this.keys, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return DateUtils.isDateOneBigger(o1, o2) ? -1 : 1;
                        }
                    });
                }
            }
            notifyDataSetChanged();
        }


        public void clear() {
            this.mapItem.clear();
            this.keys.clear();
        }
    }


    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvThem;
        private CustomListView dyList;
        private DynamicAdapter adapter;

        public ViewHolder(final View itemView) {
            super(itemView);
            tvThem = (TextView) itemView.findViewById(R.id.tvThem);
            dyList = (CustomListView) itemView.findViewById(R.id.dynList);
            dyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Dynamic.Item item = (Dynamic.Item) adapter.getItem(position);
                    if (frag == 1 && item.resultType.equals("old")) {
                        if (item.notice) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("shopId", item.getShopId());
                            dispatch(DynamicStore.REQUEST_GET_DYNAMIC_ISNOTICE, map);
                        }
                        Clit clit = new Clit();
                        clit.id = item.getShopId();
                        EventBus.getDefault().postSticky(clit);
                        getOperation().forward(MyClientDetailActivity.class);
                    } else {
                        getOperation().addParameter("shopId", item.getShopId());
                        getOperation().addParameter("shopName", item.getShopName());
                        getOperation().addParameter("frag", frag);
                        getOperation().forwardForResult(ShopDynamicActivity.class, DynamicStore.REQUEST_GET_DYNAMIC_ADD + frag);
                    }
                }
            });
        }

        public void setDateView(String key) {
            List<Dynamic.Item> item = (List<Dynamic.Item>) itemView.getTag();
            adapter = new DynamicAdapter();
            adapter.addItem(item);
            tvThem.setText(key);
            dyList.setAdapter(adapter);
        }

        private class DynamicAdapter extends BaseAdapter {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                DynamicAdapter.ViewHodler hodler = null;
                if (convertView == null) {
                    hodler = new ViewHodler();
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_tz_list_item, parent, false);
                    hodler.tvName = (TextView) convertView.findViewById(R.id.tvName);
                    hodler.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    hodler.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
                    hodler.igTy = (BGABadgeImageView) convertView.findViewById(R.id.igTy);
                    convertView.setTag(hodler);
                } else {
                    hodler = (ViewHodler) convertView.getTag();
                }
                Dynamic.Item item = (Dynamic.Item) getItem(position);
                if (frag == 0) {
                    if (item.noCheck==0)
                        hodler.igTy.hiddenBadge();
                    else hodler.igTy.showTextBadge(item.noCheck + "");
                    hodler.igTy.setImageResource(R.drawable.icdynamic_tz);
                    hodler.tvName.setText(item.getCurrentStatus() + "-" + item.getShopName());
                    hodler.tvTime.setText(item.getDetailTime());
                    hodler.tvMsg.setText(item.getHintMsg());
                } else {
                    hodler.tvTime.setText(item.getDetailTime());
                    hodler.tvMsg.setText("请处理");
                    hodler.igTy.showCirclePointBadge();
                    if (item.displayStatus == null) {
                        hodler.tvName.setText(item.currentStatus + "-" + item.getShopName());
                        hodler.igTy.setImageResource(R.drawable.ic_dynamic_yq);
                    } else {
                        hodler.tvName.setText(item.applyStep + "-" + item.getShopName());
                        hodler.igTy.setImageResource(item.displayStatus.equals("0") ? R.mipmap.ic_dynamic1 : R.mipmap.ic_dynamic2);
                    }
                }
                if (hodler.tvMsg.getText().toString().isEmpty())
                    hodler.tvMsg.setVisibility(View.GONE);
                else hodler.tvMsg.setVisibility(View.VISIBLE);
//                findSpinner((ViewGroup) convertView);
                return convertView;
            }

            private class ViewHodler {

                private TextView tvName, tvTime, tvMsg;
                private BGABadgeImageView igTy;

            }
        }
    }


    protected interface OnSetListSize {
        void onReferList();
    }
}
