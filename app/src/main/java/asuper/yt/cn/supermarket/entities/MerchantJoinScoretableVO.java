package asuper.yt.cn.supermarket.entities;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;


@DatabaseTable(tableName = "tb_join")
public class MerchantJoinScoretableVO  implements Serializable {

    private String shopAssigner;// 店铺指派人

    @DatabaseField(id = true)
    private int shopId;// '意向加盟商唯一标识',

    @DatabaseField
    private String shopName;// '店铺名称',

    @DatabaseField
    private String shopLegalperson;// '店铺老板名称',

    @DatabaseField
    private String shopOwnerPhonenumber;// '电话',

    @DatabaseField
    private String shopAddress;// '申请店铺详情地址',

    @DatabaseField
    private boolean shopIsChainshop;// '是否连锁店',

    @DatabaseField
    private boolean isNewOpen;// '是否新开店',

    @DatabaseField
    private String provinceCode; //省编号

    @DatabaseField
    private String countyCode; //县编码

    @DatabaseField
    private String shopStreetCode; //街道编码

    @DatabaseField
    private String provinceName; //省名称

    @DatabaseField
    private String countyName; //县名称

    @DatabaseField
    private String shopStreetName; //街道名称

    @DatabaseField
    private String shopCityCode;// '店铺所在地级市编号',

    @DatabaseField
    private String shopCityName;// '店铺所在地级市名称',

    @DatabaseField
    private String shopCityGrade;// '所在城市等级',

    @DatabaseField
    private String shopFloatingRatio;// '补贴上浮比例',

    @DatabaseField
    private int shopownerAge;// '服务员年龄',

    @DatabaseField
    private String shopAppearance;// '店铺内外形象',

    @DatabaseField
    private String shopDesc;// '店铺描述',

    @DatabaseField
    private String shopServicingtime;// '营业年限',

    @DatabaseField
    private String shopAcreage;// '店铺面积',

    @DatabaseField
    private String shopRent;// '店铺租金(月)',

    @DatabaseField
    public String shopMonthRent;// '店铺租金(月)',

    @DatabaseField
    public String shopLegalIdcard;// '法人身份证',

    @DatabaseField
    public String businessLicenseNumber;// '营业执照编号',

    @DatabaseField
    public String businessLicenseName;// '营业执照名称',

    @DatabaseField
    private String shopSales;// '月销售额',

    @DatabaseField
    private String shoType;// '店铺类型',

    @DatabaseField
    private String shopTakeawayPlatform;// '外卖平台',

    @DatabaseField
    private String shopCashierEquipment;// '收银设备',

    @DatabaseField
    private String shopSupplier;//商品供应

    @DatabaseField
    private String shopAllowanceMode;// '补贴方式',

    @DatabaseField
    private String shopSweepPayment;// '扫码支付',

    @DatabaseField
    private String shopOnlineShopping;// '网购经验',

    @DatabaseField
    private String shopLighting;// '店内灯光',

    @DatabaseField
    private String shopPromotionFrequency;// '促销频次',

    @DatabaseField
    private String shopOwnerAgeRange;// '经营者年龄',

    @DatabaseField
    private String shopValueaddedServices;// '增值服务',

    @DatabaseField
    private String shopServiceAttitude;// '服务态度',

    @DatabaseField
    private String user_id;

    @DatabaseField
    private String shopDoorheadSize;//门头尺寸

    @DatabaseField
    private String shopDoorheadMaterial;//门头材质单价上限

    @DatabaseField
    private int shopChainCount;// '连锁店数量',

    @DatabaseField
    private String shopDoorLength;// 门头长度

    @DatabaseField
    private String shopDoorWidth;// 门头宽度

    @DatabaseField
    private String shopDoorArea;// 门头面积

    @DatabaseField
    public String attachmentState;  //备注

    @DatabaseField
    public String auditNodeIndex;

    @DatabaseField(defaultValue = "0")
    public String is_bottom;

