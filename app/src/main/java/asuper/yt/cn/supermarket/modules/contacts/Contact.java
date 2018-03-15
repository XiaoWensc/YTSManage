package asuper.yt.cn.supermarket.modules.contacts;

/**
 * Created by zengxiaowen on 2017/10/12.
 */

public class Contact {
    private String mName;
    private String mPhone;
    private int mType;


    public Contact(String name,String phone, int type) {
        mName = name;
        mType = type;
        mPhone = phone;
    }

    public String getmName() {
        return mName;
    }

    public int getmType() {
        return mType;
    }

    public String getmPhone() {
        return mPhone;
    }
}
