package asuper.yt.cn.supermarket.modules.operate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.operate.zbar.ZBarActivity;
import asuper.yt.cn.supermarket.modules.setting.AccountActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.permission.AfterPermissionGranted;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;

/**
 * Created by zengxiaowen on 2017/12/6.
 */

public class OperateFragment extends BaseFragment implements MainActivity.MainFragment{

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private static final int ANIMATIONEACHOFFSET = 600; // 每个动画的播放时间间隔
    private AnimationSet aniSet, aniSet2, aniSet3;
    private ImageView wave1, wave2, wave3;

    @Override
    protected int getContentId() {
        return R.layout.fragment_operate;
    }

    @Override
    protected void findView(View root) {
        ((TextView)root.findViewById(R.id.tvUserName)).setText(Config.UserInfo.NAME);
        wave1 =  root.findViewById(R.id.wave1);
        wave2 = root.findViewById(R.id.wave2);
        wave3 =  root.findViewById(R.id.wave3);
        aniSet = getNewAnimationSet();
        aniSet2 = getNewAnimationSet();
        aniSet3 = getNewAnimationSet();
        root.findViewById(R.id.btn_sm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().requestPermission(252, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, new Runnable() {
                    @Override
                    public void run() {
                        getOperation().forward(ZBarActivity.class);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                       ToastUtil.error("授权失败");
                    }
                });
            }
        });
        showWaveAnimation();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x222) {
                wave2.startAnimation(aniSet2);
            } else if (msg.what == 0x333) {
                wave3.startAnimation(aniSet3);
            }
            super.handleMessage(msg);
        }

    };

    private AnimationSet getNewAnimationSet() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, 1.6f, 1f, 1.6f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATIONEACHOFFSET * 3);
        sa.setRepeatCount(-1);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(-1);// 设置循环
        as.setDuration(ANIMATIONEACHOFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        return as;
    }
    private void showWaveAnimation() {
        wave1.startAnimation(aniSet);
        handler.sendEmptyMessageDelayed(0x222, ANIMATIONEACHOFFSET);
        handler.sendEmptyMessageDelayed(0x333, ANIMATIONEACHOFFSET * 2);

    }
}
