package asuper.yt.cn.supermarket.modules.newclient;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.BuildConfig;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.common.ImageGalleryActivity;
import asuper.yt.cn.supermarket.modules.myclew.NewMission;
import asuper.yt.cn.supermarket.modules.myclient.ClientDetailDialog;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.MatcherUtils;
import asuper.yt.cn.supermarket.utils.ToolData;
import asuper.yt.cn.supermarket.utils.ToolDatabase;
import asuper.yt.cn.supermarket.utils.ToolInputFilter;
import asuper.yt.cn.supermarket.utils.ToolLocation;
import asuper.yt.cn.supermarket.utils.ToolLog;
import asuper.yt.cn.supermarket.views.AddressSelectWindow;
import asuper.yt.cn.supermarket.views.AddressSelectWindows;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.finalteam.toolsfinal.StringUtils;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolString;

/**
 * 添加客户
 * Created by ZengXw on 2017/2/14.
 */

public class AddNewClientActivity extends BaseActivity implements View.OnClickListener {


    public static final int REQUEST_DO_INIT = 0X0101;
    public static final int REQUEST_COMMIT = 0X0102;
    public static final int REQUEST_SAVE = 0X0103;
    public static final int REQUEST_UPLOAD = 0X0104;

    private ClientInfoDetail clientInfoDetail;

    @Override
    protected int getContentId() {
        return R.layout.activity_addnew_client;
    }

