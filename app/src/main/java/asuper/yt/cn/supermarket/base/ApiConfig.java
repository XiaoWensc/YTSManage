package asuper.yt.cn.supermarket.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Api接口白名单
 * Created by zengxiaowen on 2018/1/17.
 */

public class ApiConfig {
    public static final List<String> apis = new ArrayList<>();
    static {
        apis.add(Config.getURL("app/ratingScale/judgeOneLeader.htm"));
    }
}
