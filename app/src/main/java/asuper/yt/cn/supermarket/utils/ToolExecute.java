package asuper.yt.cn.supermarket.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liaoqinsen on 2017/9/5 0005.
 */

public class ToolExecute {
    private static ExecutorService service = Executors.newFixedThreadPool(5);

    public static void run(Runnable runnable){
        service.execute(runnable);
    }

}
