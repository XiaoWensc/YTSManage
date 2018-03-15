package asuper.yt.cn.supermarket.utils;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

import asuper.yt.cn.supermarket.base.YTApplication;

/**
 * 配置文件工具类
 *
 * @author zxw
 * @version 1.0
 */
public final class ToolProperties extends Properties {

    private static Properties property = new Properties();

    public static String readAssetsProp(String fileName, String key) {
        String value = "";
        try {
            InputStream in = YTApplication.get().getAssets().open(fileName);
            property.load(in);
            value = property.getProperty(key);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return value;
    }

    public static String readAssetsProp(Context context, String fileName, String key) {
        String value = "";
        try {
            InputStream in = context.getAssets().open(fileName);
            property.load(in);
            value = property.getProperty(key);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return value;
    }

    public static String readAssetsProp(String fileName, String key, String defaultValue) {
        String value = "";
        try {
            InputStream in = YTApplication.get().getAssets().open(fileName);
            property.load(in);
            value = property.getProperty(key, defaultValue);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return value;
    }

    public static String readAssetsProp(Context context, String fileName, String key, String defaultValue) {
        String value = "";
        try {
            InputStream in = context.getAssets().open(fileName);
            property.load(in);
            value = property.getProperty(key, defaultValue);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return value;
    }
}
