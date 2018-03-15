package asuper.yt.cn.supermarket.modules.myclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.entities.ButtonInfos;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class ToolGetButtoninfos {

    public static int[] getIconAndBackground(String step){
        step = step == null?"":step;
        switch (step){
            case "INTENTION_JOIN":
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status};
            case "step_1":
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status};
            case "step_2"://待申请加盟
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status};
            case "step_3"://加盟申请审核中
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_shop_status_green};
            case "step_4"://加盟申请被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_shop_status_red};
            case "step_5"://待签合同
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status};
            case "step_6"://合同审核中
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_shop_status_green};
            case "step_7"://合同被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_shop_status_red};
            case "step_8"://合同已通过
                return new int[]{R.drawable.ic_passed,R.drawable.bg_shop_status};
            case "subsidy_pending"://待申请租金补贴
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_shop_status};
            case "subsidy_audited"://补贴已通过
                return new int[]{R.drawable.ic_passed,R.drawable.bg_shop_status};
            case "subsidy_auditing"://待申请补贴
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status_green};
            case "subsidy_reject"://补贴被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_shop_status_red};
            case "step_11": // 支付逾期
            case "step_9": // 签约逾期
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_shop_status_red};
        }
        return new int[]{R.drawable.ic_waiting,R.drawable.bg_shop_status};
    }

    public static int[] getClientStepBackground(String step){
        step = step == null?"":step;
        switch (step){
            case "INTENTION_JOIN":
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step};
            case "step_1":
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step};
            case "step_2"://待申请加盟
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step};
            case "step_3"://加盟申请审核中
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_client_step_auditing};
            case "step_4"://加盟申请被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_client_step_reject};
            case "step_5"://待签合同
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step};
            case "step_6"://合同审核中
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_client_step_auditing};
            case "step_7"://合同被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_client_step_reject};
            case "step_8"://合同已通过
                return new int[]{R.drawable.ic_passed,R.drawable.bg_client_step};
            case "subsidy_pending"://待申请租金补贴
                return new int[]{R.drawable.ic_auditing,R.drawable.bg_client_step};
            case "subsidy_audited"://补贴已通过
                return new int[]{R.drawable.ic_passed,R.drawable.bg_client_step};
            case "subsidy_auditing"://待申请补贴
                return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step_auditing};
            case "subsidy_reject"://补贴被驳回
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_client_step_reject};
            case "step_11": // 支付逾期
            case "step_9": // 逾期
                return new int[]{R.drawable.ic_rejected,R.drawable.bg_client_step_reject};
        }
        return new int[]{R.drawable.ic_waiting,R.drawable.bg_client_step};
    }

    public static List<ButtonInfos> getButtonInfo(String intentId,String step){
        List<ButtonInfos> buttonInfoses = new ArrayList<>();
        ButtonInfos joinButton = new ButtonInfos();
        ButtonInfos contactButton = new ButtonInfos();
        ButtonInfos subsidyButton = new ButtonInfos();
        joinButton.setParameterId(intentId);
        contactButton.setParameterId(intentId);
        subsidyButton.setParameterId(intentId);
        switch (step){
            case "INTENTION_JOIN":
                break;
            case "step_1":
                break;
            case "step_2"://待申请加盟
                joinButton.setButton(true);
                joinButton.setUpdate(true);
                break;
            case "step_3"://加盟申请审核中
                joinButton.setButton(false);
                joinButton.setUpdate(false);
                break;
            case "step_4"://加盟申请被驳回
                joinButton.setButton(false);
                joinButton.setUpdate(true);
                break;
            case "step_5"://待签合同
                contactButton.setButton(true);
                contactButton.setUpdate(true);
                break;
            case "step_6"://合同审核中
                contactButton.setButton(false);
                contactButton.setUpdate(false);
                break;
            case "step_7"://合同被驳回
                contactButton.setButton(false);
                contactButton.setUpdate(true);
                break;
            case "step_8"://加盟申请审核中
                subsidyButton.setButton(true);
                subsidyButton.setUpdate(true);
                break;
            case "subsidy_audited"://补贴已通过
                subsidyButton.setButton(false);
                subsidyButton.setUpdate(false);
                break;
            case "subsidy_auditing"://待申请补贴
                subsidyButton.setButton(false);
                subsidyButton.setUpdate(false);
                break;
            case "subsidy_reject"://补贴被驳回
                subsidyButton.setButton(false);
                subsidyButton.setUpdate(true);
                break;
        }
        return buttonInfoses;
    }
}
