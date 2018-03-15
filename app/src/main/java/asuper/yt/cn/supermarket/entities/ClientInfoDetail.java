package asuper.yt.cn.supermarket.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;
import asuper.yt.cn.supermarket.utils.ToolStringToList;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 客户详情
 * Created by zengxiaowen on 2017/3/15.
 */

@DatabaseTable(tableName = "tb_client")
public class ClientInfoDetail implements Serializable {

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private String user_id;

    private int franchiseeNumber;//意向加盟商家编号
    private String followUpPersonnel;//跟进人员

    @DatabaseField
    private String shopName;//店铺名称

    @DatabaseField
    private String legalpersonName;//经营者姓名

    @DatabaseField
    private String provinceCode; //省编号

    @DatabaseField
    private String cityCode; //城市编号

    @DatabaseField
    private String countyCode; //县编码

    @DatabaseField
    private String applyStreetCode; //街道编码

    @DatabaseField
    private String provinceName; //省名称

    @DatabaseField
    private String cityName; //城市名称

    @DatabaseField
    private String countyName; //县名称

    @DatabaseField
    private String applyStreetName; //街道名称

    @DatabaseField
    private String phoneNumber; //经营者电话

    @DatabaseField
    private String shopAddree; //地址(后台返回字段就是shopAddree)

    @DatabaseField
    private int isInterested; //是否对加盟雅堂小超感兴趣

    @DatabaseField
    private String longitude; //经度

    @DatabaseField
    private String latitude; //纬度

    private String lastTime; //最后更新时间
    private List<String> pictures; //图片数组

    @DatabaseField
    public String pictur; //未上传图片

    @DatabaseField
    public String pictur_up; // 已上传图片

    @DatabaseField
    private String locationAddress;//定位地址

    @DatabaseField(defaultValue = "0")
    public String is_bottom;
    @DatabaseField
    public String tid;

    public List<ImageGalleryActivity.ImageGalleryItem> fileRule;

    private int status;//客户状态
    public int visitListCount;//客户状态
    private String applyStep;//步骤编号
    private List<String> historicalPersonnel;//历史跟进人员数组
    private List<ButtonInfos> buttonInfos;//

    private int pageNum;  //当前页

    private List<Clit> rows;

    private int pageSize; // 每页数量

    private int totalRow;  // zong

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<Clit> getRows() {
        return rows;
    }

    public void setRows(List<Clit> rows) {
        this.rows = rows;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public void setTotalRow(int totalRow) {
        this.totalRow = totalRow;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<PhotoInfo> getPictur() {
        List<PhotoInfo> infos = new ArrayList<>();
        List<Object> phts = ToolStringToList.StringToList(pictur);
        if (phts != null) {
            for (int i = 0; i < phts.size(); i++) {
                PhotoInfo info = new PhotoInfo();
                info.setPhotoId(0);
                info.setPhotoPath(phts.get(i).toString());
                infos.add(info);
            }
        }
        return infos;
    }

    public List<PhotoInfo> getPicturs() {
        if(pictures == null || pictures.size() < 1) return null;
        List<PhotoInfo> infos = new ArrayList<>();
        List<Object> phts = ToolStringToList.StringToList(pictures.get(0));
        if (phts != null) {
            for (int i = 0; i < phts.size(); i++) {
                PhotoInfo info = new PhotoInfo();
                info.setPhotoId(1);
                info.setPhotoPath(phts.get(i).toString());
                infos.add(info);
            }
        }
        return infos;
    }

    public void setPictur(List<PhotoInfo> list) {
        List<String> date_up = new ArrayList<>(); //已上传
        List<String> date = new ArrayList<>();  // 未上传
        if(list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getPhotoId() == 0) {
                    date.add(list.get(i).getPhotoPath());
                } else {
                    date_up.add(list.get(i).getPhotoPath());
                }
            }
        }
        this.pictur = ToolStringToList.ListToString(date);
        this.pictur_up = ToolStringToList.ListToString(date_up);
    }

    public List<PhotoInfo> getPictur_up() {
        List<PhotoInfo> infos = new ArrayList<>();
        List<Object> phts = ToolStringToList.StringToList(pictur_up);
        if (phts != null) {
            for (int i = 0; i < phts.size(); i++) {
                PhotoInfo info = new PhotoInfo();
                info.setPhotoId(1);
                info.setPhotoPath(phts.get(i).toString());
                infos.add(info);
            }
        }
        return infos;
    }

    public int getFranchiseeNumber() {
        return franchiseeNumber;
    }

    public void setFranchiseeNumber(int franchiseeNumber) {
        this.franchiseeNumber = franchiseeNumber;
    }

    public String getFollowUpPersonnel() {
        return followUpPersonnel;
    }

    public void setFollowUpPersonnel(String followUpPersonnel) {
        this.followUpPersonnel = followUpPersonnel;
    }

    public String getShopName() {
        return shopName==null?"":shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getLegalpersonName() {
        return legalpersonName==null?"":legalpersonName;
    }

    public void setLegalpersonName(String legalpersonName) {
        this.legalpersonName = legalpersonName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getProvinceName() {
        return provinceName==null?"":provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityName() {
        return cityName==null?"":cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyName() {
        return countyName==null?"":countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getPhoneNumber() {
        return phoneNumber==null?"":phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getIsInterested() {
        return isInterested;
    }

    public void setIsInterested(int isInterested) {
        this.isInterested = isInterested;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLastTime() {
        return lastTime==null?"":lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public String getLocationAddress() {
        return locationAddress==null?"":locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getApplyStep() {
        return applyStep;
    }

    public void setApplyStep(String applyStep) {
        this.applyStep = applyStep;
    }

    public List<String> getHistoricalPersonnel() {
        return historicalPersonnel;
    }

    public void setHistoricalPersonnel(List<String> historicalPersonnel) {
        this.historicalPersonnel = historicalPersonnel;
    }

    public List<ButtonInfos> getButtonInfos() {
        return buttonInfos;
    }

    public void setButtonInfos(List<ButtonInfos> buttonInfos) {
        this.buttonInfos = buttonInfos;
    }

    public String getStreetCode() {
        return applyStreetCode==null?"":applyStreetCode;
    }

    public void setStreetCode(String streetCode) {
        this.applyStreetCode = streetCode;
    }

    public String getStreetName() {
        return applyStreetName==null?"":applyStreetName;
    }

    public void setStreetName(String streetName) {
        this.applyStreetName = streetName;
    }

    @Override
    public String toString() {
        return "ClientInfoDetail{" +
                "id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", franchiseeNumber=" + franchiseeNumber +
                ", followUpPersonnel='" + followUpPersonnel + '\'' +
                ", shopName='" + shopName + '\'' +
                ", legalpersonName='" + legalpersonName + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", countyCode='" + countyCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countyName='" + countyName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", shopAddree='" + shopAddree + '\'' +
                ", isInterested=" + isInterested +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", lastTime='" + lastTime + '\'' +
                ", pictures=" + pictures +
                ", pictur='" + pictur + '\'' +
                ", pictur_up='" + pictur_up + '\'' +
                ", locationAddress='" + locationAddress + '\'' +
                ", status=" + status +
                ", applyStep='" + applyStep + '\'' +
                ", historicalPersonnel=" + historicalPersonnel +
                ", buttonInfos=" + buttonInfos +
                '}';
    }

    public void setShopAddree(String shopAddree) {
        this.shopAddree = shopAddree;
    }

    public String getShopAddree() {
        return this.shopAddree==null?"":this.shopAddree;
    }
}
