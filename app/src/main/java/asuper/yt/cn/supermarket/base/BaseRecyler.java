package asuper.yt.cn.supermarket.base;

import java.util.Collection;
import java.util.List;

/**
 * Created by zengxiaowen on 2017/10/23.
 */

interface BaseRecyler {
    void addItemDates(Collection<? extends Object> collection);
    void clear();
    List<Object> getAllList();
}
