package asuper.yt.cn.supermarket.modules.dynamic;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseAdapter;
import asuper.yt.cn.supermarket.base.BaseFragment;
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
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by zengxiaowen on 2017/9/13.
 */

public class DynamicFragment extends BaseFragment implements MainActivity.MainFragment {

    private SwipeToLoadLayout refreshLayout;
    private RecyclerView swipe_target;
    private DropRecordAdapter adapter;

    @Override
    protected int getContentId() {
        return R.layout.fragment_dynamic;
    }

    @Override
    protected void findView(View root) {
        initView(root);
        updateAll();
    }

    private void updateAll() {
        dispatch(DynamicStore.REQUEST_GET_DYNAMIC_LIST, new HashMap<String, Object>());
    }

    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_LIST)
    public void getDynamicList(JSONObject json) {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
        Dynamic dyn = ToolGson.fromJson(json.toString(), Dynamic.class);
//        adapter.addAllItems(dyn.getRows().getRows());
    }

//    @BindAction(DynamicStore.REQUEST_GET_DYNAMIC_ISNOTICE)
//    public void getDynamicIsnotice(JSONObject json) {
//        adapter.clear();
//        updateAll();
//    }

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
        refreshLayout.setLoadMoreEnabled(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                updateAll();
                EventBus.getDefault().post(true);
            }
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new DynamicStore(dependencyDelegate);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }


    private class DropRecordAdapter extends EmptyViewRecyclerAdapter {

        private Map<String, List<Dynamic.Item>> mapItem = new HashMap<>();
        private List<String> keys = new ArrayList<>();

        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setTag(keys.get(position));
            ((ViewHolder) holder).setDateView(mapItem.get(keys.get(position)));
        }

        @Override
        public int GetItemCount() {
            return keys.size();
        }

        public void addAllItems(Map<String, List<Dynamic.Item>> mapItem) {
            if (mapItem != null) {
                for (String key :mapItem.keySet()){
                    this.keys.add(key);
                }
                Collections.sort(this.keys, new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return DateUtils.isDateOneBigger(o1,o2)?-1:1;
                    }
                });
                this.mapItem = mapItem;
            }else{
                this.mapItem = new HashMap<>();
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
//                    if (item.isNotice()) {
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("shopId", item.getShopId());
//                        dispatch(DynamicStore.REQUEST_GET_DYNAMIC_ISNOTICE, map);
//                    }
//                        getOperation().addParameter("shopId",item.getShopId());
                    Clit clit = new Clit();
                    clit.id = item.getShopId();
                    EventBus.getDefault().postSticky(clit);
                    getOperation().forward(MyClientDetailActivity.class);

                }
            });
        }

        public void setDateView(List<Dynamic.Item> item) {
            adapter = new DynamicAdapter();
            adapter.addItem(item);
            tvThem.setText(itemView.getTag().toString());
            dyList.setAdapter(adapter);

        }

        private class DynamicAdapter extends BaseAdapter {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHodler hodler =null;
                if (convertView ==null ){
                    hodler = new ViewHodler();
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_dynamic_list_item,parent,false);
                    hodler.tvName = (TextView) convertView.findViewById(R.id.tvName);
                    hodler.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                    hodler.igTy = (ImageView) convertView.findViewById(R.id.igTy);
                    convertView.setTag(hodler);
                }else{
                    hodler = (ViewHodler) convertView.getTag();
                }
                Dynamic.Item item = (Dynamic.Item) getItem(position);
//                hodler.igTy.setImageResource(item.getDisplayStatus() == 0 ? R.mipmap.ic_dynamic1 : R.mipmap.ic_dynamic2);
//                hodler.tvName.setText(item.getApplyStep() + "-" + item.getShopName());
                hodler.tvTime.setText(item.getDetailTime());
                return convertView;
            }

            private class ViewHodler {

                private TextView  tvName, tvTime;
                private ImageView igTy;

            }
        }
    }
}
