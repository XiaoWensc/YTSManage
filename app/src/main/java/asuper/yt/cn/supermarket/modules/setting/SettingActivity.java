package asuper.yt.cn.supermarket.modules.setting;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.SplashActivity;
import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolPreference;
import supermarket.cn.yt.asuper.ytlibrary.widgets.BottomPopView;

/**
 * Created by liaoqinsen on 2017/4/14 0014.
 */

public class SettingActivity extends PreferenceActivity {

    private ListPreference listPreference;
    private EditTextPreference host, img_host;
    private SwitchPreference crashTest, globalCapture, autoFillAttachment;
    private boolean isGlobalCapture = !BuildConfig.DEBUG;

    public static final String HOST_DEV_KEY = "host_dev_key";
    public static final String HOST_IMG_DEV_KEY = "host_img_dev_key";
    public static final String HOST_TAG_DEV_KEY = "host_tag_dev_key";
    public static final String HOST_TEST_KEY = "host_test_key";
    public static final String HOST_IMG_TEST_KEY = "host_img_test_key";
    public static final String HOST_TAG_TEST_KEY = "host_tag_test_key";
    public static final String HOST_SIT3_KEY = "host_sit3_key";
    public static final String HOST_IMG_SIT3_KEY = "host_img_sit3_key";
    public static final String HOST_TAG_SIT3_KEY = "host_tag_sit3_key";
    public static final String HOST_PRO_KEY = "host_pro_key";
    public static final String HOST_IMG_PRO_KEY = "host_img_pro_key";
    public static final String HOST_TAG_PRO_KEY = "host_tag_pro_key";
    public static final String HOST_TYPE_KEY = "host_type_key";
    public static final String ENABLE_GLOBAL_CAPTURE_KEY = "global_capture";
    public static final String ENABLE_AUTO_FILL_ATTCHMENT_KEY = "auto_fill_attachment";
    public static final String ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY = "auto_fill_attachment_path";

    public static final String KEY_HOST_PREF = "key_host_pref";
    public static final String KEY_IMG_HOST_PREF = "key_img_host_pref";
    public static final String KEY_TAG_HOST_PREF = "key_tag_host_pref";

    private String host_dev, host_img_dev, host_tag_dev, host_test, host_img_test, host_tag_test, host_sit3, host_img_sit3, host_tag_sit3, host_pro, host_img_pro, host_tag_pro, type, auto_fill_attachment_path;
    private boolean auto_fill_attachment;
    private String jpshTag;
    private BottomPopView bottomPopView;

