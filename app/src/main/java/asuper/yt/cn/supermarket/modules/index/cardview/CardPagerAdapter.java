package asuper.yt.cn.supermarket.modules.index.cardview;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.R;

/**
 * Created by zengxiaowen on 2017/7/17.
 */
public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<View> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Context mContext;

    private int mChildCount = 0;

    public CardPagerAdapter(Context context) {
        this.mContext = context;
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public void setCardItem(int i,CardItem item){
        mData.set(i,item);
    }

    public void clear() {
        mViews.clear();
        mData.clear();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }



    @Override
    public View getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public List<View> getCardView() {
        return mViews;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_card,container,false);
        container.addView(view);

        ((TextView)view.findViewById(R.id.title)).setText(mData.get(position).getTitle());

        View cardView =  view.findViewById(R.id.cardView);
        RelativeLayout linear = (RelativeLayout) view.findViewById(R.id.linear);
        linear.removeAllViews();
        linear.addView(new HomeDiagram(container.getContext(), mData.get(position).getText()));
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    @Override
    public void notifyDataSetChanged() {
        mChildCount = getCount();
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object)   {
        if ( mChildCount > 0) {
            mChildCount --;
            return POSITION_NONE;
        }
        return super.getItemPosition(object);
    }
}
