package asuper.yt.cn.supermarket.modules.plan;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.myclew.MyClewActivity;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/9/7.
 */

public class PlanStore extends Store {

    List<ClientInfoDetail> clientelesOld;

    public static final int REQUEST_GET_INDEX_WEEKVISITCOUNT = 0x2001;  // 每日拜访数
    public static final int REQUEST_GET_INDEX_WEEKPLANCOUNT = 0x2008;  // 每日计划数
    public static final int REQUEST_GET_INDEX_NEWCUSTOMERCOUNT = 0x2002;  // 每日新增客户数
    public static final int REQUEST_GET_PLAN_INSERT = 0x2003; //添加计划
    public static final int REQUEST_GET_PLAN_LIST = 0x2004; //查询计划
    public static final int REQUEST_GET_PLAN_RE = 0x2005; //重新计划
    public static final int REQUEST_GET_PLAN_UPDATE = 0x2006; //结束拜访
    public static final int REQUEST_GET_PLAN_HISTORY = 0x2007; //拜访历史

    public PlanStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case REQUEST_GET_INDEX_WEEKPLANCOUNT:
                ToolOkHTTP.post(Config.getURL("app/myPlan/weekPlanCount.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_INDEX_WEEKVISITCOUNT:
                ToolOkHTTP.post(Config.getURL("app/myPlan/weekVisitCount.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_INDEX_NEWCUSTOMERCOUNT:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_NEWCUSTOMERCOUNT), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_PLAN_INSERT:
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_INSERT), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_PLAN_LIST:
                ToolOkHTTP.post(Config.getURL("app/myPlan/list.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_PLAN_RE:
                ToolOkHTTP.post(Config.getURL("app/myPlan/rePlaning.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_PLAN_UPDATE:
                ToolOkHTTP.post(Config.getURL("app/myPlan/update.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case REQUEST_GET_PLAN_HISTORY:
                ToolOkHTTP.post(Config.getURL("app/myPlan/visitingHistory.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,response);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
            case MyClewActivity.REQUEST_GET_COUNT:
                int unCompleteCount = 0;
                try {
                    clientelesOld = ToolDbOperation.getClientDao().queryForEq("user_id", Config.UserInfo.USER_ID);
                    unCompleteCount = clientelesOld.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int finalUnCompleteCount = unCompleteCount;
                ToolOkHTTP.post(Config.getURL(Config.URL.URL_NEW_MISSION_COUNT), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type, new int[]{response.optInt("resultObject", 0), finalUnCompleteCount});
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, new int[]{0, finalUnCompleteCount});
                    }

                });
                break;
        }
    }
}
