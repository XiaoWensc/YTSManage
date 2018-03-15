package asuper.yt.cn.supermarket.modules.myclient;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclient.contact.ContractFragment;
import asuper.yt.cn.supermarket.modules.myclient.contact.PopWindowActivity;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.modules.myclient.join.JoinFragment;
import asuper.yt.cn.supermarket.modules.myclient.susidy.SubsidyFragment;
import asuper.yt.cn.supermarket.modules.myclient.visit.VisitRecordsFragment;
import asuper.yt.cn.supermarket.modules.plan.AddPlanActivity;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;

/**
 * Created by liaoqinsen on 2017/9/12 0012.
 */

public class MyClientDetailActivity extends BaseActivity implements View.OnClickListener, SaveCompleteListener {

    private ViewPager viewPager;
    private List<MainActivity.MainFragment> mainFragments;
    private MainActivity.MainFragment fragment;
    private AppCompatRadioButton baseInfo, join, contact, subsidy, visit;
    private AppCompatRadioButton[] buttons;
    private List<Integer> buttonIndexs;

    private TextView noIntention, addPlan, tmpVisit;
    private View indicator;

    public static final int REQUEST_GET_TAB_INFO = 0x3001;
    public static final int REQUEST_GIVE_UP = 0x3002;
    public static final int REQUEST_RECOVER = 0x3003;
    private int id = 0;

    private TextView joinBtn, edit, save, recover, recall;
    private LinearLayout floatBar;
    private ClientInfoDetail detail;
    private Clit clit;

    private TextView shopName, name, phone, step;

    private TextView remindJoin, remindContract, remindSusidy;

    private boolean hasGiveup;
    private boolean fromLocal;
    private boolean isUpdate = true;  // 内容是否可编辑

    private ClientDetailDialog clientDetailDialog;

    private List<PageButtonState> pageButtonStates = new ArrayList<>();

    private int mCurrentPosition = 0; // viewPage当前显示页面

    private String applyId;
    private String shopId;

    @Override
    protected int getContentId() {
        return R.layout.activity_my_client;
    }

    @Override
    protected void findView(View root) {
        viewPager = (ViewPager) root.findViewById(R.id.myclnt_pager);
        clientDetailDialog = new ClientDetailDialog(this);
        baseInfo = (AppCompatRadioButton) root.findViewById(R.id.my_client_detail_baseinfo);
        join = (AppCompatRadioButton) root.findViewById(R.id.my_client_detail_join);
        contact = (AppCompatRadioButton) root.findViewById(R.id.my_client_detail_contact);
        subsidy = (AppCompatRadioButton) root.findViewById(R.id.my_client_detail_subsidy);
        visit = (AppCompatRadioButton) root.findViewById(R.id.my_client_detail_visit);
        indicator = root.findViewById(R.id.client_detail_indicator);
        baseInfo.setOnClickListener(this);
        join.setOnClickListener(this);
        contact.setOnClickListener(this);
        subsidy.setOnClickListener(this);
        visit.setOnClickListener(this);

        root.findViewById(R.id.my_client_detail_cover).setOnClickListener(this);
        root.findViewById(R.id.my_client_detail_back).setOnClickListener(this);

        Bundle bundle = getIntent().getBundleExtra("extra");
        if (bundle != null) hasGiveup = bundle.getBoolean("isGiveup", false);
        if (bundle != null) isUpdate = bundle.getBoolean("isUpdate", true);
        fromLocal = getIntent().getBooleanExtra("fromLocal", false);

        shopName = (TextView) root.findViewById(R.id.my_client_detail_shopname);
        name = (TextView) root.findViewById(R.id.my_client_detail_name);
        phone = (TextView) root.findViewById(R.id.my_client_detail_phone);
        step = (TextView) root.findViewById(R.id.my_client_detail_step);


        remindContract = (TextView) root.findViewById(R.id.my_client_detail_remind_contract);
        remindJoin = (TextView) root.findViewById(R.id.my_client_detail_remind_join);
        remindSusidy = (TextView) root.findViewById(R.id.my_client_detail_remind_subsidy);

        noIntention = (TextView) root.findViewById(R.id.my_client_detail_nointention);
        addPlan = (TextView) root.findViewById(R.id.my_client_detail_addplan);
        addPlan.setOnClickListener(this);
        tmpVisit = (TextView) root.findViewById(R.id.my_client_detail_tmpvisit);
        tmpVisit.setOnClickListener(this);
        floatBar = (LinearLayout) root.findViewById(R.id.my_client_detail_float_bar);
        noIntention.setTag(-1);
        noIntention.setOnClickListener(this);
        buttons = new AppCompatRadioButton[]{join, contact, subsidy, visit};
        mainFragments = new ArrayList<>();
        clit = EventBus.getDefault().getStickyEvent(Clit.class);
        EventBus.getDefault().removeStickyEvent(Clit.class);
        if (clit != null) id = clit.id;
        viewPager.setAdapter(new MainActivity.MainPagerAdapter(mainFragments, this));
        showProgress();
        getTabInfo("0");
    }

