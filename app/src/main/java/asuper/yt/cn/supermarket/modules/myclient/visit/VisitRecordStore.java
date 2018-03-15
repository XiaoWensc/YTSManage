package asuper.yt.cn.supermarket.modules.myclient.visit;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/16 0016.
 */

public class VisitRecordStore extends Store {

    public VisitRecordStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case VisitRecordsFragment.REQUEST_GET_RECORDS:
                ToolOkHTTP.post(Config.getURL("app/myPlan/recordList.htm"),data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        List<VisitRecord> visitRecords = ToolGson.fromJson(response.optString("resultObject"),new TypeToken<List<VisitRecord>>(){}.getType());
                        callBack.onResult(type,visitRecords);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
        }
    }

}
