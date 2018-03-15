package asuper.yt.cn.supermarket.modules.common;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.ToolLog;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolString;

/**
 * 证件照片详情
 * Created by liaoqinsen on 2017/6/7 0007.
 */

public class PreviewActivity extends BaseActivity {

    private ViewPager viewPager;
    private List<ImageGalleryActivity.ImageGalleryItem> state;
    private List<PreviewItem> previewItemList;
    private HashMap<String, Integer> pathIndex;
    private String defualtPath = null;
    private int defualtPage = 0;
    private View design_bottom_sheet;
    private TextView btnOpenTv, title1, title2, title3, title4, name1, name2, name3, name4;
    private ImageView btnOpenImg;

    private JSONObject auditingInfo;

    @Override
    protected int getContentId() {
        return R.layout.activity_preview;
    }

    @Override
    protected void findView(View root) {
        initView();
        pathIndex = new HashMap<>();
        state = new ArrayList<>();
        state = EventBus.getDefault().getStickyEvent(state.getClass());
        previewItemList = new ArrayList<>();
        defualtPath = getIntent().getStringExtra("defaultPath");
        String json = getIntent().getStringExtra("json");
        if (json != null) {
            try {
                auditingInfo = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (state != null) {
            for (int i = 0; i < state.size(); i++) {
                ArrayList<PhotoInfo> photoInfos = state.get(i).photoInfo;
                if (photoInfos == null || state.get(i).info == null) continue;
                for (int j = 0; j < photoInfos.size(); j++) {
                    PreviewItem previewItem = new PreviewItem();
                    previewItem.parentPosition = i;
                    previewItem.position = j;
                    previewItem.total = photoInfos.size();
                    previewItem.path = photoInfos.get(j).getPhotoId() == 0 ? photoInfos.get(j).getPhotoPath() : Config.IMG_HOST + photoInfos.get(j).getPhotoPath();
                    previewItem.parentTitle = state.get(i).info.name;
                    pathIndex.put(photoInfos.get(j).getPhotoPath() + "_" + i + "_" + j, previewItemList.size());
                    previewItemList.add(previewItem);
                }
            }
        }

        if (previewItemList.size() < 1) {
            finish();
            return;
        }

        defualtPage = pathIndex.get(defualtPath) == null ? 0 : pathIndex.get(defualtPath);

        final Toolbar toolbar = ActionBarManager.initBackToolbar(this, (String.format("%1$s %2$d/%3$d", previewItemList.get(defualtPage).parentTitle, previewItemList.get(defualtPage).position + 1, previewItemList.get(defualtPage).total)));
        if (auditingInfo != null)
            setBottomSheet(previewItemList.get(defualtPage).parentTitle, auditingInfo);
        toolbar.setBackgroundColor(getResources().getColor(android.R.color.black));
        viewPager = root.findViewById(R.id.preview_pager);
        viewPager.setAdapter(new PagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ((TextView) toolbar.findViewById(R.id.toolbar_title)).setText((String.format("%1$s %2$d/%3$d", previewItemList.get(position).parentTitle, previewItemList.get(position).position + 1, previewItemList.get(position).total)));
                if (auditingInfo != null)
                    setBottomSheet(previewItemList.get(position).parentTitle, auditingInfo);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(defualtPage, false);
    }

    @SuppressLint("DefaultLocale")
    private void setBottomSheet(String title, JSONObject object) {
        design_bottom_sheet.setVisibility(View.VISIBLE);
        switch (title) {
            case "营业执照":
                title1.setText("营业执照名称：");
                title2.setText("注册号/信用代码：");
                title3.setText("营业场所：");
                title4.setText("注册时间：");
                name1.setText(object.optString("businessLicenseName",""));
                name2.setText(object.optString("businessLicenseNumber",""));
                name3.setText(object.optString("businessPlace",""));
                name4.setText(object.optString("registerTime",""));
                break;
            case "食品流通许可证":
                title1.setText("食品流通许可证号：");
                title2.setText("");
                title3.setText("食品流通许可证有效期：");
                title4.setText("");
                name1.setText(ToolString.getString(object.optString("foodCode")));
                name2.setText("");
                name3.setText(ToolString.getString(object.optString("foodTimeStart")+"-"+ToolString.getString(object.optString("foodTimeEnd"))));
                name4.setText("");
                break;
            case "烟草专卖零售许可证":
                title1.setText("烟草专卖零售许可证号：");
                title2.setText("");
                title3.setText("烟草专卖零售许有效期：");
                title4.setText("");
                name1.setText(ToolString.getString(object.optString("tobaccoCode")));
                name2.setText("");
                name3.setText(ToolString.getString(object.optString("tobaccoTimeStart")+"-"+ToolString.getString(object.optString("tobaccoTimeEnd"))));
                name4.setText("");
                break;
            default:
                design_bottom_sheet.setVisibility(View.GONE);
                break;
        }
    }

    private void initView() {
        design_bottom_sheet = findViewById(R.id.design_bottom_sheet);
        if (auditingInfo == null) design_bottom_sheet.setVisibility(View.GONE);
        else design_bottom_sheet.setVisibility(View.VISIBLE);
        btnOpenImg = (ImageView) findViewById(R.id.btnOpenImg);
        btnOpenTv = (TextView) findViewById(R.id.btnOpenTv);
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
        title3 = (TextView) findViewById(R.id.title3);
        title4 = (TextView) findViewById(R.id.title4);
        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        name3 = (TextView) findViewById(R.id.name3);
        name4 = (TextView) findViewById(R.id.name4);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(design_bottom_sheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //这里是bottomSheet 状态的改变，根据slideOffset可以做一些动画
//                if (behavior.getState()==BottomSheetBehavior.STATE_COLLAPSED) btnOpenTv.setVisibility(View.VISIBLE);
//                else btnOpenTv.setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //这里是拖拽中的回调，根据slideOffset可以做一些动画
                btnOpenImg.setRotation(180 * slideOffset);
                btnOpenTv.setTranslationY(btnOpenTv.getHeight() * slideOffset);
            }
        });
        btnOpenImg.setOnClickListener(view -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
        btnOpenTv.setOnClickListener(view -> {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            else behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

    }

    @Override
    protected int statusBarColor() {
        return R.color.black;
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    private class PagerAdapter extends android.support.v4.view.PagerAdapter {

        private LinkedList<ImageView> destoriedImageViewLinkedList = new LinkedList<>();

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = null;
            if (destoriedImageViewLinkedList.size() < 1) {
                imageView = new ImageView(getContext());
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            } else {
                imageView = destoriedImageViewLinkedList.pop();
            }
            Glide.with(getContext()).load(previewItemList.get(position).path).placeholder(R.drawable.ic_gf_default_photo).into(imageView);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            destoriedImageViewLinkedList.push((ImageView) object);
        }

        @Override
        public int getCount() {
            return previewItemList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class PreviewItem {
        public String path;
        public String parentTitle;
        public int parentPosition;
        public int total;
        public int position;
    }
}
