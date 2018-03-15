package asuper.yt.cn.supermarket.modules.myprofile;

import java.util.HashMap;

import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/9/14.
 */

public class AccountStore extends Store {

    public AccountStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {

    }
}
