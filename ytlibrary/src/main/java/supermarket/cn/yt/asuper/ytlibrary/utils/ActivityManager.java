package supermarket.cn.yt.asuper.ytlibrary.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public class ActivityManager {

    private static final Stack<WeakReference<Activity>> activitys = new Stack<>();

    public static void in(Activity activity){
        activitys.add(new WeakReference<>(activity));
    }
    public static Activity current(){
        if(activitys.firstElement() == null) return null;
        return  activitys.firstElement().get();
    }

    public static Activity activityTop(){
        if (activitys.lastElement() == null) return null;
        return activitys.lastElement().get();
    }

    public static void out(Activity activity){
        int i = 0;
        for(WeakReference<Activity> weakReference:activitys){
            if(weakReference == null){
                i ++;
                continue;
            }
            Activity act = weakReference.get();
            if(act == null) {
                i ++;
                continue;
            }
            if(act.equals(activity)){
                break;
            }
            i ++;
        }
        if(activitys.size() > i) {
            activitys.removeElementAt(i);
        }
    }

    public static void finishAll(){
        while (activitys.size() > 0){
            WeakReference<Activity> aw = activitys.pop();
            if(aw == null) continue;
            Activity activity = aw.get();
            if(activity == null || activity.isFinishing() || activity.isDestroyed()) continue;
            activity.finish();
        }
    }

    public static Stack<WeakReference<Activity>> getActivitys() {
        return activitys;
    }
}
