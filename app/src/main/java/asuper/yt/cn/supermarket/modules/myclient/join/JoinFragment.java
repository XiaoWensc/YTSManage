package asuper.yt.cn.supermarket.modules.myclient.join;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailDialog;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.SaveCompleteListener;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.ToolData;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import asuper.yt.cn.supermarket.utils.ToolTagView;
import asuper.yt.cn.supermarket.utils.ToolViewGroup;
import asuper.yt.cn.supermarket.views.AddressSelectWindow;
import asuper.yt.cn.supermarket.views.CustomDatePicker;
import asuper.yt.cn.supermarket.views.RadioButton;
import asuper.yt.cn.supermarket.views.RadioGroup;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;

/**
 * 加盟评分表
 * Created by zengxiaowen on 2018/1/3 1743.
 */

public class JoinFragment extends BaseFragment implements MainActivity.MainFragment, MyClientDetailActivity.MyClientDetailFragment, BaseActivity.BeforeFinishInterceptor {

    private LinearLayout frame_root;

    //top
    private LinearLayout node_infos_container;
    private AppCompatCheckBox node_infos_title;
    //------审核流程-- pending_auditing=等待审核，complete=已通过，reject=未通过
    private LinearLayout pending_auditing, complete, reject;
    private TextView currentAuditingNodeName, shopTotalScore, shopScoreGrade, xcCode, currentAuditingNodeNameNo, rejectReson;

    //layout1
    private EditText verifyCode;
    private LinearLayout fujian;
    EditText shopRealMan ;
    EditText shopRealManIdCard ;
    private TextView countdown;
    private EditText shopLegalperson,shopLegalIdcard,shopOwnerPhonenumber;
    private Button ifPhone;
    private List<EditText> editTexts = new ArrayList<>();


    private TextView sqCity; //所在城市

    private View auditingNodeListContainer;
    private EditText shopRealManPhone, shopDoorLength, shopDoorWidth, shopDoorArea, shopAcreage;

    private TextView btnUpload;
    private Button getVerifyCode;

    private String isNew;
    private boolean isUpdate = false;
    private int shopId;

    private ButtonInfos buttonInfos;
    private JSONObject pageData;
    private boolean fromLocal = false;
    private DTO<String, Object> formData;
    private DTO<String, Object> commitMap;  //post提交参数
    private boolean saveForError = false;
    private boolean isRecall = false;
    private List<ImageGalleryActivity.ImageGalleryItem> items;
    public static final int REQUEST_GET_JOIN_DETAIL = 0x0401;
    public static final int REQUEST_GET_JOIN_NODE_INFO = 0x0402;
    public static final int REQUEST_GET_JOIN_VERIFY = 0x0403;
    public static final int REQUEST_JOIN_UPLOAD = UploadStore.REQUEST_UPLOAD;
    public static final int REQUEST_JOIN_COMMIT = 0x0405;
    public static final int REQUEST_GET_JOIN_ATTACHMENT = 0x0406;
    public static final int REQUEST_JOIN_SAVE = 0x0408;
    private SaveCompleteListener saveListener;
    private ClientDetailDialog clientDetailDialog;

    private int isOverdueRevert = 0;  //逾期还原


    public static JoinFragment newInstance(ButtonInfos bts, int shopId, String over, int groupId, boolean isUpdate) {
        Bundle args = new Bundle();
        args.putSerializable("bts", bts);
        args.putInt("id", shopId);
        JoinFragment fragment = new JoinFragment();
        fragment.setArguments(args);
//        fragment.isOver = over;
//        fragment.groupId = groupId;
        fragment.isNew = bts.isNew;
        fragment.isUpdate = isUpdate ? bts.isButton() || bts.isUpdate() : isUpdate;
//        fragment.isUpdate = bts.isUpdate();
        fragment.buttonInfos = bts;
        fragment.shopId = shopId;
        return fragment;
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_join_new;
    }

