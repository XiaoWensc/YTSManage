package asuper.yt.cn.supermarket.entities;

import java.io.Serializable;

/**
 * Created by zengxiaowen on 2017/3/16.
 */
public class MerchantJoinSelectProperty implements Serializable {

    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MerchantJoinSelectProperty{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
