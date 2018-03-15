package asuper.yt.cn.supermarket.modules.common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.UploadStore;
import asuper.yt.cn.supermarket.modules.myclient.ToolAttachment;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2018/1/5.
 */

public class GalleryStroe extends UploadStore {

    protected static final int POST_GALLERY_LEGAL = 0x1534845;

    public GalleryStroe(StoreDependencyDelegate delegate) {
        super(delegate);
    }
    @Override
    protected void DoAction(int type, HashMap<String, Object> data, StoreResultCallBack callBack) {
        switch (type){
            case POST_GALLERY_LEGAL:
                ToolOkHTTP.post(Config.getURL("/app/ratingScale/queryRatingScaleAnnexByLegal2.htm"), data, new ToolOkHTTP.OKHttpCallBack() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        JSONArray res = response.optJSONArray("resultObject");

//                        ToolAttachment.reGenerateAttachmentInfo(pageData,imageGalleryItemList,res,shopId+"_1");
//                        callBack.onResult(type,imageGalleryItemList);

                        callBack.onResult(type,response.toString());
                    }

                    @Override
                    public void onFailure() {
                        callBack.onResult(type, null);
                    }
                });
                break;
        }
    }


}
