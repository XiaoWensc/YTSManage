package asuper.yt.cn.supermarket.modules.contacts;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.utils.ToolCacheUtil;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/9/6 0006.
 */

public class PhoneListStore extends Store {

    private ArrayList<Phone> phones;
    private PinYin pinYin = new PinYin();
    private BaseActivity baseActivity;
    private final String phoneTxt = "/phone"+Config.UserInfo.USER_ID+".txt";

    public PhoneListStore(StoreDependencyDelegate delegate) {
        super(delegate);
        if(delegate.getReducer() instanceof BaseActivity) {
            baseActivity = (BaseActivity) delegate.getReducer();
        }else if(delegate.getReducer() instanceof BaseFragment){
            baseActivity = (BaseActivity) ((BaseFragment) delegate.getReducer()).getActivity();
        }
    }

    @Override
    public void doAction(final int type, final HashMap<String, Object> data, final StoreResultCallBack callBack) {
        switch (type){
            case PhoneListFragment.REQUEST_GET_PHONE_LIST:
                String dateStr = ToolCacheUtil.getUrlCache(baseActivity.getCacheDir()+phoneTxt, ToolCacheUtil.ConfigCacheModel.CONFIG_CACHE_MODEL_ML);
                if (dateStr==null||dateStr.isEmpty()) {
                    ToolOkHTTP.post(Config.getURL("app/sys/communicationList.htm"), data, new ToolOkHTTP.OKHttpCallBack() {

                        @Override
                        public void onSuccess(JSONObject response) {
                            if (response == null) return;
                            if (response.optInt("pageNum") == 1) {
                                phones = ToolGson.fromJson(response.optString("resultObject"), new TypeToken<List<Phone>>() {
                                }.getType());
                            } else {
                                List<Phone> phones = ToolGson.fromJson(response.optString("resultObject"), new TypeToken<List<Phone>>() {
                                }.getType());
                                phones.addAll(phones);
                            }
                            ToolCacheUtil.setUrlCache(response.optString("resultObject"),baseActivity.getCacheDir()+ phoneTxt);
                            callBack.onResult(type, phones);
                        }

                        @Override
                        public void onFailure() {
                            callBack.onResult(type, null);
                        }

                    });
                }else{
                    callBack.onResult(type, gosnPhone(dateStr));
                }
                break;
            case PhoneListFragment.REQUEST_GET_SEARCH:
                String s = data.get("key").toString();
                List<Phone> ss = search(s.toString(), phones);
                callBack.onResult(type,ss);
                break;
        }
    }


    private ArrayList<Phone> gosnPhone(String json){
        phones = ToolGson.fromJson(json, new TypeToken<List<Phone>>() {
        }.getType());
        return phones;
    }

    /**
     * 按号码-拼音搜索联系人
     *
     * @param str
     */
    private ArrayList<Phone> search(final String str,
                                    final ArrayList<Phone> allContacts) {
        if (str==null||str.isEmpty()) return allContacts;
        ArrayList<Phone> contactList = new ArrayList<Phone>();
        // 如果搜索条件以0 1 +开头则按号码搜索
        if (str.startsWith("0") || str.startsWith("1") || str.startsWith("+")) {
            for (Phone contact : allContacts) {
                if (contact.getEmployeePhonenumber() != null && contact.getEmployeeName() != null) {
                    if (contact.getEmployeePhonenumber().contains(str)
                            || contact.getEmployeeName().contains(str)) {
//                        contact.setGroup(str);
                        contactList.add(contact);
                    }
                }
            }
            return contactList;
        }

        // final ChineseSpelling finder = ChineseSpelling.getInstance();
        // finder.setResource(str);
        // final String result = finder.getSpelling();
        // 先将输入的字符串转换为拼音
        // final String result = PinYinUtil.getFullSpell(str);
        final String result = pinYin.getStringPinYin(str);
        for (Phone contact : allContacts) {
            if (contains(contact, result)) {
                contactList.add(contact);
            }
        }

        return contactList;
    }

    /**
     * 根据拼音搜索
     * <p>
     * 正则表达式
     * 拼音
     * 搜索条件是否大于6个字符
     *
     * @return
     */
    private boolean contains(Phone contact, String search) {
        if (TextUtils.isEmpty(contact.getEmployeeName()) || TextUtils.isEmpty(search)) {
            return false;
        }

        boolean flag = false;

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            // String firstLetters = FirstLetterUtil.getFirstLetter(contact
            // .getName());
            // 获得首字母字符串
            String firstLetters = UnicodeGBK2Alpha
                    .getSimpleCharsOfString(contact.getEmployeeName());
            // String firstLetters =
            // PinYinUtil.getFirstSpell(contact.getName());
            // 不区分大小写
            Pattern firstLetterMatcher = Pattern.compile("^" + search,
                    Pattern.CASE_INSENSITIVE);
            flag = firstLetterMatcher.matcher(firstLetters).find();
        }

        if (!flag) { // 如果简拼已经找到了，就不使用全拼了
            // 全拼匹配
            // ChineseSpelling finder = ChineseSpelling.getInstance();
            // finder.setResource(contact.getName());
            // 不区分大小写
            Pattern pattern2 = Pattern
                    .compile(search, Pattern.CASE_INSENSITIVE);
            Matcher matcher2 = pattern2.matcher(pinYin.getStringPinYin(contact
                    .getEmployeeName()));
            flag = matcher2.find();
        }

        return flag;
    }
}
