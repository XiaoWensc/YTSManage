package asuper.yt.cn.supermarket.modules.index;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseAdapter;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.modules.plan.DropInActivity;
import asuper.yt.cn.supermarket.modules.plan.Plan;
import asuper.yt.cn.supermarket.modules.plan.PlanStore;
import asuper.yt.cn.supermarket.views.CustomDatePicker;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by zengxiaowen on 2017/9/7.
 */

public class IndexLvAdapter extends EmptyViewRecyclerAdapter {

    private Drawable drawable1;
    private Drawable drawable2;
    private CustomDatePicker customDatePicker;
    private TimeParker timeParker;
    private int id;
    private List<Plan.PlanItem> plans = new ArrayList<>();

    public IndexLvAdapter(Context context) {
        drawable1 = context.getResources().getDrawable(R.mipmap.ic_zuji);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        drawable2 = context.getResources().getDrawable(R.mipmap.ic_phone);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        initDatePicker(context);
    }

    private void initDatePicker(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

        customDatePicker = new CustomDatePicker(context, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                timeParker.hander(id, time);
            }
        }, now, "2100-1-1 00:00", true); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker.showSpecificTime(false); // 不显示时和分
        customDatePicker.setIsLoop(false); // 不允许循环滚动

    }

    @Override
    public int OnEmptyView() {
        return R.layout.adapter_index_nodate;
    }


    @Override
    public RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHodler(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_indexlv, parent, false));
    }

    public void addItem(List<Plan.PlanItem> plans) {
        this.plans.clear();
        this.plans.addAll(plans);
    }

    @Override
    public void OnBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHodler hodler = (ViewHodler) holder;
        Plan.PlanItem item = plans.get(position);
        hodler.itemView.setTag(item.getId());
        hodler.setDateView(item, position > 0 && item.getVisitType().equals((plans.get(position - 1)).getVisitType()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public int GetItemCount() {
        return plans.size();
    }

    private class ViewHodler extends RecyclerView.ViewHolder {
        private TextView tvType, tvDname, tvUse, tvDes;
        private Button btnHe;
        private View linear;

        public ViewHodler(View convertView) {
            super(convertView);
            btnHe = (Button) convertView.findViewById(R.id.btnHe);
            tvType = (TextView) convertView.findViewById(R.id.tvType);
            tvDname = (TextView) convertView.findViewById(R.id.tvDname);
            tvUse = (TextView) convertView.findViewById(R.id.tvUse);
            tvDes = (TextView) convertView.findViewById(R.id.tvDes);
            linear = convertView.findViewById(R.id.linear);
            btnHe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((boolean) v.getTag()) {
                        BaseActivity activity = (BaseActivity) itemView.getContext();
                        activity.getOperation().addParameter("id", (int)itemView.getTag());
                        activity.getOperation().forwardForResult(DropInActivity.class, PlanStore.REQUEST_GET_PLAN_UPDATE);
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        String now = sdf.format(new Date());
                        customDatePicker.show(now);
                        id = (int)itemView.getTag();
                    }
                }
            });
        }

        public void setDateView(Plan.PlanItem item, int ViewG) {
            if (item.getVisitType().equals("home_visit")) {
                tvType.setText("上门拜访");
                tvType.setCompoundDrawables(drawable1, null, null, null);
                btnHe.setBackgroundResource(R.drawable.bg_sld_1b82d1_rd_cn);
            } else if (item.getVisitType().equals("phone_visit")) {
                tvType.setText("电话拜访");
                tvType.setCompoundDrawables(drawable2, null, null, null);
                btnHe.setBackgroundResource(R.drawable.bg_sld_41d9aa_rd_cn);
            } else {
                tvType.setText("临时拜访");
            }

            if (item.getVisit() == 1) {
                btnHe.setText("已拜访");
                btnHe.setTextColor(Color.parseColor("#999999"));
                btnHe.setBackgroundColor(Color.TRANSPARENT);
                btnHe.setEnabled(false);
            } else {
                btnHe.setEnabled(true);
                btnHe.setTextColor(Color.parseColor("#ffffff"));
                String time = item.getCreateTime().split(" ")[0];
                try {
                    if (TimeCompare(time)) {
                        btnHe.setText("重新计划");
                        btnHe.setBackgroundResource(R.drawable.bg_sld_ff7372_rd_cn);
                        btnHe.setTag(false);
                    } else if (dengCompare(time)) {
                        btnHe.setText("开始拜访");
                        btnHe.setBackgroundResource(R.drawable.bg_sld_1b82d1_rd_cn);
                        btnHe.setTag(true);
                    } else if (dayCompare(time)) {
                        btnHe.setVisibility(View.GONE);
                    } else {
                        ToastUtil.error("警告");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    ToastUtil.error("数据格式错误！");
                }
            }

            if (item.getVisitType().equals("home_visit")) {
                btnHe.setBackgroundResource(R.drawable.bg_sld_1b82d1_rd_cn);
            } else if (item.getVisitType().equals("phone_visit")) {
                btnHe.setBackgroundResource(R.drawable.bg_sld_41d9aa_rd_cn);
            }
            /**
             *  Modified by dengjiang on 2017/10/09
             *  CRM-CRM-CRM-1530
             *  设置红色背景
             *  start
             */
            String time = item.getCreateTime().split(" ")[0];
            try {
                if (TimeCompare(time)) {
                    btnHe.setBackgroundResource(R.drawable.bg_sld_prmr_red_cn);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                ToastUtil.error("数据格式错误！");
            }
            /**
             *  Modified by dengjiang on 2017/10/09
             *  end
             */
            tvDname.setText(item.getShopName());
            tvUse.setText(item.getShoplegalPerson() + " | " + item.getPhoneNumber() + " | " + item.getShopAddress());
            tvDes.setText(item.getRemark()==null?"":("备注：" + item.getRemark()));
            tvType.setVisibility(ViewG);
            linear.setVisibility(ViewG);
        }
    }

    /**
     * 判断是否小于今天
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private boolean TimeCompare(String time) throws ParseException {
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        Date date = CurrentTime.parse(time);
        Date date2 = CurrentTime.parse(CurrentTime.format(new Date()));

        return date.getTime() < date2.getTime();
    }


    /**
     * 判断是否大于今天
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private boolean dayCompare(String time) throws ParseException {
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        Date date = CurrentTime.parse(time);
        Date date2 = CurrentTime.parse(CurrentTime.format(new Date()));
        return date.getTime() > date2.getTime();
    }

    /**
     * 判断是否大于今天
     *
     * @param time
     * @return
     * @throws ParseException
     */
    private boolean dengCompare(String time) throws ParseException {
        SimpleDateFormat CurrentTime = new SimpleDateFormat("yyyy-MM-dd");
        return time.equals(CurrentTime.format(new Date()));
    }

    public interface TimeParker {
        void hander(int id, String time);
    }

    public void setTimeParker(TimeParker timeParker) {
        this.timeParker = timeParker;
    }
}
