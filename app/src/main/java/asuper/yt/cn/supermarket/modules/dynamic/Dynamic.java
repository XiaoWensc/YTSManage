package asuper.yt.cn.supermarket.modules.dynamic;

import java.util.List;
import java.util.Map;

/**
 * Created by zengxiaowen on 2017/9/13.
 */

public class Dynamic {

    private String errorMessage ;
    private int code;
    private Map<String ,ResultItem> resultObject;
    private boolean success;

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

    public Map<String, ResultItem> getResultObject() {
        return resultObject;
    }

    public void setResultObject(Map<String, ResultItem> resultObject) {
        this.resultObject = resultObject;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public class ResultItem {

        private int pageNum;
        private int startIndex;
        private int pageSize;
        private int totalRow;

        private List<Item> rows;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
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

        public List<Item> getRows() {
            return rows;
        }

        public void setRows(List<Item> rows) {
            this.rows = rows;
        }
    }

    public class Item {
        public int id;
        public int shopId;
        public int employeeId;
        public String employeeName;
        public String shopName;
        public String currentStatus;
        public String hintMsg;
        public String detailTime;
        public String resultType;
        public String isNew;
        public String applyStep;
        public String displayStatus;
        public String stepCode;
        public boolean notice;
        public int noCheck;



        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getShopId() {
            return shopId;
        }

        public void setShopId(int shopId) {
            this.shopId = shopId;
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public String getEmployeeName() {
            return employeeName;
        }

        public void setEmployeeName(String employeeName) {
            this.employeeName = employeeName;
        }

        public String getDetailTime() {
            return detailTime;
        }

        public void setDetailTime(String detailTime) {
            this.detailTime = detailTime;
        }

        public String getCurrentStatus() {
            return currentStatus;
        }

        public void setCurrentStatus(String currentStatus) {
            this.currentStatus = currentStatus;
        }

        public String getHintMsg() {
            return hintMsg;
        }

        public void setHintMsg(String hintMsg) {
            this.hintMsg = hintMsg;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", shopId=" + shopId +
                    ", employeeId=" + employeeId +
                    ", employeeName=" + employeeName +
                    ", shopName='" + shopName + '\'' +
                    ", detailTime='" + detailTime + '\'' +
                    ", currentStatus='" + currentStatus + '\'' +
                    ", hintMsg='" + hintMsg + '\'' +
                    '}';
        }
    }
}
