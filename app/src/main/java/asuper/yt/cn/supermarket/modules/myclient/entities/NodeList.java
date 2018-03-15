package asuper.yt.cn.supermarket.modules.myclient.entities;

import java.util.List;

/**
 * Created by liaoqinsen on 2017/5/18 0018.
 */

public class NodeList {

    public List<NodeInfo> resultObject;

    public class NodeInfo {

        public String auditorId;
        public String auditorName;
        public String applyId;
        public String taskId;
        public String auditMessage;
        public String processType;
        public String auditNodeName;
        public String auditTime;
        public String businessData;
        public String taskDefId;
        public String EAPSubsidiaryReviewer;
        public String taskDefName;
        public String intentionId;
        public String formId;
        public String type;
        public String originatorId;
        public String originatorName;
        public String telephone;
        public String originatorCompanyId;
        public String companyId;
        public String roleId;
        public String processStatus;

        @Override
        public String toString() {
            return "NodeInfo{" +
                    "auditorId='" + auditorId + '\'' +
                    ", auditorName='" + auditorName + '\'' +
                    ", applyId='" + applyId + '\'' +
                    ", taskId='" + taskId + '\'' +
                    ", auditMessage='" + auditMessage + '\'' +
                    ", processType='" + processType + '\'' +
                    ", auditNodeName='" + auditNodeName + '\'' +
                    ", auditTime='" + auditTime + '\'' +
                    ", businessData='" + businessData + '\'' +
                    ", taskDefId='" + taskDefId + '\'' +
                    ", EAPSubsidiaryReviewer='" + EAPSubsidiaryReviewer + '\'' +
                    ", taskDefName='" + taskDefName + '\'' +
                    ", intentionId='" + intentionId + '\'' +
                    ", formId='" + formId + '\'' +
                    ", type='" + type + '\'' +
                    ", originatorId='" + originatorId + '\'' +
                    ", originatorName='" + originatorName + '\'' +
                    ", telephone='" + telephone + '\'' +
                    ", originatorCompanyId='" + originatorCompanyId + '\'' +
                    ", companyId='" + companyId + '\'' +
                    ", roleId='" + roleId + '\'' +
                    ", processStatus='" + processStatus + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NodeList{" +
                "resultObject=" + resultObject +
                '}';
    }
}

