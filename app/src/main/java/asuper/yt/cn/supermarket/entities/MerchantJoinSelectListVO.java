package asuper.yt.cn.supermarket.entities;

import java.io.Serializable;
import java.util.List;

public class MerchantJoinSelectListVO implements Serializable {

    // 增值服务下拉
    private List<MerchantJoinSelectProperty> addedServicesList;
    // 内外形象下拉
    private List<MerchantJoinSelectProperty> appearanceList;
    // 收银设备使用下拉
    private List<MerchantJoinSelectProperty> cashierEquipmentList;
    // 外卖平台下拉
    private List<MerchantJoinSelectProperty> takeawayPlatformtList;
    // 店铺类型下拉
    private List<MerchantJoinSelectProperty> shopTypeList;
    // 补贴方式下拉
    private List<MerchantJoinSelectProperty> allowanceModeList;
    // 商品供应下拉
    private List<MerchantJoinSelectProperty> supplierList;
    // 扫码支付下拉
    private List<MerchantJoinSelectProperty> sweepPaymentList;
    // 服务态度下拉
    private List<MerchantJoinSelectProperty> serviceAttitudeList;
    // 灯光下拉
    private List<MerchantJoinSelectProperty> lightingList;
    // 网购行为下拉
    private List<MerchantJoinSelectProperty> onlineShoppingList;
    // 促销频率下拉
    private List<MerchantJoinSelectProperty> promotionFrequencyList;
    //经营者年龄
    private List<MerchantJoinSelectProperty> ownerAgeRangeList;
    //月销售额下拉
    private List<MerchantJoinSelectProperty> salesList;
    //租金下拉
    private List<MerchantJoinSelectProperty> rentList;
    //面积下拉
    private List<MerchantJoinSelectProperty> acreageList;
    //店铺营业年限
    private List<MerchantJoinSelectProperty> shopServicingtimeList;
    //城市等级下拉
    private List<MerchantJoinSelectProperty> cityGradeList;
    // 更换门头材质
    private List<MerchantJoinSelectProperty> doorHeadMaterialList;

    public List<MerchantJoinSelectProperty> relationShipList;

    public List<MerchantJoinSelectProperty> getDoorHeadMaterialList() {
        return doorHeadMaterialList;
    }

    public void setDoorHeadMaterialList(List<MerchantJoinSelectProperty> doorHeadMaterialList) {
        this.doorHeadMaterialList = doorHeadMaterialList;
    }

    public List<MerchantJoinSelectProperty> getCityGradeList() {
        return cityGradeList;
    }

    public void setCityGradeList(List<MerchantJoinSelectProperty> cityGradeList) {
        this.cityGradeList = cityGradeList;
    }

    public List<MerchantJoinSelectProperty> getShopServicingtimeList() {
        return shopServicingtimeList;
    }

    public void setShopServicingtimeList(List<MerchantJoinSelectProperty> shopServicingtimeList) {
        this.shopServicingtimeList = shopServicingtimeList;
    }

    public List<MerchantJoinSelectProperty> getOwnerAgeRangeList() {
        return ownerAgeRangeList;
    }

    public void setOwnerAgeRangeList(List<MerchantJoinSelectProperty> ownerAgeRangeList) {
        this.ownerAgeRangeList = ownerAgeRangeList;
    }

    public List<MerchantJoinSelectProperty> getSalesList() {
        return salesList;
    }

    public void setSalesList(List<MerchantJoinSelectProperty> salesList) {
        this.salesList = salesList;
    }

    public List<MerchantJoinSelectProperty> getRentList() {
        return rentList;
    }

    public void setRentList(List<MerchantJoinSelectProperty> rentList) {
        this.rentList = rentList;
    }

    public List<MerchantJoinSelectProperty> getAcreageList() {
        return acreageList;
    }

    public void setAcreageList(List<MerchantJoinSelectProperty> acreageList) {
        this.acreageList = acreageList;
    }

    public List<MerchantJoinSelectProperty> getAddedServicesList() {
        return addedServicesList;
    }

    public void setAddedServicesList(List<MerchantJoinSelectProperty> addedServicesList) {
        this.addedServicesList = addedServicesList;
    }

    public List<MerchantJoinSelectProperty> getAppearanceList() {
        return appearanceList;
    }

    public void setAppearanceList(List<MerchantJoinSelectProperty> appearanceList) {
        this.appearanceList = appearanceList;
    }

    public List<MerchantJoinSelectProperty> getCashierEquipmentList() {
        return cashierEquipmentList;
    }

    public void setCashierEquipmentList(List<MerchantJoinSelectProperty> cashierEquipmentList) {
        this.cashierEquipmentList = cashierEquipmentList;
    }

    public List<MerchantJoinSelectProperty> getTakeawayPlatformtList() {
        return takeawayPlatformtList;
    }

    public void setTakeawayPlatformtList(List<MerchantJoinSelectProperty> takeawayPlatformtList) {
        this.takeawayPlatformtList = takeawayPlatformtList;
    }

    public List<MerchantJoinSelectProperty> getShopTypeList() {
        return shopTypeList;
    }

    public void setShopTypeList(List<MerchantJoinSelectProperty> shopTypeList) {
        this.shopTypeList = shopTypeList;
    }

    public List<MerchantJoinSelectProperty> getAllowanceModeList() {
        return allowanceModeList;
    }

    public void setAllowanceModeList(List<MerchantJoinSelectProperty> allowanceModeList) {
        this.allowanceModeList = allowanceModeList;
    }

    public List<MerchantJoinSelectProperty> getSupplierList() {
        return supplierList;
    }

    public void setSupplierList(List<MerchantJoinSelectProperty> supplierList) {
        this.supplierList = supplierList;
    }

    public List<MerchantJoinSelectProperty> getSweepPaymentList() {
        return sweepPaymentList;
    }

    public void setSweepPaymentList(List<MerchantJoinSelectProperty> sweepPaymentList) {
        this.sweepPaymentList = sweepPaymentList;
    }

    public List<MerchantJoinSelectProperty> getServiceAttitudeList() {
        return serviceAttitudeList;
    }

    public void setServiceAttitudeList(List<MerchantJoinSelectProperty> serviceAttitudeList) {
        this.serviceAttitudeList = serviceAttitudeList;
    }

    public List<MerchantJoinSelectProperty> getLightingList() {
        return lightingList;
    }

    public void setLightingList(List<MerchantJoinSelectProperty> lightingList) {
        this.lightingList = lightingList;
    }

    public List<MerchantJoinSelectProperty> getOnlineShoppingList() {
        return onlineShoppingList;
    }

    public void setOnlineShoppingList(List<MerchantJoinSelectProperty> onlineShoppingList) {
        this.onlineShoppingList = onlineShoppingList;
    }

    public List<MerchantJoinSelectProperty> getPromotionFrequencyList() {
        return promotionFrequencyList;
    }

    public void setPromotionFrequencyList(List<MerchantJoinSelectProperty> promotionFrequencyList) {
        this.promotionFrequencyList = promotionFrequencyList;
    }
}
