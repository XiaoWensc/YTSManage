package asuper.yt.cn.supermarket.entities;

import java.util.List;
import java.util.Map;

/**
 * Created by zengxiaowen on 2017/3/20.
 */

public class ImgUploading {

    private int responseCode;
    private Map<String, List<String>> fileUrls;
    private String errMsg;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Map<String, List<String>> getFileUrls() {
        return fileUrls;
    }

    public void setFileUrls(Map<String, List<String>> fileUrls) {
        this.fileUrls = fileUrls;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