    @DatabaseField
    public String shopRealMan;
    @DatabaseField
    public String shopRealManPhone;
    @DatabaseField
    public String shopRealManIdCard;
    @DatabaseField
    public String relationShip;
    @DatabaseField
    public String businessPlace; //营业场所
    @DatabaseField
    public String originalScript; //营业执照
    @DatabaseField
    public String registerTime; //营业执照注册时间
    @DatabaseField
    public String isFood; // 食品流通许可证字段   1:有 2：没有
    @DatabaseField
    public String foodCode; // 食品流通许可证号
    @DatabaseField
    public String foodTimeStart; // 食品流通许可证有效期开始时间
    @DatabaseField
    public String foodTimeEnd; // 食品流通许可证有效期结束时间
    @DatabaseField
    public String isTobacco; // 是否售卖烟草  1：是 2：否
    @DatabaseField
    public String isNotTobacco;//  暂无   1:有  2：没有
    @DatabaseField
    public String tobaccoCode;//烟草专卖零售许可证号
    @DatabaseField
    public String tobaccoTimeStart;//烟草专卖零售许可证有效期开始时间
    @DatabaseField
    public String tobaccoTimeEnd;//烟草专卖零售许可证有效期结束时间
    @DatabaseField
    public String storeSource;//1:租凭房屋  2：自有房屋
    @DatabaseField
    public String leaseTermOfValidityStart;//租赁合同有效期限开始时间
    @DatabaseField
    public String leaseTermOfValidityEnd;//租赁合同有效期限结束时间
    @DatabaseField
    public String verificationCode;//验证码

    public int isOverdueRevert; // 1逾期还原







    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    private String status;//pending_auditing=等待审核，complete=已通过，reject=未通过
    private String rejectReson;//拒绝原因

    private String currentAuditingNodeName;//当前审批流程节点
    private List<MerchantJSAuditingNodeInfo> auditingNodeList;//审核流程信息

    private String shopTotalScore;// '综合得分',
    private String shopScoreGrade;// '综合评级',
    private String xcCode;//小超编号
    private String itbtLoginName;//金融账号
    private String storeLoginName;//小超门店账号
    private String doorAllowanceAmount;//门头补贴金额
    private String rentAllowanceAmount;//租金补贴金额
    private String agreementAmount;//合同总金额
    private String shopAllowanceAmount;// '补贴金额',
    private String applyTime;// '评分表提交时间',
    private int applyId;// '流程数据',
    private String modifierId;// '修改者ID',
    private String modifier;// '修改者',
    private String gmtModify;// '修改时间',
    private int isDeleted;// '0=未删除，1=删除'
    private String enclosureFileCode;//上传附件文件编号
    //上传的附件文件信息,APP无法支持集合传递，组成字符串在解析成集合


    public String getShopDoorLength() {
        return shopDoorLength;
    }

    public void setShopDoorLength(String shopDoorLength) {
        this.shopDoorLength = shopDoorLength;
    }

    public String getShopDoorWidth() {
        return shopDoorWidth;
    }

    public void setShopDoorWidth(String shopDoorWidth) {
        this.shopDoorWidth = shopDoorWidth;
    }

    public String getShopDoorArea() {
        return shopDoorArea;
    }

    public void setShopDoorArea(String shopDoorArea) {
        this.shopDoorArea = shopDoorArea;
    }

    public String getShopDoorheadSize() {
        return shopDoorheadSize;
    }

    public void setShopDoorheadSize(String shopDoorheadSize) {
        this.shopDoorheadSize = shopDoorheadSize;
    }

    public String getShopDoorheadMaterial() {
        return shopDoorheadMaterial;
    }

    public void setShopDoorheadMaterial(String shopDoorheadMaterial) {
        this.shopDoorheadMaterial = shopDoorheadMaterial;
    }

    public int getShopChainCount() {
        return shopChainCount;
    }

