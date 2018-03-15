package asuper.yt.cn.supermarket.modules.contacts;

import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import asuper.yt.cn.supermarket.utils.ToolLog;

/**
 * 通讯录
 * Created by zengxiaowen on 2017/4/26 0026.
 */

public class PhoneListFragment extends BaseFragment implements MainActivity.MainFragment {

    private ListView list_view;

    private MyLetterListView letterListView; // A-Z listview

    private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置

    private List<String> listString;  //放出手写字母

    private ClearEditText query;

    private PhoneBaseAdapter adapter;

    private List<Phone> phones = new ArrayList<>();

    private PinYin pinYin = new PinYin();

    public static PhoneListFragment newInstance() {
        return new PhoneListFragment();
    }


    private boolean isScroll = false;

    @Override
    protected int getContentId() {
        return R.layout.activity_phone;
    }

    public static final int REQUEST_GET_PHONE_LIST = 0X10001;
    public static final int REQUEST_GET_SEARCH = 0X10002;

    @Override
    protected void findView(View root) {
        list_view = (ListView) root.findViewById(R.id.list_view);
        query = (ClearEditText) root.findViewById(R.id.query);
        alphaIndexer = new HashMap<>();
        listString = new ArrayList<>();
        letterListView = (MyLetterListView) root.findViewById(R.id.myexpand_list);
        adapter = new PhoneBaseAdapter();
        list_view.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
                    isScroll = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isScroll) {
                    return;
                }
            }
        });


        query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本变化之前
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //文本变化中
            }

            @Override
            public void afterTextChanged(Editable s) {

                //文本变化之后
                if (s!=null&&!"".equals(s.toString())){
                    List<Phone> ss =  search(s.toString(),phones);
                    adapter.setPhones(ss);
                    adapter.notifyDataSetChanged();
                    letterListView.setListString(listString);
                }else{
                    adapter.setPhones(phones);
                    adapter.notifyDataSetChanged();
                    letterListView.setListString(listString);
                }
            }
        });

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageNum", 1);
        map.put("companyId", Config.UserInfo.COMPANYID);
        map.put("pageSize", 500);
        dispatch(REQUEST_GET_PHONE_LIST, map);
    }

    @BindAction(REQUEST_GET_PHONE_LIST)
    public void getPhones(List<Phone> phones) {
        this.phones.clear();
        this.phones.addAll(phones);
        adapter.setPhones(phones);
        list_view.setAdapter(adapter);
        letterListView.setListString(listString);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

    }

    @BindAction(REQUEST_GET_SEARCH)
    public void searchResult(List<Phone> phones){
            adapter.setPhones(phones);
            adapter.notifyDataSetChanged();
            letterListView.setListString(listString);
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PhoneListStore(dependencyDelegate);
    }

    private class LetterListViewListener implements MyLetterListView.OnTouchingLetterChangedListener {

        @Override
        public void onTouchingLetterChanged(final String s) {
            isScroll = false;
            if (alphaIndexer.get(s) != null) {
                int position = alphaIndexer.get(s);
                list_view.setSelection(position);
            }
        }
    }


    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }

    /**
     * a-z排序
     */
    @SuppressWarnings("rawtypes")
    Comparator comparator = new Comparator<Phone>() {
        @Override
        public int compare(Phone lhs, Phone rhs) {

            String a = pinYin.getStringPinYin(lhs.getEmployeeName());
            String b = pinYin.getStringPinYin(rhs.getEmployeeName());
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }
        }
    };

    public class PhoneBaseAdapter extends BaseAdapter {

        private List<Phone> Phones;

        public void setPhones(List<Phone> phones) {
            Phones = phones;
            alphaIndexer = new HashMap<>();
            listString = new ArrayList<>();
            if (phones != null) {
                Collections.sort(Phones, comparator);
                for (int i = 0; i < Phones.size(); i++) {
                    String old = (i - 1) < 0 ? "" : pinYin.getStringPinYin(Phones.get(i - 1).getEmployeeName()).substring(0, 1);
                    String nw = pinYin.getStringPinYin(Phones.get(i).getEmployeeName()).substring(0, 1);
                    if (nw != old && !old.equals(nw)) {
                        alphaIndexer.put(nw, i);
                        listString.add(nw);
                    }
                }

            }
        }

        @Override
        public int getCount() {
            return Phones == null ? 0 : Phones.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder = null;
            if (convertView == null) {
                holder = new MyViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_phone_item, null);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.phone = (TextView) convertView.findViewById(R.id.phone);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            final Phone phone = Phones.get(position);
            if (position % 2 == 0) {
                convertView.setBackgroundColor(getContext().getResources().getColor(R.color.white));
            } else {
                convertView.setBackgroundColor(getContext().getResources().getColor(R.color.gray_white));
            }
            holder.name.setText(phone.getEmployeeName());
            holder.phone.setText(phone.getEmployeePhonenumber());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phone.getEmployeePhonenumber()));
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }


    }

    private class MyViewHolder {
        TextView name, phone;
    }


    /**
     * 按号码-拼音搜索联系人
     *
     * @param str
     */
    private ArrayList<Phone> search(final String str,
                                    final List<Phone> allContacts) {
        ArrayList<Phone> contactList = new ArrayList<>();
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
     *
     *            正则表达式
     *            拼音
     *            搜索条件是否大于6个字符
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
