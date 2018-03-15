package asuper.yt.cn.supermarket.utils;

import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.entities.MerchantJoinSelectProperty;
import asuper.yt.cn.supermarket.modules.coauditing.AuditingMessageActivity;
import asuper.yt.cn.supermarket.views.CheckBox;
import asuper.yt.cn.supermarket.views.RadioButton;
import asuper.yt.cn.supermarket.views.RadioGroup;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;
import supermarket.cn.yt.asuper.ytlibrary.widgets.Option;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SingleSpinner;
import supermarket.cn.yt.asuper.ytlibrary.widgets.SpinnerAdapter;

import static asuper.yt.cn.supermarket.utils.ToolDateTime.DF_YYYY_MM_DD;

/**
 * Created by zengxiaowen on 2017/12/11.
 */

public class ToolTagView {


    public static void setTagView(ViewGroup group, String json) throws JSONException {
        setTagView(group, json, "%", null);
    }

    /**
     * 填充页面数据 !多字段拼接 %键值对
     *
     * @param group 页面主容器
     * @param json  数据
     * @throws JSONException
     */
    public static void setTagView(ViewGroup group, String json, String key, onRest rest) throws JSONException {
        JSONObject object = new JSONObject(json);
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof RadioGroup) {
                if (view.getTag() != null && !view.getTag().toString().isEmpty()) {
                    String to = object.optString(view.getTag().toString(), "");
                    if (!to.equals("null")) ((RadioGroup) view).setValue(to);
                }
            } else if (view instanceof ViewGroup) setTagView((ViewGroup) view, json, key, rest);
            else if (view instanceof CheckBox) {
                String cKey = ((CheckBox) view).getKey();
                if (cKey == null || cKey.isEmpty()) continue;
                String vaule = object.optString(cKey);
                ((CheckBox) view).setValue(vaule);
            } else if (view instanceof TextView) {
                if (view.getTag() != null) {
                    String tag = view.getTag().toString();
                    if (tag.equals("onClick")) continue;
                    if (tag.contains("!")) {  // 多字段拼接
                        ((TextView) view).setText("");
                        for (String t : tag.split("!")) {
                            String to = object.optString(t, "");
                            if (!to.equals("null"))
                                ((TextView) view).append(object.optString(t, "") + " ");
                        }
                    } else if (tag.contains(key)) {  // 键值对
                        if (rest != null) rest.onRest(view, object.optString(tag.split(key)[0]));
                    } else {
                        String to = object.optString(tag, "");
                        if (!to.equals("null"))
                            ((TextView) view).setText(object.optString(tag, ""));
                    }
                }
            }
        }
    }

    public static void setTagSpinner(ViewGroup group, String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof AppCompatSpinner) {
                String lists = object.optJSONObject("selectListData").optString(view.getTag().toString(), "");
                if (lists.isEmpty()) return;
                List<MerchantJoinSelectProperty> selecs = ToolGson.fromJson(lists, new TypeToken<List<MerchantJoinSelectProperty>>() {
                }.getType());
                if (selecs == null) return;
                List<Option> options = getOptions(selecs);
                SpinnerAdapter mSpinnerAdapter = new SpinnerAdapter(YTApplication.get(), R.layout.view_spinner_drop_list_hover, options);
                mSpinnerAdapter.setDropDownViewResource(R.layout.view_spinner_drop_list_ys);
                ((SingleSpinner) view).setAdapter(mSpinnerAdapter);

                ((SingleSpinner) view).setSelection(getIndexSelection(object.optString(((SingleSpinner) view).getKey(), ""), selecs) < 0 ? 0 : getIndexSelection(object.optString(((SingleSpinner) view).getKey(), ""), selecs), true);

            } else if (view instanceof ViewGroup) setTagSpinner((ViewGroup) view, json);
        }
    }

    /**
     * 获取选中位置
     *
     * @param index
     * @param listVO
     * @return
     */
    private static int getIndexSelection(String index, List<MerchantJoinSelectProperty> listVO) {
        List<Option> opt = getOptions(listVO);
        for (int i = 0; i < opt.size(); i++) {
            if (index != null && index.equals(opt.get(i).getLabel())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取Spinner 数据
     *
     * @param array
     * @return
     */
    private static List<Option> getOptions(List<MerchantJoinSelectProperty> array) {
        List<Option> data = new ArrayList<>();
        data.add(new Option("请选择", ""));
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                MerchantJoinSelectProperty property = array.get(i);
                data.add(new Option(property.getValue(), property.getKey()));
            }
        }
        return data;
    }

    public static void initViewDate(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if (view instanceof AppCompatSpinner) {
                SingleSpinner spinner = (SingleSpinner) view;
                if (spinner.getSelectedIndex()==0) spinner.setSelection(1);
            }else if (view instanceof ViewGroup) initViewDate((ViewGroup) view);
            else if (view instanceof EditText) {
                if (view.getTag() == null || view.getTag().toString().equals("onClick")) continue;
                String tag = view.getTag().toString();
                String string = ((EditText) view).getText().toString();
                if (string.isEmpty())
                    if (tag.equals("shopLegalIdcard"))
                        ((EditText) view).setText("513030199504028325");//实际经营者身份证
                    else ((EditText) view).setText("1");
                else if (string.equals("0")) ((EditText) view).setText("1");
            } else if (view instanceof TextView) {
                if (view.getTag() == null || view.getTag().toString().equals("onClick")) continue;
                String string = ((TextView) view).getText().toString();
                if (string.equals("请选择") || string.equals("起始时间") || string.equals("结束时间"))
                    ((TextView)view).setText(ToolDateTime.formatDateTime(new Date(), DF_YYYY_MM_DD));
            }
        }
    }


    public interface onRest {
        void onRest(View v, String key);
    }

}
