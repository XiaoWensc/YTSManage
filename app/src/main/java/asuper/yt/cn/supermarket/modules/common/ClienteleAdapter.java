package asuper.yt.cn.supermarket.modules.common;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientListActivity;
import asuper.yt.cn.supermarket.modules.myclient.ToolGetButtoninfos;
import asuper.yt.cn.supermarket.modules.myclient.entities.Clit;

/**
 * Created by liaoqinsen on 2017/9/19 0019.
 */

public class ClienteleAdapter extends EmptyViewRecyclerAdapter {

    private List<Clit> clits = new ArrayList<>();
    private boolean isSelectable = false;
    private Clit selectVO;
    private CheckBox selectedCheckBox;
    private Bundle extraData;
    private BaseActivity baseActivity;

    public Clit getSelectVO(){
        return selectVO;
    }

    public ClienteleAdapter() {
        this(false);
    }

    public ClienteleAdapter(BaseActivity activity, Bundle bundle){
        this(false);
        extraData = bundle;
        this.baseActivity = activity;
    }

    public ClienteleAdapter(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    @Override
    public int OnEmptyView() {
        return 0;
    }

    @Override
    public ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(YTApplication.get()).inflate(R.layout.item_shop_card, null),extraData,baseActivity);
        viewHolder.onItemSelected = new ViewHolder.OnItemSelected() {
            @Override
            public void onSelected(Clit clit, CheckBox checkBox) {
                selectVO = clit;
                selectedCheckBox = checkBox;
                notifyDataSetChanged();
            }
        };
        return viewHolder;
    }

    @Override
    public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).setData(clits.get(position),clits.get(position).equals(selectVO));
    }

    @Override
    public int GetItemCount() {
        return clits.size();
    }

    public void addItems(List<Clit> clits) {
        this.clits.addAll(clits);
        notifyDataSetChanged();
    }

    public void clear() {
        clits.clear();
        notifyDataSetChanged();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, shopName, address, phone, date, status;
        private View dateLine;
        private CheckBox checkBox;
        private boolean isSelectable =false;
        public ViewHolder.OnItemSelected onItemSelected;
        private Bundle extraData;
        private BaseActivity baseActivity;
        public ViewHolder(View itemView) {
            this(itemView,false);
        }
        public ViewHolder(View itemView,Bundle extraData,BaseActivity activity) {
            this(itemView,false);
            this.extraData = extraData;
            this.baseActivity = activity;
        }
        public ViewHolder(View itemView,boolean selectable) {
            super(itemView);
            isSelectable = selectable;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Clit p = (Clit) v.getTag();
                    if(!isSelectable) {
                        EventBus.getDefault().postSticky(p);
                        if(baseActivity != null){
                            baseActivity.getOperation().addParameter("extra",extraData);
                            baseActivity.getOperation().forwardForResult(MyClientDetailActivity.class,100);
                        }else {
                            YTApplication.getOperation().addParameter("extra", extraData);
                            YTApplication.getOperation().forward(MyClientDetailActivity.class);
                        }
                    }else{
                        if(onItemSelected != null){
                            onItemSelected.onSelected(p,checkBox);
                        }
                    }

                }
            });
            name = (TextView) itemView.findViewById(R.id.shop_card_owner_name);
            shopName = (TextView) itemView.findViewById(R.id.shop_card_shop_name);
            address = (TextView) itemView.findViewById(R.id.shop_card_address);
            phone = (TextView) itemView.findViewById(R.id.shop_card_owner_phone);
            date = (TextView) itemView.findViewById(R.id.shop_card_date);
            status = (TextView) itemView.findViewById(R.id.shop_card_shop_status);
            dateLine = itemView.findViewById(R.id.shop_card_date_line);
            checkBox = (CheckBox) itemView.findViewById(R.id.shop_card_selected);
            checkBox.setEnabled(false);
            checkBox.setClickable(false);
            checkBox.setFocusable(false);
            if(isSelectable){
                checkBox.setVisibility(View.VISIBLE);
            }
        }

        public void setData(Clit clit,boolean isChecked) {
            itemView.setTag(clit);
            if(clit.getApplyStep() == null || clit.getApplyStep().isEmpty()){
                name.setText(clit.applyLegalperson);
                shopName.setText(clit.applyShopName);
                address.setText(clit.applyAddress);
                phone.setText(clit.applyPhoneNumber);
                date.setText("回收时间："+DateFormat.format("yyyy-MM-dd HH:mm:ss",new Date(clit.operationTime)));
                dateLine.setVisibility(View.GONE);
                status.setText(clit.getApplyStepName());
                checkBox.setChecked(isChecked);
                if(clit.getApplyStepName() == null || clit.getApplyStepName().trim().isEmpty()){
                    status.setVisibility(View.INVISIBLE);
                    return;
                }else{
                    status.setVisibility(View.VISIBLE);
                }
                int[] res = ToolGetButtoninfos.getIconAndBackground(clit.applySteup);
                status.setBackgroundResource(res[1]);
                Drawable drawable = shopName.getResources().getDrawable(res[0]);
                drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
                status.setCompoundDrawables(drawable,null,null,null);
                return;
            }
            name.setText(clit.getLegalpersonName());
            shopName.setText(clit.getShopName());
            address.setText(clit.getShopAddress());
            phone.setText(clit.getPhoneNumber());
            status.setText(clit.getApplyStepName());
            checkBox.setChecked(isChecked);

            if(clit.getApplyStepName() == null || clit.getApplyStepName().trim().isEmpty()){
                status.setVisibility(View.INVISIBLE);
                return;
            }else{
                status.setVisibility(View.VISIBLE);
            }
            int[] res = ToolGetButtoninfos.getIconAndBackground(clit.getApplyStep());
            status.setBackgroundResource(res[1]);
            Drawable drawable = shopName.getResources().getDrawable(res[0]);
            drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
            status.setCompoundDrawables(drawable,null,null,null);
        }

        private interface OnItemSelected{
            public void onSelected(Clit clit,CheckBox checkBox);
        }
    }
}
