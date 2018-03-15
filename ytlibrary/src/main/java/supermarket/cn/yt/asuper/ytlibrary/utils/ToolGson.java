package supermarket.cn.yt.asuper.ytlibrary.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.internal.bind.JsonTreeWriter;

import java.lang.reflect.Type;

/**
 * Created by liaoqinsen on 2017/9/4 0004.
 */

public class ToolGson {
    private static Gson gson = new Gson();

    public static JsonElement toJsonTree(Object src) {
        return gson.toJsonTree(src);
    }

    public static JsonElement toJsonTree(Object src, Type typeOfSrc) {
        return gson.toJsonTree(src,typeOfSrc);
    }

    public static <T> T fromJson(String json,Class<T> type){
        return gson.fromJson(json,type);
    }

    public static <T> T fromJson(String json,Type type){
        return gson.fromJson(json,type);
    }

    public static String toJson(Object object){
        return  gson.toJson(object);
    }

}
