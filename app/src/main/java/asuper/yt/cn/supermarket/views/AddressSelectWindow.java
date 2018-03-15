package asuper.yt.cn.supermarket.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseAdapter;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.utils.DensityUtil;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

import static supermarket.cn.yt.asuper.ytlibrary.widgets.BottomPopView.getAllChildViews;

/**
 * Created by liaoqinsen on 2017/7/31 0031.
 */

public class AddressSelectWindow {

    private static UpdateCallback internalCallBack;

    private static MyAddresAdapter regionsAdapter;

    private static PopupWindow popupWindow;

    public static void show(View anchor, final BaseActivity context, final RegionSelectedListener listener) {
        ListView regionsList = new ListView(context);
        regionsList.setFocusable(true);
        if (regionsAdapter == null) regionsAdapter = new MyAddresAdapter();
        regionsAdapter.clear();
        regionsList.setAdapter(regionsAdapter);
        regionsList.setOnItemClickListener((parent, view, position, id) -> {
            Region region = (Region) regionsAdapter.getItem(position);
            listener.onRegionSelect(region);
            parent.setVisibility(View.INVISIBLE);
            getAddress(region.code, region.regionType, internalCallBack);
        });
        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(Color.WHITE);
        root.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        regionsList.setLayoutParams(new FrameLayout.LayoutParams(-1,-1));
        regionsList.setBackgroundColor(Color.WHITE);
        regionsList.setVisibility(View.INVISIBLE);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dip2px(context,40), DensityUtil.dip2px(context,40),Gravity.CENTER));
        root.addView(progressBar);
        root.addView(regionsList);

        popupWindow = new PopupWindow(root, DensityUtil.dip2px(context,200), ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(0x66,0x00,0x00,0x00)));
        popupWindow.setAnimationStyle(R.style.slide_right);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(anchor, Gravity.RIGHT, 0, 0);
        getAllChildViews(context.getWindow().getDecorView(),0.5f);
//        setBackgroundAlpha(0.5f,context);
        popupWindow.setOnDismissListener(() -> {
//                setBackgroundAlpha(1f,context);
            getAllChildViews(context.getWindow().getDecorView(),1f);
        });
        getAddress(null, "2", internalCallBack);
    }



    static {
        internalCallBack = new UpdateCallback() {
            @Override
            public void onComplete(String regionCode, String regionType, final List<Region> regions) {
                popupWindow.getContentView().post(() -> {
                    if (regions==null||regions.isEmpty()) if(popupWindow != null) popupWindow.dismiss();
                    regionsAdapter.clear();
                    regionsAdapter.addItem(regions);
                    regionsAdapter.notifyDataSetChanged();
                    ((ViewGroup)popupWindow.getContentView()).getChildAt(1).setVisibility(View.VISIBLE);
                });
            }
        };
    }

    public static void destroy(){
        popupWindow = null;
    }

    public static void setBackgroundAlpha(float bgAlpha,Context context) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) context).getWindow().setAttributes(lp);
    }

    public static void getAddress(final String regionCode, final String regionType, final UpdateCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("regionCode", regionCode);
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_ADD), params, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optBoolean("success")) {
                    try {
                        List<Region> regions = ToolGson.fromJson(response.optString("resultObject"), new TypeToken<List<Region>>() {
                        }.getType());
                        if (regions != null) {
                            callback.onComplete(regionCode, regionType, regions);
                        } else {
                           ToastUtil.error("获取地址失败");
                        }
                    } catch (Exception e) {
                        ToastUtil.error("获取地址失败");
                    }
                } else {
                    ToastUtil.error("获取地址失败");
                }
            }

            @Override
            public void onFailure() {
                ToastUtil.error("获取地址失败");
            }
        });
    }

    public interface UpdateCallback {
        public void onComplete(String regionCode, String regionType, List<Region> regions);
    }

    public interface RegionSelectedListener{
        public void onRegionSelect(Region region);
    }


    public class Region {
        public String id;
        public String code;
        public String parentCode;
        public String regionName;
        public String regionType;
        public String abbName;
        public String zip;

        @Override
        public String toString() {
            return "Region{" +
                    "id='" + id + '\'' +
                    ", code='" + code + '\'' +
                    ", parentCode='" + parentCode + '\'' +
                    ", regionName='" + regionName + '\'' +
                    ", regionType='" + regionType + '\'' +
                    ", abbName='" + abbName + '\'' +
                    ", zip='" + zip + '\'' +
                    '}';
        }
    }

    private static class MyAddresAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_area, null);
                holder.tv_select = (TextView) convertView.findViewById(R.id.tv_select);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Region add = (Region) getItem(position);
            holder.tv_select.setText(add.regionName);

            return convertView;
        }

        public class ViewHolder {
            private TextView tv_select;
        }
    }
}
