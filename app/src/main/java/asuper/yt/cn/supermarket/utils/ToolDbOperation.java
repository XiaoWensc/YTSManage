package asuper.yt.cn.supermarket.utils;

import android.content.Context;
import android.os.Handler;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.entities.Contract;
import asuper.yt.cn.supermarket.entities.LocalVo;
import asuper.yt.cn.supermarket.entities.MerchantJoinScoretableVO;
import asuper.yt.cn.supermarket.entities.SubsidyLocalVO;

/**
 * Created by Chanson on 2017/3/23.
 */

public class ToolDbOperation {

    private static ToolDatabase toolDatabase;

    private static boolean hasDestroied = true;

    private static final ToolDbOperation instance = new ToolDbOperation();

    private static Handler handler;

    private static ExecutorService dbOpreationThreadPool;

    private static AndroidDatabaseConnection conn;

    private static Dao<ClientInfoDetail, String> clientDao; //意向加盟表
    private static Dao<MerchantJoinScoretableVO, String> joinDao; //加盟表
    private static Dao<Contract, String> ractDao; //合同表
    private static Dao<SubsidyLocalVO, String> subsidyDao;
    private static Dao<LocalVo, String> localVODao;

    private ToolDbOperation() {
    }

    public static ToolDbOperation get() {
        return instance;
    }

    public static void init(Context context) {
        if (context == null) return;
        toolDatabase = new ToolDatabase(context);
        handler = new Handler(context.getMainLooper());
        hasDestroied = false;

        dbOpreationThreadPool = Executors.newFixedThreadPool(2);
    }

    public static void destroy() {
        hasDestroied = true;
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        toolDatabase.releaseAll();
    }


    public static Dao<ClientInfoDetail, String> getClientDao() {
        if (hasDestroied) return null;
        if (clientDao == null) {
            toolDatabase.createTable(ClientInfoDetail.class);
            try {
                clientDao = toolDatabase.getDao(ClientInfoDetail.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return clientDao;
    }

    public static Dao<MerchantJoinScoretableVO, String> getJoinDao() {
        if (hasDestroied) return null;
        if (joinDao == null) {
            toolDatabase.createTable(MerchantJoinScoretableVO.class);
            try {
                joinDao = toolDatabase.getDao(MerchantJoinScoretableVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return joinDao;
    }

    public static Dao<Contract, String> getRactDao() {
        if (hasDestroied) return null;
        if (ractDao == null) {
            toolDatabase.createTable(Contract.class);
            try {
                ractDao = toolDatabase.getDao(Contract.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ractDao;
    }

    public static Dao<SubsidyLocalVO, String> getSubsidyDao() {
        if (hasDestroied) return null;
        if (subsidyDao == null) {
            toolDatabase.createTable(SubsidyLocalVO.class);
            try {
                subsidyDao = toolDatabase.getDao(SubsidyLocalVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return subsidyDao;
    }

    public static Dao<LocalVo, String> getLocalVODao() {
        if (hasDestroied) return null;
        if (localVODao == null) {
            toolDatabase.createTable(LocalVo.class);
            try {
                localVODao = toolDatabase.getDao(LocalVo.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return localVODao;
    }


    public boolean checkIsDestroy() {
        return hasDestroied;
    }

    public static <T extends Object> boolean deleteSync(T object, Class<T> cls) {

        if (hasDestroied) return false;

        Dao<T, String> dao;

        int result = -1;

        try {
            dao = toolDatabase.getDao(cls);
        } catch (SQLException e) {
            e.printStackTrace();
            return result >= 0;
        }

        try {
            result = dao.delete(object);
        } catch (SQLException e) {
            return result >= 0;
        }
        return result >= 0;
    }

    public static void runOnMainThread(Runnable runnable) {
        if (hasDestroied) return;
        handler.post(runnable);
    }

    public static <T extends Object> void deleteAsync(final T object, final Class<T> cls, final CommonDBOpreationCallBack callBack) {
        if (hasDestroied) return;
        dbOpreationThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                Dao<T, String> dao;

                int result = -1;

                try {
                    dao = toolDatabase.getDao(cls);
                } catch (SQLException e) {
                    e.printStackTrace();
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onResult(false);
                            }
                        }
                    });
                    return;
                }

                try {
                    result = dao.delete(object);
                } catch (SQLException e) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onResult(false);
                            }
                        }
                    });
                }

                if (result >= 0) {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onResult(true);
                            }
                        }
                    });
                } else {
                    runOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            if (callBack != null) {
                                callBack.onResult(false);
                            }
                        }
                    });
                }
            }
        });
    }


    public interface CommonDBOpreationCallBack {
        void onResult(boolean isSuccess);
    }

}
