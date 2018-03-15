package asuper.yt.cn.supermarket.modules.common;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolFile;
import supermarket.cn.yt.asuper.ytlibrary.widgets.BottomPopView;

/**
 * Created by liaoqinsen on 2017/9/11 0011.
 */

public class ImageGalleryActivity extends BaseActivity {

    private List<ImageGalleryItem> items;
    private RecyclerView expandable;
    private boolean canUpdate = false;
    private BottomPopView bottomPopView;
    private String fromDate ;
    private int maxPhotoNumber = 3;

    private int currentGroupPosition = 0;

    Uri tempUri;
    /***
     * 使用照相机拍照获取图片
     */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    /***
     * 使用相册中的图片
     */
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;


    @Override
    protected int getContentId() {
        return R.layout.activity_image_gallery;
    }

    @Override
    protected void findView(View root) {
        items = new ArrayList<>();
        items = EventBus.getDefault().getStickyEvent(items.getClass());
        canUpdate = getIntent().getBooleanExtra("canUpdate", false);
        fromDate = getIntent().getStringExtra("json");
        ActionBarManager.initBackToolbar(this, canUpdate ? "选择图片" : "查看图片");
        expandable = (RecyclerView) root.findViewById(R.id.upload_expandable);
        if (canUpdate) {
            root.findViewById(R.id.upload_remind).setVisibility(View.VISIBLE);
        }
        expandable.setLayoutManager(new UploadLayoutMananger(this));
        expandable.setItemAnimator(new DefaultItemAnimator());
        expandable.setAdapter(new UploadAdapter(items));
        initBottomPopView();

    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new GalleryStroe(dependencyDelegate);
    }

