package asuper.yt.cn.supermarket.modules.dynamic;

import java.util.List;
import java.util.Map;

/**
 * Created by zengxiaowen on 2017/11/13.
 */

public class DynmicDaiBan {

    private Map<String ,List<Dynamic.Item>> resultObject;

    public Map<String, List<Dynamic.Item>> getResultObject() {
        return resultObject;
    }

    public void setResultObject(Map<String, List<Dynamic.Item>> resultObject) {
        this.resultObject = resultObject;
    }
}