    public void setShopChainCount(int shopChainCount) {
        this.shopChainCount = shopChainCount;
    }

    public String getCurrentAuditingNodeName() {
        return currentAuditingNodeName;
    }

    public void setCurrentAuditingNodeName(String currentAuditingNodeName) {
        this.currentAuditingNodeName = currentAuditingNodeName;
    }

    public List<MerchantJSAuditingNodeInfo> getAuditingNodeList() {
        return auditingNodeList;
    }

    public void setAuditingNodeList(List<MerchantJSAuditingNodeInfo> auditingNodeList) {
        this.auditingNodeList = auditingNodeList;
    }

    @DatabaseField
    private String qiye;//企业相关证件

    @DatabaseField
    private String fcode;//法人身份证件

    @DatabaseField
    private String zulin;//经营店面的租赁合同

    @DatabaseField
    private String liushui;//相关经营店面营业流水

    @DatabaseField
    private String xingxpho;//内外形象照片

    @DatabaseField
    private String qita;//声明函及其他附件

    @DatabaseField
    public String businessLicense; //营业执照或三证"
    @DatabaseField
    public String foodCirculationPermit; //食品流通许可证
    @DatabaseField
    public String htaBusinessLicense; //卫生/烟草/酒类等经营许可证
    @DatabaseField
    public String expiredCertificateUndertaking; //过期证件承诺书
    @DatabaseField
    public String corporateIdentityCard; //法人身份证（正/反面)
    @DatabaseField
    public String rentContract; //房租合同(或房产证）
    @DatabaseField
    public String rentPaymentVoucher; //租金缴费凭证
    @DatabaseField
    public String monthlySalesCertificate; //月销售证明
    @DatabaseField
    public String doorStreetPhoto; //含门头街景照
    @DatabaseField
    public String accordanceShopPhoto; //店内整体照
    @DatabaseField
    public String commodityDisplayPhoto; //商品陈列照
    @DatabaseField
    public String outsideShopPhoto; //店外周边照
    @DatabaseField
    public String cashierPhoto; //收银台照片
    @DatabaseField
    public String declarationLetter; //声明函(签字,带手印)
    @DatabaseField
    public String franchiseeCommitment; //加盟商承诺书
    @DatabaseField
    public String otherPhoto; //加盟商承诺书
    @DatabaseField
    public String groupPhoto; //加盟商承诺书
    @DatabaseField
    public String proxyBook; //加盟商承诺书
    @DatabaseField
    public String verifyCode; //加盟商承诺书


    @DatabaseField
    public String isOver;

    @DatabaseField
    public int groupId;

    public List<AttachmentInfo> fileRule;


    //获取定义的下拉数据信息
    private MerchantJoinSelectListVO selectListData;

    @DatabaseField
    private String selectListDataJson;

    public String getShopAssigner() {
        return shopAssigner;
    }

