package asuper.yt.cn.supermarket.modules.plan;

import java.util.List;

/**
 * 拜访记录
 * Created by zengxiaowen on 2017/9/12.
 */

public class DropRecord {
    private int pageNum;
    private List<Plan.PlanItem> rows;
    private int pageSize;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List<Plan.PlanItem> getRows() {
        return rows;
    }

    public void setRows(List<Plan.PlanItem> rows) {
        this.rows = rows;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
