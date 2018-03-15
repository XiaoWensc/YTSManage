package asuper.yt.cn.supermarket.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.math.BigDecimal;

/**
 * Created by Chanson on 2017/3/30.
 */

public class ToolInputFilter {

    public static final InputFilter PERSON_NAME = (source, start, end, dest, dstart, dend) -> {
        if (source != null && source.length() > 0 && Character.isDigit(source.charAt(0))) {
            return "";
        }
        return null;
    };

    public static InputFilter getEmojiFilter(){
        return (source, start, end, dest, dstart, dend) -> {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {
                    return "";
                }
            }
            return null;
        };
    }

    public static InputFilter createDecimalFilter(final double maxValue, final int maxDecimal, final int maxLenth) {
        return new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source == null) return null;
                if (dest.toString().length() >= maxLenth) return "";
                if (!source.equals(".") && dest.length() < 1) {
                    return null;
                }
                if (!source.equals("")) {
                    if (source.equals(".") && !dest.toString().contains(".")) {
                        if (dstart == 0) {
                            return "";
                        }
                        if (dest.length() - dstart > maxDecimal) {
                            return "";
                        }
                        if (new BigDecimal(dest.toString()).floatValue() >= maxValue) {
                            return "";
                        }
                        return null;
                    }
                }
                String s = dest.toString();
                String pre = s.substring(0, dstart);
                String ends = "";
                if (!source.equals("") && dstart < s.length()) {
                    ends = s.substring(dstart);
                } else if (source.equals("") && dstart < s.length() - 1) {
                    ends = s.substring(dstart + 1);
                }
                String target = pre + source + ends;
                if (!source.equals(".") && dest.toString().contains(".")) {
                    String[] temp = target.split("\\.");
                    if (temp.length > 1 && !temp[0].isEmpty() && temp[temp.length - 1].length() > maxDecimal) {
                        return "";
                    }
                }
                try {
                    BigDecimal bigDecimal = new BigDecimal(target);
                    if (bigDecimal.floatValue() > maxValue) {
                        if (source.equals("") && dest.charAt(dstart) == '.') {
                            return ".";
                        }
                        return "";
                    }
                    if (dest.length() > 0 && source.length() > 0 && Character.isDigit(source.charAt(0)) && dest.charAt(0) == '.') {
                        return null;
                    }
                    if (source.equals("") && dstart == 0 && dest.length() > 1 && dest.charAt(1) == '.') {
                        return dest.charAt(0) + "";
                    }
                } catch (Exception e) {
                    if (dest.length() > 0 && source.length() > 0 && Character.isDigit(source.charAt(0)) && dest.charAt(0) == '.') {
                        return null;
                    }
                    return "";
                }
                return null;
            }
        };
    }
}
