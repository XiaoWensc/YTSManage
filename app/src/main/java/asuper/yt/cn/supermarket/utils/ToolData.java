package asuper.yt.cn.supermarket.utils;

import android.inputmethodservice.ExtractEditText;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.views.CheckBox;
import asuper.yt.cn.supermarket.views.RadioButton;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolString;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SingleSpinner;

/**
 * 数据工具类
 *
 * @author zxw
 * @version 1.0
 */
public class ToolData {

    public static final String TAG = "ToolData";
    /**
     * 数据分页条数
     */
    public static Integer pageSize = 10;

    static {
        try {
            String value = ToolProperties.readAssetsProp("config.properties", "pageSize");
            if (ToolString.isNoBlankAndNoNull(value)) {
                pageSize = Integer.valueOf(value);
            }
        } catch (Exception e) {
            Log.w(TAG, "读取配置文件assets目录config.properties文件pageSize失败，原因：" + e.getMessage());
        }
    }

    /**
     * 添加客户数据字典
     */
    public static Map<String, String> addClientMap = new HashMap<>();


    public static final String TAG_IMG_BANNER = "beta_upgrade_banner";
    public static final String TAG_TITLE = "beta_title";
    public static final String TAG_UPGRADE_INFO = "beta_upgrade_info";
    public static final String TAG_UPGRADE_FEATURE = "beta_upgrade_feature";
    public static final String TAG_CANCEL_BUTTON = "beta_cancel_button";
    public static final String TAG_CONFIRM_BUTTON = "beta_confirm_button";
    public static final String TAG_TIP_MESSAGE = "beta_tip_message";

    static {
        //
        addClientMap.put("phone", "经营者电话");
        addClientMap.put("name", "经营者姓名");
        addClientMap.put("isJoin", "店主意向");
        addClientMap.put("addres", "店铺地址");
        addClientMap.put("shopName", "店铺名称");
        addClientMap.put("sqCity", "申请城市");
        addClientMap.put("lotlatName", "定位信息");
        addClientMap.put("lotlat", "定位地址");
    }

    /**
     * 加盟表key字典
     */
    public static Map<String, String> joinMap = new HashMap<>();

    static {
        joinMap.put("shopName", "店铺名称");
        joinMap.put("shopLegalperson", "实际经营者姓名");
        joinMap.put("shopOwnerPhonenumber", "实际经营者手机号码");
        joinMap.put("shopAddress", "店铺地址");
        joinMap.put("shopIsChainshop", "是否连锁");
        joinMap.put("isNewOpen", "是否新开");
        joinMap.put("relationShip", "实际经营者");
        joinMap.put("shopMonthRent", "月租金");
        joinMap.put("city", "城市");
        joinMap.put("shopCityGrade", "城市评级");
        joinMap.put("make", "补贴上浮比例");
        joinMap.put("shopChainCount", "连锁店数量");
        joinMap.put("shopownerAge", "服务员年龄");
        joinMap.put("shopAppearance", "内外形象");
        joinMap.put("shopDesc", "店铺描述");
        joinMap.put("shopServicingtime", "营业年限");
        joinMap.put("shopAcreage", "面积");
        joinMap.put("shopSales", "月销售额");
        joinMap.put("shoType", "店铺类型");
        joinMap.put("shopTakeawayPlatform", "外卖平台");
        joinMap.put("shopCashierEquipment", "收银设备");
        joinMap.put("shopping", "商品供应");
        joinMap.put("subsidy", "补贴方式");
        joinMap.put("shopSweepPayment", "扫码支付");
        joinMap.put("onlineShopping", "网购行为");
        joinMap.put("shopLighting", "店内灯光");
        joinMap.put("sales", "促销频率");
        joinMap.put("shopOwnerAgeRange", "经营者年龄");
        joinMap.put("shopValueaddedServices", "增值服务");
        joinMap.put("manner", "服务态度");
        joinMap.put("qiye", "企业相关证件");
        joinMap.put("zulin", "经营店面的租赁合同");
        joinMap.put("liushui", "相关经营店面营业流水");
        joinMap.put("xingxpho", "内外形象照片");
        joinMap.put("shopLegalIdcard", "实际经营者身份证");
        joinMap.put("businessLicenseNumber", "注册号/信用代码");
        joinMap.put("businessLicenseName", "营业执照名称");
        joinMap.put("qita", "声明函及其他附件");
        joinMap.put("shopDoorheadMaterial", "门头材质");
        joinMap.put("verificationCode", "验证码");
        joinMap.put("shopRealMan", "法人姓名");
        joinMap.put("shopRealManPhone", "联系方式");
        joinMap.put("shopRealManIdCard", "法人身份证");
        joinMap.put("shopDoorLength", "门头长度");
        joinMap.put("shopDoorWidth", "门头宽度");
        joinMap.put("businessPlace", "营业场所");
        joinMap.put("registerTime", "营业执照注册时间");
        joinMap.put("isFood", "食品流通许可证");
        joinMap.put("foodCode", "食品流通许可证");
        joinMap.put("foodTimeStart", "食品流通许可证有效期开始时间");
        joinMap.put("foodTimeEnd", "食品流通许可证有效期结束时间");
        joinMap.put("isTobacco", "是否售卖烟草");
        joinMap.put("isNotTobacco", "暂无");
        joinMap.put("tobaccoCode", "烟草专卖零售许可证号");
        joinMap.put("tobaccoTimeStart", "烟草专卖零售许可证有效期开始时间");
        joinMap.put("tobaccoTimeEnd", "烟草专卖零售许可证有效期结束时间");
        joinMap.put("storeSource", "店铺来源");
        joinMap.put("leaseTermOfValidityStart", "租赁合同有效期限开始时间");
        joinMap.put("leaseTermOfValidityEnd", "租赁合同有效期限结束时间");
    }


