package asuper.yt.cn.supermarket.modules.plan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.index.GestureListener;
import asuper.yt.cn.supermarket.modules.index.IndexFragment;
import asuper.yt.cn.supermarket.modules.index.IndexGridAdapter;
import asuper.yt.cn.supermarket.modules.index.IndexLvAdapter;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.PinyinComparator;
import asuper.yt.cn.supermarket.utils.ToolLog;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * 我的计划
 * Created by zengxiaowen on 2017/9/7.
 */

public class PlanActivity extends BaseActivity {

    private TextView tvCalendar;
    private ViewFlipper flipper;  //日历控件
    private GestureDetector gestureDetector = null;
    private GridView gridView = null;
    private IndexGridAdapter adapter;
    private int selectPostion = 0;

    private RecyclerView listPlan;
    private PlanLvAdapter lPadapter;

    private boolean isNew = true;


    @Override
    protected int getContentId() {
        return R.layout.activity_plan;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initTitleToolbar(this, "");
        showProgress();
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
        adapter = new IndexGridAdapter();
        adapter.setActivity(1);
        addGridView();
        gridView.setAdapter(adapter);
        selectPostion = adapter.getTodayPosition();
        gridView.setSelection(selectPostion);
        flipper.addView(gridView, 0);

        updateAll();
    }

    private void initView(View root) {
        tvCalendar = (TextView) root.findViewById(R.id.tvCalendar);
        flipper = (ViewFlipper) root.findViewById(R.id.flipper);
        root.findViewById(R.id.rectView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOperation().addParameter("time", adapter.getAllDay().get(selectPostion));
                getOperation().forwardForResult(AddPlanActivity.class, PlanStore.REQUEST_GET_PLAN_INSERT);
            }
        });
        listPlan = (RecyclerView) root.findViewById(R.id.swipe_target);

        listPlan.setLayoutManager(new SafetyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1, -2);
            }
        });
        tvCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void updateAll() {
        // 请求每日拜访数
        Map<String, Object> map = new HashMap<>();
        map.put("listTime", ListtoString(adapter.getAllDay()));
        dispatch(PlanStore.REQUEST_GET_INDEX_WEEKPLANCOUNT, map);
    }

    private String ListtoString(List<String> strs) {
        String str = strs.get(0);
        for (int i = 1; i < strs.size(); i++) {
            str = str + "," + strs.get(i);
        }
        return str;
    }

    private void updateList() {
        if (isNew) {
            selectPostion = adapter.getToDay();
            ToolLog.i("第一次:" + selectPostion);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("createTime", adapter.getAllDay().get(selectPostion));
        dispatch(PlanStore.REQUEST_GET_PLAN_LIST, map);
    }


    @BindAction(PlanStore.REQUEST_GET_PLAN_RE)
    public void setRePlan(JSONObject jsonObject) {
        if (jsonObject == null)
            return;
        if (jsonObject.optBoolean("success", false)) {
            ToastUtil.success("操作成功");
            setResult(PlanStore.REQUEST_GET_PLAN_RE);
            updateAll();
        }
    }

    @BindAction(PlanStore.REQUEST_GET_PLAN_LIST)
    public void initList(JSONObject jsonObject) {
        dismissProgress();
        if (jsonObject == null)
            return;
        Plan plan = ToolGson.fromJson(jsonObject.toString(), Plan.class);

        if (isNew) {
            selectPostion = adapter.getToDay();
            adapter.setSeclection(adapter.getToDay());
            adapter.notifyDataSetChanged();
            isNew = false;
            tvCalendar.setText(adapter.getCurrentYear(selectPostion) + "年"
                    + adapter.getCurrentMonth(selectPostion) + "月");

        }

        List<Plan.PlanItem> plans = plan.getResultObject();
        Collections.sort(plans, new PinyinComparator());
        lPadapter = new PlanLvAdapter(getContext());
        lPadapter.addItem(plans);
        listPlan.setAdapter(lPadapter);
        lPadapter.setTimeParker(new IndexLvAdapter.TimeParker() {
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

    @BindAction(PlanStore.REQUEST_GET_INDEX_WEEKPLANCOUNT)
    public void initWeek(JSONObject jsonObject) {
        if (jsonObject == null){
            dismissProgress();
            return;
        }
        IndexFragment.WeekNum weel = ToolGson.fromJson(jsonObject.toString(), IndexFragment.WeekNum.class);
          if(weel != null &&  weel.getResultObject() != null){
              List<Integer> list = new ArrayList<>();
              for (String key : weel.getResultObject().keySet()) {
                  list.add(weel.getResultObject().get(key));
              }
              adapter.clear();
              adapter.addItem(list);
              adapter.notifyDataSetChanged();
              updateList();
          }else {
              dismissProgress();
              return;
          }
    }


    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PlanStore(dependencyDelegate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.actionbar_menu, menu);

        ActionBarManager.initActionBarSubmitButton(menu, ActionBarManager.TOOLBARBTN.PLAN);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        getOperation().forward(DropRecordActivity.class);
        return super.onOptionsItemSelected(item);
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
                tvCalendar.setText(adapter.getCurrentYear(selectPostion) + "年"
                        + adapter.getCurrentMonth(selectPostion) + "月");

                updateList();
            }
        });
        gridView.setLayoutParams(params);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PlanStore.REQUEST_GET_PLAN_UPDATE:
            case PlanStore.REQUEST_GET_PLAN_INSERT:
                updateAll();
                break;
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
        adapter.setActivity(1);
        adapter.setSeclection(selectPostion);
        adapter.setNumDay(numDay);
        gridView.setAdapter(adapter);
        tvCalendar.setText(adapter.getCurrentYear(selectPostion) + "年"
                + adapter.getCurrentMonth(selectPostion) + "月");

        gvFlag++;
        flipper.addView(gridView, gvFlag);
        if (isLift) {
            flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.push_left_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.push_left_out));
            flipper.showNext();
        } else {
            flipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.push_right_in));
            flipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.push_right_out));
            flipper.showPrevious();
        }
        flipper.removeViewAt(0);

        updateAll();
    }
}
