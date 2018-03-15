package asuper.yt.cn.supermarket.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liaoqinsen on 2017/5/9 0009.
 */

@DatabaseTable(tableName = "subsidy_table")
public class SubsidyLocalVO implements Serializable {


    @DatabaseField
    public String user_id;

    @DatabaseField
    public String shopName;
    @DatabaseField
    public String shopAssigner;
    @DatabaseField
    public String xcCode;
    @DatabaseField
    public String xcFinalName;
    @DatabaseField
    public String joinTime;
    @DatabaseField
    public String rentAmount;
    @DatabaseField
    public String adBoot;
    @DatabaseField
    public String cashierBoot;
    @DatabaseField
    public String goodShelfDisplay;
    @DatabaseField
    public String unicorporatedStatement;
    @DatabaseField
    public String approveCheck;
    @DatabaseField
    public String otherPhoto;
    @DatabaseField
    public String waterScreenshots;
    @DatabaseField
    public String auditNodeName;
    @DatabaseField
    public String auditStatus;
    @DatabaseField
    public String auditMessage;
    @DatabaseField
    public String attachmentState;
    @DatabaseField
    public String fileRuleJson;
    @DatabaseField
    public String isSendFrozen;
    @DatabaseField
    public String realName;
    @DatabaseField
    public String storeCode;
    @DatabaseField
    public String dataJson;
    @DatabaseField(defaultValue = "0")
    public String is_bottom;

    public List<AttachmentInfo> fileRule;
    @DatabaseField
    public String shopAddress;
    @DatabaseField(id = true)
    public String intentionId;
    @DatabaseField
    public String shopPhoneNumber;
    @DatabaseField
    public long auditTime;



    @DatabaseField
    public String isOver;
    @DatabaseField
    public int groupId;
}