    public void setShopAssigner(String shopAssigner) {
        this.shopAssigner = shopAssigner;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLegalperson() {
        return shopLegalperson;
    }

    public void setShopLegalperson(String shopLegalperson) {
        this.shopLegalperson = shopLegalperson;
    }

    public String getShopOwnerPhonenumber() {
        return shopOwnerPhonenumber;
    }

    public void setShopOwnerPhonenumber(String shopOwnerPhonenumber) {
        this.shopOwnerPhonenumber = shopOwnerPhonenumber;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public boolean isShopIsChainshop() {
        return shopIsChainshop;
    }

    public void setShopIsChainshop(boolean shopIsChainshop) {
        this.shopIsChainshop = shopIsChainshop;
    }

    public boolean isNewOpen() {
        return isNewOpen;
    }

    public void setNewOpen(boolean newOpen) {
        isNewOpen = newOpen;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getShopCityCode() {
        return shopCityCode;
    }

    public void setShopCityCode(String shopCityCode) {
        this.shopCityCode = shopCityCode;
    }

    public String getShopCityName() {
        return shopCityName;
    }

    public void setShopCityName(String shopCityName) {
        this.shopCityName = shopCityName;
    }

    public String getShopCityGrade() {
        return shopCityGrade;
    }

    public void setShopCityGrade(String shopCityGrade) {
        this.shopCityGrade = shopCityGrade;
    }

    public String getShopFloatingRatio() {
        return shopFloatingRatio;
    }

    public void setShopFloatingRatio(String shopFloatingRatio) {
        this.shopFloatingRatio = shopFloatingRatio;
    }

    public int getShopownerAge() {
        return shopownerAge;
    }

    public void setShopownerAge(int shopownerAge) {
        this.shopownerAge = shopownerAge;
    }

    public String getShopAppearance() {
        return shopAppearance;
    }

    public void setShopAppearance(String shopAppearance) {
        this.shopAppearance = shopAppearance;
    }

    public String getShopDesc() {
        return shopDesc;
    }

    public void setShopDesc(String shopDesc) {
        this.shopDesc = shopDesc;
    }

    public String getShopServicingtime() {
        return shopServicingtime;
    }

    public void setShopServicingtime(String shopServicingtime) {
        this.shopServicingtime = shopServicingtime;
    }

    public String getShopStreetCode() {
        return shopStreetCode;
    }

    public void setShopStreetCode(String shopStreetCode) {
        this.shopStreetCode = shopStreetCode;
    }

    public String getShopStreetName() {
        return shopStreetName;
    }

    public void setShopStreetName(String shopStreetName) {
        this.shopStreetName = shopStreetName;
    }

    public String getShopAcreage() {
        return shopAcreage;
    }

    public void setShopAcreage(String shopAcreage) {
        this.shopAcreage = shopAcreage;
    }

    public String getShopRent() {
        return shopRent;
    }

    public void setShopRent(String shopRent) {
        this.shopRent = shopRent;
    }

    public String getShopSales() {
        return shopSales;
    }

    public void setShopSales(String shopSales) {
        this.shopSales = shopSales;
    }

    public String getShoType() {
        return shoType;
    }

    public void setShoType(String shoType) {
        this.shoType = shoType;
    }

    public String getShopTakeawayPlatform() {
        return shopTakeawayPlatform;
    }

    public void setShopTakeawayPlatform(String shopTakeawayPlatform) {
        this.shopTakeawayPlatform = shopTakeawayPlatform;
    }

    public String getShopCashierEquipment() {
        return shopCashierEquipment;
    }

    public void setShopCashierEquipment(String shopCashierEquipment) {
        this.shopCashierEquipment = shopCashierEquipment;
    }

    public String getShopSupplier() {
        return shopSupplier;
    }

    public void setShopSupplier(String shopSupplier) {
        this.shopSupplier = shopSupplier;
    }

    public String getShopAllowanceMode() {
        return shopAllowanceMode;
    }

    public void setShopAllowanceMode(String shopAllowanceMode) {
        this.shopAllowanceMode = shopAllowanceMode;
    }

    public String getShopSweepPayment() {
        return shopSweepPayment;
    }

    public void setShopSweepPayment(String shopSweepPayment) {
        this.shopSweepPayment = shopSweepPayment;
    }

    public String getShopOnlineShopping() {
        return shopOnlineShopping;
    }

    public void setShopOnlineShopping(String shopOnlineShopping) {
        this.shopOnlineShopping = shopOnlineShopping;
    }

    public String getShopLighting() {
        return shopLighting;
    }

    public void setShopLighting(String shopLighting) {
        this.shopLighting = shopLighting;
    }

    public String getShopPromotionFrequency() {
        return shopPromotionFrequency;
    }

    public void setShopPromotionFrequency(String shopPromotionFrequency) {
        this.shopPromotionFrequency = shopPromotionFrequency;
    }

    public String getShopOwnerAgeRange() {
        return shopOwnerAgeRange;
    }

    public void setShopOwnerAgeRange(String shopOwnerAgeRange) {
        this.shopOwnerAgeRange = shopOwnerAgeRange;
    }

    public String getShopValueaddedServices() {
        return shopValueaddedServices;
    }

    public void setShopValueaddedServices(String shopValueaddedServices) {
        this.shopValueaddedServices = shopValueaddedServices;
    }

    public String getShopServiceAttitude() {
        return shopServiceAttitude;
    }

    public void setShopServiceAttitude(String shopServiceAttitude) {
        this.shopServiceAttitude = shopServiceAttitude;
    }

    public String getShopTotalScore() {
        return shopTotalScore;
    }

    public void setShopTotalScore(String shopTotalScore) {
        this.shopTotalScore = shopTotalScore;
    }

    public String getShopScoreGrade() {
        return shopScoreGrade;
    }

    public void setShopScoreGrade(String shopScoreGrade) {
        this.shopScoreGrade = shopScoreGrade;
    }

    public String getXcCode() {
        return xcCode;
    }

    public void setXcCode(String xcCode) {
        this.xcCode = xcCode;
    }

    public String getItbtLoginName() {
        return itbtLoginName;
    }

    public void setItbtLoginName(String itbtLoginName) {
        this.itbtLoginName = itbtLoginName;
    }

    public String getStoreLoginName() {
        return storeLoginName;
    }

    public void setStoreLoginName(String storeLoginName) {
        this.storeLoginName = storeLoginName;
    }

    public String getDoorAllowanceAmount() {
        return doorAllowanceAmount;
    }

    public void setDoorAllowanceAmount(String doorAllowanceAmount) {
        this.doorAllowanceAmount = doorAllowanceAmount;
    }

    public String getRentAllowanceAmount() {
        return rentAllowanceAmount;
    }

    public void setRentAllowanceAmount(String rentAllowanceAmount) {
        this.rentAllowanceAmount = rentAllowanceAmount;
    }

    public String getAgreementAmount() {
        return agreementAmount;
    }

    public void setAgreementAmount(String agreementAmount) {
        this.agreementAmount = agreementAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectReson() {
        return rejectReson;
    }

    public void setRejectReson(String rejectReson) {
        this.rejectReson = rejectReson;
    }

    public String getShopAllowanceAmount() {
        return shopAllowanceAmount;
    }

    public void setShopAllowanceAmount(String shopAllowanceAmount) {
        this.shopAllowanceAmount = shopAllowanceAmount;
    }

    public String getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(String applyTime) {
        this.applyTime = applyTime;
    }

    public int getApplyId() {
        return applyId;
    }

    public void setApplyId(int applyId) {
        this.applyId = applyId;
    }

    public String getModifierId() {
        return modifierId;
    }

    public void setModifierId(String modifierId) {
        this.modifierId = modifierId;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(String gmtModify) {
        this.gmtModify = gmtModify;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getEnclosureFileCode() {
        return enclosureFileCode;
    }

    public void setEnclosureFileCode(String enclosureFileCode) {
        this.enclosureFileCode = enclosureFileCode;
    }

    public String getQiye() {
        return qiye;
    }

    public void setQiye(String qiye) {
        this.qiye = qiye;
    }

    public String getFcode() {
        return fcode;
    }

    public void setFcode(String fcode) {
        this.fcode = fcode;
    }

    public String getZulin() {
        return zulin;
    }

    public void setZulin(String zulin) {
        this.zulin = zulin;
    }

    public String getLiushui() {
        return liushui;
    }

    public void setLiushui(String liushui) {
        this.liushui = liushui;
    }

    public String getXingxpho() {
        return xingxpho;
    }

    public void setXingxpho(String xingxpho) {
        this.xingxpho = xingxpho;
    }

    public String getQita() {
        return qita;
    }

    public void setQita(String qita) {
        this.qita = qita;
    }

    public MerchantJoinSelectListVO getSelectListData() {
        return selectListData;
    }

    public void setSelectListData(MerchantJoinSelectListVO selectListData) {
        this.selectListData = selectListData;
    }

    @Override
    public String toString() {
        return "MerchantJoinScoretableVO{" +
                "shopAssigner='" + shopAssigner + '\'' +
                ", shopId=" + shopId +
                ", shopName='" + shopName + '\'' +
                ", shopLegalperson='" + shopLegalperson + '\'' +
                ", shopOwnerPhonenumber='" + shopOwnerPhonenumber + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopIsChainshop='" + shopIsChainshop + '\'' +
                ", isNewOpen='" + isNewOpen + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", countyCode='" + countyCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", countyName='" + countyName + '\'' +
                ", shopCityCode='" + shopCityCode + '\'' +
                ", shopCityName='" + shopCityName + '\'' +
                ", shopCityGrade='" + shopCityGrade + '\'' +
                ", shopFloatingRatio='" + shopFloatingRatio + '\'' +
                ", shopownerAge=" + shopownerAge +
                ", shopAppearance='" + shopAppearance + '\'' +
                ", shopDesc='" + shopDesc + '\'' +
                ", shopServicingtime=" + shopServicingtime +
                ", shopAcreage=" + shopAcreage +
                ", shopRent=" + shopRent +
                ", shopSales=" + shopSales +
                ", shoType='" + shoType + '\'' +
                ", shopTakeawayPlatform='" + shopTakeawayPlatform + '\'' +
                ", shopCashierEquipment='" + shopCashierEquipment + '\'' +
                ", shopSupplier='" + shopSupplier + '\'' +
                ", shopAllowanceMode='" + shopAllowanceMode + '\'' +
                ", shopSweepPayment='" + shopSweepPayment + '\'' +
                ", shopOnlineShopping='" + shopOnlineShopping + '\'' +
                ", shopLighting='" + shopLighting + '\'' +
                ", shopPromotionFrequency='" + shopPromotionFrequency + '\'' +
                ", shopOwnerAgeRange='" + shopOwnerAgeRange + '\'' +
                ", shopValueaddedServices='" + shopValueaddedServices + '\'' +
                ", shopServiceAttitude='" + shopServiceAttitude + '\'' +
                ", shopTotalScore='" + shopTotalScore + '\'' +
                ", shopScoreGrade='" + shopScoreGrade + '\'' +
                ", xcCode='" + xcCode + '\'' +
                ", itbtLoginName='" + itbtLoginName + '\'' +
                ", storeLoginName='" + storeLoginName + '\'' +
                ", doorAllowanceAmount=" + doorAllowanceAmount +
                ", rentAllowanceAmount=" + rentAllowanceAmount +
                ", agreementAmount=" + agreementAmount +
                ", status='" + status + '\'' +
                ", rejectReson='" + rejectReson + '\'' +
                ", shopAllowanceAmount=" + shopAllowanceAmount +
                ", applyTime=" + applyTime +
                ", applyId=" + applyId +
                ", modifierId='" + modifierId + '\'' +
                ", modifier='" + modifier + '\'' +
                ", gmtModify=" + gmtModify +
                ", isDeleted=" + isDeleted +
                ", enclosureFileCode='" + enclosureFileCode + '\'' +
                ", qiye='" + qiye + '\'' +
                ", fcode='" + fcode + '\'' +
                ", zulin='" + zulin + '\'' +
                ", liushui='" + liushui + '\'' +
                ", xingxpho='" + xingxpho + '\'' +
                ", qita='" + qita + '\'' +
                ", selectListData=" + selectListData +
                '}';
    }

    public String getSelectListDataJson() {
        return selectListDataJson;
    }

    public void setSelectListDataJson(String selectListDataJson) {
        this.selectListDataJson = selectListDataJson;
    }
}
