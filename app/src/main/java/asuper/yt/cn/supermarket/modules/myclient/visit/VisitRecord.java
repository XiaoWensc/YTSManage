package asuper.yt.cn.supermarket.modules.myclient.visit;

/**
 * Created by liaoqinsen on 2017/9/16 0016.
 */

public class VisitRecord {
    public String recordTime;//记录时间
    public String remark;//我的计划-备注
    public String visitType;//拜访方式    home_visit=登门拜访，phone_visit=电话拜访
    public String visitName;//登门拜访,电话拜访
    public String visit;//0=未拜访，1=已拜访
    public String visitContent;//拜访记录（内容）
    public String applyType;//revert=还原操作 giveupJoining=放弃加盟
    public String giveUpReason;//放弃加盟原因
}
