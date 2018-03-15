package asuper.yt.cn.supermarket.modules.coauditing;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.BaseFragment;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.modules.main.MainActivity;
import asuper.yt.cn.supermarket.utils.ToolOkHTTP;
import chanson.androidflux.Store;
import chanson.androidflux.StoreDependencyDelegate;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToolGson;

/**
 * Created by liaoqinsen on 2017/6/1 0001.
 */

public class CoauditingFragment extends BaseFragment implements MainActivity.MainFragment {

    private String parentId;
    private List<MainButtonSubVO> mainButtonSubVOs;

    private RecyclerView list;

    public static CoauditingFragment newInstance(String parentId){
        CoauditingFragment fragment = new CoauditingFragment();
        fragment.parentId = parentId;
        return fragment;
    }

    public void doBusiness() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("parentId",parentId);
        map.put("platform","android");
        map.put("userId", Config.UserInfo.USER_ID);
        ToolOkHTTP.post(Config.getURL(Config.URL.URL_MAIN_SUB_BUTTON), map, new ToolOkHTTP.OKHttpCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                    try {
                        mainButtonSubVOs = ToolGson.fromJson(response.optJSONArray("resultObject").toString(), new TypeToken<List<MainButtonSubVO>>() {
                        }.getType());
                        if(mainButtonSubVOs == null){
                            mainButtonSubVOs = new ArrayList<>();
                            ToastUtil.error("服务器返回参数有误");
                            return;
                        }
                    }catch (Exception e){
                        ToastUtil.error("服务器返回参数有误");
                        return;
                    }
                    list.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure() {

            }

        });
    }


    @Override
    protected int getContentId() {
        return R.layout.fragment_co_auditing;
    }

    @Override
    protected void findView(View view) {
        list = (RecyclerView) view.findViewById(R.id.co_auditing_list);
        list.setLayoutManager(new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false));
        list.setAdapter(new CoAuditingAdapter());
        list.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        doBusiness();
    }

    @Override
    protected Store bindStore(StoreDependencyDelegate dependencyDelegate) {
        return null;
    }

    @Override
    public BaseFragment getFragment() {
        return this;
    }

    @Override
    public void refresh() {

    }

    private class CoAuditingAdapter extends RecyclerView.Adapter<CoAuditingViewHolder>{
        @Override
        public CoAuditingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coauditing_list, null);
            CoAuditingViewHolder mainSubViewHolder = new CoAuditingViewHolder(content);
            return mainSubViewHolder;
        }

        @Override
        public void onBindViewHolder(CoAuditingViewHolder holder, int position) {
            MainButtonSubVO vo = mainButtonSubVOs.get(position);
            holder.name.setText(vo.title);
            holder.itemView.setTag(vo);
            switch (vo.id){
                case 16:
                    holder.icon.setImageResource(R.drawable.ic_coauditing_missions);
                    break;
                case 17:
                    holder.icon.setImageResource(R.drawable.ic_coauditing_missions_history);
                    break;
                case 18:
                    holder.icon.setImageResource(R.drawable.ic_coauditing_notify);
                    break;
                default:
                    holder.icon.setImageResource(R.drawable.ic_coauditing_missions);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return mainButtonSubVOs == null ? 0:mainButtonSubVOs.size();
        }
    }

    private class CoAuditingViewHolder extends  RecyclerView.ViewHolder{
        public TextView name;
        public ImageView icon;

        public CoAuditingViewHolder(View itemView) {
            super(itemView);
            this.name = (TextView) itemView.findViewById(R.id.main_screen_subitem_title);
            this.icon = (ImageView) itemView.findViewById(R.id.main_screen_subitem_icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getTag() == null || !(v.getTag() instanceof MainButtonSubVO)){
                        return;
                    }
                    MainButtonSubVO vo = (MainButtonSubVO) v.getTag();
                    if(!vo.enable){
                        ToastUtil.info(vo.message);
                        return;
                    }
                    EventBus.getDefault().postSticky(vo);
                    getOperation().addParameter("canAuditing","misson".equals(vo.action));
                    getOperation().addParameter("title",vo.title);
                    getOperation().forward(MissonsActivity.class);
                }
            });
        }
    }
}
