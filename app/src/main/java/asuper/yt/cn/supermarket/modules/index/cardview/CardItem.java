package asuper.yt.cn.supermarket.modules.index.cardview;


import java.util.List;

public class CardItem {

    private String mTitleResource;
    private List<Integer> mDays;

    public CardItem(String title, List<Integer> list) {
        mTitleResource = title;
        mDays = list;
    }

    public List<Integer> getText() {
        return mDays;
    }

    public String getTitle() {
        return mTitleResource;
    }
}
