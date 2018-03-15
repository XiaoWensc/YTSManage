package asuper.yt.cn.supermarket.modules.myclient.contact;

import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.BaseSelectInfo;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.MerchantJoinSelectProperty;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailDialog;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.SaveCompleteListener;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.ToolData;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import asuper.yt.cn.supermarket.views.CustomDatePicker;
import asuper.yt.cn.supermarket.views.RadioButton;
import asuper.yt.cn.supermarket.views.RadioGroupEx;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolString;
import supermarket.cn.yt.asuper.ytlibrary.widgets.Option;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SingleSpinner;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SpinnerAdapter;

/**
 * 客户详情-合同审批
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class ContractFragment extends BaseFragment implements MyClientDetailActivity.MyClientDetailFragment,View.OnClickListener {

    private int shopId;

    private SingleSpinner payType;

    private RadioGroupEx agreementType;

    private EditText sellerIdCard, attchment_describe, agreementName, agreementSeller, contactWay, shopAddrees, subjectContent, contractPartyMaster, contractPartyFollow, agreementMoney,doorAllowanceAmount,rentAllowanceAmount;

    private LinearLayout frame_root;

    private LinearLayout pending_auditing, complete, reject;

    private TextView currentAuditingNodeName, failNodeName, failDesc,performStartDateStr, performEndDateStr;

    private View btnUpload;

    private LinearLayout fujian;

    private DTO<String, Object> formData;

    private String over;
    private int groupId;


    private boolean isRecall = false;


    private LinearLayout node_infos_container;
    private AppCompatCheckBox node_infos_title;

    private String isNew;

    private ButtonInfos buttonInfos;
    private ClientInfoDetail detail;
    private JSONObject pageData;
    private List<ImageGalleryActivity.ImageGalleryItem> items;
    private String uniqueType;
    private boolean isUpdate;
    private float rentMax,doorMax;

    public static final int REQUEST_GET_CONTRACT_DETAIL = 0x0501;
    public static final int REQUEST_GET_CONTRACT_NODE_INFO = 0x0502;
    public static final int REQUEST_CONTRACT_COMMIT = 0x0503;
    public static final int REQUEST_CONTRACT_UPLOAD = UploadStore.REQUEST_UPLOAD;
    public static final int REQUEST_CONTRACT_SAVE = 0x0507;

    private CustomDatePicker picker;
    private TextView clickedTimer;
    private SaveCompleteListener saveListener;
    private ClientDetailDialog clientDetailDialog;

    public static ContractFragment newInstance(ClientInfoDetail buttonInfos, int shopId, String over, int groupId) {
        Bundle args = new Bundle();
        args.putSerializable("buttonInfos", buttonInfos);
        args.putInt("id", shopId);
        ContractFragment fragment = new ContractFragment();
        fragment.setArguments(args);
        fragment.over = over;
        fragment.groupId = groupId;
        fragment.shopId = shopId;
        fragment.detail = buttonInfos;
        fragment.isNew = buttonInfos.getButtonInfos().get(1).isNew;
        fragment.buttonInfos = buttonInfos.getButtonInfos().get(1);
        fragment.isUpdate = fragment.buttonInfos.isButton() || fragment.buttonInfos.isUpdate();
        return fragment;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_contract;
    }

    @Override
    protected void findView(View root) {
        formData = new DTO<>();
        initView(root);
        getContractDetail();
    }


    public void getNodeInfo(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("applyId", pageData.optString("auditId", null));
        hashMap.put("intentionId", shopId);
        dispatch(REQUEST_GET_CONTRACT_NODE_INFO,hashMap);
    }

    @BindAction(REQUEST_GET_CONTRACT_NODE_INFO)
    public void getNodeInfoResult(NodeList nodeInfos){
        ToolLog.i("合同审批："+nodeInfos.toString());
        if(nodeInfos != null && nodeInfos.resultObject.size() > 0){
            for(NodeList.NodeInfo nodeInfo:nodeInfos.resultObject){
                if(nodeInfo == null) continue;
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_nodeinfo,null);
                ((TextView)v.findViewById(R.id.auditMessage)).setText(nodeInfo.auditMessage);
                ((TextView)v.findViewById(R.id.auditorName)).setText(nodeInfo.taskDefName);
                node_infos_container.addView(v);
            }

            node_infos_title.setVisibility(View.VISIBLE);
            node_infos_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    node_infos_container.setVisibility(isChecked?View.VISIBLE:View.GONE);
                }
            });
        }else{
            node_infos_title.setVisibility(View.GONE);
        }
    }

    private void getContractDetail(){
        if (buttonInfos==null) return;
        Map<String, Object> map = new HashMap<>();
        if (!buttonInfos.isButton()) {
            map.put("approveId", buttonInfos.getParameterId());
        }
        map.put("employeeId", Config.UserInfo.USER_ID);
        map.put("intentionId", shopId);
        map.put("isUpdate", buttonInfos.isUpdate());
        map.put("isNew", buttonInfos.isButton());
        dispatch(REQUEST_GET_CONTRACT_DETAIL,map);
    }

    @BindAction(REQUEST_GET_CONTRACT_DETAIL)
    public void getContractDetailResult(HashMap<String, Object> res){
        if(res == null) return;
        pageData = (JSONObject) res.get("pageData");
        items = (List<ImageGalleryActivity.ImageGalleryItem>) res.get("items");
        try {
            Contract contract = ToolGson.fromJson(pageData.toString(), Contract.class);



            setViewData(contract,isUpdate);
        }catch (Exception e){

        }
        boolean fromLocal = (boolean) res.get("local");
        if(fromLocal || (!buttonInfos.isButton() && buttonInfos.isUpdate())){
            ToolAttachment.disableAllView(frame_root,false);
            ((TextView)btnUpload).setText("去查看");
            btnUpload.setEnabled(true);
        }


        if (isRecall&&pageData.optInt("auditNodeIndex",0)==1){
            ((MyClientDetailActivity)getActivity()).showRecall(pageData.optString("auditId", null),shopId+"");
        }
    }

    public void uploadImg() {
        formData = getViewForm();
        if (formData != null) {
            if (!ToolAttachment.isImagesSatifyied(items)) return;
            clientDetailDialog.show("合同提交", "检查无误后提交至服务器", "", new ClientDetailDialog.OnClientDialogConfirmListener() {
                @Override
                public void onConfirm() {
                    dispatch(REQUEST_CONTRACT_UPLOAD, null);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(items != null && fujian != null) {
            ToolAttachment.addFujian(items, fujian, getActivity());
        }
    }

    @BindAction(REQUEST_CONTRACT_UPLOAD)
    public void uploadImgResult(boolean success) {
        if(success){
            showProgress("正在提交");
            DTO<String,Object> form = formData;
            Map<String, Object> map = new HashMap<>();
            if (buttonInfos.isUpdate()) {
                map.put("approveId", buttonInfos.getParameterId());
                map.put("gradeId", pageData.optString("gradeId",null));
            } else {
                map.put("gradeId", buttonInfos.getParameterId());
            }
            map.put("intentionId", shopId);
            map.put("initiatePeople", Config.UserInfo.USER_ID);
            map.put("agreementType", form.get("agreementType"));
            map.put("payType", form.get("payType"));
            map.put("agreementSeller", form.get("agreementSeller"));
            map.put("contractPartyMaster", form.get("contractPartyMaster"));
            map.put("contractPartyFollow", form.get("contractPartyFollow"));
            map.put("doorAllowanceAmount", form.get("doorAllowanceAmount"));
            map.put("rentAllowanceAmount", form.get("rentAllowanceAmount"));
            map.put("contractPartyFollow", form.get("contractPartyFollow"));
            map.put("shopAddrees", form.get("shopAddrees"));
            map.put("agreementName", form.get("agreementName") + "加盟合同");
            map.put("contactWay", form.get("contactWay"));
            map.put("subjectContent", form.get("subjectContent") + "加盟协议");
            map.put("performStartDateStr", form.get("performStartDateStr") + " 00:00:00");
            map.put("performEndDateStr", form.get("performEndDateStr") + " 00:00:00");
            map.put("agreementMoney", form.get("agreementMoney"));
            map.put("sellerIdCard", form.get("sellerIdCard"));
            map.put("attachmentState", attchment_describe.getText().toString());

            for (int i = 0; i < items.size(); i++) {
                List<String> paths = new ArrayList<>();
                List<PhotoInfo> photoInfos = items.get(i).photoInfo;
                for (PhotoInfo photoInfo : photoInfos) {
                    paths.add(photoInfo.getPhotoPath());
                }
                map.put(items.get(i).info.key, ToolStringToList.ListToString(paths));
            }
            dispatch(REQUEST_CONTRACT_COMMIT,map);
        }else {
            ToastUtil.error("上传图片失败");
        }
    }


    @BindAction(REQUEST_CONTRACT_COMMIT)
    public void commitResult(boolean success) {
        dissmissProgress();
        if(success){
            ToastUtil.success("提交成功");
            getActivity().setResult(101);
            getActivity().finish();
        }else{
//            ToastUtil.success("提交失败");
        }
    }

    @BindAction(REQUEST_CONTRACT_SAVE)
    public void saveResult(boolean success) {
        if(success){
            ToastUtil.success("保存成功");
            isUpdate = false;
            ToolAttachment.disableAllView(frame_root,false);
            btnUpload.setEnabled(true);
            ((TextView)btnUpload).setText("去查看");
        }else{
            ToastUtil.success("保存失败");
        }
        if(saveListener != null) saveListener.result(success,this);
    }

    public void initView(View view) {
        clientDetailDialog = new ClientDetailDialog(getContext());
        uniqueType = shopId+"_2";
        performStartDateStr = (TextView) view.findViewById(R.id.performStartDateStr);
        performEndDateStr = (TextView) view.findViewById(R.id.performEndDateStr);

        pending_auditing = (LinearLayout) view.findViewById(R.id.pending_auditing);
        complete = (LinearLayout) view.findViewById(R.id.complete);
        reject = (LinearLayout) view.findViewById(R.id.reject);
        currentAuditingNodeName = (TextView) view.findViewById(R.id.currentAuditingNodeName);
        failNodeName = (TextView) view.findViewById(R.id.failNodeName);
        failDesc = (TextView) view.findViewById(R.id.failDesc);


        node_infos_container = (LinearLayout) view.findViewById(R.id.node_infos_container);
        node_infos_title = (AppCompatCheckBox) view.findViewById(R.id.node_infos_title);


        agreementType = (RadioGroupEx) view.findViewById(R.id.agreementType);
        agreementType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                android.widget.RadioButton radioButton = (android.widget.RadioButton) agreementType.findViewById(checkedId);
                if(radioButton != null){
                    ((TextView)((ViewGroup)agreementType.getParent()).getChildAt(0)).setText(radioButton.getText());
                }
            }
        });
        payType = (SingleSpinner) view.findViewById(R.id.payType);
        frame_root = (LinearLayout) view.findViewById(R.id.frame_root);

        attchment_describe = (EditText) view.findViewById(R.id.attchment_describe);
        attchment_describe.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        attchment_describe.setSingleLine(false);
        attchment_describe.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter()});
        agreementName = (EditText) view.findViewById(R.id.agreementName);
        agreementSeller = (EditText) view.findViewById(R.id.agreementSeller);
        agreementSeller.setFilters(new InputFilter[]{ToolInputFilter.PERSON_NAME});
        contactWay = (EditText) view.findViewById(R.id.contactWay);
        shopAddrees = (EditText) view.findViewById(R.id.shopAddrees);
        subjectContent = (EditText) view.findViewById(R.id.subjectContent);
        contractPartyMaster = (EditText) view.findViewById(R.id.contractPartyMaster);
        contractPartyFollow = (EditText) view.findViewById(R.id.contractPartyFollow);
        agreementMoney = (EditText) view.findViewById(R.id.agreementMoney);
        sellerIdCard = (EditText) view.findViewById(R.id.sellerIdCard);
        rentAllowanceAmount  = (EditText) view.findViewById(R.id.contract_rentsubsidy);
        doorAllowanceAmount  = (EditText) view.findViewById(R.id.contract_doorsubsidy);
        if("1".equals(isNew)){
            ((TextView)((ViewGroup)rentAllowanceAmount.getParent()).getChildAt(0)).getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            ((TextView)((ViewGroup)doorAllowanceAmount.getParent()).getChildAt(0)).getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            rentAllowanceAmount.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            doorAllowanceAmount.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG );
            rentAllowanceAmount.setEnabled(false);
            doorAllowanceAmount.setEnabled(false);
            ((ViewGroup)rentAllowanceAmount.getParent()).setVisibility(View.GONE);
            ((ViewGroup)doorAllowanceAmount.getParent()).setVisibility(View.GONE);
        }
        sellerIdCard = (EditText) view.findViewById(R.id.sellerIdCard);
        rentAllowanceAmount.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(dest.toString());
                    if(bigDecimal.floatValue() <= 0){
                        rentAllowanceAmount.setText(new char[]{source.charAt(0)},start,end);
                        return "";
                    }
                }catch (Exception e){

                }
                return null;
            }
        }});
        doorAllowanceAmount.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(dest.toString());
                    if(bigDecimal.floatValue() <= 0){
                        doorAllowanceAmount.setText(new char[]{source.charAt(0)},start,end);
                        return "";
                    }
                }catch (Exception e){

                }
                return null;
            }
        }});
        rentAllowanceAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(rentAllowanceAmount.getText().toString());
                    if(bigDecimal.floatValue() > rentMax){
                        rentAllowanceAmount.setText(rentMax+"");
                    }
                }catch (Exception e){
                    rentAllowanceAmount.setText("0");
                }
            }
        });

        doorAllowanceAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    BigDecimal bigDecimal = new BigDecimal(doorAllowanceAmount.getText().toString());
                    if(bigDecimal.floatValue() > doorMax){
                        doorAllowanceAmount.setText(doorMax+"");
                    }
                }catch (Exception e){
                    doorAllowanceAmount.setText("0");
                }
            }
        });


        btnUpload = view.findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(this);
        fujian = (LinearLayout) view.findViewById(R.id.fujian);

        initTimeView();  //初始化时间选择
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new ContactStore(dependencyDelegate);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {
        isRecall = true;
    }

    /**
     * 设置静态数据
     */
    private void setViewData(Contract rac, boolean update) {
        if (isDetached()) return;
        agreementType.removeAllViews();
        BaseSelectInfo info = rac.getBaseSelectInfo();
        if (info != null) {
            for (int i = 0; i < info.getAgreementType().size(); i++) {
                MerchantJoinSelectProperty priperty = info.getAgreementType().get(i);
                RadioButton radio = new RadioButton(getContext());
//            radio.setEnabled(b);
                radio.setText(priperty.getValue());
                radio.setKey("agreementType");
                radio.setId(i);
                radio.setValue(priperty.getKey());
                radio.setButtonDrawable(null);
                Drawable drawable = getResources().getDrawable(R.drawable.ic_sl_checkbox);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                radio.setCompoundDrawables(drawable,null,null,null);
                radio.setCompoundDrawablePadding(20);
                RadioGroup.LayoutParams params = new RadioGroupEx.LayoutParams(-2,-2);
                params.setMargins(0,0,30,0);
                agreementType.addView(radio,params);
                if(i == 0) {
                    agreementType.check(i);
                }
                if (rac.getAgreementType() != null && rac.getAgreementType().equals(info.getAgreementType().get(i).getKey())) {
                    agreementType.check(i);
                }
            }

            setSpinner(payType, info.getPayType());
        }
        if(!update) {
            ((TextView)btnUpload).setText("去查看");
        }
        payType.setEnabled(update);
        setViewDataTxt(rac, update);
        ToolAttachment.addFujian(items,fujian,getActivity());
    }

    /**
     * 设置默认数据
     *
     * @param ract
     */
    private void setViewDataTxt(Contract ract, boolean update) {
        if (update && getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
//            ((DetailsActivity) getActivity()).enableSave(this);
        }

        try {
            doorMax = new BigDecimal(ract.doorAllowanceAmountMax).floatValue();
            rentMax = new BigDecimal(ract.rentAllowanceAmountMax).floatValue();
        }catch (Exception e){

        }

        performStartDateStr.setText(ract.getPerformStartDateStr());
        performStartDateStr.setEnabled(update);
        performEndDateStr.setText(ract.getPerformEndDateStr());
        performEndDateStr.setEnabled(update);
        agreementName.setText(ract.getAgreementName() + (update ? "" : "加盟合同"));
        agreementName.setEnabled(false);
        agreementSeller.setText(ract.getAgreementSeller());
        agreementSeller.setEnabled(false);
        contactWay.setText(ract.getContactWay());
        contactWay.setEnabled(false);
        shopAddrees.setText(ract.getShopAddrees());
        shopAddrees.setEnabled(false);
        subjectContent.setText(ract.getSubjectContent() + (update ? "" : "加盟协议"));
        subjectContent.setEnabled(false);
        contractPartyMaster.setText(ract.getContractPartyMaster());
        contractPartyMaster.setEnabled(false);
        contractPartyFollow.setText(ract.getContractPartyFollow());
        contractPartyFollow.setEnabled(false);
        agreementMoney.setText(ract.getAgreementMoney());
        agreementMoney.setEnabled(update);
        sellerIdCard.setText(ract.getSellerIdCard());
        sellerIdCard.setEnabled(update && (ract.getSellerIdCard() == null || ract.getSellerIdCard().isEmpty()));
        attchment_describe.setText(ract.attachmentState);
        attchment_describe.setEnabled(update);
        doorAllowanceAmount.setText(ract.doorAllowanceAmount);
        doorAllowanceAmount.setEnabled(update);
        rentAllowanceAmount.setText(ract.rentAllowanceAmount);
        rentAllowanceAmount.setEnabled(update);
        if("3".equals(ract.getAgreementStatus()) && detail.getButtonInfos().size() > 2) {
            int rentAmount = 0;
            try{
                rentAmount = new BigDecimal(ract.rentAllowanceAmount.trim()).intValue();
            }catch (Exception e){

            }
            if (rentAmount == 0) {
                ToastUtil.info("租金补贴金额为0，不可提交。");
            }
        }
        //当店铺地址，乙方姓名等不可编辑时，可以显示多行
        agreementName.setSingleLine(update);
        shopAddrees.setSingleLine(update);
        subjectContent.setSingleLine(update);
        contractPartyMaster.setSingleLine(update);
        contractPartyFollow.setSingleLine(update);
        if (ract.getBaseSelectInfo() != null) {
            List<MerchantJoinSelectProperty> info = ract.getBaseSelectInfo().getAgreementType();
            for (int i = 1; i < info.size(); i++) {
                RadioButton rad = (RadioButton) agreementType.getChildAt(i);
                rad.setEnabled(update);
            }
            payType.setSelection(getIndexSelection(ract.getPayType(), ract.getBaseSelectInfo().getPayType()));
        }

        payType.setEnabled(false);

       initViewTop(ract);

        if("1".equals(isNew)){
            doorAllowanceAmount.setText("-");
            rentAllowanceAmount.setText("-");
            doorAllowanceAmount.setEnabled(false);
            rentAllowanceAmount.setEnabled(false);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload:
                ToolAttachment.gotoUpload(isUpdate,items);
                break;
        }
    }


    private DTO<String, Object> getViewForm() {
        String msg = null;
        DTO<String, Object> formData = new DTO<>();
        formData = ToolData.gainForm(frame_root, formData);
        for (String key : formData.keySet()) {
            ToolLog.i(key + "," + formData.get(key));
            if (formData.get(key) == null || formData.get(key).toString().isEmpty() || formData.get(key).toString().equals("")) {
                msg = ToolData.mapContract.get(key) + "不能为空";
                break;
            } else {
                msg = checkFormat(key, formData);
                if (msg != null) {
                    break;
                }
            }
        }
        if (msg != null) {
            ToolAlert.dialog(getContext(), "保存提示", msg, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            return null;
        }
        return formData;
    }

    private String checkFormat(String key, DTO<String, Object> formData) {
        if ("sellerIdCard".equals(key) && !ToolString.isROCIDCard(formData.get(key).toString())) {
            return ToolData.mapContract.get("sellerIdCard") + "格式错误";
        }
        return null;
    }

    /**
     * 时间选择效果实现
     */
    private void initTimeView() {
        picker = new CustomDatePicker(getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                if(clickedTimer == null){
                    clickedTimer = performStartDateStr;
                }
                clickedTimer.setText(time);
            }
        },"2017-1-1 00:00","2100-1-1 00:00",false);
        picker.showSpecificTime(false);
        picker.setIsLoop(false);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        final String now = sdf.format(new Date());
        performStartDateStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedTimer = performStartDateStr;
                if(performEndDateStr.getText().toString().trim().isEmpty()) {
                    picker.show(now);
                }else{
                    picker.show(performStartDateStr.getText().toString());
                }
            }
        });
        performEndDateStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(performStartDateStr.getText().toString().trim().isEmpty()){
                    ToastUtil.info("请先选择开始时间");
                    return;
                }
                clickedTimer = performEndDateStr;
                if(performEndDateStr.getText().toString().trim().isEmpty()) {
                    picker.show(performStartDateStr.getText().toString());
                }else{
                    picker.show(performEndDateStr.getText().toString());
                }
            }
        });

        if (buttonInfos!=null&&!buttonInfos.isButton() && buttonInfos.isUpdate()) {
            performEndDateStr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedTimer = performEndDateStr;
                    if(performEndDateStr.getText().toString().trim().isEmpty()){
                        picker.show(now);
                    }else {
                        picker.show(performEndDateStr.getText().toString());
                    }
                }
            });
        }


    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 设置Spinner数据
     *
     * @param spin
     * @param array
     */
    private SingleSpinner setSpinner(SingleSpinner spin, List<MerchantJoinSelectProperty> array) {
        List<Option> options = getOptions(array);
        SpinnerAdapter mSpinnerAdapter = new SpinnerAdapter(getContext(), R.layout.view_spinner_drop_list_hover, options);
        mSpinnerAdapter.setDropDownViewResource(R.layout.view_spinner_drop_list_ys);
        spin.setAdapter(mSpinnerAdapter);
        return spin;
    }

    /**
     * 获取Spinner 数据
     *
     * @param array
     * @return
     */
    private List<Option> getOptions(List<MerchantJoinSelectProperty> array) {

        List<Option> data = new ArrayList<>();
        data.add(new Option( "请选择",""));
        for (int i = 0; i < array.size(); i++) {
            MerchantJoinSelectProperty property = array.get(i);
            data.add(new Option(property.getValue(),property.getKey()));
        }
        return data;
    }

    /**
     * 获取选中位置
     *
     * @param index
     * @param listVO
     * @return
     */
    private int getIndexSelection(String index, List<MerchantJoinSelectProperty> listVO) {
        List<Option> opt = getOptions(listVO);
        for (int i = 0; i < opt.size(); i++) {
            if (index != null && index.equals(opt.get(i).getLabel())) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 显示节点
     */
    private void initViewTop(Contract ract) {
        if (ract.getAgreementStatus() != null) {
            switch (ract.getAgreementStatus()) {
                case "1":
                    pending_auditing.setVisibility(View.VISIBLE);
                    currentAuditingNodeName.setText(ract.getFailNodeName());
                    getNodeInfo();
                    break;
                case "2":
                    reject.setVisibility(View.VISIBLE);
                    failNodeName.setText(ract.getFailNodeName());
                    failDesc.setText(ract.getFailDesc());
                    getNodeInfo();
                    break;
                case "3":
                    complete.setVisibility(View.VISIBLE);
                    getNodeInfo();
                    break;
            }
        }
    }

    public void setContractExtraInfo(String shopName, String legalPerson, String phoneNumber) {
//        if (model.getState().data.ract != null) {
//            model.getState().data.ract.setLegalPersonName(legalPerson);
//            model.getState().data.ract.setPhoneNumber(phoneNumber);
//            model.getState().data.ract.setShopName(shopName);
//        }
    }

    private boolean checkNeedResponseActivityOpreation(){
        if(buttonInfos == null) return false;
        return  buttonInfos.isButton() || buttonInfos.isUpdate();
    }


    @Override
    public void commit() {
        if(checkNeedResponseActivityOpreation()){
            uploadImg();
        }
    }

    @Override
    public void save(SaveCompleteListener listener) {
        if(checkNeedResponseActivityOpreation()&&frame_root!=null&&formData!=null){
            formData = ToolData.gainForm(frame_root,formData);
            formData.put("att",attchment_describe.getText().toString());
            dispatch(REQUEST_CONTRACT_SAVE,formData);
            saveListener = listener;
        }
    }

    @Override
    public void cancel() {

    }

    @Override
    public void edit() {
        if(buttonInfos != null) {
            isUpdate = buttonInfos.isButton() || buttonInfos.isUpdate();
            if(isUpdate) {
                ToolAttachment.disableAllView(frame_root,true);
                try {
                    Contract contract = ToolGson.fromJson(pageData.toString(), Contract.class);
                    sellerIdCard.setEnabled(isUpdate && (contract.getSellerIdCard() == null || contract.getSellerIdCard().isEmpty()));
                }catch (Exception e){

                }
                agreementName.setEnabled(false);
                agreementSeller.setEnabled(false);
                contactWay.setEnabled(false);
                shopAddrees.setEnabled(false);
                subjectContent.setEnabled(false);
                contractPartyMaster.setEnabled(false);
                contractPartyFollow.setEnabled(false);
                btnUpload.setEnabled(true);
                payType.setEnabled(false);
                if("1".equals(isNew)){
                    doorAllowanceAmount.setText("-");
                    rentAllowanceAmount.setText("-");
                    doorAllowanceAmount.setEnabled(false);
                    rentAllowanceAmount.setEnabled(false);
                }
                ((TextView)btnUpload).setText("去上传");
            }
        }
    }
}