    private void getTabInfo(String tyy) {   // 0 : 加载全部  1: 更新头部记录
        Map<String, Object> params = new HashMap<>();
        params.put("franchiseeNumber", id);
        params.put("employeeId", Config.UserInfo.USER_ID);
        Map<String, Object> map = new HashMap<>();
        map.put("date", params);
        map.put("key", tyy);
        dispatch(REQUEST_GET_TAB_INFO, map);
    }

    private void giveup() {
        Map<String, Object> map = new HashMap<>();
        map.put("intentionID", id);
        map.put("applyStep", detail.getApplyStep());
        dispatch(REQUEST_GIVE_UP, map);
    }

    @BindAction(REQUEST_GET_TAB_INFO)
    public void getTabInfoResult(HashMap<String, Object> data) {
        if (data == null) {
            dismissProgress();
            return;
        }
        String key = data.get("key").toString();
        ClientInfoDetail buts = (ClientInfoDetail) data.get("date");

        if (key.equals("0")) {
            detail = buts;
            buttonIndexs = new ArrayList<>();
            buttonIndexs.add(0);
            mainFragments.clear();
            mainFragments.add(ClientDetailFragment.newInstance(buts));
            if (buts.getButtonInfos() != null) {
                noIntention.setTag(1);
                if (buts.getButtonInfos().size() > 0 && !buts.getButtonInfos().get(0).isButton()) {
                    noIntention.setText("放弃加盟");
                    noIntention.setTag(2);
                }
            }
            baseInfo.setEnabled(true);
            baseInfo.setChecked(true);
            if (buts.getButtonInfos() == null || buts.getButtonInfos().size() < 1) {
                dismissProgress();
            }
            if (buts.getButtonInfos() != null) {
                int infos = "step_9&step_10&step_11&step_12&step_13".contains(buts.getApplyStep())?1:buts.getButtonInfos().size();
                for (int i = 0; i < infos; i++) {
                    mainFragments.add(newFragment(buts, buts.getButtonInfos().get(i), buts.getFranchiseeNumber(), i));
                    if (i == 0) {
                        interceptor = (BeforeFinishInterceptor) mainFragments.get(mainFragments.size() - 1);
                    }
                    buttons[i].setEnabled(true);
                    if (buts.getButtonInfos().get(i).isButton()) {
                        buttons[i].setActivated(true);
                    }
                    buttonIndexs.add(i + 1);
                }
                fragment = mainFragments.get(infos);
                fragment.refresh();
            }
            if (detail.visitListCount > 0) {
                mainFragments.add(VisitRecordsFragment.newInstance(buts));
                buttonIndexs.add(4);
                visit.setEnabled(true);
            } else {
                visit.setEnabled(false);
            }
            viewPager.getAdapter().notifyDataSetChanged();
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }
                @Override
                public void onPageSelected(int position) {
                    mCurrentPosition = position;
                    int index = buttonIndexs.get(position);
                    if (index < 1) {
                        ((RadioGroup) baseInfo.getParent()).check(baseInfo.getId());
                        indicator.setTranslationX(-indicator.getLeft());
                        indicator.setTranslationX(baseInfo.getLeft() + baseInfo.getMeasuredWidth() / 2 + ((View) baseInfo.getParent()).getLeft() - indicator.getWidth() / 2);
                    } else {
                        ((RadioGroup) buttons[index - 1].getParent()).check(buttons[index - 1].getId());
                        indicator.setTranslationX(-indicator.getLeft());
                        indicator.setTranslationX(buttons[index - 1].getLeft() + buttons[index - 1].getMeasuredWidth() / 2 + ((View) buttons[index - 1].getParent()).getLeft() - indicator.getWidth() / 2);
                    }
                    if (pageButtonStates.size() > position) {
                        if (mainFragments.get(position) instanceof VisitRecordsFragment) {
                            pageButtonStates.get(position).dis();
                            return;
                        }
                        pageButtonStates.get(position).show();
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            initTitle(buts);
            initRightFloatActionBar(buts);
            initRemindView(buts);
            initPageButtonStates(buts, 0);



        } else if (key.equals("1")) {
            if (detail == null) return;
            if (detail.visitListCount <= 0 && buts.visitListCount > 0) {
                mainFragments.add(VisitRecordsFragment.newInstance(buts));
                buttonIndexs.add(4);
                visit.setEnabled(true);
                initPageButtonStates(buts, mCurrentPosition);
                viewPager.getAdapter().notifyDataSetChanged();
            } else {
                visit.setEnabled(true);
                for (MainActivity.MainFragment fragment : mainFragments) {
                    if (fragment instanceof VisitRecordsFragment) {
                        fragment.refresh();
                    }
                }
            }
        }
    }

    private void initPageButtonStates(ClientInfoDetail clientInfoDetail, int pageNum) {
        PageButtonState baseInfoState = new PageButtonState();
        baseInfoState.showEdit = true;
        baseInfoState.showSave = false;
        pageButtonStates.add(baseInfoState);
        if (clientInfoDetail == null || clientInfoDetail.getButtonInfos() == null) return;
        baseInfoState.showEdit = false;
        int index = 0;
        for (ButtonInfos buttonInfos : clientInfoDetail.getButtonInfos()) {
            PageButtonState state = new PageButtonState(buttonInfos, (!buttonInfos.isButton() && buttonInfos.isUpdate()) || (clientInfoDetail.getButtonInfos().size() > index && fromLocal));
            pageButtonStates.add(state);
            index++;
        }
        if (clientInfoDetail.getButtonInfos().size() == 1 ) {
            if (clientInfoDetail.getButtonInfos().get(0).isButton()) {
                baseInfoState.showEdit = true;
                pageButtonStates.get(0).showJoin = true;
                pageButtonStates.get(0).joinText = "加盟";
                pageButtonStates.get(0).joinTag = "join";
            }else if(clientInfoDetail.getApplyStep().equals("sign_waiting")){
                pageButtonStates.get(0).showJoin = true;
                pageButtonStates.get(0).joinText = "签合同";
                pageButtonStates.get(0).joinTag = "contract";

                pageButtonStates.get(1).showJoin = true;
                pageButtonStates.get(1).joinText = "签合同";
                pageButtonStates.get(1).joinTag = "contract";
            }
        } else if (clientInfoDetail.getButtonInfos().size() == 2 && clientInfoDetail.getButtonInfos().get(1).isButton()) {
            pageButtonStates.get(0).showJoin = true;
            pageButtonStates.get(0).joinText = "签合同";
            pageButtonStates.get(0).joinTag = "contract";
            pageButtonStates.get(1).showJoin = true;
            pageButtonStates.get(1).joinText = "签合同";
            pageButtonStates.get(1).joinTag = "contract";
        } else if (clientInfoDetail.getButtonInfos().size() == 3 && clientInfoDetail.getButtonInfos().get(2).isButton()) {
            pageButtonStates.get(0).showJoin = true;
            pageButtonStates.get(0).joinText = "填补贴";
            pageButtonStates.get(0).joinTag = "subsidy";
            pageButtonStates.get(1).showJoin = true;
            pageButtonStates.get(1).joinText = "填补贴";
            pageButtonStates.get(1).joinTag = "subsidy";
            pageButtonStates.get(2).showJoin = true;
            pageButtonStates.get(2).joinText = "填补贴";
            pageButtonStates.get(2).joinTag = "subsidy";
            joinBtn.setTag("subsidy");
        }
        if (clientInfoDetail.visitListCount > 0) {
            pageButtonStates.add(new PageButtonState());
        }

        pageButtonStates.get(pageNum).show();
    }

    /**
     * 设置tabBar
     *
     * @param buts
     */
    private void initRemindView(ClientInfoDetail buts) {
        if (buts.getButtonInfos().size() == 1) {
            initRemindViewItem(buts.getButtonInfos().get(0), remindJoin);
        } else if (buts.getButtonInfos().size() == 2) {
            initRemindViewItem(buts.getButtonInfos().get(1), remindContract);
        } else if (buts.getButtonInfos().size() == 3) {
            initRemindViewItem(buts.getButtonInfos().get(2), remindSusidy);
        }
    }

    private void initRemindViewItem(ButtonInfos buttonInfos, TextView remindView) {
        if (buttonInfos.isButton()) {
            remindView.setVisibility(View.VISIBLE);
        }
        if (!buttonInfos.isButton() && buttonInfos.isUpdate()) {
            remindView.setVisibility(View.VISIBLE);
            remindView.setText("请处理");
        }
        if (!buttonInfos.isButton() && !buttonInfos.isUpdate() && buttonInfos.getButtonName().contains("审核中")) {
            remindView.setVisibility(View.VISIBLE);
            remindView.setBackgroundResource(R.drawable.ic_chu_ing);
            remindView.setText("审核中");
        }
    }

    private void initTitle(ClientInfoDetail buts) {
        shopName.setText(buts.getShopName());
        name.setText(buts.getLegalpersonName());
        phone.setText(buts.getPhoneNumber());
        step.setText(clit.getApplyStepName());
        name.setVisibility(name.getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
        phone.setVisibility(phone.getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
        step.setVisibility(step.getText().toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
        int[] res = ToolGetButtoninfos.getClientStepBackground(clit.applySteup);
        Drawable drawable = getResources().getDrawable(res[0]);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        step.setCompoundDrawables(drawable, null, null, null);
        step.setBackgroundResource(res[1]);
    }

    @BindAction(REQUEST_GIVE_UP)
    public void giveupResult(boolean res) {
        if (res) {
            ToastUtil.success("修改客户意向成功");
            finish();
        } else {
            ToastUtil.success("修改客户意向失败");
        }
    }

    private MainActivity.MainFragment newFragment(ClientInfoDetail clientInfoDetail, ButtonInfos buttonInfos, int id, int type) {
        switch (type) {
            case 0:
                return JoinFragment.newInstance(buttonInfos, id, "", 0, isUpdate);
            case 1:
                return ContractFragment.newInstance(clientInfoDetail, id, "", 0);
            case 2:
                return SubsidyFragment.newInstance(buttonInfos, id, "", 0);
        }
        return SubsidyFragment.newInstance(buttonInfos, id, "", 0);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new MyClientDetailStore(dependencyDelegate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case 100:
                finish();
                break;
            case 101:
                refresh();
                break;
            case 202:
                getTabInfo("1");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_client_detail_baseinfo:
                viewPager.setCurrentItem(0);
                break;
            case R.id.my_client_detail_join:
                viewPager.setCurrentItem(1);
                break;
            case R.id.my_client_detail_contact:
//                ToolAlert.popwindow(getContext(),R.layout.activity_popwin_client,true).showAtLocation(root, Gravity.CENTER,0,0);
                viewPager.setCurrentItem(2);
                break;
            case R.id.my_client_detail_subsidy:
                viewPager.setCurrentItem(3);
                break;
            case R.id.my_client_detail_visit:
                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                break;
            case R.id.my_client_detail_nointention:
                int tag = (int) v.getTag();
                if (tag == 1) {
                    clientDetailDialog.show("暂无意向", "是否将该客户加入到回收站中?", "可在 客户回收站>暂无意向 中找到", new ClientDetailDialog.OnClientDialogConfirmListener() {
                        @Override
                        public void onConfirm() {
                            giveup();
                        }
                    });
                } else {
                    getOperation().addParameter("id", detail.getFranchiseeNumber());
                    getOperation().forwardForResult(GiveupActivity.class, 100);
                }
                break;
            case R.id.my_client_detail_addplan:
                if (detail != null) {
                    getOperation().addParameter("data", detail.getFranchiseeNumber() + "");
                    getOperation().forwardForResult(AddPlanActivity.class,202);
                }
                break;
            case R.id.my_client_detail_tmpvisit:
                if (detail == null) return;
                getOperation().addParameter("id", detail.getFranchiseeNumber() + "");
                getOperation().forwardForResult(TempVisitActivity.class, 202);
                break;
            case R.id.my_client_detail_join_btn:
                if (v.getTag() != null && "join".equals(v.getTag())) {
                    viewPager.setCurrentItem(1, true);
                    return;
                } else if (v.getTag() != null && "contract".equals(v.getTag())) {
                    if (detail.getApplyStep().equals("sign_waiting")){
                        getOperation().forward(PopWindowActivity.class);
                    }else {
                        viewPager.setCurrentItem(2, true);
                    }
                    return;
                } else if (v.getTag() != null && "subsidy".equals(v.getTag())) {
                    viewPager.setCurrentItem(3, true);
                    return;
                }
                commit();
                break;
            case R.id.my_client_detail_edit_btn:
                if (detail != null && (detail.getButtonInfos() == null || detail.getButtonInfos().size() < 1) && mainFragments.get(0) instanceof ClientDetailFragment) {
                    if (edit.getText().toString().equals("编辑")) {
                        ((ClientDetailFragment) mainFragments.get(0)).enableEdit(true);
                        joinBtn.setVisibility(View.VISIBLE);
                        edit.setText("取消");
                    } else {
                        ((ClientDetailFragment) mainFragments.get(0)).enableEdit(false);
                        joinBtn.setVisibility(View.GONE);
                        edit.setText("编辑");
                    }
                }
                edit();
                break;
            case R.id.my_client_detail_save_btn:
                save();
                break;
            case R.id.my_client_detail_cover:
            case R.id.my_client_detail_back:
                finish();
                break;
            case R.id.my_client_detail_recover:
                if(recall.getVisibility()==View.VISIBLE) return;
                recover();
            case R.id.my_client_detail_recall:   // 撤回
                if(recover.getVisibility()==View.VISIBLE) return;
                recall();
                break;
        }
    }

    private void initRightFloatActionBar(ClientInfoDetail clientInfoDetail) {
        if (clientInfoDetail == null) return;
        floatBar.setVisibility(View.VISIBLE);
        joinBtn = (TextView) floatBar.findViewById(R.id.my_client_detail_join_btn);
        joinBtn.setOnClickListener(this);
        edit = (TextView) floatBar.findViewById(R.id.my_client_detail_edit_btn);
        edit.setOnClickListener(this);
        save = (TextView) floatBar.findViewById(R.id.my_client_detail_save_btn);
        recover = (TextView) floatBar.findViewById(R.id.my_client_detail_recover);
        save.setOnClickListener(this);
        recall = (TextView) floatBar.findViewById(R.id.my_client_detail_recall);
        if (hasGiveup) {
            recover.setVisibility(View.VISIBLE);
            recover.setOnClickListener(this);
        }
        if (clientInfoDetail.getButtonInfos().size() < 1 || (clientInfoDetail.getButtonInfos().size() > 0 && clientInfoDetail.getButtonInfos().get(0).isButton())) {
            noIntention.setEnabled(true);
            noIntention.setVisibility(View.VISIBLE);
        }
        if (clientInfoDetail.getButtonInfos().size() > 0 && !clientInfoDetail.getButtonInfos().get(0).isButton()) {
            noIntention.setEnabled(false);
            noIntention.setVisibility(View.GONE);
        }
        if (clientInfoDetail.getButtonInfos().size() > 0 && !clientInfoDetail.getButtonInfos().get(0).isButton() && clientInfoDetail.getButtonInfos().get(0).isUpdate()) {
            noIntention.setEnabled(true);
            noIntention.setVisibility(View.VISIBLE);
        } else if (clientInfoDetail.getButtonInfos().size() > 1 && clientInfoDetail.getButtonInfos().get(1).isButton()) {
            noIntention.setEnabled(true);
            noIntention.setVisibility(View.VISIBLE);
        } else if (clientInfoDetail.getButtonInfos().size() > 1 && !clientInfoDetail.getButtonInfos().get(1).isButton() && clientInfoDetail.getButtonInfos().get(1).isUpdate()) {
            noIntention.setEnabled(true);
            noIntention.setVisibility(View.VISIBLE);
        }
        if (hasGiveup) {
            noIntention.setEnabled(false);
            noIntention.setVisibility(View.GONE);
        }
    }

    public void commit() {
        ((MyClientDetailFragment) mainFragments.get(mCurrentPosition)).commit();
    }

    public void save() {
        ((MyClientDetailFragment) mainFragments.get(mCurrentPosition)).save(this);
    }

    public void edit() {
        pageButtonStates.get(mCurrentPosition).showEdit = false;
        pageButtonStates.get(mCurrentPosition).showSave = true;
        if (mCurrentPosition == 0) {
            pageButtonStates.get(mCurrentPosition).saveText = "提交";
        }
        pageButtonStates.get(mCurrentPosition).show();
        ((MyClientDetailFragment) mainFragments.get(mCurrentPosition)).edit();
    }


    public void cancel() {
        ((MyClientDetailFragment) mainFragments.get(mCurrentPosition)).cancel();
    }

    public void refresh() {
        (mainFragments.get(mCurrentPosition)).refresh();
    }

    public void recover() {
        clientDetailDialog.show("还原", "是否将该客户还原?", "解冻后的客户将还原到 我的客户 中", new ClientDetailDialog.OnClientDialogConfirmListener() {
            @Override
            public void onConfirm() {
                Map<String, Object> map = new HashMap<>();
                map.put("intentionID", detail.getFranchiseeNumber());
                dispatch(REQUEST_RECOVER, map);
            }
        });
    }

    public void recall() {
        if (fragment == null) return;
        Map<String, Object> map = new HashMap<>();
        final Map<String, Object> parm = new HashMap<>();
        map.put("applyId", applyId);
        map.put("intentionId", shopId);
        parm.put("map", map);
        String txt = "";
        if (fragment instanceof JoinFragment) {
            parm.put("url", Config.getURL("app/ratingScale/revoke.htm"));
            txt = "加盟评分表";
        } else if (fragment instanceof ContractFragment) {
            parm.put("url", Config.getURL("oles/app/agreementApproveAction/revoke.htm"));
            txt = "合同审批表";
        } else if (fragment instanceof SubsidyFragment) {
            parm.put("url", Config.getURL("oles/app/rental/revoke.htm"));
            txt = "补贴申请表";
        }
        clientDetailDialog.show("撤回","是否撤回正在审核的",txt,new ClientDetailDialog.OnClientDialogConfirmListener() {

            @Override
            public void onConfirm() {
                dispatch(MyClientDetailStore.POST_URL_CLIENT_RECALL, parm);
            }
        });
    }

    @BindAction(MyClientDetailStore.POST_URL_CLIENT_RECALL)
    public void recallDetail(boolean success) {
        if (success) {
            ToastUtil.success("撤销成功");
            finish();
        }
    }

    @BindAction(REQUEST_RECOVER)
    public void recoverResult(boolean success) {
        if (success) {
            ToastUtil.success("还原成功");
            setResult(100);
            finish();
        } else {
            ToastUtil.error("还原失败");
        }
    }

    @Override
    public void result(boolean success, MyClientDetailFragment fragment) {
        PageButtonState state = null;
        if (fragment instanceof ClientDetailFragment) state = pageButtonStates.get(0);
        if (fragment instanceof JoinFragment) state = pageButtonStates.get(1);
        if (fragment instanceof ContractFragment) state = pageButtonStates.get(2);
        if (fragment instanceof SubsidyFragment) state = pageButtonStates.get(3);
        if (state == null) return;
        state.showEdit = success;
        state.showSave = !success;
        state.show();
    }


    public interface MyClientDetailFragment extends MainActivity.MainFragment {
        public void commit();

        public void save(SaveCompleteListener listener);

        public void cancel();

        public void edit();
    }


    public class PageButtonState {

        public boolean showEdit = false;
        public String editText = "编辑";
        public boolean showJoin = false;
        public String joinText = "提交";
        public String joinTag = null;
        public boolean showSave = false;
        public String saveText = "保存";

        public PageButtonState() {
        }

        public PageButtonState(ButtonInfos buttonInfos, boolean local) {
            if (buttonInfos != null) {
                if (buttonInfos.isButton() || buttonInfos.isUpdate()) {
                    showEdit = local;
                    showJoin = true;
                    showSave = !local;
                }
            }
        }

        public void dis() {
            joinBtn.setVisibility(View.GONE);
            save.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }

        public void show() {
            if (hasGiveup) {
                showEdit = false;
                showJoin = false;
                showSave = false;
            }
            joinBtn.setVisibility(showJoin ? View.VISIBLE : View.GONE);
            joinBtn.setText(joinText);
            joinBtn.setTag(joinTag);
            save.setVisibility(showSave ? View.VISIBLE : View.GONE);
            save.setText(saveText);
            edit.setVisibility(showEdit ? View.VISIBLE : View.GONE);
            edit.setText(editText);
        }
    }

    public void showRecall(String applyId, String shopId) {
        if (!clit.getApplyStepName().contains("审核中"))return;
        this.applyId = applyId;
        this.shopId = shopId;
        recall.setVisibility(View.VISIBLE);
        recall.setOnClickListener(this);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
    }
}
