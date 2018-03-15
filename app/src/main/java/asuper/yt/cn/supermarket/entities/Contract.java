package asuper.yt.cn.supermarket.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * 合同审批表基类
 * Created by zengxiaowen on 2017/3/19.
 */

@DatabaseTable(tableName = "tb_contract")
public class Contract implements Serializable {

    @DatabaseField(id = true)
    private String intentionId; //意向加盟编号

    @DatabaseField
    private String approveId;//合同审批编号
    @DatabaseField
    private String gradeId; //加盟评分编号
    @DatabaseField
    private String cobberNo; //小超编号
    @DatabaseField
    private String financeNo; //金融帐号
    @DatabaseField
    private String initiateDate;
    @DatabaseField
    private String initiatePeople; //发起人编号
    @DatabaseField
    private String organizationNumber;

    @DatabaseField
    private String user_id;

    @DatabaseField
    private String agreementType;  //合同类型:standard:标准,update:修改

    @DatabaseField
    private String payType;  //付款方式:YTFINANCE：雅堂金融账户

    @DatabaseField
    private String agreementSeller;  //加盟商家

    @DatabaseField
    private String contractPartyMaster;  //签约方(甲)

    @DatabaseField
    private String contractPartyFollow;  //签约方(乙)

    @DatabaseField
    private String shopAddrees;  //加盟店铺地址

    @DatabaseField
    private String agreementName;  //合同名称

    @DatabaseField
    private String contactWay;   //联系方式

    @DatabaseField
    private String subjectContent;  //主题内容

    @DatabaseField
    private String agreementMoney;  //合同金额

    @DatabaseField
    private String performStartDateStr; //合同开始时间

    @DatabaseField
    private String performEndDateStr;  //合同履行时间

    @DatabaseField
    private String sellerIdCard; //法人身份证号

    @DatabaseField
    private String address;

    @DatabaseField
    private String shopName;

    @DatabaseField
    private String legalPersonName;

    @DatabaseField
    private String phoneNumber;

    @DatabaseField
    private String franchiseAgreement;

    @DatabaseField
    private String advertisingAgreement;

    @DatabaseField
    private String unincorporatedStatement;

    @DatabaseField
    private String otherPhoto;

    @DatabaseField
    public String attachmentState;
    @DatabaseField
    public String rentAllowanceAmount;
    @DatabaseField
    public String doorAllowanceAmount;
    @DatabaseField
    public String doorAllowanceAmountMax;
    @DatabaseField
    public String rentAllowanceAmountMax;

    @DatabaseField(defaultValue = "0")
    public String is_bottom;



    @DatabaseField
    public String auditNodeIndex;

    public List<AttachmentInfo> fileRule;
    @DatabaseField
    public String fileRuleJson;

    public String auditId;

    private String performStartDate;
    private String performEndDate;
    private String agreementStatus;  //审核状态
    private String financeMessage;  //金融帐号未生成原因
    private String failNodeName;  //审批节点名称
    private String failDesc;   //审核不通过原因

    private List<AppendixOne> appendixOne;

    @DatabaseField
    private String appendixOneJson; //以json形式存储附件的数据

    private BaseSelectInfo baseSelectInfo;

    @DatabaseField
    private String baseSelectInfoJson; //以json形式存储spinner的数据

    @DatabaseField
    public String isOver;
    @DatabaseField
    public int groupId;

    public String getSellerIdCard() {
        return sellerIdCard;
    }

