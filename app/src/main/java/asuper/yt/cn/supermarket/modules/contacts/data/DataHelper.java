package asuper.yt.cn.supermarket.modules.contacts.data;

/**
 * Copyright (C) 2015 Ari C.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.text.TextUtils;
import android.widget.Filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asuper.yt.cn.supermarket.modules.contacts.Phone;
import asuper.yt.cn.supermarket.modules.contacts.PinYin;
import asuper.yt.cn.supermarket.modules.contacts.UnicodeGBK2Alpha;

public class DataHelper {

    private static List<ColorSuggestion> sColorSuggestions = new ArrayList<>();
    private static PinYin pinYin = new PinYin();

    public static void init(List<Phone> phones) {
        if (phones==null) return;
        sColorSuggestions.clear();
        for (int i=0;i<phones.size();i++){
            sColorSuggestions.add(new ColorSuggestion(phones.get(i).getEmployeeName()));
        }
    }

    public interface OnFindSuggestionsListener {
        void onResults(List<ColorSuggestion> results);
    }


    public static List<ColorSuggestion> getHistory(Context context, int count) {

        List<ColorSuggestion> suggestionList = new ArrayList<>();
        ColorSuggestion colorSuggestion;
        for (int i = 0; i < sColorSuggestions.size(); i++) {
            colorSuggestion = sColorSuggestions.get(i);
            colorSuggestion.setIsHistory(true);
            suggestionList.add(colorSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
    }

    public static void resetSuggestionsHistory() {
        for (ColorSuggestion colorSuggestion : sColorSuggestions) {
            colorSuggestion.setIsHistory(false);
        }
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                DataHelper.resetSuggestionsHistory();
                List<ColorSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {
                    List<ColorSuggestion> list = new ArrayList<>();
                    list.addAll(search(constraint.toString(),sColorSuggestions));
                    for (int i= 0;i<(list.size()>5?5:list.size());i++){
                        suggestionList.add(list.get(i));
                    }
                }

                FilterResults results = new FilterResults();
                Collections.sort(suggestionList, new Comparator<ColorSuggestion>() {
                    @Override
                    public int compare(ColorSuggestion lhs, ColorSuggestion rhs) {
                        return lhs.getIsHistory() ? -1 : 0;
                    }
                });
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null &&results!=null) {
                    listener.onResults((List<ColorSuggestion>) results.values);
                }
            }
        }.filter(query);

    }


    /**
     * 按号码-拼音搜索联系人
     *
     * @param str
     */
    private static ArrayList<ColorSuggestion> search(final String str,
                                    final List<ColorSuggestion> allContacts) {
        ArrayList<ColorSuggestion> contactList = new ArrayList<>();

        final String result = pinYin.getStringPinYin(str);
        for (ColorSuggestion contact : allContacts) {
            if (contains(contact, result)) {
                contactList.add(contact);
            }
        }

        return contactList;
    }

    /**
     * 根据拼音搜索
     *
     *            正则表达式
     *            拼音
     *            搜索条件是否大于6个字符
     * @return
     */
    private static boolean contains(ColorSuggestion contact, String search) {
        if (TextUtils.isEmpty(contact.getBody()) || TextUtils.isEmpty(search)) {
            return false;
        }

        boolean flag = false;

        // 简拼匹配,如果输入在字符串长度大于6就不按首字母匹配了
        if (search.length() < 6) {
            // String firstLetters = FirstLetterUtil.getFirstLetter(contact
            // .getName());
            // 获得首字母字符串
            String firstLetters = UnicodeGBK2Alpha
                    .getSimpleCharsOfString(contact.getBody());
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
                    .getBody()));
            flag = matcher2.find();
        }

        return flag;
    }
}