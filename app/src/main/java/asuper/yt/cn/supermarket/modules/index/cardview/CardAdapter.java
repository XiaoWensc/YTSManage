package asuper.yt.cn.supermarket.modules.index.cardview;

import android.view.View;

import java.util.List;

/**
 * CardAdapter
 * Created by zengxiaowen on 2017/7/17.
 */

public interface CardAdapter {
    int MAX_ELEVATION_FACTOR = 5;

    float getBaseElevation();

    View getCardViewAt(int position);

    List<View> getCardView();

    int getCount();
}
