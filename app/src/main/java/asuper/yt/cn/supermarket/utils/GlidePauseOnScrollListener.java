package asuper.yt.cn.supermarket.utils;

import com.bumptech.glide.Glide;

import cn.finalteam.galleryfinal.PauseOnScrollListener;

/**
 * Created by ZengXw on 2017/3/22.
 */
public class GlidePauseOnScrollListener extends PauseOnScrollListener {

    public GlidePauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        super(pauseOnScroll, pauseOnFling);
    }

    @Override
    public void resume() {
        if(getActivity() == null) return;
        Glide.with(getActivity()).resumeRequests();
    }

    @Override
    public void pause() {
        if(getActivity() == null) return;
        Glide.with(getActivity()).pauseRequests();
    }
}
