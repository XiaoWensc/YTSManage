package asuper.yt.cn.supermarket.modules.myclient;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.entities.AttachmentInfo;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.main.MainActivity.MainFragment;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.toolsfinal.StringUtils;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public class ClientDetailFragment extends BaseFragment implements MainFragment, MyClientDetailActivity.MyClientDetailFragment {

    ClientInfoDetail clientInfoDetaill;

    private TextView sqCity, time, lotlatName, upload;
    private EditText shopName, bossName, bossPhone, addres;
    private LinearLayout fujian;


    private AlertDialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    BDLocation location = (BDLocation) msg.obj;
                    ToolLog.i("地址信息：" + location.getAddrStr() + "----" + location.getLongitude() + "," + location.getLatitude());
                    clientInfoDetaill.setLatitude(location.getLatitude() + "");
                    clientInfoDetaill.setLongitude(location.getLongitude() + "");
                    clientInfoDetaill.setLocationAddress(location.getAddrStr());
                    lotlatName.setText(location.getAddrStr());
                    if ((location.getAddrStr() == null || location.getAddrStr() == "") && dialog == null) {
                        ToastUtil.error("请到空旷的地方进行定位");
                    }
                    dissmissProgress();
                    break;
            }
        }
    };

    public static final ClientDetailFragment newInstance(ClientInfoDetail clientInfoDetail) {
        ClientDetailFragment clientDetailFragment = new ClientDetailFragment();
        AttachmentInfo attachmentInfo = new AttachmentInfo();
        attachmentInfo.des = "";
        attachmentInfo.key = "pictures";
        attachmentInfo.name = "客户照片";
        attachmentInfo.max = "3";
        attachmentInfo.min = "3";
        List<ImageGalleryActivity.ImageGalleryItem> attachmentInfos = new ArrayList<>();
        ImageGalleryActivity.ImageGalleryItem imageGalleryItem = new ImageGalleryActivity.ImageGalleryItem(attachmentInfo);
        imageGalleryItem.storeType = clientInfoDetail.getId() + "_0";
        attachmentInfos.add(imageGalleryItem);
        clientInfoDetail.fileRule = attachmentInfos;
        clientDetailFragment.clientInfoDetaill = clientInfoDetail;
        ArrayList<PhotoInfo> photoInfos = new ArrayList<>();
        if (clientInfoDetail.getPicturs()!=null) photoInfos.addAll(clientInfoDetail.getPicturs());
        if (clientInfoDetail.getPictur_up()!=null) photoInfos.addAll(clientInfoDetail.getPictur_up());
        attachmentInfos.get(0).photoInfo = photoInfos;

        return clientDetailFragment;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_client_base_info;
    }

    public void enableEdit(boolean enable) {

        shopName.setEnabled(enable);
        bossName.setEnabled(enable);
        addres.setEnabled(enable);
        bossPhone.setEnabled(enable);
        if (enable) {
            shopName.requestFocus();
        }
    }

    @Override
    protected void findView(View view) {
        sqCity = (TextView) view.findViewById(R.id.sqCity);
        shopName = (EditText) view.findViewById(R.id.shopName);
        bossName = (EditText) view.findViewById(R.id.name);
        bossPhone = (EditText) view.findViewById(R.id.phone);
        addres = (EditText) view.findViewById(R.id.addres);
        time = (TextView) view.findViewById(R.id.time);
        lotlatName = (TextView) view.findViewById(R.id.btnGetLock);
        fujian = (LinearLayout) view.findViewById(R.id.fujian);
        upload = (TextView) view.findViewById(R.id.uploadImage);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolAttachment.gotoUpload(false, clientInfoDetaill.fileRule);
            }
        });
        shopName.setEnabled(false);
        bossName.setEnabled(false);
        bossPhone.setEnabled(false);
        addres.setEnabled(false);
        setView(clientInfoDetaill);
        if(clientInfoDetaill==null) return;
        ToolAttachment.addFujian(clientInfoDetaill.fileRule, fujian, getActivity());
    }

    private void setView(ClientInfoDetail client) {
        if (client==null) return;
        shopName.setText(client.getShopName());
        bossName.setText(client.getLegalpersonName());
        bossPhone.setText(client.getPhoneNumber());
        addres.setText(client.getShopAddree());
        lotlatName.setText(client.getLocationAddress());
        time.setText(client.getLastTime());
        sqCity.setText(String.format("%1$s%2$s%3$s%4$s", client.getProvinceName(), client.getCityName(), client.getCountyName(), StringUtils.nullStrToEmpty(client.getStreetName())));
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

    @Override
    public void commit() {
    }

    @Override
    public void save(final SaveCompleteListener listener) {
        if (shopName == null || !shopName.isEnabled()) return;
        if (clientInfoDetaill != null) {
            if (clientInfoDetaill.getButtonInfos() == null || clientInfoDetaill.getButtonInfos().size() <= 1) {
                if (!check()) return;
                Map<String, Object> map = new HashMap<>();
                map.put("shopName", shopName.getText().toString());
                map.put("legalpersonName", bossName.getText().toString());
                map.put("phoneNumber", bossPhone.getText().toString());
                map.put("shopAddree", addres.getText().toString());
                map.put("franchiseeNumber", clientInfoDetaill.getFranchiseeNumber());
                if (shopName != null && shopName.isEnabled()) {
                    ToolOkHTTP.post(Config.getURL("oles/app/myClient/updateBaseInfo.htm"), map, new ToolOkHTTP.OKHttpCallBack() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            shopName.setEnabled(false);
                            bossName.setEnabled(false);
                            bossPhone.setEnabled(false);
                            addres.setEnabled(false);
                            lotlatName.setOnClickListener(null);
                            ToastUtil.error("修改成功");
                            if(listener != null){
                                listener.result(true,ClientDetailFragment.this);
                            }
                        }

                        @Override
                        public void onFailure() {
                            ToastUtil.error("修改失败");
                            if(listener != null){
                                listener.result(false,ClientDetailFragment.this);
                            }
                        }
                    });
                }
            }
        }
    }

    private boolean check() {
        if (shopName.getText().toString().trim().isEmpty()) {
            ToastUtil.error("店铺名称不能为空");
            return false;
        }
        if (bossName.getText().toString().trim().isEmpty()) {
            ToastUtil.error("店铺姓名不能为空");
            return false;
        }
        if (bossPhone.getText().toString().trim().isEmpty()) {
            ToastUtil.error("联系方式不能为空");
            return false;
        }
        if (addres.getText().toString().trim().isEmpty()) {
            ToastUtil.error("店铺地址不能为空");
            return false;
        }
        return true;
    }

    @Override
    public void cancel() {

    }

    @Override
    public void edit() {
        if (clientInfoDetaill != null) {
            if (clientInfoDetaill.getButtonInfos() == null || clientInfoDetaill.getButtonInfos().size() <= 1) {
                shopName.setEnabled(true);
                bossName.setEnabled(true);
                bossPhone.setEnabled(true);
                shopName.requestFocus();
            }
        }
    }
}
