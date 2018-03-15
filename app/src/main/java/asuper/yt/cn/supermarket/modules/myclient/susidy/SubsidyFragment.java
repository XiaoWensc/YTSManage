package asuper.yt.cn.supermarket.modules.myclient.susidy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.main.MainActivity.MainFragment;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailDialog;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.SaveCompleteListener;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.modules.myclient.entities.NodeList;
import asuper.yt.cn.supermarket.utils.CommonRequest;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.ToolData;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import asuper.yt.cn.supermarket.views.CustomDatePicker;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class SubsidyFragment extends BaseFragment implements MainFragment, View.OnClickListener, MyClientDetailActivity.MyClientDetailFragment {

    public EditText shopName;
    public EditText shopLegalperson;
    public EditText shopcobberNo;
    public EditText financeNo;
    public EditText rentAmount;
    public EditText nowNode, nowRejectNode;
    public EditText auditStatus;
    public EditText auditOpinion;
    public EditText attchment_describe;

    private CustomDatePicker timePopupWindow;
    private TextView clickedPicker;
    private TextView joinTime;

    public View upload;

    public LinearLayout fujian;

    private LinearLayout frame_root;


    private AlertDialog uploadDialog;

    private ProgressBar uploadProgress;
    private TextView progressTextView;

    private DTO<String, Object> form;

    private String over;
    private int groupId;
    private boolean isUpdate;
    private int shopId;


    private ButtonInfos buttonInfos;
    private JSONObject pageData;
    private List<ImageGalleryActivity.ImageGalleryItem> items;


    private LinearLayout node_infos_container;
    private AppCompatCheckBox node_infos_title;


    public static final int REQUEST_GET_SUBSIDY_DETAIL = 0x0601;
    public static final int REQUEST_GET_SUBSIDY_NODE_INFO = 0x0602;
    public static final int REQUEST_SUBSIDY_UPLOAD = UploadStore.REQUEST_UPLOAD;
    public static final int REQUEST_SUBSIDY_COMMIT = 0x0605;
    public static final int REQUEST_SUBSIDY_SAVE = 0x0607;

    private NewSubsidyVO newSubsidyVO;
    private SaveCompleteListener saveListener;
    private ClientDetailDialog clientDetailDialog;


    private boolean isRecall = false;



    public static SubsidyFragment newInstance(ButtonInfos buttonInfos, int shopId, String over, int groupId) {
        final SubsidyFragment fragment = new SubsidyFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("bts", buttonInfos);
        bundle.putInt("id", shopId);
        fragment.setArguments(bundle);
        fragment.over = over;
        fragment.groupId = groupId;
        fragment.shopId = shopId;
        fragment.isUpdate = buttonInfos.isButton() || buttonInfos.isUpdate();
        fragment.buttonInfos = buttonInfos;
        return fragment;
    }

    public void getNodeInfo() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("applyId", newSubsidyVO.applyId);
        hashMap.put("intentionId", shopId);
        dispatch(REQUEST_GET_SUBSIDY_NODE_INFO, hashMap);
    }

    @BindAction(REQUEST_GET_SUBSIDY_NODE_INFO)
    public void getNodeInfoResult(NodeList nodeInfos) {
        if (nodeInfos != null && nodeInfos.resultObject.size() > 0) {
            for (NodeList.NodeInfo nodeInfo : nodeInfos.resultObject) {
                if (nodeInfo == null) continue;
                View v = LayoutInflater.from(getContext()).inflate(R.layout.item_nodeinfo, null);
                ((TextView) v.findViewById(R.id.auditMessage)).setText(nodeInfo.auditMessage);
                ((TextView) v.findViewById(R.id.auditorName)).setText(nodeInfo.taskDefName);
                node_infos_container.addView(v);
            }

            node_infos_title.setVisibility(View.VISIBLE);
            node_infos_title.setEnabled(true);
            node_infos_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    node_infos_container.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            node_infos_title.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_subsidy;
    }

    @Override
    protected void findView(View root) {
        clientDetailDialog = new ClientDetailDialog(getContext());
        initView(root);
        getSubsidyDetail();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new SubsidyStore(dependencyDelegate);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {
        isRecall = true;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (items != null && fujian != null) {
            ToolAttachment.addFujian(items, fujian, getActivity());
        }
    }


    private void getSubsidyDetail() {
        if (buttonInfos==null) return;
        HashMap<String, Object> map = new HashMap<>();
        if (buttonInfos.isButton()) {
            map.put("id", null);
            map.put("shopId", shopId);
        } else {
            map.put("shopId", shopId);
        }
        map.put("uploadPeople", Config.UserInfo.USER_ID);
        map.put("isUpdate", buttonInfos.isUpdate());
        map.put("isNew", buttonInfos.isButton());
        dispatch(REQUEST_GET_SUBSIDY_DETAIL, map);

    }

    @BindAction(REQUEST_GET_SUBSIDY_DETAIL)
    public void getSubsidyDetailResult(HashMap<String, Object> res) {
        if (res == null) return;
        pageData = (JSONObject) res.get("pageData");
        items = (List<ImageGalleryActivity.ImageGalleryItem>) res.get("items");
        if (pageData == null) return;
        JSONObject webdto = pageData.optJSONObject("subsidySubjectWebDTO");
        if (webdto != null && webdto.opt("dataJson") != null) {
            newSubsidyVO = ToolGson.fromJson(webdto.optString("dataJson", "{}"), NewSubsidyVO.class);
            newSubsidyVO.setAuditStatus(webdto.optString("auditStatus",null));
            newSubsidyVO.setAuditMessage (webdto.optString("auditMessage"));
            newSubsidyVO.setAuditNodeName(webdto.optString("auditNodeName"));
            newSubsidyVO.setAuditStatus (webdto.optString("auditStatus"));
            newSubsidyVO.setIsSendFrozen(res.get("isSendFrozen").toString());
            newSubsidyVO.setAuditTime(webdto.optLong("auditTime"));
            newSubsidyVO.setAuditNodeIndex(webdto.optString("auditNodeIndex"));
            newSubsidyVO.setStoreCode(webdto.optString("storeCode"));
        } else {
            newSubsidyVO = new NewSubsidyVO();
        }

        boolean fromLocal = (boolean) res.get("local");

        if (fromLocal || (!buttonInfos.isButton() && buttonInfos.isUpdate())) {
            ToolAttachment.disableAllView(frame_root, false);
            ((TextView) upload).setText("去查看");
            upload.setEnabled(true);
        }

        if (isRecall&&newSubsidyVO.getAuditNodeIndex().equals("1")){
            ((MyClientDetailActivity)getActivity()).showRecall(newSubsidyVO.applyId,newSubsidyVO.getStoreCode());
        }

        setViewData(newSubsidyVO);
    }

    public void uploadImg() {
        form = getViewForm();
        if (form != null) {
            if (!ToolAttachment.isImagesSatifyied(items)) return;
            clientDetailDialog.show("补贴提交", "检查无误后提交至服务器", "", new ClientDetailDialog.OnClientDialogConfirmListener() {
                @Override
                public void onConfirm() {
                    dispatch(REQUEST_SUBSIDY_UPLOAD, null);
                }
            });
        }
    }


    @BindAction(REQUEST_SUBSIDY_UPLOAD)
    public void uploadImgResult(boolean success) {
        if (success) {
            showProgress("正在提交");
            HashMap<String, Object> map = new HashMap<>();
            map.put("joinTime", joinTime.getText().toString());
            map.put("shop_id", shopId);
            map.put("creater_id", Config.UserInfo.USER_ID);
            JSONObject jsonObject = pageData.optJSONObject("subsidySubjectWebDTO");
            if (jsonObject != null) {
                map.put("storeCode", jsonObject.optString("storeCode"));
            }
            map.put("att", attchment_describe.getText().toString());
            for (int i = 0; i < items.size(); i++) {
                List<String> paths = new ArrayList<>();
                List<PhotoInfo> photoInfos = items.get(i).photoInfo;
                for (PhotoInfo photoInfo : photoInfos) {
                    paths.add(photoInfo.getPhotoPath());
                }
                map.put(items.get(i).info.key, ToolStringToList.ListToString(paths));
            }
            dispatch(REQUEST_SUBSIDY_COMMIT, map);
        } else {
            ToastUtil.error("上传图片失败");
        }
    }


    @BindAction(REQUEST_SUBSIDY_COMMIT)
    public void commitResult(boolean success) {
        dissmissProgress();
        if (success) {
            ToastUtil.success("提交成功");
            getActivity().setResult(101);
            getActivity().finish();
        } else {
//            ToastUtil.success("提交失败");
        }
    }

    public void initView(View view) {
        frame_root = (LinearLayout) view.findViewById(R.id.frame_root);
        shopName = (EditText) view.findViewById(R.id.shopName);
        shopLegalperson = (EditText) view.findViewById(R.id.shopLegalperson);
        shopcobberNo = (EditText) view.findViewById(R.id.shopcobberNo);
        financeNo = (EditText) view.findViewById(R.id.financeNo);
        rentAmount = (EditText) view.findViewById(R.id.rentAmount);
        nowNode = (EditText) view.findViewById(R.id.nowNode);
        joinTime = (TextView) view.findViewById(R.id.joinTime);

        node_infos_container = (LinearLayout) view.findViewById(R.id.node_infos_container);
        node_infos_title = (AppCompatCheckBox) view.findViewById(R.id.node_infos_title);
        attchment_describe = (EditText) view.findViewById(R.id.attchment_describe);
        attchment_describe.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        attchment_describe.setSingleLine(false);
        attchment_describe.setFilters(new InputFilter[]{ToolInputFilter.getEmojiFilter()});
        nowRejectNode = (EditText) view.findViewById(R.id.nowRejectNode);
        auditStatus = (EditText) view.findViewById(R.id.auditStatus);
        auditOpinion = (EditText) view.findViewById(R.id.auditOpinion);


        (fujian = (LinearLayout) view.findViewById(R.id.fujian)).setOnClickListener(this);

        (upload = view.findViewById(R.id.btnUpload)).setOnClickListener(this);
        joinTime.setOnClickListener(this);
        joinTime.setText(DateFormat.format("yyyy-MM-dd", new Date()));

        timePopupWindow = new CustomDatePicker(getContext(), new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) {
                joinTime.setText(time);
            }
        }, "2017-1-1 00:00", "2100-1-1 00:00", false);
        timePopupWindow.showSpecificTime(false);
        timePopupWindow.setIsLoop(false);
    }


    public void setViewData(NewSubsidyVO subsidyVO) {
        if (isUpdate && getActivity() != null && !getActivity().isFinishing() && !getActivity().isDestroyed()) {
//            ((DetailsActivity) getActivity()).enableSave(this);
        }

        if (subsidyVO == null) return;

        if (!isUpdate) {
            ((TextView) upload).setText("去查看");
        }
        ToolAttachment.addFujian(items, fujian, getActivity());
        shopName.setText(subsidyVO.shopName);
        shopLegalperson.setText(subsidyVO.realName);
        shopcobberNo.setText(subsidyVO.xcCode);
        financeNo.setText(subsidyVO.xcFinalName);
        rentAmount.setText(subsidyVO.rentAmount);
        nowNode.setText(subsidyVO.auditNodeName);
        nowRejectNode.setText(subsidyVO.auditNodeName);
        auditOpinion.setText(subsidyVO.auditMessage);
        attchment_describe.setText(subsidyVO.attachmentState);
        attchment_describe.setEnabled(isUpdate);
        joinTime.setEnabled(isUpdate);
        joinTime.setClickable(isUpdate);
        joinTime.setFocusable(isUpdate);


        joinTime.setText(subsidyVO.accpetTime);

        ToolLog.i(subsidyVO.toString());
        if (subsidyVO.auditStatus == null) return;
        switch (subsidyVO.auditStatus) {
            case "1":
                auditStatus.setText("审批中");
                ((View) nowNode.getParent()).setVisibility(View.VISIBLE);
                getNodeInfo();
                break;
            case "2":
                auditStatus.setText("审批通过");
                ((View) auditStatus.getParent()).setVisibility(View.VISIBLE);
                getNodeInfo();
                break;
            case "3":
                auditStatus.setText("审批拒绝");
                ((View) auditStatus.getParent()).setVisibility(View.VISIBLE);
                auditStatus.setTextColor(Color.RED);
                ((View) nowRejectNode.getParent()).setVisibility(View.VISIBLE);
                if (subsidyVO.auditMessage != null && !subsidyVO.auditMessage.isEmpty()) {
                    ((View) auditOpinion.getParent()).setVisibility(View.VISIBLE);
                }
                getNodeInfo();
                break;
        }

    }

    private DTO<String, Object> getViewForm() {
        String msg = null;
        DTO<String, Object> formData = new DTO<>();
        formData = ToolData.gainForm(frame_root, formData);
        for (String key : formData.keySet()) {
            ToolLog.i(key + "," + formData.get(key));
            if (key == null || key.isEmpty()) {
                continue;
            }
            if (formData.get(key) == null || formData.get(key).toString().isEmpty() || formData.get(key).equals("")) {
                msg = ToolData.joinMap.get(key) + "不能为空";
                break;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpload:
                ToolAttachment.gotoUpload(isUpdate, items);
                break;
            case R.id.joinTime:
                if (joinTime.getText().toString().trim().isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                    final String now = sdf.format(new Date());
                    timePopupWindow.show(now);
                } else {
                    timePopupWindow.show(joinTime.getText().toString());
                }
                break;
        }
    }

    @Override
    public void commit() {
        if (checkNeedResponseActivityOpreation()) {
            if ("0".equals(newSubsidyVO.isSendFrozen) || newSubsidyVO.isSendFrozen == null || newSubsidyVO.isSendFrozen.trim().isEmpty()) {
                ToastUtil.error("补贴冻结款尚未发放，请等财务审核放款之后再行提交");
                return;
            }
            uploadImg();
        }
    }

    @Override
    public void save(SaveCompleteListener listener) {
        if (checkNeedResponseActivityOpreation()) {
            saveListener = listener;
            final DTO<String, Object> form = new DTO<>();
            ToolData.gainForm(frame_root, form);
            if (pageData != null) {

                newSubsidyVO.attachmentState = attchment_describe.getText().toString();
                newSubsidyVO.accpetTime = joinTime.getText().toString();
                JSONObject jsonObject = pageData.optJSONObject("subsidySubjectWebDTO");
                if (jsonObject == null) try {
                    pageData.put("subsidySubjectWebDTO", new JSONObject("{}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject != null) {
                    try {
                        jsonObject.put("dataJson", ToolGson.toJson(newSubsidyVO));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.error("保存失败");
                        return;
                    }
                }
                if (items != null) {
                    HashMap<String, String> imgs = new HashMap<>();
                    for (int i = 0; i < items.size(); i++) {
                        imgs.put(items.get(i).info.key, ToolGson.toJson(items.get(i).photoInfo));
                    }
                    try {
                        pageData.put("imgs", new JSONObject(imgs));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ToastUtil.error("保存失败");
                    }
                }

                SubsidyLocalVO localVo = new SubsidyLocalVO();
                localVo.user_id = Config.UserInfo.USER_ID;
                localVo.dataJson = pageData.toString();

//                localVo.isOver = state.data.isOver;
//                localVo.groupId = state.data.groupId;
                localVo.intentionId = shopId + "";
//                localVo.shopAssigner = newSubsidyVO.shopAssigner;
//                localVo.shopName = newSubsidyVO.shopName;
//                localVo.shopAddress = newSubsidyVO.shopAddress;
//                localVo.shopPhoneNumber = newSubsidyVO.shopPhoneNumber;
//                localVo.auditTime = newSubsidyVO.auditTime;
                try {
                    ToolDbOperation.getSubsidyDao().createOrUpdate(localVo);
                    ((TextView) upload).setText("去查看");
                    isUpdate = false;
                    ToastUtil.success("保存成功");
                    CommonRequest.notifyServerWhenSaveLocal(localVo.intentionId);
                    if (saveListener != null) saveListener.result(true, this);
                    ToolAttachment.disableAllView(frame_root, false);
                    upload.setEnabled(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                    ToastUtil.error("保存失败");
                }
            }

        }
    }

    private boolean checkNeedResponseActivityOpreation() {
        if (buttonInfos == null) return false;
        return buttonInfos.isButton() || buttonInfos.isUpdate();
    }


    @Override
    public void cancel() {

    }

    @Override
    public void edit() {
        if (buttonInfos != null) {
            isUpdate = buttonInfos.isButton() || buttonInfos.isUpdate();
            if (isUpdate) {
                upload.setEnabled(true);
                joinTime.setClickable(true);
                joinTime.setFocusable(true);
                joinTime.setEnabled(true);
                ((TextView) upload).setText("去上传");
            }
        }
    }
}
