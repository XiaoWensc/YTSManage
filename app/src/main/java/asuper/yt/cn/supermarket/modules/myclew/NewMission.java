package asuper.yt.cn.supermarket.modules.myclew;

import java.util.List;

import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class NewMission {
    public String shopName;
    public String shopAddress;
    public String shopkeeperName;
    public String shopkeeperMobile;
    public String shopProvinces;
    public String shopCity;
    public String shopCountry;
    public String shopProvincesCode;
    public String shopCityCode;
    public String shopCountryCode;
    public long createTime;
    public String createrName;
    public String createrId;
    public long updateTime;
    public String updaterName;
    public String updaterId;
    public String createTimeStart;
    public String createTimeEnd;
    public String salesman;
    public String salesmanId;
    public String shopId;
    public String isRead;
    public String hasJoinScore;
    public String customSource;
    public String remark;
    public String applySteup;
    public long franchiseeTime;
    public String isFranchisee;
    public String id;

    public String customSourceCode;

    public List<ImageGalleryActivity.ImageGalleryItem> fileRule;

    @Override
    public String toString() {
        return "NewMission{" +
                "shopName='" + shopName + '\'' +
                ", shopAddress='" + shopAddress + '\'' +
                ", shopkeeperName='" + shopkeeperName + '\'' +
                ", shopkeeperMobile='" + shopkeeperMobile + '\'' +
                ", shopProvinces='" + shopProvinces + '\'' +
                ", shopCity='" + shopCity + '\'' +
                ", shopCountry='" + shopCountry + '\'' +
                ", shopProvincesCode='" + shopProvincesCode + '\'' +
                ", shopCityCode='" + shopCityCode + '\'' +
                ", shopCountryCode='" + shopCountryCode + '\'' +
                ", createTime=" + createTime +
                ", createrName='" + createrName + '\'' +
                ", createrId='" + createrId + '\'' +
                ", updateTime=" + updateTime +
                ", updaterName='" + updaterName + '\'' +
                ", updaterId='" + updaterId + '\'' +
                ", createTimeStart='" + createTimeStart + '\'' +
                ", createTimeEnd='" + createTimeEnd + '\'' +
                ", salesman='" + salesman + '\'' +
                ", salesmanId='" + salesmanId + '\'' +
                ", shopId='" + shopId + '\'' +
                ", isRead='" + isRead + '\'' +
                ", hasJoinScore='" + hasJoinScore + '\'' +
                ", customSource='" + customSource + '\'' +
                ", remark='" + remark + '\'' +
                ", applySteup='" + applySteup + '\'' +
                ", franchiseeTime=" + franchiseeTime +
                ", isFranchisee='" + isFranchisee + '\'' +
                ", id='" + id + '\'' +
                ", customSourceCode='" + customSourceCode + '\'' +
                '}';
    }
}
