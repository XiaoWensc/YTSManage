package chanson.androidflux;

import android.os.Handler;
import android.util.SparseArray;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by Chanson on 17/3/9.
 */
public abstract class Store{

    public Handler handler;
    private StoreDependencyDelegate delegate;
    private SparseArray<Method> methodSparseArray;
    private Executor executorService = Executors.newSingleThreadExecutor();

    public Store(StoreDependencyDelegate delegate){
        this.delegate = delegate;
        methodSparseArray = new SparseArray<>();
        this.delegate.storeActionBindedMethod(methodSparseArray);
        if(Thread.currentThread().getName().equals("main")) {
            handler = new Handler();
        }else{
            throw new RuntimeException("Store must be initialized in main thread!");
        }
    }

    public void doAction(final Action action){
        if(action != null && action.type > -1) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    final Reducer reducer = delegate.getReducer();
                    if(reducer == null || !reducer.avaliable()) return;
                    doAction(action.type, action, new StoreResultCallBack() {
                        @Override
                        public void onResult(final int type, final Object data) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        methodSparseArray.get(type).invoke(reducer,data);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    });
                }
            });

        }
    }

    public abstract void doAction(int type,HashMap<String,Object> data,StoreResultCallBack callBack);

    public void destroy(){
        this.handler = null;
    }

    public interface StoreResultCallBack{
        public void onResult(int type,Object data);
    }
}