    @Override
    protected void findView(final View root) {
        clientDetailDialog = new ClientDetailDialog(getContext());
        initVw(root);
        getJoinDetail();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new JoinStore(dependencyDelegate);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {
        isRecall = true;
    }

    public void getJoinDetail() {
        if (buttonInfos == null) return;
        HashMap<String, Object> map = new HashMap<>();
        if (buttonInfos.isButton()) {
            map.put("id", "0");
            map.put("shopId", shopId);
        } else {
            map.put("id", buttonInfos.getParameterId());
            map.put("shopId", shopId);
        }
        map.put("isUpdate", buttonInfos.isUpdate());
        map.put("isNew", buttonInfos.isButton());
        dispatch(REQUEST_GET_JOIN_DETAIL, map);
    }

    @BindAction(REQUEST_GET_JOIN_DETAIL)
    public void getJoinDetailResult(HashMap<String, Object> res) {
        dissmissProgress();
        if (res == null) return;
        pageData = (JSONObject) res.get("pageData");
        isOverdueRevert = pageData.optInt("isOverdueRevert",0);
        items = (List<ImageGalleryActivity.ImageGalleryItem>) res.get("items");
        fromLocal = (boolean) res.get("local");
        initViewDate(pageData, isUpdate);
        if (fromLocal || (!buttonInfos.isButton() && buttonInfos.isUpdate())) {
            ToolAttachment.disableAllView(frame_root, false);
            btnUpload.setText("去查看");
        }
        if (isRecall && pageData.optInt("auditNodeIndex", 0) == 1) {
            ((MyClientDetailActivity) getActivity()).showRecall(pageData.optString("applyId", null), shopId + "");
        }
    }

    public void getNodeInfo() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("applyId", pageData.optString("applyId", null));
        hashMap.put("intentionId", shopId);
        dispatch(REQUEST_GET_JOIN_NODE_INFO, hashMap);
    }

    @BindAction(REQUEST_GET_JOIN_NODE_INFO)
    public void getNodeInfoResult(NodeList nodeInfos) {
        dissmissProgress();
        if (nodeInfos != null && nodeInfos.resultObject.size() > 0) {
            for (NodeList.NodeInfo nodeInfo : nodeInfos.resultObject) {
                if (nodeInfo == null) continue;
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_nodeinfo, null);
                ((TextView) v.findViewById(R.id.auditMessage)).setText(nodeInfo.auditMessage);
                ((TextView) v.findViewById(R.id.auditorName)).setText(nodeInfo.taskDefName);
                node_infos_container.addView(v);
            }

            node_infos_title.setVisibility(View.VISIBLE);
            node_infos_title.setOnCheckedChangeListener((buttonView, isChecked) -> node_infos_container.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        } else {
            node_infos_title.setVisibility(View.GONE);
        }
    }

