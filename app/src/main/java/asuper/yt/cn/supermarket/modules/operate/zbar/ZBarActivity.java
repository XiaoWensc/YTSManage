package asuper.yt.cn.supermarket.modules.operate.zbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;

/**
 * Created by zengxiaowen on 2017/11/28.
 */

public class ZBarActivity extends BaseActivity {

    private QRCodeView mQRCodeView;
    private CheckBox flashlight;

    @Override
    protected int getContentId() {
        return R.layout.activity_zbar;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"扫描");
        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        flashlight = (CheckBox) findViewById(R.id.flashlight);
        mQRCodeView.setDelegate(new QRCodeView.Delegate() {
            @Override

            public void onScanQRCodeSuccess(String s) {
                mQRCodeView.stopCamera();
                showProgress();
                Map<String,Object> map = new HashMap<>();
                map.put("shopCodeStr",s);
                dispatch(ZBarStore.REQUEST_POST_ZBAR_GETINFO,map);
                vibrate();
            }

            @Override
            public void onScanQRCodeOpenCameraError() {
                ToolAlert.dialog(getContext(), "警告", "检测到您的相机权限未打开\n将影响正常使用，立即前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
        });
        flashlight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) mQRCodeView.openFlashlight();
                else mQRCodeView.closeFlashlight();
            }
        });
    }

    @BindAction(ZBarStore.REQUEST_POST_ZBAR_GETINFO)
    public void getInfo(String bean){
        dismissProgress();
        if (bean==null) {
            mQRCodeView.startCamera();
            mQRCodeView.showScanRect();
            mQRCodeView.startSpot();
            return;
        }
        getOperation().addParameter("bean",bean);
        getOperation().forward(WhiteInfoActivity.class);
    }

    /**
     * 震动
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        mQRCodeView.changeToScanQRCodeStyle();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mQRCodeView.startSpot();
        mQRCodeView.closeFlashlight();

    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new ZBarStore(dependencyDelegate);
    }
}
