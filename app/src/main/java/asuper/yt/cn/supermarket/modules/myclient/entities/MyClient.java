package asuper.yt.cn.supermarket.modules.myclient.entities;

import java.util.List;

/**
 * Created by ZengXw on 2017/3/3.
 */

public class MyClient {

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
}
