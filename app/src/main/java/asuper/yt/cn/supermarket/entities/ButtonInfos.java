package asuper.yt.cn.supermarket.entities;

import java.io.Serializable;

/**
 * Created by zengxiaowen on 2017/3/15.
 */
public class ButtonInfos implements Serializable {

    private String buttonName; //按钮或页签名称

    private String buttonUri; //对应的url地址

    private String parameterId;//对应的业务Id

    public String isNew;

    private boolean update;//是否需要修改

    private boolean button;//是否是按钮
    public String processType;

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonUri() {
        return buttonUri;
    }

    public void setButtonUri(String buttonUri) {
        this.buttonUri = buttonUri;
    }

    public String getParameterId() {
        return parameterId;
    }

    public void setParameterId(String parameterId) {
        this.parameterId = parameterId;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public boolean isButton() {
        return button;
    }

    public void setButton(boolean button) {
        this.button = button;
    }

    @Override
    public String toString() {
        return "ButtonInfos{" +
                "buttonName='" + buttonName + '\'' +
                ", buttonUri='" + buttonUri + '\'' +
                ", parameterId='" + parameterId + '\'' +
                ", isNew='" + isNew + '\'' +
                ", update=" + update +
                ", button=" + button +
                ", processType='" + processType + '\'' +
                '}';
    }
}
