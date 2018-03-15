package asuper.yt.cn.supermarket.modules.dynamic;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by zengxiaowen on 2017/9/13.
 */

public class DynamicStore extends Store {

    public static final int REQUEST_GET_DYNAMIC_LIST = 0x3001;  // 通知列表
    public static final int REQUEST_GET_DYNAMIC_TZ_INFO = 0x3002;  // 通知历史消息列表
    public static final int REQUEST_GET_DYNAMIC_ISNOTICE = 0x3003;  // 通知历史消息列表
//    public static final int REQUEST_GET_DYNAMIC_LISTSIZE = 0x3004;  // 获取通知代办列表条数
    public static final int REQUEST_GET_DYNAMIC_ADD = 0x3005;  // 新增逾期原因

    public DynamicStore(StoreDependencyDelegate delegate) {
        super(delegate);
    }

    @Override
    public void doAction(final int type, final HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type) {
            case REQUEST_GET_DYNAMIC_LIST:
                final int frag = (int) data.get("frag");
                data.remove("frag");
                ToolOkHTTP.post(Config.getURL(frag == 0 ? "app/shopDynamic/noticeList.htm" : "app/shopDynamic/todoList.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        Map<String,Object> mapDate = new HashMap<>();
                        mapDate.put("frag",frag);
                        int pageNum = (int) data.get("start");
                        pageNum++;
                        mapDate.put("pageNum",pageNum);
                        if (frag==0) {
                            Dynamic dyn = ToolGson.fromJson(response.toString(), Dynamic.class);
                            mapDate.put("date",dyn);
                            callBack.onResult(type, mapDate);
                        }else{
                            DynmicDaiBan daiBan = ToolGson.fromJson(response.toString(),DynmicDaiBan.class);
                            mapDate.put("date",daiBan);
                            callBack.onResult(type,mapDate);
                        }
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case REQUEST_GET_DYNAMIC_TZ_INFO:
                ToolOkHTTP.post(Config.getURL("app/shopDynamic/noticeHistory.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        Notice notice = ToolGson.fromJson(response.optString("resultObject"), Notice.class);
                        callBack.onResult(type, notice);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
            case REQUEST_GET_DYNAMIC_ISNOTICE:
                ToolOkHTTP.post(Config.getURL("app/shopDynamic/removeIsNoticeDynamic.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

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
//            case REQUEST_GET_DYNAMIC_LISTSIZE:
//                ToolOkHTTP.post(Config.getURL("app/shopDynamic/getCount.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
//
//                    @Override
//                    public void onSuccess(JSONObject response) {
//                        JSONObject object = response.optJSONObject("resultObject");
//                        int[] toNot = new int[3];
//                        toNot[0] = object.optInt("total",0);
//                        toNot[1] = object.optInt("notice",0);
//                        toNot[2] = object.optInt("todo",0);
//                        callBack.onResult(type,toNot);
//                    }
//
//                    @Override
//                    public void onFailure() {
//                        callBack.onResult(type,null);
//                    }
//                });
//                break;
            case REQUEST_GET_DYNAMIC_ADD:
                final String str = data.get("str").toString();
                data.remove("str");
                ToolOkHTTP.post(Config.getURL(str.equals("add")?"app/shopDynamic/addOverdue.htm":"app/shopDynamic/removeNotice.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                    @Override
                    public void onSuccess(JSONObject response) {
                        callBack.onResult(type,str);
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type,null);
                    }
                });
                break;
        }
    }

    public class Notice {
        public int pageNum;
        public int startIndex;
        public int pageSize;
        public int totalRow;
        public List<Info> rows;

        public class Info {
            public int id;
            public int shopId;
            public int employeeId;
            public String employeeName;
            public int isCheck;
            public int isDeleted;
            public int noticeType;
            public String shopName;
            public String realManName;
            public String realManPhone;
            public String shopAddress;
            public String gmtCreate;
            public String gmtUpdate;
            public String detailContent;
            public String hint;
            public String currentStatus;
            public String preStatusCompleteTime;
            public String forStatusEndTime;
            public String noticeEffectTime;
            public String completeTime;
            public String endTime;
            public String confirmButton;
        }
    }
}
