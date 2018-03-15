package asuper.yt.cn.supermarket.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import asuper.yt.cn.supermarket.R;

/**
 * Created by zengxiaowen on 2017/10/23.
 */

public abstract class BaseRecyclerAdapter extends RecyclerView.Adapter implements BaseRecyler{

    private static final int EMPTY_VIEW = -2;
    private List<Object> mDateList = new ArrayList<>();

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EMPTY_VIEW){
            View view = LayoutInflater.from(YTApplication.get()).inflate(OnEmptyView()==0? R.layout.layout_empty_view:OnEmptyView(),parent,false);
            view.setLayoutParams(new RecyclerView.LayoutParams(-1,-1));
            return new EmptyViewHolder(view);
        }
        return OnCreateViewHolder(parent,viewType);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(!(holder instanceof EmptyViewHolder)){
            OnBindViewHolder(holder,position);
        }
    }

    @Override
    public final int getItemCount() {
        return mDateList.size()>0?mDateList.size():1;
    }

    @Override
    public final int getItemViewType(int position) {
        return mDateList.size()==0?EMPTY_VIEW:GetItemViewType(position);
    }


    public abstract int OnEmptyView();
    public abstract RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);
    public abstract void OnBindViewHolder(RecyclerView.ViewHolder holder, int position);
//    public abstract int GetItemCount();
    public int GetItemViewType(int position){
        return super.getItemViewType(position);
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void addItemDates(Collection<? extends Object> collection) {
        if (collection!=null&&collection.size()>0){
            mDateList.addAll(collection);
        }
    }

    @Override
    public void clear() {
        mDateList.clear();
    }

    @Override
    public List<Object> getAllList() {
        return mDateList;
    }
}