    private void initBottomPopView() {
        bottomPopView = new BottomPopView(this, expandable) {
            @Override
            public void onTopButtonClick() {
                dismiss();
                if (maxPhotoNumber <= 0) {
                    ToastUtil.info("选择的图片已达上限");
                    return;
                }
                requestPermission(222, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new Runnable() {
                    @Override
                    public void run() {
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg"));
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                            startActivityForResult(openCameraIntent, SELECT_PIC_BY_TACK_PHOTO);
                        } else {
                            Uri imageUri = FileProvider.getUriForFile(getContext(), "asuper.yt.cn.supermarket.camera_photos.fileprovider", new File(tempUri.getPath()));
                            openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(openCameraIntent, SELECT_PIC_BY_TACK_PHOTO);
                        }
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        ToolAlert.dialog(getContext(), "授权提示", "授权失败", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                    }
                });
            }

            @Override
            public void onBottomButtonClick() {
                dismiss();
                if (maxPhotoNumber <= 0) {
                    ToastUtil.info("选择的图片已达上限");
                    return;
                }
                GalleryFinal.openGalleryMuti(SELECT_PIC_BY_PICK_PHOTO, YTApplication.initGalleryFinal(getContext(), maxPhotoNumber, items.get(currentGroupPosition).photoInfo), new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        if (SELECT_PIC_BY_PICK_PHOTO == reqeustCode && resultList != null && resultList.size() > 0) {
                            List<PhotoInfo> photoInfos = items.get(currentGroupPosition).photoInfo;
                            if (photoInfos == null) return;
                            for (int i = 0; i < (resultList.size() < maxPhotoNumber ? resultList.size() : maxPhotoNumber); i++) {
                                PhotoInfo info = resultList.get(i);
                                info.setPhotoId(0);
                                info.setHeight(0);
                                info.setWidth(0);
                                String[] splits = info.getPhotoPath().split("/");
                                String fileName = splits[splits.length - 1];
                                String folder = getContext().getApplicationContext().getCacheDir() + "/" + Config.UserInfo.USER_ID + "/" + items.get(currentGroupPosition).storeType + "/";
                                File file = new File(folder);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                ToolFile.copyFile(info.getPhotoPath(), folder + fileName);
                                info.setPhotoPath(folder + fileName);
                                photoInfos.add(info);
                            }
                            expandable.getAdapter().notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onHanlderFailure(int requestCode, String errorMsg) {
                        ToastUtil.error(errorMsg);
                    }
                });
            }
        };
        bottomPopView.setTopText("拍照");
        bottomPopView.setBottomText("图库");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1 && tempUri != null) {
            PhotoInfo info = new PhotoInfo();
            info.setPhotoId(0);
            info.setHeight(0);
            info.setWidth(0);
            info.setPhotoPath(tempUri.getPath());
            String[] splits = info.getPhotoPath().split("/");
            String fileName = splits[splits.length - 1];
            String folder = getContext().getApplicationContext().getCacheDir() + "/" + Config.UserInfo.USER_ID + "/" + items.get(currentGroupPosition).storeType + "/";
            File file = new File(folder);
            if (!file.exists()) {
                file.mkdirs();
            }
            ToolFile.copyFile(info.getPhotoPath(), folder + fileName);
            info.setPhotoPath(folder + fileName);
            items.get(currentGroupPosition).photoInfo.add(info);
            expandable.getAdapter().notifyDataSetChanged();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(tempUri);
            this.sendBroadcast(intent);
        }
    }


    private class UploadLayoutMananger extends LinearLayoutManager {

        public UploadLayoutMananger(Context context) {
            super(context);
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            return new RecyclerView.LayoutParams(-1, -2);
        }
    }

    private class SubUploadLayoutManager extends LinearLayoutManager {

        public SubUploadLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public RecyclerView.LayoutParams generateDefaultLayoutParams() {
            return new RecyclerView.LayoutParams((int) (YTApplication.mScreenWidth / 4.2f), (int) (YTApplication.mScreenWidth / 4.2f));
        }
    }

    private class UploadAdapter extends RecyclerView.Adapter {

        private List<ImageGalleryItem> model;

        public UploadAdapter(List<ImageGalleryItem> model) {
            this.model = model;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_upload_parent_item, null);
            UploadViewHolder uploadViewHolder = new UploadViewHolder(v);
            return uploadViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
            UploadViewHolder holder = (UploadViewHolder) vh;
            int number = model.get(position).photoInfo == null ? 0 : model.get(position).photoInfo.size();
            if (canUpdate) {
                if (model.get(position) == null) {
                    try {
                        int max = new BigDecimal(model.get(position).info.max.trim()).intValue();
                        int min = new BigDecimal(model.get(position).info.min.trim()).intValue();
                        if (min > 0) {
                            holder.title.setText("*" + model.get(position).info.name + "(" + number + "/" + max + ")");
                        } else {
                            holder.title.setText(model.get(position).info + "(" + number + "/" + max + ")");
                        }
                    } catch (Exception e) {
                        holder.title.setText(model.get(position).info.name + "(共" + model.get(position).photoInfo.size() + "张)");
                    }
                } else {
                    int max = model.get(position).max();
                    int min = model.get(position).min();
                    if (min > 0) {
                        holder.title.setText("*" + model.get(position).info.name + "(" + number + "/" + max + ")");
                    } else {
                        holder.title.setText(model.get(position).info.name + "(" + number + "/" + max + ")");
                    }
                    holder.edit.setEnabled(number < max);
                }
            } else {
                holder.title.setText(model.get(position).info.name + "(共" + model.get(position).photoInfo.size() + "张)");
            }
            String label = model.get(position).info.des;
            if (label != null && !label.isEmpty()) {
                label = label.replace("\\n", "\n");
                holder.des.setVisibility(View.VISIBLE);
                holder.des.setText(label);
            } else {
                holder.des.setVisibility(View.GONE);
            }
            if ("authorizationProvePhoto".equals(model.get(position).info.key)) {
                holder.des.setTextColor(Color.RED);
            } else {
                holder.des.setTextColor(getResources().getColor(R.color.gray_light));
            }
            holder.edit.setVisibility(canUpdate ? View.VISIBLE : View.INVISIBLE);
            holder.edit.setTag(position);
            holder.title.setTag(position);
            holder.title.setClickable(canUpdate);

            holder.groupZn.setText(model.get(position).title);
            if (position == 0 || (position < model.size() - 1 && !model.get(position).title.equals(model.get(position - 1).title))) {
                holder.groupZn.setVisibility(View.VISIBLE);
            } else {
                holder.groupZn.setVisibility(View.GONE);
            }


            holder.photoList.setLayoutManager(new SubUploadLayoutManager(holder.photoList.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.photoList.setItemAnimator(new DefaultItemAnimator());
            holder.photoList.setAdapter(new UploadSubAdapter(model.get(position).photoInfo, position));
        }

        @Override
        public int getItemCount() {
            if (model == null) return 0;
            int size = model.size();
            if (size > 0) {
                expandable.setVisibility(View.VISIBLE);
            } else {
                expandable.setVisibility(View.GONE);
            }
            return size;
        }

    }

    private class UploadViewHolder extends RecyclerView.ViewHolder {

        public TextView title, edit, des, groupZn;
        public RecyclerView photoList;
        public View divider;

        public UploadViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.upload_parent_item_title);
            edit = (TextView) itemView.findViewById(R.id.upload_parent_item_edit);
            des = (TextView) itemView.findViewById(R.id.upload_parent_item_des);
            groupZn = itemView.findViewById(R.id.upload_parent_item_groupZn);
            photoList = (RecyclerView) itemView.findViewById(R.id.upload_parent_item_list);
            divider = itemView.findViewById(R.id.upload_parent_item_divider);
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null) {
                        currentGroupPosition = (Integer) v.getTag();
                        if (items.size() > currentGroupPosition) {
                            AttachmentInfo attachmentInfo = items.get(currentGroupPosition).info;
                            maxPhotoNumber = items.get(currentGroupPosition) == null ? 0 : items.get(currentGroupPosition).max() - items.get(currentGroupPosition).photoInfo.size();
                        }
                        if (maxPhotoNumber < 0) maxPhotoNumber = 0;
                    }
                    bottomPopView.show();
                }
            });
            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null) {
                        currentGroupPosition = (Integer) v.getTag();
                        if (items.size() > currentGroupPosition) {
                            AttachmentInfo attachmentInfo = items.get(currentGroupPosition).info;
                            maxPhotoNumber = items.get(currentGroupPosition) == null ? 0 : items.get(currentGroupPosition).max() - items.get(currentGroupPosition).photoInfo.size();
                        }
                        if (maxPhotoNumber < 0) maxPhotoNumber = 0;
                    }
                    bottomPopView.show();
                }
            });
        }
    }

    private class UploadSubAdapter extends RecyclerView.Adapter<UploadSubViewHolder> {

        private List<PhotoInfo> photoInfos;
        private int parentIndex = 0;

        public UploadSubAdapter(List<PhotoInfo> photoInfos, int parentIndex) {
            this.photoInfos = photoInfos;
            this.parentIndex = parentIndex;
        }

        @Override
        public UploadSubViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_upload_sub_item, null);
            UploadSubViewHolder uploadSubViewHolder = new UploadSubViewHolder(v);
            return uploadSubViewHolder;
        }

        @Override
        public void onBindViewHolder(UploadSubViewHolder holder, final int position) {
            final PhotoInfo photoInfo = photoInfos.get(position);
            if (photoInfo.getPhotoId() == 0) {
                Glide.with(getContext()).load(photoInfo.getPhotoPath()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.ic_gf_default_photo).override(300, 300).into(holder.imageView);
            } else {
                Glide.with(getContext()).load(Config.IMG_HOST + photoInfo.getPhotoPath()).override(300, 300).placeholder(R.drawable.ic_gf_default_photo).into(holder.imageView);
            }
            holder.delete.setVisibility(canUpdate ? View.VISIBLE : View.INVISIBLE);
            holder.delete.setTag(position);
            holder.delete.setOnClickListener(v -> ToolAlert.dialog(getContext(), "删除提示", "确认删除？", (dialogInterface, post) -> {
                if (v.getTag() != null) {
                    int i = (int) v.getTag();
                    if (i < photoInfos.size()) {
                        photoInfos.remove(i);
                    } else {
                        ToastUtil.info("删除出错，请重试！");
                    }
                }
                expandable.getAdapter().notifyDataSetChanged();
            }, (dialogInterface, i) -> {
            }));
            holder.imageView.setOnClickListener(v -> {
                getOperation().addParameter("defaultPath", photoInfo.getPhotoPath() + "_" + parentIndex + "_" + position);
                getOperation().addParameter("json", fromDate);
                getOperation().forward(PreviewActivity.class);
            });
        }

        @Override
        public int getItemCount() {
            return photoInfos == null ? 0 : photoInfos.size();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (items == null) return;
        EventBus.getDefault().removeStickyEvent(items.getClass());
        Glide.get(this).clearMemory();
        System.gc();
    }

    private class UploadSubViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ImageButton delete;

        public UploadSubViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.upload_sub_item_img);
            delete = (ImageButton) itemView.findViewById(R.id.upload_sub_item_delete);
        }
    }

    public static class ImageGalleryItem implements PhotoNumberLimiter, Serializable {
        public ArrayList<PhotoInfo> photoInfo;
        public AttachmentInfo info;
        public int groupId;
        public String storeType = "default";
        private int min = 0, max = 0;
        public String title = "附件信息";
        public String groupEn;

        public ImageGalleryItem(AttachmentInfo info) {
            photoInfo = new ArrayList<>();
            this.info = info;
            try {
                this.min = new BigDecimal(info.min.trim()).intValue();
            } catch (Exception e) {
                this.min = 0;
            }

            try {
                this.max = new BigDecimal(info.max.trim()).intValue();
            } catch (Exception e) {
                this.max = 0;
            }
        }

        @Override
        public boolean check(String prefix, int number) {
            if (number < min)
                ToastUtil.error(info.name + "不能少于" + min + "张");
            if (number > max)
                ToastUtil.error(info.name + "不能超过" + max + "张");
            return number >= min && number <= max;
        }

        public boolean check() {
            if (photoInfo.size() < min)
                ToastUtil.error(info.name + "不能少于" + min + "张");
            if (photoInfo.size() > max)
                ToastUtil.error(info.name + "不能超过" + max + "张");
            return photoInfo.size() >= min && photoInfo.size() <= max;
        }

        @Override
        public int max() {
            return max;
        }

        @Override
        public int min() {
            return min;
        }

        @Override
        public String toString() {
            return "ImageGalleryItem{" +
                    "photoInfo=" + photoInfo +
                    ", info=" + info +
                    ", groupId=" + groupId +
                    ", storeType='" + storeType + '\'' +
                    ", min=" + min +
                    ", max=" + max +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public interface PhotoNumberLimiter {
        boolean check(String prefix, int number);

        int max();

        int min();
    }
}
