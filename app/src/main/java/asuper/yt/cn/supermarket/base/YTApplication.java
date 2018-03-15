package asuper.yt.cn.supermarket.base;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import asuper.yt.cn.supermarket.modules.setting.SettingActivity;
import asuper.yt.cn.supermarket.utils.GlideImageLoader;
import asuper.yt.cn.supermarket.utils.GlidePauseOnScrollListener;
import asuper.yt.cn.supermarket.utils.ToolDbOperation;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageFilter;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.jpush.android.api.JPushInterface;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolPreference;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public class YTApplication extends DefaultApplicationLike {
    private static Context instance;
    private static Operation operation;
    public static ToolLog.Builder lBuilder;


    /***
     * 屏幕宽高
     **/
    public static int mScreenWidth = 0;
    public static int mScreenHeight = 0;

    private static Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    private static Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restart();
        }
    };


    public static boolean debugEnable = false;

    public YTApplication(Application application, int tinkerFlags,
                         boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                         long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }


    public static Operation getOperation() {
        return operation;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ToastUtil.initToast(getApplication());
        initIP();
        operation = new Operation(getApplication());
        instance = this.getApplication();
        boolean is = ToolPreference.get().getBoolean(SettingActivity.ENABLE_GLOBAL_CAPTURE_KEY);
        boolean isFirstIn = !ToolPreference.get().getBoolean("is_first_in");

        if (isFirstIn) {
            enableGlobalCapture(true);
            ToolPreference.get().putBoolean(SettingActivity.ENABLE_GLOBAL_CAPTURE_KEY, true);
            ToolPreference.get().putBoolean("is_first_in", true);
        } else {
            enableGlobalCapture(is);
        }
        calcScreenSize();
        initBugly();
        ToolDbOperation.init(getApplication());

        Config.autoFillAttachmentPath = ToolPreference.get().getString(SettingActivity.ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY);
        Config.isAutoFillAttachment = ToolPreference.get().getBoolean(SettingActivity.ENABLE_AUTO_FILL_ATTCHMENT_KEY);

        lBuilder = new ToolLog.Builder()
                .setLogSwitch(BuildConfig.DEBUG)// 设置log总开关，默认开
                .setGlobalTag("YT")// 设置log全局标签，默认为空
                // 当全局标签不为空时，我们输出的log全部为该tag，
                // 为空时，如果传入的tag为空那就显示类名，否则显示tag
                .setLog2FileSwitch(false)// 打印log时是否存到文件的开关，默认关
                .setBorderSwitch(false)// 输出日志是否带边框开关，默认开
                .setLogHeadSwitch(true);//头部开关
//                .setLogFilter(ToolLog.E);// log过滤器，和logcat过滤器同理，默认Verbose

        //激光推送
        initPush();
    }

    private void initPush() {
        JPushInterface.setDebugMode(BuildConfig.DEBUG);
        JPushInterface.init(getApplication());
        Set<String> stringSet = new HashSet<>();//设置tag
        stringSet.add(Config.JPshTag);
        ToolLog.i(Config.JPshTag);
        JPushInterface.setTags(getApplication(), stringSet, null);
    }

    public static void enableGlobalCapture(boolean enable) {
        if (enable) {
            Thread.setDefaultUncaughtExceptionHandler(restartHandler);
        } else {
            Thread.setDefaultUncaughtExceptionHandler(defaultHandler);
        }
    }

    public static Context get() {
        return instance;
    }

    private void initIP() {
        ToolPreference.get().init(getApplication());
        String hostValue = ToolPreference.get().getString(SettingActivity.KEY_HOST_PREF);
        String imgHostValue = ToolPreference.get().getString(SettingActivity.KEY_IMG_HOST_PREF);
        String JPshTag = ToolPreference.get().getString(SettingActivity.KEY_TAG_HOST_PREF);
        if (hostValue != null && !hostValue.isEmpty()) {
            Config.HOST = hostValue;
        }
        if (imgHostValue != null && !imgHostValue.isEmpty()) {
            Config.IMG_HOST = imgHostValue;
        }
        if (JPshTag != null && !JPshTag.isEmpty()) {
            Config.JPshTag = JPshTag;
        }
    }

    private void initBugly() {
        //bugly初始化必须放在全局异常捕获初始化之后，否则无法捕获异常
        Beta.upgradeDialogLayoutId = R.layout.layout_update;
        Beta.enableNotification = true;
        Beta.largeIconId = R.drawable.ic_login_logo;
        Beta.smallIconId = R.drawable.ic_login_logo;
        Beta.enableNotification = true;
        Bugly.init(getApplication(), BuildConfig.BUGLY_APPID, BuildConfig.DEBUG);
        CrashReport.initCrashReport(getApplication(), BuildConfig.BUGLY_APPID, BuildConfig.DEBUG);
    }

    public static void restart() {
        Intent intent = new Intent(instance, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }

    public static FunctionConfig initGalleryFinal(Context mContext, ArrayList<PhotoInfo> selected) {
        return initGalleryFinal(mContext, 3, selected);
    }


    public static FunctionConfig initGalleryFinal(Context mContext, int max, ArrayList<PhotoInfo> selected) {

        ThemeConfig theme = new ThemeConfig.Builder().setTitleBarBgColor(instance.getResources().getColor(R.color.colorPrimary))
                .setIconBack(R.drawable.ic_back)
                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(false)//相机
//                .setEnableEdit(true) //编辑
//                .setEnableCrop(true) //裁剪
                .setEnableRotate(true) //开启选择功能
                .setImageFilter(new ImageFilter() {
                    @Override
                    public boolean filter(String path) {
                        return path != null && (path.endsWith("jpg"));
                    }
                })
//                .setCropSquare(true) ////裁剪正方形
                .setEnablePreview(true)//是否开启预览功能
                .setMutiSelectMaxSize(max)//配置多选数量
                .setFilter(selected)
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(instance, new GlideImageLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new GlidePauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);
        return functionConfig;
    }

    public static void logOut(final BaseActivity context) {
        Map<String, Object> map = new HashMap<>();
        map.put("login", Config.UserInfo.USER_NAME);
        ToolOkHTTP.post(Config.getURL("app/sys/logout.htm"), map, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Config.UserInfo.PASSWORD = null;
                Config.UserInfo.save();
                context.getOperation().forward(LoginActivity.class);
                context.finish();
                ToastUtil.error("退出登录成功");
            }

            @Override
            public void onFailure() {
                ToastUtil.error("退出登录失败");
            }
        });
    }


    /****
     * 计算屏幕宽高
     ***/
    private void calcScreenSize() {
        WindowManager manager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }
}
