package asuper.yt.cn.supermarket.modules.myclew;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.common.ClienteleAdapter;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientListActivity;
import asuper.yt.cn.supermarket.modules.myclient.ToolGetButtoninfos;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.modules.newclient.AddNewClientActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.DensityUtil;
import asuper.yt.cn.supermarket.views.SlantTextView;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class MyClewActivity extends BaseActivity {

    private RecyclerView missionList,uncompleteList;
    private SwipeToLoadLayout[] containers = new SwipeToLoadLayout[]{
            (SwipeToLoadLayout) LayoutInflater.from(YTApplication.get()).inflate(R.layout.layout_simple_swipe_refresh_list,null),
            (SwipeToLoadLayout) LayoutInflater.from(YTApplication.get()).inflate(R.layout.layout_simple_swipe_refresh_list,null)
    };

    private ViewPager pager;

    public static final int REQUEST_GET_MISSION = 0x0201;
    public static final int REQUEST_GET_UNCOMPLETE = 0x0202;
    public static final int REQUEST_GET_COUNT = 0x0203;

    private int missionPagerNumber=1,uncompletePagerNumber=1,pageSize = 10;
    private boolean missionFirstPage = true,uncompleteFirstPage = true;
    private final String step = "step_2";

    private RadioButton newMisson,unComplete;
    private TextView newMissionCount,unCompleteCount;

    @Override
    protected int getContentId() {
        return R.layout.activity_client_recycle;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"我的线索");
        containers[0].setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                missionFirstPage = true;
                missionPagerNumber = 1;
                getmission();
            }
        });
        containers[1].setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                uncompleteFirstPage = true;
                uncompletePagerNumber = 1;
                getuncomplete();
            }
        });
        containers[0].setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getmission();
            }
        });
        containers[1].setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getuncomplete();
            }
        });
        missionList = (RecyclerView) containers[0].findViewById(R.id.swipe_target);
        uncompleteList = (RecyclerView) containers[1].findViewById(R.id.swipe_target);
        newMisson = (RadioButton) root.findViewById(R.id.client_list_first);
        newMissionCount = (TextView) root.findViewById(R.id.client_list_first_num);
        newMisson.setChecked(true);
        unComplete = (RadioButton) root.findViewById(R.id.client_list_second);
        unCompleteCount = (TextView) root.findViewById(R.id.client_list_second_num);
        newMisson.setText("任务指派");
        Drawable newMissondrawable = getResources().getDrawable(R.drawable.slct_ic_newmission);
        Drawable unCompletedrawable = getResources().getDrawable(R.drawable.slct_ic_uncomlete);
        newMissondrawable.setBounds(0,0,newMissondrawable.getIntrinsicWidth(),newMissondrawable.getIntrinsicHeight());
        unCompletedrawable.setBounds(0,0,unCompletedrawable.getIntrinsicWidth(),unCompletedrawable.getIntrinsicHeight());
        newMisson.setCompoundDrawables(null,newMissondrawable,null,null);
        unComplete.setCompoundDrawables(null,unCompletedrawable,null,null);
        unComplete.setText("资料未完善");
        unComplete = (RadioButton) root.findViewById(R.id.client_list_second);
        newMisson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unComplete.setChecked(false);
                pager.setCurrentItem(0,true);
            }
        });
        unComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newMisson.setChecked(false);
                pager.setCurrentItem(1,true);
            }
        });
        pager = (ViewPager) root.findViewById(R.id.client_recycle_pager);
        pager.setAdapter(new PagerAdapter());
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                newMisson.setChecked(position==0?true:false);
                unComplete.setChecked(!newMisson.isChecked());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        missionList.setLayoutManager(new SafetyLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(-1,-2);
                int dp10 = DensityUtil.dip2px(MyClewActivity.this,10);
                params.setMargins(dp10,dp10/2,dp10,dp10/2);
                return params;
            }
        });
        uncompleteList.setLayoutManager(new SafetyLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }
        });
        missionList.setAdapter(new NewMissionAdapter());
        uncompleteList.setAdapter(new ClienteleAdapter());
        showProgress();
        getMissionCount();
        getuncomplete();
        getmission();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new MyClewStore(dependencyDelegate);
    }

    private void getmission() {
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("pageNum", missionPagerNumber);
        map.put("pageSize", pageSize);
        map.put("applySteup", step);
        dispatch(REQUEST_GET_MISSION, map);
    }

    private void getuncomplete(){
        Map<String, Object> map = new HashMap<>();
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("pageNum", uncompletePagerNumber);
        map.put("pageSize", pageSize);
        map.put("applySteup", step);
        dispatch(REQUEST_GET_UNCOMPLETE, map);
    }

    private void getMissionCount(){
        Map<String,Object> params = new HashMap<>();
        params.put("employeeId", Config.UserInfo.USER_ID);
        dispatch(REQUEST_GET_COUNT,params);
    }

    @BindAction(REQUEST_GET_MISSION)
    public void getmissionResult(List<NewMission> newMissions){
        dismissProgress();
        containers[0].setRefreshing(false);
        containers[0].setLoadingMore(false);
        if (newMissions == null) return;
        if (missionFirstPage) ((NewMissionAdapter)missionList.getAdapter()).clear();
        missionFirstPage = false;
        ((NewMissionAdapter)missionList.getAdapter()).addItems(newMissions);
        if (newMissions.size() < pageSize) {
            containers[0].setLoadMoreEnabled(false);
        } else {
            containers[0].setLoadMoreEnabled(true);
            missionPagerNumber++;
        }
    }

    @BindAction(REQUEST_GET_UNCOMPLETE)
    public void getuncompleteResult(List<ClientInfoDetail> clienteles){
        containers[1].setRefreshing(false);
        containers[1].setLoadingMore(false);
        if (clienteles == null ) return;
        if (uncompleteFirstPage) ((ClienteleAdapter)uncompleteList.getAdapter()).clear();
        uncompleteFirstPage = false;
        ((ClienteleAdapter)uncompleteList.getAdapter()).addItems(clienteles);
        if (clienteles.size() < pageSize) {
            containers[1].setLoadMoreEnabled(false);
        } else {
            containers[1].setLoadMoreEnabled(true);
            uncompletePagerNumber++;
        }
    }

    @BindAction(REQUEST_GET_COUNT)
    public void getMissionCountResult(int[] count){
        if(count == null) return;
        int i = 0,j = 0;
        if(count.length >= 1) i = count[0];
        if(count.length >= 2) j = count[1];
        if(i > 0){
            newMissionCount.setText(i+"");
            newMissionCount.setVisibility(View.VISIBLE);
        }
        if(j > 0){
            unCompleteCount.setText(j+"");
            unCompleteCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 101){
            uncompleteFirstPage = true;
            if (missionList.getAdapter().getItemCount()<10)missionFirstPage = true;
            showProgress();
            getMissionCount();
            getuncomplete();
            getmission();
        }
        if(resultCode == 100){
            finish();
        }
    }

    private class PagerAdapter extends android.support.v4.view.PagerAdapter{

        @Override
        public int getCount() {
            return containers.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(containers[position]);
            return containers[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class NewMissionAdapter extends EmptyViewRecyclerAdapter {

        private List<NewMission> clits = new ArrayList<>();

        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_new_mission, null));
            return viewHolder;
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder)holder).setData(clits.get(position));
        }

        @Override
        public int GetItemCount() {
            return clits.size();
        }

        public void addItems(List<NewMission> clits) {
            this.clits.addAll(clits);
            notifyDataSetChanged();
        }

        public void clear() {
            clits.clear();
            notifyDataSetChanged();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name,date;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewMission p = (NewMission) v.getTag();
                    getOperation().addParameter("id",p.id);
                    getOperation().forwardForResult(NewMissionDetailActivity.class,100);

                }
            });
            name = (TextView) itemView.findViewById(R.id.item_new_mission_title);
            date = (TextView) itemView.findViewById(R.id.item_new_mission_date);
        }

        public void setData(NewMission newMission) {
            itemView.setTag(newMission);
            String title = String.format("%1$s  给您分配了  %2$s，请跟进",newMission.updaterName,newMission.shopkeeperName);
            String dateS = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(newMission.franchiseeTime));
            name.setText(title);
            date.setText(dateS);
        }
    }


    public class ClienteleAdapter extends EmptyViewRecyclerAdapter {

        private List<ClientInfoDetail> clits = new ArrayList<>();


        @Override
        public int OnEmptyView() {
            return 0;
        }

        @Override
        public ClientViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
            ClientViewHolder viewHolder = new ClientViewHolder(LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_shop_card, null));
            return viewHolder;
        }

        @Override
        public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ClientViewHolder)holder).setData(clits.get(position));
        }

        @Override
        public int GetItemCount() {
            return clits.size();
        }

        public void addItems(List<ClientInfoDetail> clits) {
            this.clits.addAll(clits);
            notifyDataSetChanged();
        }

        public void clear() {
            clits.clear();
            notifyDataSetChanged();
        }
    }

    private class ClientViewHolder extends RecyclerView.ViewHolder {

        private TextView name, shopName, address, phone, date, status;
        private SlantTextView local;
        private View dateLine;
        private CheckBox checkBox;
        private boolean isSelectable =false;
        public ClientViewHolder(View itemView) {
            this(itemView,false);
        }
        public ClientViewHolder(View itemView,boolean selectable) {
            super(itemView);
            isSelectable = selectable;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClientInfoDetail p = (ClientInfoDetail) v.getTag();
                        getOperation().addParameter("client",p);
                        getOperation().addParameter("isNew",false);
                        getOperation().forwardForResult(AddNewClientActivity.class,100);

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
            if(isSelectable){
                checkBox.setVisibility(View.VISIBLE);
            }
        }

        public void setData(ClientInfoDetail clit) {
            itemView.setTag(clit);
            name.setText(clit.getLegalpersonName());
            shopName.setText(clit.getShopName());
            address.setText(clit.getShopAddree());
            phone.setText(clit.getPhoneNumber());
            date.setText(clit.getLastTime());
            date.setVisibility(View.GONE);
            status.setText("新增客户");
            status.setVisibility(View.VISIBLE);
            local.setVisibility(View.VISIBLE);
            local.setText("客户资料未完善");
            int[] res = ToolGetButtoninfos.getIconAndBackground(clit.getApplyStep());
            status.setBackgroundResource(res[1]);
            Drawable drawable = shopName.getResources().getDrawable(res[0]);
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            status.setCompoundDrawables(drawable,null,null,null);
        }


    }
}