    Uri tempUri;

    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    public static final int SELECT_PIC_BY_PICK_PHOTO = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_activity);

        host_dev = ToolPreference.get().getString(HOST_DEV_KEY);
        host_img_dev = ToolPreference.get().getString(HOST_IMG_DEV_KEY);
        host_tag_dev = ToolPreference.get().getString(HOST_TAG_DEV_KEY);
        host_test = ToolPreference.get().getString(HOST_TEST_KEY);
        host_img_test = ToolPreference.get().getString(HOST_IMG_TEST_KEY);
        host_tag_test = ToolPreference.get().getString(HOST_TAG_TEST_KEY);
        host_sit3 = ToolPreference.get().getString(HOST_SIT3_KEY);
        host_img_sit3 = ToolPreference.get().getString(HOST_IMG_SIT3_KEY);
        host_tag_sit3 = ToolPreference.get().getString(HOST_TAG_SIT3_KEY);
        host_pro = ToolPreference.get().getString(HOST_PRO_KEY);
        host_img_pro = ToolPreference.get().getString(HOST_IMG_PRO_KEY);
        host_tag_pro = ToolPreference.get().getString(HOST_TAG_PRO_KEY);
        isGlobalCapture = ToolPreference.get().getBoolean(ENABLE_GLOBAL_CAPTURE_KEY);
        auto_fill_attachment = ToolPreference.get().getBoolean(ENABLE_AUTO_FILL_ATTCHMENT_KEY);
        auto_fill_attachment_path = ToolPreference.get().getString(ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY);

        if (host_dev == null || host_dev.isEmpty()) {
            host_dev = Config.DEV_HOST;
        }
        if (host_img_dev == null || host_img_dev.isEmpty()) {
            host_img_dev = Config.DEV_HOST_IMG;
        }
        if (host_tag_dev == null || host_tag_dev.isEmpty()) {
            host_tag_dev = Config.DEV_HOST_TAG;
        }
        if (host_test == null || host_test.isEmpty()) {
            host_test = Config.TEST_HOST;
        }
        if (host_img_test == null || host_img_test.isEmpty()) {
            host_img_test = Config.TEST_HOST_IMG;
        }
        if (host_tag_test == null || host_tag_test.isEmpty()) {
            host_tag_test = Config.TEST_HOST_TAG;
        }
        if (host_sit3 == null || host_sit3.isEmpty()) {
            host_sit3 = BuildConfig.DEBUG ? BuildConfig.CLIENT_HOST : Config.SIT3_HOST;
        }
        if (host_img_sit3 == null || host_img_sit3.isEmpty()) {
            host_img_sit3 = BuildConfig.DEBUG ? BuildConfig.IMG_HOST : Config.SIT3_HOST_IMG;
        }
        if (host_tag_sit3 == null || host_tag_sit3.isEmpty()) {
            host_tag_sit3 = BuildConfig.DEBUG ? BuildConfig.JPUSH_TAG : Config.SIT3_HOST_TAG;
        }
        if (host_pro == null || host_pro.isEmpty()) {
            host_pro = !BuildConfig.DEBUG ? BuildConfig.CLIENT_HOST : Config.PRO_HOST;
        }
        if (host_img_pro == null || host_img_pro.isEmpty()) {
            host_img_pro = !BuildConfig.DEBUG ? BuildConfig.IMG_HOST : Config.PRO_HOST_IMG;
        }
        if (host_tag_pro == null || host_tag_pro.isEmpty()) {
            host_tag_pro = !BuildConfig.DEBUG ? BuildConfig.JPUSH_TAG : Config.PRO_HOST_TAG;
        }


        type = ToolPreference.get().getString(HOST_TYPE_KEY);

        listPreference = (ListPreference) getPreferenceManager().findPreference("env_selection");
        host = (EditTextPreference) getPreferenceManager().findPreference("host");
        img_host = (EditTextPreference) getPreferenceManager().findPreference("img_host");
        crashTest = (SwitchPreference) getPreferenceManager().findPreference("crash_test");
        globalCapture = (SwitchPreference) getPreferenceManager().findPreference("global_capture");
        autoFillAttachment = (SwitchPreference) getPreferenceManager().findPreference("auto_fill_attachment");


        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                type = newValue.toString();
                ToolPreference.get().putString(HOST_TYPE_KEY, type);
                listPreference.setSummary(getSummary());
                setEdit(type);
                return true;
            }
        });

        host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                host.setSummary(newValue.toString());
                switch (type) {
                    case "dev":
                        ToolPreference.get().putString(HOST_DEV_KEY, newValue.toString());
                        break;
                    case "test":
                        ToolPreference.get().putString(HOST_TEST_KEY, newValue.toString());
                        break;
                    case "sit3":
                        ToolPreference.get().putString(HOST_SIT3_KEY, newValue.toString());
                        break;
                    case "produce":
                        ToolPreference.get().putString(HOST_PRO_KEY, newValue.toString());
                        break;
                }
                return true;
            }
        });

        img_host.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                img_host.setSummary(newValue.toString());
                switch (type) {
                    case "dev":
                        ToolPreference.get().putString(HOST_IMG_DEV_KEY, newValue.toString());
                        break;
                    case "test":
                        ToolPreference.get().putString(HOST_IMG_TEST_KEY, newValue.toString());
                        break;
                    case "sit3":
                        ToolPreference.get().putString(HOST_IMG_SIT3_KEY, newValue.toString());
                        break;
                    case "produce":
                        ToolPreference.get().putString(HOST_IMG_PRO_KEY, newValue.toString());
                        break;
                }
                return true;
            }
        });

        crashTest.setChecked(false);
        crashTest.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ((boolean) newValue) {
                    CrashReport.testJavaCrash();
                }
                return true;
            }
        });

        globalCapture.setChecked(isGlobalCapture);
        globalCapture.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                YTApplication.enableGlobalCapture((boolean) newValue);
                ToolPreference.get().putBoolean(ENABLE_GLOBAL_CAPTURE_KEY, (boolean) newValue);
                YTApplication.restart();
                return true;
            }
        });

        autoFillAttachment.setChecked(auto_fill_attachment);
        if (auto_fill_attachment_path != null) {
            autoFillAttachment.setSummary(auto_fill_attachment_path);
        }

        autoFillAttachment.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Config.isAutoFillAttachment = (boolean) newValue;
                ToolPreference.get().putBoolean(ENABLE_AUTO_FILL_ATTCHMENT_KEY, Config.isAutoFillAttachment);
                if (Config.isAutoFillAttachment) {
                    bottomPopView.show();
                }
                return true;
            }
        });


        if (type != null) {
            listPreference.setValue(type);
            listPreference.setSummary(getSummary());
            setEdit(type);
        } else {
            type = BuildConfig.DEBUG ? "sit3" : "produce";
            listPreference.setValue(type);
            listPreference.setSummary(getSummary());
            setEdit(type);
        }

        initBottomPopView();
    }


    private void initBottomPopView() {
        bottomPopView = new BottomPopView(this, getListView()) {
            @Override
            public void onTopButtonClick() {
                dismiss();
                requestPermission(222, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, () -> {
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    tempUri = Uri.fromFile(new File(YTApplication.get().getCacheDir().getAbsolutePath(), System.currentTimeMillis() + ".png"));
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, SELECT_PIC_BY_TACK_PHOTO);
                    } else {
                        Uri imageUri = FileProvider.getUriForFile(getParent(), "asuper.yt.cn.supermarket.camera_photos.fileprovider", new File(tempUri.getPath()));
                        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, SELECT_PIC_BY_TACK_PHOTO);
                    }
                }, () -> ToastUtil.error("授权失败"));
            }

            @Override
            public void onBottomButtonClick() {
                dismiss();
                GalleryFinal.openGalleryMuti(SELECT_PIC_BY_PICK_PHOTO, YTApplication.initGalleryFinal(getApplicationContext(), 1, null), new GalleryFinal.OnHanlderResultCallback() {
                    @Override
                    public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
                        if (SELECT_PIC_BY_PICK_PHOTO == reqeustCode && resultList != null && resultList.size() > 0) {
                            autoFillAttachment.setSummary(resultList.get(0).getPhotoPath());
                            Config.autoFillAttachmentPath = resultList.get(0).getPhotoPath();
                            ToolPreference.get().putString(ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY, resultList.get(0).getPhotoPath());
                            autoFillAttachment.setChecked(true);
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
        if (requestCode == 1 && resultCode == -1) {
            autoFillAttachment.setSummary(tempUri.getPath());
            Config.autoFillAttachmentPath = tempUri.getPath();
            ToolPreference.get().putString(ENABLE_AUTO_FILL_ATTCHMENT_PATH_KEY, tempUri.getPath());
            autoFillAttachment.setChecked(true);
        }
    }

    protected void requestPermission(int requestId, String[] permission,
                                     Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> pers = new ArrayList<>();
            //检查是否拥有权限
            for (int i = 0; i < permission.length; i++) {
                int checkPermission = ContextCompat.checkSelfPermission(YTApplication.get(), permission[i]);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    //弹出对话框请求授权
                    pers.add(permission[i]);
                }
            }
            if (pers.size() > 0) {
                String[] permissions = new String[pers.size()];
                for (int i = 0; i < pers.size(); i++) {
                    permissions[i] = pers.get(i);
                }
                ActivityCompat.requestPermissions(getParent(), permissions, requestId);
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }


    private void setEdit(String type) {
        switch (type) {
            case "dev":
                host.setSummary(host_dev);
                host.setText(host_dev);
                img_host.setSummary(host_img_dev);
                img_host.setText(host_img_dev);
                jpshTag = host_tag_dev;
                break;
            case "test":
                host.setSummary(host_test);
                host.setText(host_test);
                img_host.setSummary(host_img_test);
                img_host.setText(host_img_test);
                jpshTag = host_tag_test;
                break;
            case "sit3":
                host.setSummary(host_sit3);
                host.setText(host_sit3);
                img_host.setSummary(host_img_sit3);
                img_host.setText(host_img_sit3);
                jpshTag = host_tag_test;
                break;
            case "produce":
                host.setSummary(host_pro);
                host.setText(host_pro);
                img_host.setSummary(host_img_pro);
                img_host.setText(host_img_pro);
                jpshTag = host_tag_pro;
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.HOST = host.getText();
        Config.IMG_HOST = img_host.getText();
        Config.JPshTag = jpshTag;
        ToolPreference.get().putString(KEY_HOST_PREF, Config.HOST);
        ToolPreference.get().putString(KEY_IMG_HOST_PREF, Config.IMG_HOST);
        ToolPreference.get().putString(KEY_TAG_HOST_PREF, Config.JPshTag);
        restartApplication(this);
    }

    private void restartApplication(Context context) {
        Intent mStartActivity = new Intent(context, SplashActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private String getSummary() {
        switch (type) {
            case "dev":
                return "UAT环境";
            case "test":
                return "SIT环境";
            case "sit3":
                return "SIT环境";
            case "produce":
                return "PRO环境";
            default:
                return "选择当前需要访问的环境配置";
        }
    }
}
