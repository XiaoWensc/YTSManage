package supermarket.cn.yt.asuper.ytlibrary.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 单位换算工具类
 * @author 曾晓文<br>
 * 	px  ：像素 <br>
	in  ：英寸<br>
	mm  ：毫米<br>
	pt  ：磅，1/72 英寸<br>
	dp  ：一个基于density的抽象单位，如果一个160dpi的屏幕，1dp=1px<br>
	dip ：等同于dp<br>
	sp  ：同dp相似，但还会根据用户的字体大小偏好来缩放。<br>
	建议使用sp作为文本的单位，其它用dip<br>
	布局时尽量使用单位dip，少使用px <br>
 */
public class ToolUnit {


	/**获取系统显示材质***/
	public static DisplayMetrics getDisplayMetrics(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * sp转换px
	 * @param spValue sp数值
	 * @return px数值
	 */
	public static int spTopx(Context context,float spValue) {
        return (int) (spValue * getDisplayMetrics(context).scaledDensity + 0.5f);
    }

	/**
	 * px转换sp
	 * @param pxValue px数值
	 * @return sp数值
	 */
    public static int pxTosp(Context context,float pxValue) {
        return (int) (pxValue / getDisplayMetrics(context).scaledDensity + 0.5f);
    }

	/**
	 * dip转换px
	 * @param dipValue dip数值
	 * @return px数值
	 */
    public static int dipTopx(Context context,int dipValue) {
        return (int) (dipValue * getDisplayMetrics(context).density + 0.5f);
    }

	/**
	 * px转换dip
	 * @param pxValue px数值
	 * @return dip数值
	 */
    public static int pxTodip(Context context,float pxValue) {
        return (int) (pxValue / getDisplayMetrics(context).density + 0.5f);
    }
}
