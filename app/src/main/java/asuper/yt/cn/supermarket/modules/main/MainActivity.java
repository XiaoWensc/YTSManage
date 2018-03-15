package asuper.yt.cn.supermarket.modules.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.contacts.NewPhoneListFragment;
import asuper.yt.cn.supermarket.modules.coauditing.CoauditingFragment;
import asuper.yt.cn.supermarket.modules.dynamic.DynamicFragment;
import asuper.yt.cn.supermarket.modules.dynamic.DynamicStore;
import asuper.yt.cn.supermarket.modules.dynamic.NewDynamicFragment;
import asuper.yt.cn.supermarket.modules.index.IndexFragment;
import asuper.yt.cn.supermarket.modules.myprofile.MyProfileFragment;
import asuper.yt.cn.supermarket.modules.newclient.AddNewClientActivity;
import asuper.yt.cn.supermarket.modules.operate.OperateFragment;
import asuper.yt.cn.supermarket.modules.plan.PlanStore;
import asuper.yt.cn.supermarket.views.NoScrollViewPager;
import chanson.androidflux.BindAction;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import cn.bingoogolapple.badgeview.BGABadgeRadioButton;
import cn.bingoogolapple.badgeview.BGABadgeViewHelper;
import me.leolin.shortcutbadger.ShortcutBadger;
import supermarket.cn.yt.asuper.ytlibrary.utils.Common;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolAlert;
import supermarket.cn.yt.asuper.ytlibrary.widgets.RectXView;


/**
 * Created by Chanson on 2017/3/23.
 */

public class MainActivity extends BaseActivity {

    private RadioGroup main_radioGroup;
    public static final int REQUEST_GET_MAIN_BUTTON = 0x1001;
    private List<MainActivity.MainFragment> fragments = new ArrayList<>();
    private List<BGABadgeRadioButton> bgaBadgeRadioButtons = new ArrayList<>();
    private View imageButton;
    private NoScrollViewPager viewPager;

    @Override
    protected int getContentId() {
        return R.layout.activity_newmain;
    }

