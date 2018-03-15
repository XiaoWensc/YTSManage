package asuper.yt.cn.supermarket.utils;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseActivity;

/**
 * ActionBar管理器
 *
 * @author zxw
 * @version 1.0
 */
public class ActionBarManager {


    /**
     * 设置居中标题
     *
     * @param mContext 上下文
     * @param title    主标题
     */
    public static Toolbar initTitleToolbar(final BaseActivity mContext, String title) {

        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        mContext.setSupportActionBar(toolbar);
        return toolbar;
    }


    /**
     * 订制一个返回+标题的标题栏
     *
     * @param mContext
     * @param title
     */
    public static Toolbar initBackToolbar(final BaseActivity mContext, String title) {
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setText(title);
        mContext.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);//设置Navigation 图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        return  toolbar;
    }


    /**
     * 订制一个返回
     *
     * @param mContext
     * @param title
     */
    public static Toolbar initBackNoTitleToolbar(final BaseActivity mContext, String title) {
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        TextView tvTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        tvTitle.setVisibility(View.GONE);
        mContext.setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);//设置Navigation 图标
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        return  toolbar;
    }


    /**
     * 更新ActionBar中间标题
     *
     * @param toolbar Toolbar
     * @param title   中间居中显示标题
     */
    public static void updateActionCenterTitle(Toolbar toolbar, String title) {
        toolbar.setTitle(title);//标题
//		mContext.setSupportActionBar(toolbar);
    }

    public enum TOOLBARBTN {
        TIJIAO, SEARCH, FAV, SETTINGS, SHARE, SUBMIT, ADD, EDIT,DEAL,PLAN,SAIX
    }


    public static void initActionBarSubmitButton(final AppCompatActivity mContext, TOOLBARBTN type) {
        Toolbar toolbar = (Toolbar) mContext.findViewById(R.id.toolbar);
    }

    /**
     * 初始化【提交】右侧按钮菜单
     *
     * @param mOptionsMenu
     */
    public static void initActionBarSubmitButton(Menu mOptionsMenu, TOOLBARBTN type) {
        final MenuItem aboutItem = mOptionsMenu.findItem(R.id.action_about);
        if (null != aboutItem) {
            aboutItem.setVisible(false);
        }

        final MenuItem searchItem = mOptionsMenu.findItem(R.id.action_search);
        if (null != searchItem) {
            searchItem.setVisible(false);
        }

        final MenuItem favItem = mOptionsMenu.findItem(R.id.action_fav);
        if (null != favItem) {
            favItem.setVisible(false);
        }

        final MenuItem settingItem = mOptionsMenu.findItem(R.id.action_settings);
        if (null != settingItem) {
            settingItem.setVisible(false);
        }

        final MenuItem shareItem = mOptionsMenu.findItem(R.id.action_share);
        if (null != shareItem) {
            shareItem.setVisible(false);
        }

        final MenuItem submitItem = mOptionsMenu.findItem(R.id.action_submit);
        if (null != submitItem) {
            submitItem.setVisible(false);
        }

        final MenuItem addItem = mOptionsMenu.findItem(R.id.action_add);
        if (null != addItem) {
            addItem.setVisible(false);
        }
        final MenuItem editItem = mOptionsMenu.findItem(R.id.action_edit);
        if (null != editItem) {
            editItem.setVisible(false);
        }

        final MenuItem dealItem = mOptionsMenu.findItem(R.id.action_deal);
        if (null != dealItem) {
            dealItem.setVisible(false);
        }
        final MenuItem planItem = mOptionsMenu.findItem(R.id.action_plan);
        if (null != planItem) {
            planItem.setVisible(false);
        }
        final MenuItem saixItem = mOptionsMenu.findItem(R.id.action_saix);
        if (null != saixItem) {
            saixItem.setVisible(false);
        }
        switch (type) {
            case TIJIAO:
                if (null != aboutItem) {
                    aboutItem.setVisible(true);
                }
                break;
            case SEARCH:
                if (null != searchItem) {
                    searchItem.setVisible(true);
                }
                break;
            case FAV:
                if (null != favItem) {
                    favItem.setVisible(true);
                }
                break;
            case SETTINGS:
                if (null != settingItem) {
                    settingItem.setVisible(true);
                }
                break;
            case SHARE:
                if (null != shareItem) {
                    shareItem.setVisible(true);
                }
                break;
            case SUBMIT:
                if (null != submitItem) {
                    submitItem.setVisible(true);
                }
                break;
            case ADD:
                if (null != addItem) {
                    addItem.setVisible(true);
                }
                break;
            case EDIT:
                if (null != editItem) {
                    editItem.setVisible(true);
                }
                break;
            case DEAL:
                if (null != dealItem) {
                    dealItem.setVisible(true);
                }
                break;
            case PLAN:
                if (null != planItem) {
                    planItem.setVisible(true);
                }
                break;
            case SAIX:
                if (null != saixItem) {
                    saixItem.setVisible(true);
                }
                break;
        }
    }
}
