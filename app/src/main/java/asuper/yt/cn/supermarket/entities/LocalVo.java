package asuper.yt.cn.supermarket.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by liaoqinsen on 2017/5/22 0022.
 */

@DatabaseTable(tableName = "local_data")
public class LocalVo {
    @DatabaseField
    public String user_id;

    @DatabaseField(id = true,generatedId = true)
    public int id;

    @DatabaseField
    public String type;

    @DatabaseField
    public String data;

    public static class LocalType{
        public static final String SUBSIDY = "subsidy";
    }
}