    @Override
    protected void findView(View root) {
        isNew = getIntent().getBooleanExtra("isNew", true);
        newMission = EventBus.getDefault().getStickyEvent(NewMission.class);
        EventBus.getDefault().removeStickyEvent(NewMission.class);
        clientInfoDetail = (ClientInfoDetail) getIntent().getSerializableExtra("client");
        clientDetailDialog = new ClientDetailDialog(this);
        frame_root = (ViewGroup) findViewById(R.id.frame_root);
        sqCity = (TextView) findViewById(R.id.sqCity);
        sqCity.setOnClickListener(this);
        btnGetLock = (TextView) findViewById(R.id.btnGetLock);
        btnLock = (LinearLayout) findViewById(R.id.btnLock);
        shopName = (EditText) findViewById(R.id.shopName);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        addres = (EditText) findViewById(R.id.addres);
        btnCommit = (Button) findViewById(R.id.btnCommit);

        fujian = (LinearLayout) root.findViewById(R.id.fujian);
        name.setFilters(new InputFilter[]{ToolInputFilter.PERSON_NAME,new InputFilter.LengthFilter(10)});

        if (isNew || clientInfoDetail == null) {
            clientInfoDetail = new ClientInfoDetail();
            clientInfoDetail.setId(ToolString.gainUUID());
        }

        Map<String, Object> params = new HashMap<>();
        params.put("isNew", isNew);
        params.put("newMission", newMission);
        params.put("clientInfoDetail", clientInfoDetail);
        dispatch(REQUEST_DO_INIT, params);

        ActionBarManager.initBackToolbar(this, isNew ? "新增客户" : "修改客户");


        dbHelper = new ToolDatabase(this);

        findViewById(R.id.uploadImage).setOnClickListener(this);

        btnGetLock.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        findViewById(R.id.btnRight).setOnClickListener(this);
        btnCommit.setOnClickListener(v -> {
            clientInfoDetail.setIsInterested(3);
            beforeCommit();
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new AddNewClientStore(dependencyDelegate);
    }

    @BindAction(REQUEST_DO_INIT)
    public void initData(ClientInfoDetail data) {
        if (data == null) return;
        setView(data);
    }

    @BindAction(REQUEST_COMMIT)
    public void addClientResult(JSONObject response) {
        dismissProgress();
        if (response == null) return;
        ToastUtil.success("提交成功");
        delete(clientInfoDetail);
        setResult(100);
        finish();
    }

    @BindAction(REQUEST_UPLOAD)
    public void uploadResult(boolean result) {
        if (BuildConfig.DEBUG||result) {
            commitData(form);
        } else {
            ToastUtil.error("上传图片失败");
            dismissProgress();
        }
    }

    @BindAction(REQUEST_SAVE)
    public void saveAResult(boolean result) {
        if (result) {
            ToastUtil.success("保存成功");
            setResult(101);
            finish();
        } else {
            ToastUtil.error("保存失败");
        }
    }

    enum Area {
        PROVINCE, CITY, COUNTY
    }

    private Area select = Area.PROVINCE;

    private ToolDatabase dbHelper;
    private Dao<ClientInfoDetail, String> userDao;
    private ViewGroup frame_root;
    private TextView btnGetLock;
    private LinearLayout btnLock;
    private Button btnCommit;
    private TextView sqCity;
    private EditText shopName, name, phone, addres;

    private boolean isNew = true;


    private LinearLayout fujian;

    private ClientDetailDialog clientDetailDialog;


    private NewMission newMission;

    private DTO<String, Object> form = new DTO<>();


    private void addViewFujian(List<ImageGalleryActivity.ImageGalleryItem> items) {
        fujian.removeAllViews();
        if (items == null || items.size() < 1) return;
        for (int i = 0; i < items.size(); i++) {
            int size = 0;
            if (items.get(i).photoInfo != null) {
                size = items.get(i).photoInfo.size();
            }
            if ((items.get(i).min() > 0 && size < items.get(i).min()) || size > items.get(i).max()) {
                TextView textView = (TextView) getContext().getLayoutInflater().inflate(R.layout.layout_simple_text, null);
                textView.setText("*" + items.get(i).info.name + "(" + size + "/" + items.get(i).max() + ")");
                fujian.addView(textView);
            }
        }

        if (fujian.getChildCount() < 1) {
            TextView textView = (TextView) getContext().getLayoutInflater().inflate(R.layout.layout_simple_text, null);
            textView.setText("附件数量符合上传要求");
            fujian.addView(textView);
        }
    }

    private void setView(ClientInfoDetail clien) {
        shopName.setText(clien.getShopName());
        name.setText(clien.getLegalpersonName());
        phone.setText(clien.getPhoneNumber());
        addres.setText(clien.getShopAddree());
        btnGetLock.setText(clien.getLocationAddress());
        sqCity.setText(String.format("%1$s%2$s%3$s%4$s", StringUtils.nullStrToEmpty(clien.getProvinceName()), StringUtils.nullStrToEmpty(clien.getCityName()), StringUtils.nullStrToEmpty(clien.getCountyName()),StringUtils.nullStrToEmpty(clien.getStreetName())));
        addViewFujian(clien.fileRule);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //渲染菜单
//        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
//
//        //根据业务需要订制菜单
//        ActionBarManager.initActionBarSubmitButton(menu, ActionBarManager.TOOLBARBTN.SUBMIT);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//        clientDetailDialog.show("资料未完善", "是否将该未完善的资料放入我的线索中?", "可在 我的线索>资料未完善 中继续填写", new ClientDetailDialog.OnClientDialogConfirmListener() {
//            @Override
//            public void onConfirm() {
//                save();
//            }
//        });
//        return true;
//    }

    private void save() {
        String msg = null;
        DTO<String, Object> formData = new DTO<>();
        formData = ToolData.gainForm(frame_root, formData);
        try {
            for (String key : formData.keySet()) {
                ToolLog.i(key + "," + formData.get(key));
                if (formData.get(key) == null || formData.get(key).toString().isEmpty() || "".equals(formData.get(key).toString())) {
                    msg = ToolData.addClientMap.get(key) + "不能为空";
                    break;
                } else {
                    msg = checkFormat(key, formData);
                    if (msg != null) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.error("请输入必填项");
            return;
        }
        boolean nul = true;
        for (String key : formData.keySet()){
            if (formData.get(key)!=null&&!formData.get(key).toString().isEmpty()&&!"".equals(formData.get(key).toString())){
                nul = false;
            }
        }
        if (nul){
            ToastUtil.error("尚未填写任何信息");
            return;
        }


        formData.put("location", btnGetLock.getText().toString());
        dispatch(REQUEST_SAVE, formData);

    }

    private String checkFormat(String key, DTO<String, Object> formData) {
        if ("phone".equals(key) && formData.get(key).toString().length() < 7) {
            return ToolData.addClientMap.get("phone") + "格式错误";
        }
        return null;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRight:
                clientDetailDialog.show("资料未完善", "是否将该未完善的资料放入我的线索中?", "可在 我的线索>资料未完善 中继续填写", this::save);
                break;
            case R.id.uploadImage:
                EventBus.getDefault().postSticky(clientInfoDetail.fileRule);
                getOperation().addParameter("canUpdate", true);
                getOperation().forward(ImageGalleryActivity.class);
                break;
            case R.id.btnGetLock:
            case R.id.btnLock:
                showProgress();
                requestPermission(20, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, () -> {
                    //// TODO: 2017/4/19 0019 静态持有回调，间接引用activity，内存泄漏
                    ToolLocation.requestLocation(new ToolLocation.InterfaceBDLocation() {
                        @Override
                        public void onLocationSuccess(BDLocation location) {
                            dialog = null;
                            Message msg = mHandler.obtainMessage(1);
                            msg.obj = location;
                            msg.sendToTarget();
                        }
                    }, true);
                }, () -> ToolAlert.dialog(getContext(), "提示", "获取权限失败", (dialogInterface, i) -> {
                }));
                break;
            case R.id.sqCity:
                AddressSelectWindow.show(sqCity, getContext(), region -> {
                    switch (region.regionType) {
                        case "2":
                            clientInfoDetail.setProvinceCode(region.code);
                            clientInfoDetail.setProvinceName(region.regionName);
                            sqCity.setText(String.format("%1$s", clientInfoDetail.getProvinceName()));
                            break;
                        case "3":
                            clientInfoDetail.setCityCode(region.code);
                            clientInfoDetail.setCityName(region.regionName);
                            sqCity.setText(String.format("%1$s%2$s", clientInfoDetail.getProvinceName(), clientInfoDetail.getCityName()));
                            break;
                        case "4":
                            clientInfoDetail.setCountyCode(region.code);
                            clientInfoDetail.setCountyName(region.regionName);
                            sqCity.setText(String.format("%1$s%2$s%3$s", clientInfoDetail.getProvinceName(), clientInfoDetail.getCityName(), clientInfoDetail.getCountyName()));
                            break;
                        case "5":
                            clientInfoDetail.setStreetCode(region.code);
                            clientInfoDetail.setStreetName(region.regionName);
                            sqCity.setText(String.format("%1$s%2$s%3$s%4$s", clientInfoDetail.getProvinceName(), clientInfoDetail.getCityName(), clientInfoDetail.getCountyName(),clientInfoDetail.getStreetName()));
                            break;
                        default:
                            clientInfoDetail.setStreetCode(region.code);
                            clientInfoDetail.setStreetName(region.regionName);
                            sqCity.setText(String.format("%1$s%2$s%3$s%4$s", clientInfoDetail.getProvinceName(), clientInfoDetail.getCityName(), clientInfoDetail.getCountyName(),clientInfoDetail.getStreetName()));
                            break;
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (clientInfoDetail != null) addViewFujian(clientInfoDetail.fileRule);
    }

    private void beforeCommit() {
        String msg = null;
        ToolData.gainForm(frame_root, form);
        form.put("sqCity", clientInfoDetail.getCountyName());
        form.put("lotlat", clientInfoDetail.getLocationAddress());
        for (String key : form.keySet()) {
            ToolLog.i("key:" + key + "-----value:" + form.get(key));
            if (form.get(key) == null || form.get(key).toString().isEmpty() || "".equals(form.get(key).toString())) {
                if ("phone".equals(key)) {
                    continue;
                }
                msg = ToolData.addClientMap.get(key) + "不能为空";
                break;
            } else {
                msg = checkFormat(key, form);
                if (msg != null) {
                    break;
                }
            }
        }
        if (msg != null) {
            ToolAlert.dialog(getContext(), "提交提示", "资料未完善，不能提交", (dialogInterface, i) -> {
            });
            return;
        }
        if (!MatcherUtils.checkMobilePhoneNum(form.get("phone").toString())) {
            ToastUtil.error("手机号格式不对");
            return;
        }


        for (ImageGalleryActivity.ImageGalleryItem imageGalleryItem : clientInfoDetail.fileRule) {
            if (!imageGalleryItem.check()) {
                return;
            }
        }

        ToolAlert.dialog(getContext(), "提交提示", "请确认无误后提交", (dialogInterface, i) -> {
            showProgress();
            dispatch(REQUEST_UPLOAD, null);
        }, (dialogInterface, i) -> {

        });
    }


    private void commitData(DTO<String, Object> data) {
        data.put("location", btnGetLock.getText());
        dispatch(REQUEST_COMMIT, data);
    }

    private void delete(ClientInfoDetail clientele) {
        try {
            userDao = dbHelper.getDao(ClientInfoDetail.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AndroidDatabaseConnection conn = null;
        try {
            //获取数据库连接
            conn = new AndroidDatabaseConnection(dbHelper.getWritableDatabase(), true);
            conn.setAutoCommit(false);
            userDao.delete(clientele);
            conn.commit(null);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private AlertDialog dialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    BDLocation location = (BDLocation) msg.obj;
                    ToolLog.i("地址信息：" + location.getAddrStr() + "----" + location.getLongitude() + "," + location.getLatitude());
                    clientInfoDetail.setLatitude(location.getLatitude() + "");
                    clientInfoDetail.setLongitude(location.getLongitude() + "");
                    clientInfoDetail.setLocationAddress(location.getAddrStr());
                    btnGetLock.setText(location.getAddrStr());
                    if ((location.getAddrStr() == null || location.getAddrStr() == "") && dialog == null) {
                        ToastUtil.error("请到空旷的地方进行定位");
                    }
                    dismissProgress();
                    break;
            }
        }
    };


}
