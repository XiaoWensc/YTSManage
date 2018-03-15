package asuper.yt.cn.supermarket.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

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
 * Created by zengxiaowen on 2017/12/5.
 */

public class AddressSelectWindows {

    private static MyAddresAdapter regionsAdapter;
    private static PopupWindow popupWindow;
    private static UpdateCallback internalCallBack;

    public static void show(View anchor, final BaseActivity context, final RegionSelectedListener listener){
        ListView regionsList = new ListView(context);
        regionsList.setFocusable(true);
        if (regionsAdapter==null) regionsAdapter = new MyAddresAdapter();
        regionsAdapter.clear();
        regionsList.setAdapter(regionsAdapter);
        regionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Region region = (Region) regionsAdapter.getItem(position);
                listener.onRegionSelect(region);
                parent.setVisibility(View.INVISIBLE);
                getAddress(region.parentCode, region.code, region.regionType, internalCallBack);
            }
        });
        FrameLayout root = new FrameLayout(context);
        root.setBackgroundColor(Color.WHITE);
        root.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        regionsList.setLayoutParams(new FrameLayout.LayoutParams(-1,-1));
        regionsList.setBackgroundColor(Color.WHITE);
        regionsList.setVisibility(View.INVISIBLE);
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setLayoutParams(new FrameLayout.LayoutParams(DensityUtil.dip2px(context,40), DensityUtil.dip2px(context,40), Gravity.CENTER));
        root.addView(progressBar);
        root.addView(regionsList);

        popupWindow = new PopupWindow(root, DensityUtil.dip2px(context,200), ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(0x66,0x00,0x00,0x00)));
        popupWindow.setAnimationStyle(R.style.slide_right);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(anchor, Gravity.RIGHT, 0, 0);
        getAllChildViews(context.getWindow().getDecorView(),0.5f);
//        setBackgroundAlpha(0.5f,context);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                setBackgroundAlpha(1f,context);
                getAllChildViews(context.getWindow().getDecorView(),1f);
            }
        });
        getAddress(null, null, "2", internalCallBack);
    }

    static {
        internalCallBack = new UpdateCallback() {
            @Override
            public void onComplete(String regionCode, String regionType, final List<Region> regions) {
                if (regions==null) {
                    if(popupWindow != null) popupWindow.dismiss();
                    return;
                }
                popupWindow.getContentView().post(new Runnable() {
                    @Override
                    public void run() {
                        regionsAdapter.clear();
                        regionsAdapter.addItem(regions);
                        regionsAdapter.notifyDataSetChanged();
                        ((ViewGroup)popupWindow.getContentView()).getChildAt(1).setVisibility(View.VISIBLE);
                    }
                });
            }
        };
    }

    private static void getAddress(final String parentCode, final String regionCode, final String regionType, final UpdateCallback callback){
        Map<String, Object> params = new HashMap<>();
        params.put("regionCode", regionCode);
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_GET_ADD), params, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                if (response.optBoolean("success")) {
                    try {
                        List<Region> regions = ToolGson.fromJson(response.optString("resultObject"), new TypeToken<List<Region>>() {}.getType());
                        if (regions != null) {
                            if (regions.isEmpty()){
                                callback.onComplete(regionCode, regionType, null);
                            }else {
                                callback.onComplete(regionCode, regionType, regions);
                            }
                        } else {
                            callback.onComplete(regionCode, regionType, null);
                            ToastUtil.error("获取地址失败");
                        }
                    } catch (Exception e) {
                        callback.onComplete(regionCode, regionType, null);
                        ToastUtil.error("获取地址失败");
                    }
                } else {
                    callback.onComplete(regionCode, regionType, null);
                    ToastUtil.error("获取地址失败");
                }
            }

            @Override
            public void onFailure() {
                callback.onComplete(regionCode, regionType, null);
                ToastUtil.error("获取地址失败");
            }
        });
    }

    public interface RegionSelectedListener{
        public void onRegionSelect(Region region);
    }


    public interface UpdateCallback {
        public void onComplete(String regionCode, String regionType, List<Region> regions);
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
            MyAddresAdapter.ViewHolder holder = null;
            if (convertView == null) {
                holder = new MyAddresAdapter.ViewHolder();
                convertView = LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_area, null);
                holder.tv_select = (TextView) convertView.findViewById(R.id.tv_select);
                convertView.setTag(holder);
            } else {
                holder = (MyAddresAdapter.ViewHolder) convertView.getTag();
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
