package asuper.yt.cn.supermarket.modules.index;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.index.cardview.CardItem;
import asuper.yt.cn.supermarket.modules.index.cardview.CardPagerAdapter;
import asuper.yt.cn.supermarket.modules.index.cardview.ShadowTransformer;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclew.MyClewActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientListActivity;
import asuper.yt.cn.supermarket.modules.plan.Plan;
import asuper.yt.cn.supermarket.modules.plan.PlanActivity;
import asuper.yt.cn.supermarket.modules.plan.PlanStore;
import asuper.yt.cn.supermarket.modules.recyclebin.ClientRecycleActivity;
import asuper.yt.cn.supermarket.utils.DateUtils;
import asuper.yt.cn.supermarket.utils.Lauar;
import asuper.yt.cn.supermarket.utils.PinyinComparator;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.views.CustomViewPager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.bingoogolapple.badgeview.BGABadgeImageView;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by zengxiaowen on 2017/9/6.
 */

public class IndexFragment extends BaseFragment implements MainActivity.MainFragment, View.OnClickListener {

    private SwipeRefreshLayout swipe_layout;
    private TextView tvCalendar1; //阳历日期
    private TextView tvCalendar2; //农历日期
    private ViewFlipper flipper;  //日历控件
    private GestureDetector gestureDetector = null;
    private GridView gridView = null;
    private IndexGridAdapter adapter;
    private View vClew;

    private int selectPostion = 0;

    // 折线图
    private CustomViewPager mViewPager;
    private RadioGroup radioGroup;
    private ShadowTransformer mCardShadowTransformer;
    private CardPagerAdapter mCardAdapter;
    private List<Integer> list_All_NO = new ArrayList<>();//每日拜访数
    private List<Integer> list_Add_NO = new ArrayList<>();//每日新增客户数

    //
    private RecyclerView indexLv;
    private IndexLvAdapter lvAdapter;

    private boolean isNew = true;

    private Toolbar mToolbar;
    private BGABadgeImageView imgXian,imgKehu,imgJihua,imgShous;


    @Override
    protected int getContentId() {
        return R.layout.fragment_indexs;
    }

    @Override
    protected void findView(View root) {
        initView(root);
        gestureDetector = new GestureDetector(new GestureListener(new GestureListener.OnTuoListener() {
            @Override
            public void onLift() {
                onTuoLisen(true);
            }

            @Override
            public void onRight() {
                onTuoLisen(false);
            }
        }));
        addGridView();
        adapter = new IndexGridAdapter();
        gridView.setAdapter(adapter);
        flipper.addView(gridView, 0);
    }

    private void updateAll() {

        //每日计划数
        Map<String, Object> map = new HashMap<>();
        map.put("listTime", ListtoString(adapter.getAllDay()));
        dispatch(PlanStore.REQUEST_GET_INDEX_WEEKPLANCOUNT, map);

    }

    private void getMissionCount(){
        Map<String,Object> params = new HashMap<>();
        params.put("employeeId", Config.UserInfo.USER_ID);
        dispatch(MyClewActivity.REQUEST_GET_COUNT,params);
    }

    @BindAction(MyClewActivity.REQUEST_GET_COUNT)
    public void getMissionCountResult(int[] count){
        if(count == null) return;
        int i = 0,j = 0;
        if(count.length >= 1) i = count[0];
        if(count.length >= 2) j = count[1];
        if (i>0||j>0){
            imgXian.showCirclePointBadge();
            vClew.setVisibility(View.VISIBLE);
        }else{
            imgXian.hiddenBadge();
            vClew.setVisibility(View.GONE);
        }

        updateList();
    }

    @BindAction(PlanStore.REQUEST_GET_INDEX_WEEKPLANCOUNT)
    public void initWeek(JSONObject jsonObject) {
        if (swipe_layout.isRefreshing()) swipe_layout.setRefreshing(false);
        WeekNum weel = ToolGson.fromJson(jsonObject.toString(), WeekNum.class);
        Map<String,Integer> result = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        result.putAll(weel.getResultObject());

        List<Integer> list = new ArrayList<>();
        for (String key : result.keySet()) {
            list.add(result.get(key));
        }
        adapter.clear();
        adapter.addItem(list);
        adapter.notifyDataSetChanged();


        // 请求每日新增客户数
        Map<String, Object> map = new HashMap<>();
        map.put("listTime", ListtoString(adapter.getAllDay()));
        dispatch(PlanStore.REQUEST_GET_INDEX_NEWCUSTOMERCOUNT, map);
    }



    private void updateList() {
        Map<String, Object> map = new HashMap<>();
        map.put("createTime", adapter.getAllDay().get(selectPostion));
        dispatch(PlanStore.REQUEST_GET_PLAN_LIST, map);
    }

