package asuper.yt.cn.supermarket.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.MerchantJoinScoretableVO;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;

/**
 * 数据库访问帮助类
 *
 * @author zxw
 * @version 1.0
 */
public class ToolDatabase extends OrmLiteSqliteOpenHelper {

    //	private static String databaseName;
//	private static int databaseVersion;
    private List<Class> table = new ArrayList<Class>();
    private ToolDatabase dbHelper = null;

    private OnDatabaseUpragdeLitsenler onDatabaseUpragdeLitsenler;

    /**
     * 必须对外提供Public构造函数（实例化不用该方法）
     *
     * @param context 上下文
     */
    public ToolDatabase(Context context) {
        super(YTApplication.get(), Config.DB_NAME, null, Config.DB_VERSION);
    }

    public ToolDatabase(Context context, OnDatabaseUpragdeLitsenler onDatabaseUpragdeLitsenler){
        super(YTApplication.get(), Config.DB_NAME, null, Config.DB_VERSION);
        this.onDatabaseUpragdeLitsenler = onDatabaseUpragdeLitsenler;
    }

    /**
     * 实例化对象
     *
     * @param dbName  数据库名称
     * @param version 数据库版本
     * @return
     */
//    public static ToolDatabase gainInstance(Context mContext, String dbName, int version) {
//        if (dbHelper == null) {
//            databaseName = dbName;
//            databaseVersion = version;
//            //会隐式调用public构造方法
//            dbHelper = OpenHelperManager.getHelper(
//                    MApplication.gainContext(), ToolDatabase.class);
//        }
//        return dbHelper;
//    }