    public void setSellerIdCard(String sellerIdCard) {
        this.sellerIdCard = sellerIdCard;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getIntentionId() {
        return intentionId;
    }

    public void setIntentionId(String intentionId) {
        this.intentionId = intentionId;
    }

    public String getApproveId() {
        return approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getCobberNo() {
        return cobberNo;
    }

    public void setCobberNo(String cobberNo) {
        this.cobberNo = cobberNo;
    }

    public String getFinanceNo() {
        return financeNo;
    }

    public void setFinanceNo(String financeNo) {
        this.financeNo = financeNo;
    }

    public String getInitiateDate() {
        return initiateDate;
    }

    public void setInitiateDate(String initiateDate) {
        this.initiateDate = initiateDate;
    }

    public String getInitiatePeople() {
        return initiatePeople;
    }

    public void setInitiatePeople(String initiatePeople) {
        this.initiatePeople = initiatePeople;
    }

    public String getOrganizationNumber() {
        return organizationNumber;
    }

    public void setOrganizationNumber(String organizationNumber) {
        this.organizationNumber = organizationNumber;
    }

    public String getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(String agreementType) {
        this.agreementType = agreementType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getAgreementSeller() {
        return agreementSeller;
    }

    public void setAgreementSeller(String agreementSeller) {
        this.agreementSeller = agreementSeller;
    }

    public String getContractPartyMaster() {
        return contractPartyMaster;
    }

    public void setContractPartyMaster(String contractPartyMaster) {
        this.contractPartyMaster = contractPartyMaster;
    }

    public String getContractPartyFollow() {
        return contractPartyFollow;
    }

    public void setContractPartyFollow(String contractPartyFollow) {
        this.contractPartyFollow = contractPartyFollow;
    }

    public String getShopAddrees() {
        return shopAddrees;
    }

    public void setShopAddrees(String shopAddrees) {
        this.shopAddrees = shopAddrees;
    }

    public String getAgreementName() {
        return agreementName;
    }

    public void setAgreementName(String agreementName) {
        this.agreementName = agreementName;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getSubjectContent() {
        return subjectContent;
    }

    public void setSubjectContent(String subjectContent) {
        this.subjectContent = subjectContent;
    }

    public String getAgreementMoney() {
        return agreementMoney;
    }

    public void setAgreementMoney(String agreementMoney) {
        this.agreementMoney = agreementMoney;
    }

    public String getPerformStartDateStr() {
        return performStartDateStr;
    }

    public void setPerformStartDateStr(String performStartDateStr) {
        this.performStartDateStr = performStartDateStr;
    }

    public String getPerformEndDateStr() {
        return performEndDateStr;
    }

    public void setPerformEndDateStr(String performEndDateStr) {
        this.performEndDateStr = performEndDateStr;
    }

    public String getPerformStartDate() {
        return performStartDate;
    }

    public void setPerformStartDate(String performStartDate) {
        this.performStartDate = performStartDate;
    }

    public String getPerformEndDate() {
        return performEndDate;
    }

    public void setPerformEndDate(String performEndDate) {
        this.performEndDate = performEndDate;
    }

    public String getAgreementStatus() {
        return agreementStatus;
    }

    public void setAgreementStatus(String agreementStatus) {
        this.agreementStatus = agreementStatus;
    }

    public String getFinanceMessage() {
        return financeMessage;
    }

    public void setFinanceMessage(String financeMessage) {
        this.financeMessage = financeMessage;
    }

    public String getFailNodeName() {
        return failNodeName;
    }

    public void setFailNodeName(String failNodeName) {
        this.failNodeName = failNodeName;
    }

    public String getFailDesc() {
        return failDesc;
    }

    public void setFailDesc(String failDesc) {
        this.failDesc = failDesc;
    }

    public List<AppendixOne> getAppendixOne() {
        return appendixOne;
    }

    public void setAppendixOne(List<AppendixOne> appendixOne) {
        this.appendixOne = appendixOne;
    }

    public BaseSelectInfo getBaseSelectInfo() {
        return baseSelectInfo;
    }

    public void setBaseSelectInfo(BaseSelectInfo baseSelectInfo) {
        this.baseSelectInfo = baseSelectInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String legalPersonName) {
        this.legalPersonName = legalPersonName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBaseSelectInfoJson() {
        return baseSelectInfoJson;
    }

    public void setBaseSelectInfoJson(String baseSelectInfoJson) {
        this.baseSelectInfoJson = baseSelectInfoJson;
    }

    public String getAppendixOneJson() {
        return appendixOneJson;
    }

    public void setAppendixOneJson(String appendixOneJson) {
        this.appendixOneJson = appendixOneJson;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "intentionId='" + intentionId + '\'' +
                ", approveId='" + approveId + '\'' +
                ", gradeId='" + gradeId + '\'' +
                ", cobberNo='" + cobberNo + '\'' +
                ", financeNo='" + financeNo + '\'' +
                ", initiateDate='" + initiateDate + '\'' +
                ", initiatePeople='" + initiatePeople + '\'' +
                ", organizationNumber='" + organizationNumber + '\'' +
                ", user_id='" + user_id + '\'' +
                ", agreementType='" + agreementType + '\'' +
                ", payType='" + payType + '\'' +
                ", agreementSeller='" + agreementSeller + '\'' +
                ", contractPartyMaster='" + contractPartyMaster + '\'' +
                ", contractPartyFollow='" + contractPartyFollow + '\'' +
                ", shopAddrees='" + shopAddrees + '\'' +
                ", agreementName='" + agreementName + '\'' +
                ", contactWay='" + contactWay + '\'' +
                ", subjectContent='" + subjectContent + '\'' +
                ", agreementMoney='" + agreementMoney + '\'' +
                ", performStartDateStr='" + performStartDateStr + '\'' +
                ", performEndDateStr='" + performEndDateStr + '\'' +
                ", sellerIdCard='" + sellerIdCard + '\'' +
                ", address='" + address + '\'' +
                ", shopName='" + shopName + '\'' +
                ", legalPersonName='" + legalPersonName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", franchiseAgreement='" + franchiseAgreement + '\'' +
                ", advertisingAgreement='" + advertisingAgreement + '\'' +
                ", unincorporatedStatement='" + unincorporatedStatement + '\'' +
                ", otherPhoto='" + otherPhoto + '\'' +
                ", attachmentState='" + attachmentState + '\'' +
                ", rentAllowanceAmount='" + rentAllowanceAmount + '\'' +
                ", doorAllowanceAmount='" + doorAllowanceAmount + '\'' +
                ", doorAllowanceAmountMax='" + doorAllowanceAmountMax + '\'' +
                ", rentAllowanceAmountMax='" + rentAllowanceAmountMax + '\'' +
                ", is_bottom='" + is_bottom + '\'' +
                ", auditNodeIndex='" + auditNodeIndex + '\'' +
                ", fileRule=" + fileRule +
                ", fileRuleJson='" + fileRuleJson + '\'' +
                ", auditId='" + auditId + '\'' +
                ", performStartDate='" + performStartDate + '\'' +
                ", performEndDate='" + performEndDate + '\'' +
                ", agreementStatus='" + agreementStatus + '\'' +
                ", financeMessage='" + financeMessage + '\'' +
                ", failNodeName='" + failNodeName + '\'' +
                ", failDesc='" + failDesc + '\'' +
                ", appendixOne=" + appendixOne +
                ", appendixOneJson='" + appendixOneJson + '\'' +
                ", baseSelectInfo=" + baseSelectInfo +
                ", baseSelectInfoJson='" + baseSelectInfoJson + '\'' +
                ", isOver='" + isOver + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
