package asuper.yt.cn.supermarket.modules.myclient.visit;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.entities.ButtonInfos;
import asuper.yt.cn.supermarket.entities.ClientInfoDetail;
import asuper.yt.cn.supermarket.modules.common.SafetyLinearLayoutManager;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.modules.myclient.MyClientDetailActivity;
import asuper.yt.cn.supermarket.modules.myclient.SaveCompleteListener;
import asuper.yt.cn.supermarket.utils.ToolLog;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * 客户详情-拜访记录
 * Created by liaoqinsen on 2017/9/16 0016.
 */

public class VisitRecordsFragment extends BaseFragment implements MainActivity.MainFragment,MyClientDetailActivity.MyClientDetailFragment {


    RecyclerView recyclerView;
    SwipeToLoadLayout swipeToLoadLayout;
    private List<VisitRecord> visitRecords;

    public static final int REQUEST_GET_RECORDS = 0x0701;
    private ClientInfoDetail buttonInfos;

    public static VisitRecordsFragment newInstance(ClientInfoDetail buttonInfos){
        VisitRecordsFragment visitRecordsFragment = new VisitRecordsFragment();
        visitRecordsFragment.buttonInfos = buttonInfos;
        return visitRecordsFragment;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_visit_records;
    }

    @Override
    protected void findView(View root) {
        visitRecords = new ArrayList<>();
        recyclerView = (RecyclerView) root.findViewById(R.id.swipe_target);
        swipeToLoadLayout = (SwipeToLoadLayout) recyclerView.getParent();
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setRefreshEnabled(false);
        recyclerView.setLayoutManager(new SafetyLinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(-1,-2);
            }
        });
        recyclerView.setAdapter(new Adapter());
        Map<String,Object> data = new HashMap<>();
        data.put("intentionId",buttonInfos.getFranchiseeNumber());
        dispatch(REQUEST_GET_RECORDS,data);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new VisitRecordStore(dependencyDelegate);
    }

    @BindAction(REQUEST_GET_RECORDS)
    public void getRecordsResult(List<VisitRecord> visitRecordList){
        if(visitRecordList == null) return;
        visitRecords = visitRecordList;
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

        ToolLog.i("刷新记录");

        Map<String,Object> data = new HashMap<>();
        data.put("intentionId",buttonInfos.getFranchiseeNumber());
        dispatch(REQUEST_GET_RECORDS,data);
    }

    @Override
    public void commit() {

    }

    @Override
    public void save(SaveCompleteListener listener) {

    }


    @Override
    public void cancel() {

    }

    @Override
    public void edit() {

    }

    private class  Adapter extends RecyclerView.Adapter<ViewHolder>{

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.item_visit_records,null);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.setData(visitRecords.get(position));
        }

        @Override
        public int getItemCount() {
            return visitRecords.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder{

        private TextView date,title,content;
        private ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.item_visit_date);
            title = (TextView) itemView.findViewById(R.id.item_visit_type);
            content = (TextView) itemView.findViewById(R.id.item_visit_content);
            icon = (ImageView) itemView.findViewById(R.id.item_visit_icon);
        }

        public void setData(VisitRecord visitRecord){
            if(visitRecord.recordTime != null) visitRecord.recordTime = visitRecord.recordTime.substring(0,10);
            date.setText(visitRecord.recordTime);
            content.setVisibility(View.GONE);
            if(visitRecord.applyType != null && !visitRecord.applyType.trim().isEmpty()){
                if("revert".equals(visitRecord.applyType)){
                    title.setText("该客户已还原");
                }else if("giveupJoining".equals(visitRecord.applyType)){
                    title.setText("该客户放弃加盟");
                    content.setText(String.format("放弃理由:%1$s",visitRecord.giveUpReason));
                }
                icon.setImageResource(R.drawable.ic_visit_notify);
            }

            if(visitRecord.visit != null && !visitRecord.visit.trim().isEmpty()){
                if("0".equals(visitRecord.visit)){
//                    date.setText("");
                    title.setText(String.format("计划对该商家进行%1$s",visitRecord.visitName));
                    content.setVisibility(View.GONE);
                    icon.setImageResource(R.drawable.ic_visit_plan);
                }else{
                    title.setText(visitRecord.visitName);
                    content.setVisibility(View.VISIBLE);
                    content.setText(String.format("拜访记录:%1$s",visitRecord.visitContent==null?"":visitRecord.visitContent));
                    icon.setImageResource("home_visit".equals(visitRecord.visitType)?R.drawable.ic_visit_indoor:R.drawable.ic_visit_phone);
                }
            }
        }
    }
}
