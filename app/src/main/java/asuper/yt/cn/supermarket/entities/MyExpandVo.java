package asuper.yt.cn.supermarket.entities;

import java.util.List;

/**
 * Created by liaoqinsen on 2017/4/28 0028.
 */

public class MyExpandVo {
    public String groupEnName;
    public String groupZnName;
    public int id;
    public List<MyExpandSub> nodes;

    public static class MyExpandSub {
        public String nodeEnName;
        public String nodeZnName;
        public String isDelete;
        public int num;
        public String step;
        public String stepName;
    }
}
