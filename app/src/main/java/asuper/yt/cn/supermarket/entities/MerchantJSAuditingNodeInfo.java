package asuper.yt.cn.supermarket.entities;

import java.io.Serializable;

/**
 * 评分表当前审核节点信息定义
 * Created by zengxiaowen on 2017/3/18.
 */

public class MerchantJSAuditingNodeInfo implements Serializable {
    private String nodeName;// 节点名称
    private String auditingValue;// 节点负责人审核输入描述信息

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String pNodeName) {
        nodeName = pNodeName;
    }

    public String getAuditingValue() {
        return auditingValue;
    }

    public void setAuditingValue(String pAuditingValue) {
        auditingValue = pAuditingValue;
    }

}
