package asuper.yt.cn.supermarket.modules.dynamic;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;

/**
 * Created by zengxiaowen on 2017/10/31.
 */

public class NewDynamicFragment extends BaseFragment implements MainActivity.MainFragment {

    private RadioButton node,daib;
    private TextView nodeNum,daibNum;
    private ViewPager recycle_pager;
    private List<DynamicItemFragment> fragments =new ArrayList<>();
    private int[] toNot;

    public List<DynamicItemFragment> getFragments() {
        return fragments;
    }

    public void setToNot(int[] toNot) {
        this.toNot = toNot;
        if (this.toNot==null) return;
        setNodeNum(this.toNot[1]);
        setDaibNum(this.toNot[2]);
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_new_dynamic;
    }

    @Override
    protected void findView(View root) {
        initView(root);
    }

    private void initView(View root) {
        recycle_pager = (ViewPager) root.findViewById(R.id.recycle_pager);
        node = (RadioButton) root.findViewById(R.id.node_list);
        nodeNum = (TextView) root.findViewById(R.id.node_list_num);
        daib = (RadioButton) root.findViewById(R.id.daib_list);
        daibNum = (TextView) root.findViewById(R.id.daib_list_num);
        node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daib.setChecked(false);
                recycle_pager.setCurrentItem(0,true);
            }
        });
        daib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                node.setChecked(false);
                recycle_pager.setCurrentItem(1,true);
            }
        });
        recycle_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                node.setChecked(position==0?true:false);
                daib.setChecked(!node.isChecked());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        recycle_pager.setAdapter(new MainActivity.FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                DynamicItemFragment fragment = DynamicItemFragment.newInstance(position);
                fragment.setOnSetListSize(new DynamicItemFragment.OnSetListSize() {
                    @Override
                    public void onReferList() {
//                        getRusert();
                        ((MainActivity)getActivity()).getRusert();
                    }
                });
                fragments.add(fragment);
                return fragment;
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        setToNot(toNot);
    }

    private void setNodeNum(int size){
        if (size>99) {
            nodeNum.setVisibility(View.VISIBLE);
            nodeNum.setText("99+");
        }else if (size>0){
            nodeNum.setVisibility(View.VISIBLE);
            nodeNum.setText(size+"");
        }else{
            nodeNum.setVisibility(View.GONE);
        }
    }

    private void setDaibNum(int size){
        if (size>99) {
            daibNum.setVisibility(View.VISIBLE);
            daibNum.setText("99+");
        }else if (size>0){
            daibNum.setVisibility(View.VISIBLE);
            daibNum.setText(size+"");
        }else{
            daibNum.setVisibility(View.GONE);
        }
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return new DynamicStore(dependencyDelegate);
    }



    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }

}
