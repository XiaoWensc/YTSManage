package asuper.yt.cn.supermarket.utils;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zengxiaowen on 2018/1/3.
 */

public class ToolViewGroup {

    public static View getNextView(View viewGroup){
        return getNextView(viewGroup,1);
    }

    public static View getNextView(View viewGroup, int ind){
        ViewGroup view = (ViewGroup)viewGroup.getParent();
        return view.getChildAt(view.indexOfChild(viewGroup)+ind);
    }


}
