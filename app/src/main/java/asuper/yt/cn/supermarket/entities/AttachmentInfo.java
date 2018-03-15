package asuper.yt.cn.supermarket.entities;

import java.io.Serializable;

/**
 * Created by liaoqinsen on 2017/9/11 0011.
 */

public class AttachmentInfo implements Serializable {
    public String key;
    public String des;
    public String name;
    public String min;
    public String max;


    @Override
    public String toString() {
        return "AttachmentInfo{" +
                "key='" + key + '\'' +
                ", des='" + des + '\'' +
                ", name='" + name + '\'' +
                ", min='" + min + '\'' +
                ", max='" + max + '\'' +
                '}';
    }
}
