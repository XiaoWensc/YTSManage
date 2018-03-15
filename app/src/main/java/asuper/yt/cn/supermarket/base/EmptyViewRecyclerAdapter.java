package asuper.yt.cn.supermarket.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import asuper.yt.cn.supermarket.R;

/**
 * Created by liaoqinsen on 2017/9/13 0013.
 */

public abstract class EmptyViewRecyclerAdapter extends RecyclerView.Adapter {

    private static final int EMPTY_VIEW = -2;


    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == EMPTY_VIEW){
            View view = LayoutInflater.from(YTApplication.get()).inflate(OnEmptyView()==0?R.layout.layout_empty_view:OnEmptyView(),parent,false);
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
        return GetItemCount()>0?GetItemCount():1;
    }

    @Override
    public final int getItemViewType(int position) {
        return GetItemCount()==0?EMPTY_VIEW:GetItemViewType(position);
    }


    public abstract int OnEmptyView();
    public abstract RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType);
    public abstract void OnBindViewHolder(RecyclerView.ViewHolder holder, int position);
    public abstract int GetItemCount();
    public int GetItemViewType(int position){
        return super.getItemViewType(position);
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
