package asuper.yt.cn.supermarket.modules.recyclebin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.common.ClienteleAdapter;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.myclient.MyClientListActivity;
import asuper.yt.cn.supermarket.modules.myclient.entities.MyClient;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class ClientRecycleActivity extends BaseActivity {

    private RecyclerView noIntentionList,giveUpList;
    private SwipeToLoadLayout[] containers = new SwipeToLoadLayout[]{
            (SwipeToLoadLayout) LayoutInflater.from(YTApplication.get()).inflate(R.layout.layout_simple_swipe_refresh_list,null),
            (SwipeToLoadLayout) LayoutInflater.from(YTApplication.get()).inflate(R.layout.layout_simple_swipe_refresh_list,null)
    };

    private ViewPager pager;

    public static final int REQUEST_GET_NOINTENTION = 0x0201;
    public static final int REQUEST_GET_GIVEUP = 0x0202;
    public static final int REQUEST_GET_NUM = 0x0203;

    private int nointentionPagerNumber=1,giveupPagerNumber=1,pageSize = 10;
    private boolean nointentionFirstPage = true,giveupFirstPage = true;
    private final String step = "step_0";

    private RadioButton noIntention,giveUp;
    private TextView noIntentionCount,giveUpCount;

    @Override
    protected int getContentId() {
        return R.layout.activity_client_recycle;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"客户回收站");
        containers[0].setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                nointentionFirstPage = true;
                nointentionPagerNumber = 1;
                getNoIntention();
            }
        });
        containers[1].setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                giveupFirstPage = true;
                giveupPagerNumber = 1;
                getGiveup();
            }
        });
        containers[0].setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getNoIntention();
            }
        });
        containers[1].setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getGiveup();
            }
        });
        noIntentionList = (RecyclerView) containers[0].findViewById(R.id.swipe_target);
        giveUpList = (RecyclerView) containers[1].findViewById(R.id.swipe_target);
        noIntention = (RadioButton) root.findViewById(R.id.client_list_first);
        noIntention.setChecked(true);
        giveUp = (RadioButton) root.findViewById(R.id.client_list_second);
        noIntention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveUp.setChecked(false);
                pager.setCurrentItem(0,true);
            }
        });
        giveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noIntention.setChecked(false);
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
                noIntention.setChecked(position==0?true:false);
                giveUp.setChecked(!noIntention.isChecked());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        noIntentionList.setLayoutManager(new SafetyLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }
        });
        giveUpList.setLayoutManager(new SafetyLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }
        });
        Bundle bundle = new Bundle();
        bundle.putBoolean("isGiveup",true);
        bundle.putBoolean("isUpdate",false);
        noIntentionList.setAdapter(new ClienteleAdapter(this,bundle));
        giveUpList.setAdapter(new ClienteleAdapter(this,bundle));


        noIntentionCount = (TextView) root.findViewById(R.id.client_list_first_num);
        giveUpCount = (TextView) root.findViewById(R.id.client_list_second_num);
        showProgress();
        getGiveup();
        getNoIntention();
        dispatch(REQUEST_GET_NUM,null);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new ClientRecycleStore(dependencyDelegate);
    }

    private void getNoIntention() {
        Map<String, Object> map = new HashMap<>();
        map.put("applyAssignOwner", Config.UserInfo.USER_ID);
        map.put("pageNum", nointentionPagerNumber);
        map.put("pageSize", pageSize);
        dispatch(REQUEST_GET_NOINTENTION, map);
    }

    private void getGiveup(){
        Map<String, Object> map = new HashMap<>();
        map.put("applyAssignOwner", Config.UserInfo.USER_ID);
        map.put("pageNum", giveupPagerNumber);
        map.put("pageSize", pageSize);
        dispatch(REQUEST_GET_GIVEUP, map);
    }

    @BindAction(REQUEST_GET_NOINTENTION)
    public void getNoIntentionResult(MyClient clienteles){
        dismissProgress();
        containers[0].setRefreshing(false);
        containers[0].setLoadingMore(false);
        if (clienteles == null || clienteles.getRows() == null) return;
        if (nointentionFirstPage) ((ClienteleAdapter)noIntentionList.getAdapter()).clear();
        nointentionFirstPage = false;
        ((ClienteleAdapter)noIntentionList.getAdapter()).addItems(clienteles.getRows());
        if (clienteles.getRows().size() < pageSize) {
            containers[0].setLoadMoreEnabled(false);
        } else {
            containers[0].setLoadMoreEnabled(true);
            nointentionPagerNumber++;
        }
    }

    @BindAction(REQUEST_GET_GIVEUP)
    public void getGiveUpResult(MyClient clienteles){
        containers[1].setRefreshing(false);
        containers[1].setLoadingMore(false);
        if (clienteles == null || clienteles.getRows() == null) return;
        if (giveupFirstPage) ((ClienteleAdapter)giveUpList.getAdapter()).clear();
        giveupFirstPage = false;
        ((ClienteleAdapter)giveUpList.getAdapter()).addItems(clienteles.getRows());
        if (clienteles.getRows().size() < pageSize) {
            containers[1].setLoadMoreEnabled(false);
        } else {
            containers[1].setLoadMoreEnabled(true);
            giveupPagerNumber++;
        }
    }

    @BindAction(REQUEST_GET_NUM)
    public void getNumResult(int[] res){
        if(res.length > 0 && res[0]>0){
            noIntentionCount.setText(res[0]+"");
            noIntentionCount.setVisibility(View.VISIBLE);
        }
        if(res.length > 1&& res[1]>0){
            giveUpCount.setText(res[1]+"");
            giveUpCount.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100) {
            if (pager.getCurrentItem()==0) {
                nointentionFirstPage = true;
                nointentionPagerNumber = 1;
                getNoIntention();
            }else{
                giveupFirstPage = true;
                giveupPagerNumber = 1;
                getGiveup();
            }
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
}
