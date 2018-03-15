package asuper.yt.cn.supermarket.base;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by zengxiaowen on 2017/10/30.
 */

public class SampleApplication extends TinkerApplication {
    public SampleApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "asuper.yt.cn.supermarket.base.YTApplication",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
