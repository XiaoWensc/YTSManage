package supermarket.cn.yt.asuper.ytlibrary.widgets;

import java.io.Serializable;

/**
 * 下拉框、单选框、复选框选项Bean
 *
 * @author zxw
 * @version 1.0
 */
public class Option implements Serializable, Cloneable {

    private static final long serialVersionUID = -724868344947644938L;

    private String value = "请选择...";

    private String label = "";

    public Option() {

    }

    public Option(String value, String label) {
        if (value!=null)
            this.value = value;
        if (label!=null)
            this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return value;
    }


}
