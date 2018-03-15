package asuper.yt.cn.supermarket.modules.operate.zbar;

import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.utils.ActionBarManager;
import asuper.yt.cn.supermarket.utils.DTO;
import asuper.yt.cn.supermarket.utils.ToolData;
import asuper.yt.cn.supermarket.utils.ToolTagView;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by zengxiaowen on 2017/12/11.
 */

public class WhiteInfoActivity extends BaseActivity {

    String constat = "";
    private LinearLayout root ;
    private Button goBack,yesOk;
    private TextView tvMsg,deliveryCode;

    @Override
    protected int getContentId() {
        return R.layout.activity_whiteinfo;
    }

    @Override
    protected void findView(View root) {
        ActionBarManager.initBackToolbar(this,"扫描结果");
        constat = getIntent().getStringExtra("bean");
        initView();
        setView(constat);

    }

    private void initView() {
        root = (LinearLayout) findViewById(R.id.root);
        tvMsg = (TextView) findViewById(R.id.tvMsg);
        deliveryCode = (TextView) findViewById(R.id.deliveryCode);
        (goBack = (Button) findViewById(R.id.btn_Sao)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        (yesOk = (Button) findViewById(R.id.btn_Ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                Map<String,Object> map = new HashMap<>();
                map.put("shopCode",ToolData.gainForm(root, new DTO<String, Object>()).get("storeId").toString());
                dispatch(ZBarStore.REQUEST_POST_ZBAR_JSONWS,map);
            }
        });
    }

    @BindAction(ZBarStore.REQUEST_POST_ZBAR_JSONWS)
    public void postJsonWs(boolean suc){
        dismissProgress();
        getOperation().addParameter("suc",suc);
        getOperation().forward(OperResultActivity.class);
        finish();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new ZBarStore(dependencyDelegate);
    }

    private void setView(String bean) {
        try {
            ToolTagView.setTagView(root, bean, "%", (v, key) -> ((TextView)v).setText(key.equals("0")?"否":"是"));
            DTO<String, Object> formData = new DTO<>();
            formData = ToolData.gainForm(this.root, formData);
            if (formData.get("deliveryWarehouseCode").toString().isEmpty()&&formData.get("deliveryWarehouseName").toString().isEmpty()) {
                deliveryCode.setText(Html.fromHtml("<font color='#FF0000'>无</font>"));
                tvMsg.setText(Html.fromHtml("<font color='#FF0000'>商家不合签到规则，请先维护好该客户</font>"));
                yesOk.setVisibility(View.GONE);
            }else if (formData.get("scPurchaseFlag%").toString().equals("是")){
                tvMsg.setText(Html.fromHtml("<font color='#FF0000'>该商家已签到</font>"));
                yesOk.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            ToastUtil.error("数据异常");
            finish();
        }
    }
}
