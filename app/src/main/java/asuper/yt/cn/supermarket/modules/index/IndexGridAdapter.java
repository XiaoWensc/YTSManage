package asuper.yt.cn.supermarket.modules.index;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseAdapter;
import asuper.yt.cn.supermarket.utils.Lauar;
import asuper.yt.cn.supermarket.utils.ToolLunar;

/**
 * Created by zengxiaowen on 2017/9/25.
 */

public class IndexGridAdapter extends BaseAdapter {

    private int zm = Color.parseColor("#AAAAAA");
    private int gz = Color.parseColor("#333333");
    private int numDay ;
    private Calendar ca;
    private int clickTemp = -1; //标识选中
    private int activity = 0;

    private int toDay = -1; // 当天

    @Override
    public int getCount() {
        return 7;
    }

    public int getNumDay() {
        return numDay;
    }

    public void setNumDay(int numDay) {
        this.numDay = numDay;
    }

    public void setSeclection(int seclection) {
        this.clickTemp = seclection;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ca = getWeekStartTime(numDay);
        ca.add(Calendar.DAY_OF_MONTH, position);
        ViewHodler hodler = null;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar, parent, false);
            hodler.tvDay1 = (TextView) convertView.findViewById(R.id.tvDay1);
            hodler.tvDay2 = (TextView) convertView.findViewById(R.id.tvDay2);
            hodler.layout = (LinearLayout) convertView.findViewById(R.id.layout);
            hodler.viewGb = convertView.findViewById(R.id.viewGb);
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }
        int dayNum = getItem(position) == null ? 0 : (int) getItem(position);
        hodler.viewGb.setVisibility(dayNum > 0 ? View.VISIBLE : View.GONE);
        hodler.tvDay1.setText(getCurrentDay(position));
        String days = getChinaDay(position);
        hodler.tvDay2.setText(days.split("月")[1]);
        if (activity == 0) {
            if (position == 0 || position == 6) {
                hodler.tvDay1.setTextColor(zm);
                hodler.tvDay2.setTextColor(zm);
            } else {
                hodler.tvDay1.setTextColor(gz);
                hodler.tvDay2.setTextColor(gz);
            }
            if (clickTemp == position) {
                hodler.layout.setSelected(true);
                hodler.layout.setBackgroundResource(R.drawable.index_rili_day_click);
            } else {
                hodler.layout.setSelected(true);
                hodler.layout.setBackgroundColor(Color.TRANSPARENT);
            }

            //当天
            if (IsToday(getTimeDay(position))) {
                toDay = position;
                hodler.layout.setBackgroundResource(R.drawable.index_rili_day);
                hodler.tvDay1.setTextColor(Color.WHITE);
                hodler.tvDay2.setTextColor(Color.WHITE);
            }
        } else {
            hodler.tvDay1.setTextColor(Color.WHITE);
            hodler.tvDay2.setTextColor(Color.WHITE);

            if (clickTemp == position) {
                hodler.layout.setSelected(true);
                hodler.layout.setBackgroundResource(R.drawable.index_rili_day);
            } else {
                hodler.layout.setSelected(true);
                hodler.layout.setBackgroundColor(Color.TRANSPARENT);
            }

            //当天
            if (IsToday(getTimeDay(position))) {
                toDay = position;
                hodler.layout.setBackgroundResource(R.drawable.plan_rili_day);
                hodler.tvDay1.setTextColor(Color.parseColor("#1A80D0"));
                hodler.tvDay2.setTextColor(Color.parseColor("#1A80D0"));
            }
        }

        return convertView;
    }


    public void setActivity(int activity) {
        this.activity = activity;
    }

    private class ViewHodler {
        private TextView tvDay1, tvDay2;
        private LinearLayout layout;
        private View viewGb;
    }

    public String getTimeDay(int position) {
        ca = getWeekStartTime(numDay);
        ca.add(Calendar.DAY_OF_MONTH, position);
        return getCurrentYear(position) + "-" +  getCurrentMonth(position) + "-" + getCurrentDay(position);
    }

    public List<String> getAllDay() {
        ca = getWeekStartTime(numDay);
        List<String> days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            days.add(getCurrentYear(i) + "-" + getCurrentMonth(i) + "-" + getCurrentDay(i));
        }
        return days;
    }

    public String getCurrentYear(int position) {
        ca = getWeekStartTime(numDay);
        ca.add(Calendar.DAY_OF_MONTH, position);
        return ca.get(Calendar.YEAR) + "";
    }

    public String getCurrentMonth(int position) {
        ca = getWeekStartTime(numDay);
        ca.add(Calendar.DAY_OF_MONTH, position);
        return getMonth(ca.get(Calendar.MONTH) + 1);
    }

    public String getCurrentDay(int position) {
        ca = getWeekStartTime(numDay);
        ca.add(Calendar.DAY_OF_MONTH, position);
        return getMonth(ca.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * start
     * 本周开始时间戳
     */
    private Calendar getWeekStartTime(int numDay) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, numDay);
        // 获取星期日开始时间戳
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param day 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    private boolean IsToday(String day){
        try {
            Calendar pre = Calendar.getInstance();
            Date predate = new Date(System.currentTimeMillis());
            pre.setTime(predate);
            Calendar cal = Calendar.getInstance();
            Date date = getDateFormat().parse(day);
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
                int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);
                if (diffDay == 0) {
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }


    private ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<>();

    private String getMonth(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return month + "";
        }
    }

    /**
     * 获取选中的日期（农历）
     */
    public String getChinaDay(int position) {
        return Lauar.getLunar(getCurrentYear(position), getCurrentMonth(position), getCurrentDay(position));
    }


    /**
     * 获取今天的日期（农历）
     */
    public String getChinaDay() {
        return getChinaDay(clickTemp);
    }

    public int getTodayPosition(){
        return clickTemp;
    }

    public int getToDay() {
        return toDay;
    }
}
