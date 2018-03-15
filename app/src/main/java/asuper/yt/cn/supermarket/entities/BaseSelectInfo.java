package asuper.yt.cn.supermarket.entities;

import java.util.List;

/**
 * 合同审批表动态数据
 * Created by zengxiaowen on 2017/3/19.
 */
public class BaseSelectInfo {

    private List<MerchantJoinSelectProperty> agreementType;

    private List<MerchantJoinSelectProperty> payType;

    public List<MerchantJoinSelectProperty> getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(List<MerchantJoinSelectProperty> agreementType) {
        this.agreementType = agreementType;
    }

    public List<MerchantJoinSelectProperty> getPayType() {
        return payType;
    }

    public void setPayType(List<MerchantJoinSelectProperty> payType) {
        this.payType = payType;
    }
}
