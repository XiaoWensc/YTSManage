package asuper.yt.cn.supermarket.service;

/**
 * Created by zengxiaowen on 2017/11/16.
 */

public class JPushExt {
    private int shopId; //店铺ID
    private int noticeType; //
    private String activity;  //用户行为
    public String userId;  // 用户ID



    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(int noticeType) {
        this.noticeType = noticeType;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
