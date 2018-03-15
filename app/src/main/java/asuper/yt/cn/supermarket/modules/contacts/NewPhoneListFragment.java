package asuper.yt.cn.supermarket.modules.contacts;

import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.contacts.data.ColorSuggestion;
import asuper.yt.cn.supermarket.modules.contacts.data.DataHelper;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.utils.ToolLog;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/10/12.
 */

public class NewPhoneListFragment extends BaseFragment implements MainActivity.MainFragment, AppBarLayout.OnOffsetChangedListener {

    private RecyclerView contactList;
    private LinearLayoutManager layoutManager;
    private MyLetterListView letterView;
    private ContactAdapter adapter;
    private FloatingSearchView mSearchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 500;
    private AppBarLayout mAppBar;
    public static final int REQUEST_GET_PHONE_LIST = 0X10001;
    public static final int REQUEST_GET_SEARCH = 0X10002;
    private List<String> listString;  //放出手写字母
    private String mLastQuery = "";

    @Override
    protected int getContentId() {
        return R.layout.fragment_newphonelist;
    }

    @Override
    protected void findView(View root) {
        initView(root);
        setupSearchBar();

        HashMap<String, Object> map = new HashMap<>();
        map.put("pageNum", 1);
        map.put("companyId", Config.UserInfo.COMPANYID);
        map.put("pageSize", 500);
        dispatch(REQUEST_GET_PHONE_LIST, map);
    }

    @BindAction(REQUEST_GET_SEARCH)
    public void searchPhone(List<Phone> phones) {
        if (phones==null) return;
        final PinYin pinYin = new PinYin();
        DataHelper.init(phones);
        adapter.setmContactNames(phones);
        mSearchView.clearSearchFocus();
        if (phones.size()>0) {
            listString.clear();
            listString.add(pinYin.getStringPinYin(phones.get(0).getEmployeeName()).substring(0, 1).toUpperCase());
            for (int i = 1; i < phones.size(); i++) {
                String old = pinYin.getStringPinYin(phones.get(i - 1).getEmployeeName()).substring(0, 1).toUpperCase();
                String nw = pinYin.getStringPinYin(phones.get(i).getEmployeeName()).substring(0, 1).toUpperCase();
                String ordold = pinYin.check(old) ? old : "#";
                String ordnw = pinYin.check(nw) ? nw : "#";
                if (ordnw != ordold && !ordold.equals(ordnw)) {
                    listString.add(ordnw);
                }
            }
        }
        letterView.setListString(listString);
    }

    @BindAction(REQUEST_GET_PHONE_LIST)
    public void getPhones(List<Phone> phones) {
        final PinYin pinYin = new PinYin();
        DataHelper.init(phones);
        Collections.sort(phones, new Comparator<Phone>() {
            @Override
            public int compare(Phone o1, Phone o2) {
                String a = pinYin.getStringPinYin(o1.getEmployeeName());
                String b = pinYin.getStringPinYin(o2.getEmployeeName());

                int c1 = (a.charAt(0) + "").toUpperCase().hashCode();
                int c2 = (b.charAt(0) + "").toUpperCase().hashCode();

                boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
                boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母
                if (c1Flag && !c2Flag) {
                    return 1;
                } else if (!c1Flag && c2Flag) {
                    return -1;
                }

                return c1 - c2;
            }
        });
        listString.add(pinYin.getStringPinYin(phones.get(0).getEmployeeName()).substring(0, 1).toUpperCase());
        for (int i = 1; i < phones.size(); i++) {
            String old = pinYin.getStringPinYin(phones.get(i - 1).getEmployeeName()).substring(0, 1).toUpperCase();
            String nw = pinYin.getStringPinYin(phones.get(i).getEmployeeName()).substring(0, 1).toUpperCase();
            String ordold = pinYin.check(old) ? old : "#";
            String ordnw = pinYin.check(nw) ? nw : "#";
            if (ordnw != ordold && !ordold.equals(ordnw)) {
                listString.add(ordnw);
            }
        }
        letterView.setListString(listString);
        adapter.setmContactNames(phones);
    }

    private void initView(View root) {
        mSearchView = (FloatingSearchView) root.findViewById(R.id.floating_search_view);
        contactList = (RecyclerView) root.findViewById(R.id.contact_list);
        letterView = (MyLetterListView) root.findViewById(R.id.myexpand_list);
        mAppBar = (AppBarLayout) root.findViewById(R.id.appbar);

        mAppBar.addOnOffsetChangedListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        adapter = new ContactAdapter();
        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        contactList.setAdapter(adapter);

        listString = new ArrayList<>();
        letterView.setOnTouchingLetterChangedListener(new MyLetterListView.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(s), 0);
            }
        });
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new PhoneListStore(dependencyDelegate);
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSearchView.setTranslationY(verticalOffset);
    }


    private void setupSearchBar() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    mSearchView.showProgress();
                    DataHelper.findSuggestions(getActivity(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<ColorSuggestion> results) {
                                    if (results==null)return;
                                    mSearchView.swapSuggestions(results);
                                    mSearchView.hideProgress();
                                }
                            });
                }
            }
        });

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                if (item.getItemId() == R.id.action_search) {
                    mLastQuery = mSearchView.getQuery();
                    secarch(mLastQuery);
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                ColorSuggestion colorSuggestion = (ColorSuggestion) searchSuggestion;
                mLastQuery = colorSuggestion.getBody();
                secarch(mLastQuery);
            }

            @Override
            public void onSearchAction(String query) {
                mLastQuery = query;
                secarch(mLastQuery);
            }
        });

        //关闭
        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {

                //show suggestions when search bar gains focus (typically history suggestions)
                mSearchView.swapSuggestions(DataHelper.getHistory(getActivity(), 3));

            }

            @Override
            public void onFocusCleared() {

                //set the title of the bar so that when focus is returned a new query begins
                mSearchView.setSearchBarTitle(mLastQuery);

                //you can also set setSearchText(...) to make keep the query there when not focused and when focus returns
                //mSearchView.setSearchText(searchSuggestion.getBody());
            }
        });
    }

    private void secarch(String key) {
        Map<String, Object> map = new HashMap<>();
        map.put("key", key);
        dispatch(REQUEST_GET_SEARCH, map);
    }

}
