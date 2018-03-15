package asuper.yt.cn.supermarket.modules.contacts;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by zengxiaowen on 2017/6/8.
 */

public class PinYin {
    private HanyuPinyinOutputFormat format = null;
    private String[] pinyin;

    public PinYin() {
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinyin = null;
    }

    /**
     * 转换单个字符
     *
     * @param c
     * @return
     */
    public String getCharacterPinYin(char c) {
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }
        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if (pinyin == null) return null;
        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }

    /**
     * 转换一个字符串
     *
     * @param str
     * @return
     */
    public String getStringPinYin(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for (int i = 0; i < str.length(); ++i) {
            tempPinyin = getCharacterPinYin(str.charAt(i));
            if (tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            } else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    public String getStringOnePin(String str) {
        String string = getStringPinYin(str).substring(0, 1).toUpperCase();
        if (getCharacterPinYin(string.charAt(0)) == null) return "#";
        return string;
    }

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += String.valueOf(pinyinArray[0].charAt(0)).toUpperCase();
            } else {
                convert += String.valueOf(word).toUpperCase();
            }
        }
        return convert;
    }

    /**
     * 将字符串转换成ASCII码
     *
     * @param cnStr
     * @return String
     */
    public String getCnASCII(String cnStr) {
        StringBuffer strBuf = new StringBuffer();
        // 将字符串转换成字节序列
        byte[] bGBK = cnStr.getBytes();
        for (int i = 0; i < bGBK.length; i++) {
            // 将每个字符转换成ASCII码
            strBuf.append(Integer.toHexString(bGBK[i] & 0xff));
        }
        return strBuf.toString();
    }

    /**
     * 判断是否为字母
     * @param fstrData
     * @return
     */
    public boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }
}