    @Override
    protected void findView(View root) {
        showProgress();
        initView();
        HashMap<String, Object> map = new HashMap<>();
        map.put("platform", "android");
        map.put("userId", Config.UserInfo.USER_ID);
        dispatch(REQUEST_GET_MAIN_BUTTON, map);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (!Common.isNotificationEnabled(getContext()))
                ToolAlert.dialog(getContext(), "警告", "检测到您的系统通知权限未打开\n将影响正常使用，立即前往设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }


    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new MainStore(dependencyDelegate);
    }


    private void initView() {
        viewPager = (NoScrollViewPager) findViewById(R.id.frame_root);
        main_radioGroup = (RadioGroup) findViewById(R.id.main_radioGroup);
        main_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (findViewById(checkedId).getTag().toString()) {
                    case "index":
                    case "co_auditing":
                        viewPager.setCurrentItem(0);
                        break;
                    case "dynamic":
                    case "myexpand":
                        viewPager.setCurrentItem(1);
                        break;
                    case "contact":
                        viewPager.setCurrentItem(2);
                        break;
                    case "my":
                        viewPager.setCurrentItem(3);
                        break;
                }
            }
        });
        (imageButton = findViewById(R.id.imageButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOperation().forward(AddNewClientActivity.class);
            }
        });
        viewPager.setNoScroll(true);
        viewPager.setAdapter(new MainActivity.MainPagerAdapter(fragments, this));
        viewPager.setOffscreenPageLimit(4);
    }

    public void getRusert() {
        dispatch(MainStore.REQUEST_GET_DYNAMIC_LISTSIZE, null);
    }

    @BindAction(MainStore.REQUEST_GET_DYNAMIC_LISTSIZE)
    public void GetListSize(int[] toNot) {
        dismissProgress();
        if (toNot == null) return;
        bgaBadgeRadioButtons.get(1).showTextBadge(toNot[0] + "");
        ShortcutBadger.applyCount(getContext(), toNot[0]);
        ((NewDynamicFragment) fragments.get(1).getFragment()).setToNot(toNot);
    }

    private boolean isShowAdd = false;

    @BindAction(REQUEST_GET_MAIN_BUTTON)
    public void initMainButton(JSONObject response) {
        JSONArray array = response.optJSONArray("resultObject");
        if (array == null) {
            dismissProgress();
            return;}
        for (int i = 0; i < array.length(); i++) {
            BGABadgeRadioButton bgaBadgeRadioButton = (BGABadgeRadioButton) LayoutInflater.from(getContext()).inflate(R.layout.radio_main_bottom, null);
            JSONObject json = array.optJSONObject(i);
            bgaBadgeRadioButton.setEnabled(json.optBoolean("enable", false));
            bgaBadgeRadioButton.setText(bgaBadgeRadioButton.isEnabled() ? json.optString("title", "") : "");
            String action = json.optString("action");
            Drawable drawable = null;
            switch (action) {
                case "index":
                    drawable = getResources().getDrawable(R.drawable.ic_main_book);
                    if (json.optInt("ownerType",0)==4) fragments.add(new OperateFragment());
                    else fragments.add(new IndexFragment());
                    break;
                case "dynamic":
                    getRusert();
                    isShowAdd = true;
                    drawable = getResources().getDrawable(R.drawable.ic_main_coauditing);
                    fragments.add(new NewDynamicFragment());
                    break;
                case "new":
                    drawable = getResources().getDrawable(R.drawable.ic_main_book);
                    break;
                case "contact":
                    drawable = getResources().getDrawable(R.drawable.ic_main_performance);
                    fragments.add(new NewPhoneListFragment());
                    break;
                case "my":
                case "myexpand":
                    drawable = getResources().getDrawable(R.drawable.ic_main_my);
                    bgaBadgeRadioButton.setEnabled(true);
                    bgaBadgeRadioButton.setText("我的");
                    fragments.add(new MyProfileFragment());
                    break;
                case "co_auditing":
                    drawable = getResources().getDrawable(R.drawable.ic_main_coauditing);
                    fragments.add(CoauditingFragment.newInstance(json.optString("id")));
                    break;
                case "contactbook":
                    continue;
                default:
                    drawable = getResources().getDrawable(R.drawable.ic_main_book);
                    break;
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            bgaBadgeRadioButton.setCompoundDrawables(null, drawable, null, null);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            lp.setMargins(0, 10, 0, 10);
            bgaBadgeRadioButton.setLayoutParams(lp);
            bgaBadgeRadioButton.setId(json.optInt("id",0));
            bgaBadgeRadioButton.setTag(action);
            bgaBadgeRadioButton.getBadgeViewHelper().setBadgeGravity(BGABadgeViewHelper.BadgeGravity.RightTop);
//            if (i==0) bgaBadgeRadioButton.setChecked(true);
            bgaBadgeRadioButtons.add(bgaBadgeRadioButton);
            imageButton.setVisibility(isShowAdd ? View.VISIBLE : View.GONE);
            main_radioGroup.addView(bgaBadgeRadioButton);
            if (!isShowAdd) dismissProgress();
        }
        viewPager.getAdapter().notifyDataSetChanged();
        bgaBadgeRadioButtons.get(0).setChecked(true);
//        viewPager.setCurrentItem();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PlanStore.REQUEST_GET_PLAN_UPDATE:
                if (fragments.isEmpty()) return;
                if (fragments.get(0) != null)
                    fragments.get(0).refresh();
                break;
            case DynamicStore.REQUEST_GET_DYNAMIC_ADD:  //通知
                if (resultCode == DynamicStore.REQUEST_GET_DYNAMIC_ADD && fragments.get(1) != null) {
                    if (fragments.isEmpty()) return;
                    ((NewDynamicFragment) fragments.get(1)).getFragments().get(0).updateAll(1);
                    getRusert();
                }
                break;
            case DynamicStore.REQUEST_GET_DYNAMIC_ADD + 1:  //待办
                if (resultCode == DynamicStore.REQUEST_GET_DYNAMIC_ADD && fragments.get(1) != null) {
                    if (fragments.isEmpty()) return;
                    ((NewDynamicFragment) fragments.get(1)).getFragments().get(1).updateAll(1);
                    getRusert();
                }
                break;
        }
    }

    public static class MainPagerAdapter extends FragmentPagerAdapter {

        private List<MainFragment> mainFragments;

        public MainPagerAdapter(List<MainFragment> fragments, FragmentActivity activity) {
            super(activity.getSupportFragmentManager());
            mainFragments = fragments;
        }

        @Override
        public int getCount() {
            return mainFragments == null ? 0 : mainFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mainFragments.get(position).getFragment();
        }
    }

    public interface MainFragment {
        BaseFragment getFragment();

        void refresh();
    }


    public static abstract class FragmentPagerAdapter extends PagerAdapter {
        private static final String TAG = "FragmentPagerAdapter";
        private static final boolean DEBUG = false;

        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private Fragment mCurrentPrimaryItem = null;

        public FragmentPagerAdapter(FragmentManager fm) {
            mFragmentManager = fm;
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        public abstract Fragment getItem(int position);

        @Override
        public void startUpdate(ViewGroup container) {
            if (container.getId() == View.NO_ID) {
                throw new IllegalStateException("ViewPager with adapter " + this
                        + " requires a view id");
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            final long itemId = getItemId(position);

            // Do we already have this fragment?
            String name = makeFragmentName(container.getId(), itemId);
            Fragment fragment = mFragmentManager.findFragmentByTag(name);
            if (fragment != null) {
                if (DEBUG) Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
                if (!fragment.isAdded()) {
                    mCurTransaction.attach(fragment);
                }
            } else {
                fragment = getItem(position);
                if (DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
                mCurTransaction.add(container.getId(), fragment,
                        makeFragmentName(container.getId(), itemId));
            }
            if (fragment != mCurrentPrimaryItem) {
                fragment.setMenuVisibility(false);
                fragment.setUserVisibleHint(false);
            }

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            if (DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                    + " v=" + ((Fragment) object).getView());
            mCurTransaction.detach((Fragment) object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment) object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try {
                if (mCurTransaction != null) {
                    mCurTransaction.commitAllowingStateLoss();
                    mCurTransaction = null;
                }
            } catch (Exception e) {
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        /**
         * Return a unique identifier for the item at the given position.
         * <p>
         * <p>The default implementation returns the given position.
         * Subclasses should override this method if the positions of items can change.</p>
         *
         * @param position Position within this adapter
         * @return Unique identifier for the item at position
         */
        public long getItemId(int position) {
            return position;
        }

        private String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }

    @Override
    protected boolean enableSliding() {
        return false;
    }
}
