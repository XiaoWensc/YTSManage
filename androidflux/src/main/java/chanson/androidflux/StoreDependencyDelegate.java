package chanson.androidflux;

import android.app.Activity;
import android.util.SparseArray;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by liaoqinsen on 2017/5/12 0012.
 */

public class StoreDependencyDelegate {

    private WeakReference<Reducer> reducer;
    private SparseArray<String> methodStore;

    public StoreDependencyDelegate(Reducer reducer){
        this.reducer = new WeakReference<Reducer>(reducer);
        methodStore = new SparseArray();
    }

    public Reducer getReducer(){
        return reducer == null?null:reducer.get();
    }

    public void destroy(){
        if(this.reducer == null) return;
        this.reducer.clear();
        this.reducer = null;
    }

    public void storeActionBindedMethod(SparseArray array){
        if(array == null || this.reducer == null || this.reducer.get() == null) return;
        Method[] methods = getReducer().getClass().getDeclaredMethods();
        for(Method method:methods){
            BindAction annotation = method.getAnnotation(BindAction.class);
            if(annotation != null){
                array.put(annotation.value(),method);
                methodStore.put(annotation.value(),method.getName());
            }
        }
    }

    public void invoke(int type,Object ...args){
        if(this.reducer == null || this.reducer.get() == null) return;
        String methodName = methodStore.get(type).toString();
        if(methodName == null) return;
        try {
            Method method = getReducer().getClass().getDeclaredMethod(methodName, HashMap.class);
            method.invoke(getReducer(),args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