    public void verifyForLocal() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("verificationCode", verifyCode.getText().toString());
        hashMap.put("shopOwnerPhonenumber", shopOwnerPhonenumber.getText().toString());
        hashMap.put("shopId", shopId);
        hashMap.put("businessType", CommonRequest.VERIFY_CODE_TYPE_JOIN);
        dispatch(REQUEST_GET_JOIN_VERIFY, hashMap);
    }

    @BindAction(REQUEST_GET_JOIN_VERIFY)
    public void getVerifyResult(boolean success) {
        dissmissProgress();
        if (success) {
            getVerifyCode.setEnabled(false);
            setImageRight(false);
        }
//        internalSaveLocal();
    }


    @BindAction(REQUEST_GET_JOIN_ATTACHMENT)
    public void getAttachmentResult(List<ImageGalleryActivity.ImageGalleryItem> items) {
        dissmissProgress();
        if (items == null) return;
        this.items = items;
        ToolLog.i(items.toString());
        if (!fromLocal && (YTApplication.debugEnable || BuildConfig.DEBUG) && Config.isAutoFillAttachment && buttonInfos.isButton()) {
            for (ImageGalleryActivity.ImageGalleryItem imageGalleryItem : this.items) {
                if (imageGalleryItem == null) continue;
                for (int i = 0; i < imageGalleryItem.max(); i++) {
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setPhotoId(0);
                    photoInfo.setPhotoPath(Config.autoFillAttachmentPath);
                    if (imageGalleryItem.photoInfo == null)
                        imageGalleryItem.photoInfo = new ArrayList<>();
                    imageGalleryItem.photoInfo.add(photoInfo);
                }
            }
        }
        ToolAttachment.addFujian(this.items, fujian, getActivity());
    }

    public void uploadImg() {
        commitMap = new DTO<>();
        commitMap = getViewForm();
        if (commitMap != null) {
            if (!ToolAttachment.isImagesSatifyied(items)) return;
            clientDetailDialog.show("加盟提交", "检查无误后提交至服务器", "", () -> dispatch(REQUEST_JOIN_UPLOAD, null));
        }
    }

    @BindAction(REQUEST_JOIN_UPLOAD)
    public void uploadImgResult(boolean success) {
        dissmissProgress();
        if (BuildConfig.DEBUG||success) {
            showProgress("正在提交");
            if (formData == null) return;
            if (buttonInfos.isUpdate()) {
                String id = buttonInfos.getParameterId();
                commitMap.put("id", id == null || id.isEmpty() ? "0" : id);
            }
            commitMap.put("shopId", shopId);
            commitMap.put("shopAssigner", Config.UserInfo.NAME);
            commitMap.put("businessType", CommonRequest.VERIFY_CODE_TYPE_JOIN);

            ToolLog.i(pageData);
            commitMap.put("provinceCode",pageData.optString("provinceCode"));
            commitMap.put("shopCityCode",pageData.optString("shopCityCode"));
            commitMap.put("countyCode",pageData.optString("countyCode"));
            commitMap.put("shopStreetCode",pageData.optString("shopStreetCode"));

            commitMap.put("shopAllowanceMode", "-");
            commitMap.put("shopOnlineShopping", "-");
            commitMap.put("shopServiceAttitude", "-");
            commitMap.put("shopPromotionFrequency", "-");
            commitMap.put("shopSupplier", "-");
            commitMap.put("shopFloatingRatio", "0");

            //实际经营者是法人本人
            if (commitMap.get("relationShip").toString().equals("key1")) {
                commitMap.put("shopRealMan", commitMap.get("shopLegalperson"));
                commitMap.put("shopRealManIdCard", commitMap.get("shopLegalIdcard"));
                commitMap.put("shopRealManPhone", commitMap.get("shopOwnerPhonenumber"));
            }
            for (int i = 0; i < items.size(); i++) {
                List<String> paths = new ArrayList<>();
                List<PhotoInfo> photoInfos = items.get(i).photoInfo;
                for (PhotoInfo photoInfo : photoInfos) {
                    paths.add(photoInfo.getPhotoPath());
                }
                commitMap.put(items.get(i).info.key, ToolStringToList.ListToString(paths));
            }
            dispatch(REQUEST_JOIN_COMMIT, commitMap);
        } else {
            ToastUtil.error("上传图片失败");
        }
    }

    @Override
    public void commit() {
        if (checkNeedResponseActivityOpreation()) {
            uploadImg();
        }
    }


    private boolean checkNeedResponseActivityOpreation() {
        if (buttonInfos == null) return false;
        return buttonInfos.isButton() || buttonInfos.isUpdate();
    }

    @Override
    public void save(SaveCompleteListener listener) {
        if (!checkNeedResponseActivityOpreation()) return;
        saveListener = listener;
        internalSaveLocal();
    }

    @BindAction(REQUEST_JOIN_SAVE)
    public void saveResult(boolean success) {
        dissmissProgress();
        if (success) {
            saveForError = false;
            ToastUtil.success("保存成功");
            isUpdate = false;
            ToolAttachment.disableAllView(frame_root, false);
            btnUpload.setText("去查看");
        } else {
            ToastUtil.success("保存失败");
        }
        if (saveListener != null) saveListener.result(success, this);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void edit() {
        if (buttonInfos != null) {
            isUpdate = buttonInfos.isButton() || buttonInfos.isUpdate();
            if (isUpdate) {
                ToolAttachment.disableAllView(frame_root, true);
                if (isOverdueRevert==1){  // 逾期还原 不允许更改以下内容
                    frame_root.findViewById(R.id.businessLicenseName).setEnabled(false);
                    frame_root.findViewById(R.id.businessPlace).setEnabled(false);
                    frame_root.findViewById(R.id.businessLicenseNumber).setEnabled(false);
                    shopLegalIdcard.setEnabled(false);
                }
                btnUpload.setText("去上传");
            }
        }
    }

    @BindAction(REQUEST_JOIN_COMMIT)
    public void commitResult(boolean success) {
        dissmissProgress();
        saveForError = !success;
        if (success) {
            ToastUtil.success("提交成功");
            getActivity().setResult(101);
            getActivity().finish();
        }
    }

    /**
     * 初始化视图
     *
     * @param view
     */
    private void initVw(View view) {
        frame_root = view.findViewById(R.id.frame_root);
        initLayoutTop(view);
        initLayout1(view);
        initLayout2(view);
        initLayout3(view);
    }

    private void initLayoutTop(View view) {
        node_infos_container = view.findViewById(R.id.node_infos_container);
        node_infos_title = view.findViewById(R.id.node_infos_title);
        pending_auditing = view.findViewById(R.id.pending_auditing);
        complete = view.findViewById(R.id.complete);
        reject = view.findViewById(R.id.reject);
        currentAuditingNodeName =  view.findViewById(R.id.currentAuditingNodeName);
        currentAuditingNodeNameNo = view.findViewById(R.id.currentAuditingNodeNameNo);
        auditingNodeListContainer = view.findViewById(R.id.auditingNodeListContainer);
        shopTotalScore =  view.findViewById(R.id.shopTotalScore);
        shopScoreGrade = view.findViewById(R.id.shopScoreGrade);
        xcCode = view.findViewById(R.id.xcCode);
        rejectReson = view.findViewById(R.id.rejectReson);
    }

    private void initLayout3(View view) {

        EditText attchment_describe = view.findViewById(R.id.attachmentState);
        attchment_describe.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        attchment_describe.setSingleLine(false);
        attchment_describe.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter()});
        EditText describe = view.findViewById(R.id.describe);
        describe.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        describe.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter()});
        describe.setSingleLine(false);
        describe.setHorizontallyScrolling(false);

        InputFilter _100less = ToolInputFilter.createDecimalFilter(99.99, 2, 4);
        InputFilter _1000less = ToolInputFilter.createDecimalFilter(999.99, 2, 5);
        InputFilter _100000000less = ToolInputFilter.createDecimalFilter(99999999.99, 2, 10);
        ((EditText)view.findViewById(R.id.year)).setFilters(new InputFilter[]{_100less});
        ((EditText)view.findViewById(R.id.shopAcreage)).setFilters(new InputFilter[]{_1000less});
        ((EditText)view.findViewById(R.id.sale)).setFilters(new InputFilter[]{_1000less});
        ((EditText)view.findViewById(R.id.rent_monthly)).setFilters(new InputFilter[]{_100000000less});
        fujian = view.findViewById(R.id.fujian);
        shopDoorArea = view.findViewById(R.id.shopDoorArea);
        (shopDoorLength = view.findViewById(R.id.shopDoorLength)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                setDateArea(s.toString(), shopDoorWidth.getText().toString());
            }
        });
        (shopDoorWidth = view.findViewById(R.id.shopDoorWidth)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                setDateArea(s.toString(), shopDoorLength.getText().toString());
            }
        });
        setDateArea(shopDoorWidth.getText().toString(), shopDoorLength.getText().toString());
        shopDoorLength.setFilters(new InputFilter[]{_100less});
        shopDoorWidth.setFilters(new InputFilter[]{_100less});
        //点击上传
        (btnUpload = view.findViewById(R.id.btnUpload)).setOnClickListener(v -> ToolAttachment.gotoUpload(isUpdate, items));
    }

    private void initLayout2(View view) {
        ((RadioGroup) view.findViewById(R.id.businessLicense)).setOnCheckedChangeListener((radioGroup, i) -> {
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent(),2).setVisibility(radioGroup.getCheckedRadioButtonId() == R.id.businessY ? View.VISIBLE : View.GONE);
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent(),2).setTag(radioGroup.getCheckedRadioButtonId() == R.id.businessY ? null : "nocheck");
            intAttachment();
        });
        ((CheckBox) view.findViewById(R.id.foodPapers)).setOnCheckedChangeListener((compoundButton, b) -> {
            ToolViewGroup.getNextView((ViewGroup) compoundButton.getParent().getParent()).setVisibility(!b ? View.VISIBLE : View.GONE);
            ToolViewGroup.getNextView((ViewGroup) compoundButton.getParent().getParent()).setTag(!b ? null : "nocheck");
            intAttachment();
        });
        ((RadioGroup) view.findViewById(R.id.smokePapers)).setOnCheckedChangeListener((radioGroup, i) -> {
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setVisibility(i == R.id.smokeY ? View.VISIBLE : View.GONE);
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setTag(i == R.id.smokeY ? null : "nocheck");
            intAttachment();
        });
        ((CheckBox) view.findViewById(R.id.smokePapersMs)).setOnCheckedChangeListener((compoundButton, b) -> {
            ToolViewGroup.getNextView((ViewGroup) compoundButton.getParent().getParent()).setVisibility(!b ? View.VISIBLE : View.GONE);
            ToolViewGroup.getNextView((ViewGroup) compoundButton.getParent().getParent()).setTag(!b ? null : "nocheck");
            intAttachment();
        });
        ((RadioGroup) view.findViewById(R.id.rentPapers)).setOnCheckedChangeListener((radioGroup, i) -> {
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setVisibility(i == R.id.rentY ? View.GONE : View.VISIBLE);
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setTag(i == R.id.rentY ? "nocheck" : null);
            intAttachment();
        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        final String now = sdf.format(new Date());
        view.findViewById(R.id.businessTime).setOnClickListener(view1 -> new CustomDatePicker(getContext(), ((TextView) view1)::setText, "1949-1-1 00:00", now, false).showSpecificTime(false).setIsLoop(false).show(now));
        view.findViewById(R.id.foodTimeStart).setOnClickListener(view1 -> new CustomDatePicker(getContext(), ((TextView) view1)::setText, "1949-1-1 00:00", now, false).showSpecificTime(false).setIsLoop(false).show(now));
        view.findViewById(R.id.foodTimeEnd).setOnClickListener(view1 -> {
            if (((TextView) ToolViewGroup.getNextView(view1, -2)).getText().toString().equals("起始时间")) {
                ToastUtil.info("请先选择起始时间");
                return;
            }
            new CustomDatePicker(getContext(), ((TextView) view1)::setText, now, "2100-1-1 00:00", false).showSpecificTime(false).setIsLoop(false).show(now);
        });
        view.findViewById(R.id.smokeTimeStart).setOnClickListener(view1 -> new CustomDatePicker(getContext(), ((TextView) view1)::setText, "1949-1-1 00:00", now, false).showSpecificTime(false).setIsLoop(false).show(now));
        view.findViewById(R.id.smokeTimeEnd).setOnClickListener(view1 -> {
            if (((TextView) ToolViewGroup.getNextView(view1, -2)).getText().toString().equals("起始时间")) {
                ToastUtil.info("请先选择起始时间");
                return;
            }
            new CustomDatePicker(getContext(), ((TextView) view1)::setText, now, "2100-1-1 00:00", false).showSpecificTime(false).setIsLoop(false).show(now);
        });
        view.findViewById(R.id.rentTimeStart).setOnClickListener(view1 -> new CustomDatePicker(getContext(), ((TextView) view1)::setText, "1949-1-1 00:00", now, false).showSpecificTime(false).setIsLoop(false).show(now));
        view.findViewById(R.id.rentTimeEnd).setOnClickListener(view1 -> {
            if (((TextView) ToolViewGroup.getNextView(view1, -2)).getText().toString().equals("起始时间")) {
                ToastUtil.info("请先选择起始时间");
                return;
            }
            new CustomDatePicker(getContext(), ((TextView) view1)::setText, now, "2100-1-1 00:00", false).showSpecificTime(false).setIsLoop(false).show(now);
        });
    }

    private void initLayout1(View view) {
        // 所在城市
        (sqCity = view.findViewById(R.id.sqCity)).setOnClickListener(v -> {
            ((TextView)v).setText("");
            AddressSelectWindow.show(sqCity, getContext(), region -> {
                ((TextView)v).append(region.regionName+" ");
                try {
                    switch (region.regionType) {
                        case "2":
                            pageData.put("provinceCode", region.code);
                            pageData.put("provinceName", region.regionName);
                            break;
                        case "3":
                            pageData.put("shopCityCode", region.code);
                            pageData.put("shopCityName", region.regionName);
                            break;
                        case "4":
                            pageData.put("countyCode", region.code);
                            pageData.put("countyName", region.regionName);
                            break;
                        case "5":
                            pageData.put("shopStreetCode", region.code);
                            pageData.put("shopStreetName", region.regionName);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                }
            });
        });
        //是否是连锁店
        ((RadioGroup) view.findViewById(R.id.shopIsChainshop)).setOnCheckedChangeListener((radioGroup, i) -> {
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setVisibility(i == R.id.liansYes ? View.VISIBLE : View.GONE);
            ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent()).setTag(i==R.id.liansYes?null:"nocheck");
        });
        // 实际经营者联系方式
        getVerifyCode = view.findViewById(R.id.join_get_verifycode);
        // 实际经营者是否为法人本人
        (shopLegalperson = view.findViewById(R.id.shopLegalperson)).addTextChangedListener(new MyTextWatcher(shopLegalperson));   //姓名
        editTexts.add(shopLegalperson);
        (shopLegalIdcard = view.findViewById(R.id.shopLegalIdcard)).addTextChangedListener(new MyTextWatcher(shopLegalIdcard));  // 身份证
        editTexts.add(shopLegalIdcard);
        (verifyCode = view.findViewById(R.id.join_verifycode)).addTextChangedListener(new MyTextWatcher(verifyCode));   // 验证码
        editTexts.add(verifyCode);
        (shopOwnerPhonenumber = view.findViewById(R.id.shopOwnerPhonenumber)).addTextChangedListener(new MyTextWatcher(shopOwnerPhonenumber));
        editTexts.add(shopOwnerPhonenumber);

        shopRealMan = view.findViewById(R.id.shopRealMan);
        shopRealManIdCard = view.findViewById(R.id.shopRealManIdCard);
        shopRealManPhone = view.findViewById(R.id.shopRealManPhone);
        ((RadioGroup) view.findViewById(R.id.relationShip)).setOnCheckedChangeListener((radioGroup, i) -> {
            View viewGroup = ToolViewGroup.getNextView((ViewGroup) radioGroup.getParent());
            if (((RadioButton) radioGroup.getChildAt(1)).isChecked()) {
                viewGroup.setVisibility(View.GONE);
                viewGroup.setTag("nocheck");
            } else {
                viewGroup.setVisibility(View.VISIBLE);
                viewGroup.setTag(null);
            }
            intAttachment();
        });
        //
        countdown = view.findViewById(R.id.join_get_verifycode_countdown);
        (getVerifyCode = view.findViewById(R.id.join_get_verifycode)).setOnClickListener(v -> {
            EditText phone = shopOwnerPhonenumber;
            if (phone.getText().toString().trim().length() != 11) {
                ToastUtil.error("手机号格式错误");
                return;
            }
            getVerifyCode.setEnabled(false);
            getVerifyCode.setText("正在获取验证码");
            CommonRequest.stopCountDown();
            CommonRequest.getVerifyCode(phone.getText().toString(), CommonRequest.VERIFY_CODE_TYPE_JOIN, new CommonRequest.VerifyCodeCallBack() {
                @Override
                public boolean onResult(int interval, String msg) {
                    if (interval > 0) {
                        ToolViewGroup.getNextView((View) v.getParent().getParent()).setVisibility(View.VISIBLE);
                        return true;
                    }
                    getVerifyCode.setEnabled(true);
                    getVerifyCode.setText("获取验证码");
                    if (interval < 0) {
                        ToastUtil.error(msg);
                    }
                    return false;
                }
                @Override
                public void onCountDown(int remain) {
                    if (remain > 0) {
                        getVerifyCode.setEnabled(false);
                        getVerifyCode.setText(remain + "s");
                    } else {
                        getVerifyCode.setEnabled(true);
                        getVerifyCode.setText("获取验证码");
                    }
                }
            });
        });
        (ifPhone = view.findViewById(R.id.join_if_phone)).setOnClickListener(view1 -> {
            if (shopLegalperson.getText().length()<=0) {
                ToastUtil.info("请输入实际经营者姓名");
                return;
            }
            if (shopOwnerPhonenumber.getText().length()<11) {
                ToastUtil.info("请输入正确的联系方式");
                return;
            }
            String name = shopLegalperson.getText().toString();
            String phoneNumber = shopOwnerPhonenumber.getText().toString();
            Map<String,Object> map = new HashMap<>();
            map.put("shopId",shopId);
            map.put("name",name);
            map.put("shopOwnerPhonenumber",phoneNumber);
            map.put("verificationCode",verifyCode.getText().toString().trim());
            map.put("businessType",CommonRequest.VERIFY_CODE_TYPE_JOIN);
            map.put("idCard",shopLegalIdcard.getText().toString().trim());
            dispatch(JoinStore.POST_JOIN_IF_PHONE,map);
        });
        for (EditText editText:editTexts){
            if (editText.getText()==null||editText.getText().toString().isEmpty()) {
                ifPhone.setEnabled(false);
                break;
            }else ifPhone.setEnabled(true);
        }
        ((ViewGroup)countdown.getParent()).getBackground().setAlpha(100);
    }

    private void setImageRight(boolean suc){
        Drawable rightDrawable = getResources().getDrawable(R.drawable.ic_join_msg);
        rightDrawable.setBounds(0, 0, rightDrawable.getMinimumWidth(), rightDrawable.getMinimumHeight());
        verifyCode.setCompoundDrawables(null,null,suc?null:rightDrawable,null);
        if (!suc) verifyCode.setEnabled(false);
        verifyCode.setTag(suc?"verificationCode":"onClick");
    }

    private class MyTextWatcher implements android.text.TextWatcher {
        private String tag = "";
        public MyTextWatcher(View view) {
            if (view.getTag()==null) return;
            tag = view.getTag().toString();
        }
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        @Override
        public void afterTextChanged(Editable editable) {
            if (tag.equals("shopOwnerPhonenumber")) changePhoneNumber(shopOwnerPhonenumber.getText().toString());
            if (tag.equals("verificationCode")&&editable.length()==6&&!editable.toString().equals("验证码已验证")) verifyForLocal();
            for (EditText editText:editTexts){
                if (editText.getText()==null||editText.getText().toString().isEmpty()) {
                    ifPhone.setEnabled(false);
                    break;
                }else ifPhone.setEnabled(true);
            }
        }
    }

    @BindAction(JoinStore.POST_JOIN_IF_PHONE)
    public void joinIfPhone(Map<String, String> map){
        if (map==null) return;
        countdown.setText(map.get("msg"));
        ((ViewGroup)countdown.getParent()).setVisibility(View.VISIBLE);
        if (map.get("suc").equals("false")){
            countdown.setTextColor(Color.parseColor("#FF5D5D"));
            ((ImageView)ToolViewGroup.getNextView(countdown,-1)).setImageResource(R.drawable.ic_join_err);
            ((ViewGroup)countdown.getParent()).setBackgroundColor(Color.parseColor("#ffff5d5d"));
            ((ViewGroup)countdown.getParent()).getBackground().setAlpha(100);
        }else{
            countdown.setTextColor(Color.parseColor("#47AB10"));
            ((ViewGroup)countdown.getParent()).setBackgroundColor(Color.parseColor("#fffffde0"));
            ((ImageView)ToolViewGroup.getNextView(countdown,-1)).setImageResource(R.drawable.ic_join_msg);
            ((ViewGroup)countdown.getParent()).getBackground().setAlpha(100);
        }
    }

    private void changePhoneNumber(String phoneNumber) {
        if (phoneNumber.length()<11) {
            getVerifyCode.setEnabled(false);
            return;
        }
        CommonRequest.checkPhoneVerified(getContext(), shopId + "", phoneNumber, isChecked -> {
//            getVerifyCode.setEnabled(!isChecked);
            if (isChecked){
                verifyCode.setText("验证码已验证");
                getVerifyCode.setEnabled(false);
            }else{
                verifyCode.setText("");
                verifyCode.setEnabled(true);
                getVerifyCode.setEnabled(true);
            }
            setImageRight(!isChecked);
        });
    }

    /**
     * 计算门头面积
     * @param leng
     * @param wh
     */
    private void setDateArea(String leng, String wh) {
        try {
            shopDoorArea.setText(new BigDecimal(leng).round(MathContext.DECIMAL32).multiply(new BigDecimal(wh).round(MathContext.DECIMAL32)).round(MathContext.DECIMAL32).setScale(2, BigDecimal.ROUND_DOWN).toString());
        } catch (NumberFormatException e) {
            shopDoorArea.setText("0");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (items != null && fujian != null) {
            ToolAttachment.addFujian(items, fujian, getActivity());
        }
    }


    /**
     * private LinearLayout pending_auditing,complete,reject;
     * private TextView currentAuditingNodeName,shopTotalScore,shopScoreGrade,doorAllowanceAmount,rentAllowanceAmount,xcCode,currentAuditingNodeNameNo,rejectReson;
     * private ListView auditingNodeList;
     * 显示节点
     */
    private void initViewTop() {
        String status = pageData.optString("status", null);
        if (status != null && !status.isEmpty()) {
            switch (status) {
                case "pending_auditing":
                    ToolViewGroup.getNextView(pending_auditing,-1).setVisibility(View.VISIBLE);
                    pending_auditing.setVisibility(View.VISIBLE);
                    pending_auditing.setVisibility(View.VISIBLE);
                    currentAuditingNodeName.setText(pageData.optString("currentAuditingNodeName", null));
                    getNodeInfo();
                    break;
                case "complete":
                    ToolViewGroup.getNextView(pending_auditing,-1).setVisibility(View.VISIBLE);
                    auditingNodeListContainer.setVisibility(View.GONE);
                    complete.setVisibility(View.VISIBLE);
                    shopTotalScore.setText(pageData.optString("shopTotalScore", null));
                    shopScoreGrade.setText(pageData.optString("shopScoreGrade", null));
                    xcCode.setText(pageData.optString("xcCode", null));
                    getNodeInfo();
                    break;
                case "reject":
                    ToolViewGroup.getNextView(pending_auditing,-1).setVisibility(View.VISIBLE);
                    reject.setVisibility(View.VISIBLE);
                    currentAuditingNodeNameNo.setText(pageData.optString("currentAuditingNodeName", null));
                    rejectReson.setText(pageData.optString("rejectReson", null));
                    getNodeInfo();
                    break;
                default:
                    getNodeInfo();
                    break;
            }
        }
    }

    /**
     * 初始化内容
     *
     * @param data
     */
    private void initViewDate(JSONObject data, boolean isUpdate) {
        if (data == null) return;
        try {
            ToolTagView.setTagView(frame_root, data.toString());
            ToolTagView.setTagSpinner(frame_root, data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ToolAttachment.disableAllView(frame_root, isUpdate);
        ToolAttachment.addFujian(items, fujian, getActivity());

        initViewTop();

        if (!isUpdate) {
            btnUpload.setText("去查看");
        }
        intAttachment();

        if (!fromLocal && (YTApplication.debugEnable || BuildConfig.DEBUG) && Config.isAutoFillAttachment && buttonInfos.isButton()) {
            // debug模式  设置自动填充
            ToolTagView.initViewDate(frame_root);
        }

        }

    private void intAttachment(){
        formData = new DTO<>();
//        formData = getViewForm();
        new Handler().postDelayed(() -> {
            ToolData.gainForms(frame_root, formData);
            dispatch(REQUEST_GET_JOIN_ATTACHMENT, formData);
        },500);
    }


    private DTO<String, Object> getViewForm() {
        String msg = null;
        DTO<String, Object> formData = new DTO<>();
        formData = ToolData.gainForms(frame_root, formData);
        if ("验证码已验证".equals(verifyCode.getText().toString())) {
            formData.remove("verificationCode");
        }
        for (String key : formData.keySet()) {
            ToolLog.i(key + "," + formData.get(key));
            if (key == null || key.isEmpty()) {
                continue;
            }
            if (key.equals("attachmentState")||key.equals("shopDesc")) continue;
            if (formData.get(key) == null || formData.get(key).toString().trim().isEmpty() || formData.get(key).equals("") || formData.get(key).equals("起始时间") ||formData.get(key).equals("结束时间")||formData.get(key).equals("请选择")) {
                msg = ToolData.joinMap.get(key) + "不能为空";
                break;
            } else {
                msg = checkFormat(key, formData);
                if (msg != null) {
                    break;
                }
            }
        }


        if (msg != null) {
            ToolAlert.dialog(getContext(), "保存提示", msg, (dialogInterface, i) -> {
            });
            return null;
        }
        return formData;
    }

    private String checkFormat(String key, DTO<String, Object> formData) {
        if ("phone".equals(key) && !formData.get(key).toString().isEmpty() && formData.get(key).toString().length() < 7) {
            return ToolData.mapContract.get("contactWay") + "格式错误";
        }
        return null;
    }

    private void internalSaveLocal() {
        formData = new DTO<>();
//        formData = getViewForm();
        ToolData.gainForms(frame_root, formData);
        formData.put("verificationCode",verifyCode.getText().toString());

        formData.put("provinceCode",pageData.optString("provinceCode"));
        formData.put("shopCityCode",pageData.optString("shopCityCode"));
        formData.put("countyCode",pageData.optString("countyCode"));
        formData.put("shopStreetCode",pageData.optString("shopStreetCode"));

        ToolLog.i("布局数据："+formData);
        dispatch(REQUEST_JOIN_SAVE, formData);
    }

    private void saveError() {

        final DTO<String, Object> form = new DTO<>();
        ToolData.gainForm(frame_root, form);
        ToolAlert.dialog(getContext(), "保存提示", "您已经上传了图片，是否需要将数据保存到本地？", (dialogInterface, i) -> {
            dispatch(REQUEST_JOIN_SAVE, formData);
        }, (dialog, which) -> {
            saveForError = false;
            getContext().finish();
        });
    }


    @Override
    public boolean needIntercept() {
        return saveForError;
    }

    @Override
    public boolean doBeforeFinish() {
        saveError();
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
}
