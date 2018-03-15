package asuper.yt.cn.supermarket.modules.plan;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zengxiaowen on 2017/9/11.
 */

public class Plan {

    private String errorMessage;
    private int code;
    private List<PlanItem> resultObject;
    private boolean success;
    private int pageNum = 0;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<PlanItem> getResultObject() {
        return resultObject;
    }

    public void setResultObject(List<PlanItem> resultObject) {
        this.resultObject = resultObject;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public class PlanItem implements Serializable{
        private int id;
        private int userId;
        private int intentionId;
        private String createTime;
        private String remark;
        private int visit;
        private String endTime;
        private String visitType;
        private String startTime;
        private String shopAddress;
        private String phoneNumber;
        private String visitContent;
        private String listTime;
        private String shopName;
        private String shoplegalPerson;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getIntentionId() {
            return intentionId;
        }

        public void setIntentionId(int intentionId) {
            this.intentionId = intentionId;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getVisit() {
            return visit;
        }

        public void setVisit(int visit) {
            this.visit = visit;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getVisitType() {
            return visitType;
        }

        public void setVisitType(String visitType) {
            this.visitType = visitType;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getShopAddress() {
            return shopAddress;
        }

        public void setShopAddress(String shopAddress) {
            this.shopAddress = shopAddress;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getVisitContent() {
            return visitContent;
        }

        public void setVisitContent(String visitContent) {
            this.visitContent = visitContent;
        }

        public String getListTime() {
            return listTime;
        }

        public void setListTime(String listTime) {
            this.listTime = listTime;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getShoplegalPerson() {
            return shoplegalPerson;
        }

        public void setShoplegalPerson(String shoplegalPerson) {
            this.shoplegalPerson = shoplegalPerson;
        }

        @Override
        public String toString() {
            return "PlanItem{" +
                    "id=" + id +
                    ", userId=" + userId +
                    ", intentionId=" + intentionId +
                    ", createTime='" + createTime + '\'' +
                    ", remark='" + remark + '\'' +
                    ", visit=" + visit +
                    ", endTime='" + endTime + '\'' +
                    ", visitType='" + visitType + '\'' +
                    ", startTime='" + startTime + '\'' +
                    ", shopAddress='" + shopAddress + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", visitContent='" + visitContent + '\'' +
                    ", listTime='" + listTime + '\'' +
                    ", shopName='" + shopName + '\'' +
                    ", shoplegalPerson='" + shoplegalPerson + '\'' +
                    '}';
        }
    }
}