    private String ListtoString(List<String> strs) {
        String str = strs.get(0);
        for (int i = 1; i < strs.size(); i++) {
            str = str + "," + strs.get(i);
        }
        return str;
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_RE)
    public void setRePlan(JSONObject jsonObject) {
        if (jsonObject.optBoolean("success", false)) {
            ToastUtil.success("操作成功");
            updateAll();
        }
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_LIST)
    public void initListPlan(JSONObject jsonObject) {
        dissmissProgress();
        Plan plan = ToolGson.fromJson(jsonObject.toString(), Plan.class);
        List<Plan.PlanItem> plans = plan.getResultObject();
        Collections.sort(plans, new PinyinComparator());
        lvAdapter = new IndexLvAdapter(getActivity());
        lvAdapter.addItem(plans);
        indexLv.setAdapter(lvAdapter);
        lvAdapter.setTimeParker(new IndexLvAdapter.TimeParker() {
            @Override
            public void hander(int id, String time) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("createTime", time);
                dispatch(PlanStore.REQUEST_GET_PLAN_RE, map);
            }
        });
        flipper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, flipper.getHeight()));


    }

    @BindAction(PlanStore.REQUEST_GET_INDEX_WEEKVISITCOUNT)
    public void initWeekVisit(JSONObject jsonObject) {
        WeekNum weel = ToolGson.fromJson(jsonObject.toString(), WeekNum.class);
        /**
         *  Modified by dengjiang on 2017/10/09
         *  CRM-CRM-1478
         *  更新每日拜访数
         *  start
         */
        list_All_NO.clear();
        for (String key : weel.getResultObject().keySet()) {
            list_All_NO.add(weel.getResultObject().get(key));
        }
        /**
         *  Modified by dengjiang on 2017/10/09
         *  end
         */
        mCardAdapter.notifyDataSetChanged();

        getMissionCount();

    }

    @BindAction(PlanStore.REQUEST_GET_INDEX_NEWCUSTOMERCOUNT)
    public void initNewCust(JSONObject jsonObject) {
        WeekNum weel = ToolGson.fromJson(jsonObject.toString(), WeekNum.class);
        /**
         *  Modified by dengjiang on 2017/10/09
         *  CRM-CRM-1478
         *  更新每日新增客户数
         *  start
         */
        list_Add_NO.clear();
        for (String key : weel.getResultObject().keySet()) {
            list_Add_NO.add(weel.getResultObject().get(key));
        }
        /**
         *  Modified by dengjiang on 2017/10/09
         *  end
         */
        mCardAdapter.notifyDataSetChanged();

        if (isNew) {
            selectPostion = adapter.getToDay();
            adapter.setSeclection(selectPostion);
            adapter.notifyDataSetChanged();
            tvCalendar1.setText(adapter.getCurrentYear(selectPostion) + "年" + adapter.getCurrentMonth(selectPostion) + "月");
            String days = adapter.getChinaDay(selectPostion);
            tvCalendar2.setText(days);
            isNew = false;
        }

        // 请求每日拜访数
        Map<String, Object> map = new HashMap<>();
        map.put("listTime", ListtoString(adapter.getAllDay()));
        dispatch(PlanStore.REQUEST_GET_INDEX_WEEKVISITCOUNT, map);
    }

    private void initView(View root) {
        root.findViewById(R.id.btnUser).setOnClickListener(this);
        root.findViewById(R.id.btnPlan).setOnClickListener(this);
        root.findViewById(R.id.btnClew).setOnClickListener(this);
        root.findViewById(R.id.btnRecycle).setOnClickListener(this);
        swipe_layout = root.findViewById(R.id.swipe_layout);
        swipe_layout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent,R.color.yt_red);
        (imgXian = root.findViewById(R.id.toolbar_xians)).setOnClickListener(this);
        (imgKehu =  root.findViewById(R.id.toolbar_kehu)).setOnClickListener(this);
        (imgJihua =  root.findViewById(R.id.toolbar_jihua)).setOnClickListener(this);
        (imgShous = root.findViewById(R.id.toolbar_huis)).setOnClickListener(this);
        ((TextView) root.findViewById(R.id.tvUserName)).setText(Config.UserInfo.NAME);
        (vClew = root.findViewById(R.id.vClew)).setVisibility(View.GONE);
        tvCalendar1 = root.findViewById(R.id.tvCalendar1);
        tvCalendar2 =  root.findViewById(R.id.tvCalendar2);
        flipper = root.findViewById(R.id.flipper);
        mViewPager = root.findViewById(R.id.viewPager);
        radioGroup =  root.findViewById(R.id.radioGroup);
        mToolbar =  root.findViewById(R.id.tool_bar);
        indexLv = root.findViewById(R.id.swipe_target);

        indexLv.setLayoutManager(new SafetyLinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }
        });

        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAll();
            }
        });

        ((AppBarLayout) root.findViewById(R.id.main_abl_app_bar)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mToolbar.setBackgroundColor(changeAlpha(getResources().getColor(R.color.colorPrimary),Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()));
                if (verticalOffset>-50){
                    imgXian.setEnabled(false);
                    imgJihua.setEnabled(false);
                    imgKehu.setEnabled(false);
                    imgShous.setEnabled(false);
                    swipe_layout.setEnabled(true);
                }else{
                    imgXian.setEnabled(true);
                    imgJihua.setEnabled(true);
                    imgKehu.setEnabled(true);
                    imgShous.setEnabled(true);
                    if (!swipe_layout.isRefreshing()) swipe_layout.setEnabled(false);
                }
                imgXian.setAlpha(Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange());
                imgJihua.setAlpha(Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange());
                imgKehu.setAlpha(Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange());
                imgShous.setAlpha(Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange());
            }
        });
        initPageView();

    }


    /** 根据百分比改变颜色透明度 */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    private void initPageView() {
        mCardAdapter = new CardPagerAdapter(getContext());
        /**
         *  Modified by dengjiang on 2017/10/09
         *  CRM-CRM-1478
         *  初始化折线图
         *  start
         */
        mCardAdapter.addCardItem(new CardItem("每日新增客户数", list_Add_NO));
        mCardAdapter.addCardItem(new CardItem("每日拜访数", list_All_NO));
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        // 选中放大
//        mCardShadowTransformer.enableScaling(true);//没发现有什么作用 注释掉
        /**
         *  Modified by dengjiang on 2017/10/09
         *  end
         */
        initRadioGroup();

    }

    private void initRadioGroup() {
        for (int i = 0; i < 2; i++) {
            RadioButton raBut = new RadioButton(getContext());
            raBut.setButtonDrawable(R.drawable.checkbox_pageview);
            raBut.setPadding(5, 0, 5, 0);
            raBut.setId(i);
            raBut.setEnabled(false);
            radioGroup.addView(raBut);
        }
        radioGroup.check(0);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioGroup.check(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PlanStore(dependencyDelegate);
    }

    /**
     * 添加日历视图
     */
    private void addGridView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        gridView = new GridView(getContext());
        gridView.setNumColumns(7);
        gridView.setGravity(Gravity.CENTER_VERTICAL);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setVerticalSpacing(1);
        gridView.setHorizontalSpacing(1);
        gridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectPostion = position;
                adapter.setSeclection(position);
                adapter.notifyDataSetChanged();
                tvCalendar1.setText(adapter.getCurrentYear(selectPostion) + "年"
                        + adapter.getCurrentMonth(selectPostion) + "月");
                String days = adapter.getChinaDay();
                tvCalendar2.setText(days);

                updateList();
            }
        });
        gridView.setLayoutParams(params);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_kehu:
            case R.id.btnUser:  // 我的客户
                getOperation().forward(MyClientListActivity.class);
                break;
            case R.id.toolbar_jihua:
            case R.id.btnPlan:  // 我的计划
                getOperation().forward(PlanActivity.class);
                break;
            case R.id.toolbar_xians:
            case R.id.btnClew:  //我的线索
                getOperation().forward(MyClewActivity.class);
                break;
            case R.id.toolbar_huis:
            case R.id.btnRecycle:  // 客户回收站
                getOperation().forward(ClientRecycleActivity.class);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         *  Modified by dengjiang on 2017/10/09
         *  CRM-CRM-1478
         *  重新进入页面的时候折线图跳转到第一个item
         *  start
         */
        mViewPager.setCurrentItem(0);
        /**
         *  Modified by dengjiang on 2017/10/09
         *  end
         */
        updateAll();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {
//        updateAll();
    }


    public class WeekNum {
        private Map<String, Integer> resultObject;

        public Map<String, Integer> getResultObject() {
            return resultObject;
        }

        public void setResultObject(Map<String, Integer> resultObject) {
            this.resultObject = resultObject;
        }
    }

    private void onTuoLisen(boolean isLift) {
        showProgress();
        int gvFlag = 0;
        selectPostion = 6;
        addGridView();
        int numDay = adapter.getNumDay();
        if (isLift) {// 向左滑
            numDay = numDay + 7;
        } else {
            numDay = numDay - 7;
        }
        adapter = new IndexGridAdapter();
        adapter.setSeclection(selectPostion);
        adapter.setNumDay(numDay);
        gridView.setAdapter(adapter);
        tvCalendar1.setText(adapter.getCurrentYear(selectPostion) + "年"
                + adapter.getCurrentMonth(selectPostion) + "月");
        String days = adapter.getChinaDay();
        tvCalendar2.setText(days);

        gvFlag++;
        flipper.addView(gridView, gvFlag);
        if (isLift) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.push_left_out));
            flipper.showNext();
        } else {
            flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.push_right_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(),
                    R.anim.push_right_out));
            flipper.showPrevious();
        }
        flipper.removeViewAt(0);

        updateAll();
    }
}
