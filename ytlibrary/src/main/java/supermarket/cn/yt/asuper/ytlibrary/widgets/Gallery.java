package supermarket.cn.yt.asuper.ytlibrary.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.R;

/**
 * Created by Chanson on 2017/3/24.
 */

public class Gallery extends RelativeLayout implements View.OnClickListener {

    private View root;

    private ViewPager pager;
    private View left, right, close;

    private OnGalleryCloseListener onGalleryCloseListener;

    public Gallery(Context context) {
        super(context);
        init(context);
    }

    public Gallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        root = LayoutInflater.from(context).inflate(R.layout.layout_image_gallery, this);

        pager = (ViewPager) root.findViewById(R.id.image_gallery_pager);
        left = root.findViewById(R.id.image_gallery_left);
        right = root.findViewById(R.id.image_gallery_right);
        close = root.findViewById(R.id.image_gallery_close);

        pager.setOnClickListener(this);
        left.setOnClickListener(this);
        right.setOnClickListener(this);
        close.setOnClickListener(this);
    }

    public void addOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        pager.addOnPageChangeListener(onPageChangeListener);
    }

    public void setAdapter(PagerAdapter pagerAdapter) {
        pager.setAdapter(pagerAdapter);
    }

    public void setImages(Context context, List<String> uris) {
        setImages(context, uris, "");
    }

    public void setImages(Context context, List<String> uris, String prefix) {
        SimpleImageAdapter adapter = new SimpleImageAdapter(context, uris);
        adapter.setPrefix(prefix);
        pager.setAdapter(adapter);
    }

    public void setImages(Context context, String prefix, List<PhotoInfo> uris) {
        List<String> urls = new ArrayList<>();
        for (PhotoInfo photoInfo : uris) {
            if (photoInfo.getPhotoId() == 0) {
                urls.add(photoInfo.getPhotoPath());
            } else {
                urls.add(prefix + photoInfo.getPhotoPath());
            }
        }
        SimpleImageAdapter adapter = new SimpleImageAdapter(context, urls);
        pager.setAdapter(adapter);
    }

    public void setCurrentItem(int position, boolean smooth) {
        pager.setCurrentItem(position, smooth);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.image_gallery_pager) {
            if (pager.getAdapter() != null && pager.getAdapter().getCount() > 0) {
                switchButton();
            }

        } else if (i == R.id.image_gallery_close) {
            if (onGalleryCloseListener != null) {
                onGalleryCloseListener.onClose();
            }

        } else if (i == R.id.image_gallery_left) {
            pager.setCurrentItem(pager.getCurrentItem() - 1 < 0 ? 0 : pager.getCurrentItem() - 1, true);

        } else if (i == R.id.image_gallery_right) {
            pager.setCurrentItem(pager.getCurrentItem() + 1 >= pager.getAdapter().getCount() ? pager.getAdapter().getCount() : pager.getCurrentItem() + 1, true);

        }
    }

    private void switchButton() {
        if (left.getVisibility() == VISIBLE) {
            left.setVisibility(INVISIBLE);
            right.setVisibility(INVISIBLE);
        } else {
            left.setVisibility(VISIBLE);
            right.setVisibility(VISIBLE);
        }
    }

    public OnGalleryCloseListener getOnGalleryCloseListener() {
        return onGalleryCloseListener;
    }

    public void setOnGalleryCloseListener(OnGalleryCloseListener onGalleryCloseListener) {
        this.onGalleryCloseListener = onGalleryCloseListener;
    }

    public interface OnGalleryCloseListener {
        void onClose();
    }

    public class SimpleImageAdapter extends PagerAdapter {

        private LinkedList<ImageView> imageViewCache = new LinkedList<>();

        private List<String> urls;

        private Context context;

        private String prefix = "";

        public SimpleImageAdapter(Context context, List<String> urls) {
            this.context = context;
            this.urls = urls;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public int getCount() {
            return this.urls == null ? 0 : urls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MyRoundedImageSupportedImageView image;
            if (imageViewCache.size() < 1) {
                image = new MyRoundedImageSupportedImageView(context);
                image.setImageResource(R.drawable.default_img);
                image.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onGalleryCloseListener != null) {
                            onGalleryCloseListener.onClose();
                        }
                    }
                });
            } else {
                image = (MyRoundedImageSupportedImageView) imageViewCache.pop();
            }
            if (prefix == null || prefix.isEmpty()) {
                Glide.with(context).load(prefix + urls.get(position)).asBitmap().diskCacheStrategy(DiskCacheStrategy.NONE).into(image);
            } else {
                Glide.with(context).load(prefix + urls.get(position)).asBitmap().into(image);
            }
            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            imageViewCache.push((ImageView) object);
        }
    }

    private class MyRoundedImageSupportedImageView extends android.support.v7.widget.AppCompatImageView {

        public MyRoundedImageSupportedImageView(Context context) {
            super(context);
        }

        @Override
        public void setImageBitmap(Bitmap bm) {
            RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bm);
            roundedBitmapDrawable.setAntiAlias(true);
            roundedBitmapDrawable.setCornerRadius(20);
            super.setImageDrawable(roundedBitmapDrawable);
        }
    }
}