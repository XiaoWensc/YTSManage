package supermarket.cn.yt.asuper.ytlibrary.utils;

import android.app.Application;
import android.content.Context;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SimpleHUDToast;

/**
 * Created by liaoqinsen on 2017/9/4 0004.
 */

public class ToastUtil {
    private static SimpleHUDToast toast;

    public static Context context;

    public static void initToast(Application application){
        toast = SimpleHUDToast.init(application);
        context = application;
    }

    public static void info(String info){
        if(toast == null) return;
        toast.showInfoToast(info);
    }

    public static void error(String error){
        if(toast == null) return;
        toast.showErrorToast(error);
    }

    public static void success(String success){
        if(toast == null) return;
        toast.showSuccessToast(success);
    }

}
