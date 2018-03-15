package asuper.yt.cn.supermarket.modules.myclient.entities;


/**
 * Created by ZengXw on 2017/3/3.
 */
public class Clit {

    private String shopName;  //店铺名

    private String legalpersonName; // 经营者姓名

    private String shopAddress; //地址

    private String franchiseeNumber;

    private String phoneNumber;

    private int status;

    private String lastTime;

    private String applyStep;

    private String applyStepName;

    public String isDeleted;

    public int id;
    public String applyProvinceName;
    public String applyCityName;
    public String applyCountyName;
    public String applyShopName;
    public String applyLegalperson;
    public String applyPhoneNumber;
    public String applyAddress;
    public String applySteup;
    public String applyStatus;
    public long operationTime;

    public String getApplyStepName() {
        return applyStepName;
    }

    public void setApplyStepName(String applyStepName) {
        this.applyStepName = applyStepName;
    }

    public String getApplyStep() {
        return applyStep;
    }

    public void setApplyStep(String applyStep) {
        this.applyStep = applyStep;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLegalpersonName() {
        return legalpersonName;
    }

    public void setLegalpersonName(String legalpersonName) {
        this.legalpersonName = legalpersonName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getFranchiseeNumber() {
        return franchiseeNumber;
    }

    public void setFranchiseeNumber(String franchiseeNumber) {
        this.franchiseeNumber = franchiseeNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "Clit{" +
                "shopName='" + shopName + '\'' +
                ", legalpersonName='" + legalpersonName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", franchiseeNumber='" + franchiseeNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", status=" + status +
                ", lastTime='" + lastTime + '\'' +
                ", applyStep='" + applyStep + '\'' +
                ", applyStepName='" + applyStepName + '\'' +
                ", isDeleted='" + isDeleted + '\'' +
                ", id=" + id +
                ", applyProvinceName='" + applyProvinceName + '\'' +
                ", applyCityName='" + applyCityName + '\'' +
                ", applyCountyName='" + applyCountyName + '\'' +
                ", applyShopName='" + applyShopName + '\'' +
                ", applyLegalperson='" + applyLegalperson + '\'' +
                ", applyPhoneNumber='" + applyPhoneNumber + '\'' +
                ", applyAddress='" + applyAddress + '\'' +
                ", applySteup='" + applySteup + '\'' +
                ", applyStatus='" + applyStatus + '\'' +
                ", operationTime=" + operationTime +
                '}';
    }
}