    public static Map<String, String> subsidyMap = new HashMap<>();

    static {
        subsidyMap.put("shopName", "店铺名称");
        subsidyMap.put("shopAssigner", "店铺法人");
        subsidyMap.put("xcCode", "店铺编号");
        subsidyMap.put("xcFinalName", "金融账号");
        subsidyMap.put("rentAmount", "租金补贴");
    }

    /**
     * 我的客户属性
     * 0：未走访 1：初次洽谈，2：有意加盟，3：审核中，4：成功加盟，5：无意加盟
     */
    public static Map<Integer, String> clientStye = new HashMap<>();

    static {
        clientStye.put(0, "未走访");
        clientStye.put(1, "初次洽谈");
        clientStye.put(2, "有意加盟");
        clientStye.put(3, "审核中");
        clientStye.put(4, "成功加盟");
        clientStye.put(5, "无意加盟");
    }


    /**
     * 加盟意向字典
     */
    public static Map<Integer, String> clientJoin = new HashMap<>();

    static {
        clientJoin.put(1, "暂无意向");
        clientJoin.put(2, "意向待定");
        clientJoin.put(3, "愿意加盟");
    }

    /**
     * 表类型字典
     */
    public static Map<String, String> stepMap = new HashMap<>();

    static {
        stepMap.put("step_0", "暂无意加盟");
        stepMap.put("step_1", "意向待定");
        stepMap.put("step_2", "意向加盟");
        stepMap.put("step_3", "加盟评分表审批中");
        stepMap.put("step_4", "加盟评分表未通过");
        stepMap.put("step_5", "加盟评分表已通过");
        stepMap.put("step_6", "合同审批表审核中");
        stepMap.put("step_7", "合同审批表未通过");
        stepMap.put("step_8", "合同审批表已通过");
        stepMap.put("step_9", "等待拓展人员上传盖章合同");
        stepMap.put("step_10", "拓展人员已上传盖章合同");
        stepMap.put("step_11", "盖章合同审批中");
        stepMap.put("step_12", "开业报备表审批中");
        stepMap.put("step_13", "开业报备表未通过");
        stepMap.put("step_14", "开业报备表已通过");
        stepMap.put("step_15", "等待上传验收单");
        stepMap.put("step_16", "验收单已上传");
        stepMap.put("step_17", "验收单审批中");
        stepMap.put("step_18", "验收单审批中");
        stepMap.put("step_19", "验收单审批通过");
    }

    /**
     * 审核状态字典
     * pending_auditing=等待审核，complete=已通过，reject=未通过
     */
    public static final Map<String, String> mapStatus = new HashMap<>();

    static {
        mapStatus.put("pending_auditing", "等待审核");
        mapStatus.put("complete", "已通过");
        mapStatus.put("reject", "未通过");
    }

    public static final Map<String, String> mapContract = new HashMap<>();

    static {
        mapContract.put("agreementType", "合同类型");
        mapContract.put("performStartDateStr", "合同开始时间");
        mapContract.put("performEndDateStr", "合同履行时间");
        mapContract.put("agreementName", "合同名称");
        mapContract.put("agreementSeller", "店铺法人");
        mapContract.put("contactWay", "联系方式");
        mapContract.put("shopAddrees", "店铺地址");
        mapContract.put("subjectContent", "主题内容");
        mapContract.put("contractPartyMaster", "签约方(甲)");
        mapContract.put("contractPartyFollow", "签约方(乙)");
        mapContract.put("agreementMoney", "合同总金额");
        mapContract.put("payType", "付款方式");
        mapContract.put("sellerIdCard", "法人身份证");
        mapContract.put("shopDoorLength", "门头长度");
        mapContract.put("rentAllowanceAmount", "租金补贴");
        mapContract.put("doorAllowanceAmount", "门头补贴");
        mapContract.put("shopDoorWidth", "门头宽度");
    }

