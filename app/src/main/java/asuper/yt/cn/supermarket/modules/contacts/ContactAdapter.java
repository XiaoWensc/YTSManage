package asuper.yt.cn.supermarket.modules.contacts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import asuper.yt.cn.supermarket.R;
import asuper.yt.cn.supermarket.base.EmptyViewRecyclerAdapter;
import asuper.yt.cn.supermarket.utils.PinyinComparator;

/**
 * Created by zengxiaowen on 2017/10/12.
 */

public class ContactAdapter extends EmptyViewRecyclerAdapter {
    private List<Phone> mContactNames = new ArrayList<>(); // 联系人名称字符串数组
//    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<Contact> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List

    public enum ITEM_TYPE {
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public void setmContactNames(List<Phone> mContactNames) {
        this.mContactNames.clear();
        this.mContactNames.addAll(mContactNames);
        handleContact();
        notifyDataSetChanged();
    }

    private void handleContact() {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < mContactNames.size(); i++) {
            String pinyin = PinyinComparator.getPingYin(mContactNames.get(i).getEmployeeName());
            map.put(pinyin, mContactNames.get(i).getEmployeeName());
            mContactNames.get(i).setmName(pinyin);
        }
        Collections.sort(mContactNames, new Comparator<Phone>() {
            @Override
            public int compare(Phone o1, Phone o2) {
                int c1 = (o1.getmName().charAt(0) + "").toUpperCase().hashCode();
                int c2 = (o2.getmName().charAt(0) + "").toUpperCase().hashCode();

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

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactNames.size(); i++) {
            Phone phone = mContactNames.get(i);
            String name = phone.getmName();
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new Contact(character,phone.getEmployeePhonenumber(), ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new Contact("#",phone.getEmployeePhonenumber(), ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }

            resultList.add(new Contact(map.get(name),phone.getEmployeePhonenumber(), ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }
    }

    @Override
    public int GetItemViewType(int position) {
        return resultList.get(position).getmType();
    }

    @Override
    public int OnEmptyView() {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_character, parent, false));
        } else {
            return new ContactHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false));
        }
    }

    @Override
    public void OnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position).getmName());
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(resultList.get(position).getmName());
            ((ContactHolder) holder).mTextView1.setText(resultList.get(position).getmPhone());
            ((ContactHolder) holder).textHread.setText(resultList.get(position).getmName().substring(0,1));
        }
    }

    @Override
    public int GetItemCount() {
        return resultList == null ? 0 : resultList.size();
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.character);

        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView,mTextView1,textHread;

        ContactHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.contact_name);
            mTextView1 = (TextView) view.findViewById(R.id.contact_phone);
            textHread = (TextView) view.findViewById(R.id.textHread);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mTextView1.getText().toString()));
                    if (intent.resolveActivity(itemView.getContext().getPackageManager()) != null) {
                        itemView.getContext().startActivity(intent);
                    }
                }
            });
        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getmName().equals(character)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }
}