    /**
     * 释放数据库连接
     */
    public void releaseAll() {
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    /**
     * 配置实体
     *
     * @param cls 实体
     */
    public void addEntity(Class cls) {
        table.add(cls);
    }

    /**
     * 删除表
     *
     * @param entity 实体
     */
    public void dropTable(Class entity) {
        try {
            TableUtils.dropTable(getConnectionSource(), entity, true);
        } catch (SQLException e) {
            Log.e(ToolDatabase.class.getName(), "Unable to drop datbases", e);
        }
    }

    /**
     * 创建表
     *
     * @param entity 实体
     */
    public void createTable(Class entity) {
        try {
            TableUtils.createTableIfNotExists(getConnectionSource(), entity);
        } catch (Exception e) {
            Log.e(ToolDatabase.class.getName(), "Unable to drop datbases", e);
        }
    }

    /**
     * 创建SQLite数据库
     */
    @Override
    public void onCreate(SQLiteDatabase sqliteDatabase,
                         ConnectionSource connectionSource) {
        try {
            for (Class entity : table) {
                TableUtils.createTableIfNotExists(connectionSource, entity);
            }
        } catch (SQLException e) {
            Log.e(ToolDatabase.class.getName(), "Unable to create datbases", e);
        }
    }

    /**
     * 更新SQLite数据库
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqliteDatabase,
                          ConnectionSource connectionSource, int oldVer, int newVer) {

        ToolLog.i("oldVer < Config.DB_VERSION:"+oldVer +","+ Config.DB_VERSION);
        //数据库版本迭代
        if (oldVer < 23) {  // 更新至 23
//                    TableUtils.dropTable(connectionSource, entity, true);
            // 加盟表增加营业场所字段
            ToolDatabaseUtil.upgradeTable(sqliteDatabase, connectionSource, MerchantJoinScoretableVO.class, ToolDatabaseUtil.OPERATION_TYPE.ADD);
        }
        if (oldVer<30) {
            // 客户表增加街道编码与街道名
            // 加盟表增加街道名称街道编码、增加证件照信息
            ToolDatabaseUtil.upgradeTable(sqliteDatabase, connectionSource, ClientInfoDetail.class, ToolDatabaseUtil.OPERATION_TYPE.ADD);
            ToolDatabaseUtil.upgradeTable(sqliteDatabase, connectionSource, MerchantJoinScoretableVO.class, ToolDatabaseUtil.OPERATION_TYPE.DELETE);
            ToolDatabaseUtil.upgradeTable(sqliteDatabase, connectionSource, MerchantJoinScoretableVO.class, ToolDatabaseUtil.OPERATION_TYPE.ADD);
        }
        onCreate(sqliteDatabase,connectionSource);

        if(true) return;
        if (oldVer < Config.DB_VERSION) {

            if(onDatabaseUpragdeLitsenler != null){
                onDatabaseUpragdeLitsenler.onUpgrade(true);
            }
            String[] upgradeClientColumn = new String[]{
                    "is_bottom","tid"
            };
            String[] upgradeJoinColumn = new String[]{
                    "businessLicense",
                    "foodCirculationPermit",
                    "htaBusinessLicense",
                    "expiredCertificateUndertaking",
                    "corporateIdentityCard",
                    "rentContract",
                    "shopMonthRent",
                    "rentPaymentVoucher",
                    "monthlySalesCertificate",
                    "doorStreetPhoto",
                    "accordanceShopPhoto",
                    "commodityDisplayPhoto",
                    "outsideShopPhoto",
                    "cashierPhoto",
                    "declarationLetter",
                    "franchiseeCommitment",
                    "verifyCode",
                    "shopLegalIdcard",
                    "attachmentState",
                    "otherPhoto",
                    "businessLicenseName",
                    "isOver",
                    "auditNodeIndex",
                    "is_bottom",
                    "groupId",
                    "shopRealMan",
                    "shopRealManPhone",
                    "shopRealManIdCard",
                    "relationShip","groupPhoto","proxyBook","businessLicenseNumber"};

            String[] upgradeContractColumns = new String[]{
                    "franchiseAgreement",
                    "advertisingAgreement",
                    "otherPhoto",
                    "isOver",
                    "attachmentState",
                    "fileRuleJson",
                    "groupId",
                    "auditNodeIndex",
                    "rentAllowanceAmount",
                    "doorAllowanceAmount",
                    "is_bottom",
                    "doorAllowanceAmountMax",
                    "rentAllowanceAmountMax",
                    "unincorporatedStatement"};

            String[] upgradeSubsidy = new String[]{
                    "waterScreenshots",
                    "storeCode",
                    "is_bottom",
                    "dataJson"
            };
            try {
                TableUtils.createTableIfNotExists(connectionSource,ClientInfoDetail.class);
                TableUtils.createTableIfNotExists(connectionSource,MerchantJoinScoretableVO.class);
                TableUtils.createTableIfNotExists(connectionSource,Contract.class);
                TableUtils.createTableIfNotExists(connectionSource,SubsidyLocalVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            sqliteDatabase.beginTransaction();

            boolean success = true;
            success &= addColumn(sqliteDatabase, "tb_client", upgradeClientColumn,upgradeClientColumn.length+upgradeClientColumn.length);
            success &= addColumn(sqliteDatabase, "tb_join", upgradeJoinColumn,upgradeJoinColumn.length+upgradeContractColumns.length);
            success &= addColumn(sqliteDatabase,"tb_contract", upgradeContractColumns,upgradeJoinColumn.length+upgradeContractColumns.length);
            success &= addColumn(sqliteDatabase,"subsidy_table", upgradeSubsidy,upgradeSubsidy.length+upgradeJoinColumn.length+upgradeContractColumns.length);
            if(success){
                sqliteDatabase.setTransactionSuccessful();
            }
            sqliteDatabase.endTransaction();
            if(onDatabaseUpragdeLitsenler != null){
                onDatabaseUpragdeLitsenler.onComplete(success);
            }
            onDatabaseUpragdeLitsenler = null;

        }else{
            onDatabaseUpragdeLitsenler.onUpgrade(false);
        }

        sqliteDatabase.setVersion(newVer);
        onCreate(sqliteDatabase);

    }

    private boolean addColumn(SQLiteDatabase sqliteDatabase,String tableName, String[] columns,int total) {


        if(tableName == null){
            onDatabaseUpragdeLitsenler.onComplete(false);
            return false;
        }

        for (String column : columns) {
            try {
                String type = "groupId".equals(column)?"INTEGER":"TEXT";
                sqliteDatabase.execSQL("ALTER TABLE '" + tableName + "' ADD COLUMN " + column + " " + type);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public interface OnDatabaseUpragdeLitsenler{
        public void onUpgrade(boolean isUpdate);
        public void onComplete(boolean success);
    }
}