    public static DTO<String, Object> gainForms(ViewGroup root,DTO<String,Object> map){
        if (root==null) return map;
        for (int i=0;i<root.getChildCount();i++){
            View view = root.getChildAt(i);
            if(view.getTag() != null && (view.getTag().equals("nocheck")||view.getTag().equals("onClick"))){
                continue;
            }
            if (view instanceof SingleSpinner) {
                SingleSpinner mView = (SingleSpinner) view;
                map.put(mView.getKey(), mView.getSelectedLabel());
            }
            else if (view instanceof ViewGroup) gainForms((ViewGroup) view,map);
            else if (view instanceof RadioButton){
                if (((RadioButton) view).getKey()==null) continue;
                if (((RadioButton) view).isChecked()) map.put(((RadioButton) view).getKey(),((RadioButton) view).getValue());
                else if (map.get(((RadioButton) view).getKey())==null) map.put(((RadioButton) view).getKey(),"");
            }
            else if (view instanceof CheckBox){
                if (((CheckBox) view).getKey()==null)continue;
                map.put(((CheckBox) view).getKey(),((CheckBox) view).getValue());
            }
            else if (view instanceof TextView && view.getTag() != null) {
                if (view.getTag().toString().contains("!")){
                    String[] strings = view.getTag().toString().split("!");
                    String[] values = ((TextView) view).getText().toString().trim().split(" ");

                    for (int j=0;j<(strings.length>values.length?values.length:strings.length);j++){
                        map.put(strings[j],values[j]);
                    }
                }else map.put(view.getTag().toString(),((TextView) view).getText().toString().trim());
            }
        }
        return map;
    }

    /**
     * 获取表单控件数据
     *
     * @param root 当前表单容器
     * @param data 当前表单数据
     * @return 表单数据（CheckBox多选选项以##拼接）
     */
    public static DTO<String, Object> gainForm(ViewGroup root, DTO<String, Object> data) {
        if (root.getChildCount() > 0) {
            for (int i = 0; i < root.getChildCount(); i++) {
                View view = root.getChildAt(i);
                if(view.getTag() != null && view.getTag().equals("nocheck")){
                    continue;
                }
                try {
                    gainForm(((ViewGroup) view), data);
                } catch (Exception e) {

                }

//                if(view.getTag() == null) continue;
                // 非容器级别控件不用递归
                /**
                 * EditText.class
                 */
                if (view instanceof EditText) {
                    data.put(view.getTag().toString(), ((EditText) view).getText().toString());
                }else if (view instanceof TextView && view.getTag() != null) {
                    data.put(view.getTag().toString(), ((TextView) view).getText().toString());
                } else if (view instanceof AutoCompleteTextView) {
                    data.put(view.getTag().toString(), ((AutoCompleteTextView) view).getText().toString());
                } else if (view instanceof MultiAutoCompleteTextView) {
                    data.put(view.getTag().toString(), ((MultiAutoCompleteTextView) view).getText()
                            .toString());
                } else if (view instanceof ExtractEditText) {
                    data.put(view.getTag().toString(), ((ExtractEditText) view).getText().toString());
                }

                /**
                 * RadioButton.class
                 */
                else if (view.getClass().getName().equals(android.widget.RadioButton.class.getName())) {
                    if (((android.widget.RadioButton) view).isChecked()) {
                        data.put((String) view.getTag(), ((android.widget.RadioButton) view).getText().toString());
                    } else {
                        if (data.get(view.getTag()) == null) {
                            data.put((String) view.getTag(), "");
                        }
                    }
                } else if (view.getClass().getName().equals(RadioButton.class.getName())) {
                    RadioButton mView = (RadioButton) view;
                    if (mView.isChecked()) {
                        data.put(mView.getKey(), mView.getValue());
                    } else {
                        if (data.get(mView.getKey()) == null) {
                            data.put(mView.getKey(), "");
                        }
                    }
                }

                /**
                 * CheckBox.class(需要拼装选中复选框)
                 */
                else if (view.getClass().getName().equals(android.widget.CheckBox.class.getName())) {
                    if (((android.widget.CheckBox) view).isChecked()) {
                        if (data.containsKey(view.getTag())) {
                            String value = data.get(view.getTag())==null?"":data.get(view.getTag()).toString();
                            value = value + "##" + ((android.widget.CheckBox) view).getText().toString();
                            data.put((String) view.getTag(), value);
                        } else {
                            data.put((String) view.getTag(), ((android.widget.CheckBox) view).getText().toString());
                        }
                    }

                }

                /**
                 * Spinner.class
                 */else if (view instanceof SingleSpinner) {
                    SingleSpinner mView = (SingleSpinner) view;
                    data.put(mView.getKey(), mView.getSelectedLabel());
                }
                else if (view.getClass().getName().equals(android.widget.Spinner.class.getName())) {
                    data.put((String) view.getTag(), ((android.widget.Spinner) view).getSelectedItem().toString());
                }
            }
        }

        return data;
    }

    /**
     * 请求分页
     *
     * @param pageNo 分页号码
     */
    public static void requestPage(int pageNo) {

    }
}
