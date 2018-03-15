package supermarket.cn.yt.asuper.ytlibrary.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author 曾晓文
 */
public class ToolString {

    public static final String ID_CARD_REGEXP = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}$)";

    /**
     * 获取UUID
     *
     * @return 32UUID小写字符串
     */
    public static String gainUUID() {
        String strUUID = UUID.randomUUID().toString();
        strUUID = strUUID.replaceAll("-", "").toLowerCase();
        return strUUID;
    }

    /**
     * 判断字符串是否非空非null
     *
     * @param strParm 需要判断的字符串
     * @return 真假
     */
    public static boolean isNoBlankAndNoNull(String strParm) {
        return !((strParm == null) || (strParm.equals("")));
    }

    /**
     * 将流转成字符串
     *
     * @param is 输入流
     * @return
     * @throws Exception
     */
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * 将文件转成字符串
     *
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String getStringFromFile(File file) throws Exception {
        FileInputStream fin = new FileInputStream(file);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public static boolean isROCIDCard(String number) {
        Pattern pattern = Pattern.compile(ID_CARD_REGEXP);
        return pattern.matcher(number).matches();
    }

    public static BigDecimal stringMulti(String a, String... b) {
        try {
            BigDecimal ba = a == null?new BigDecimal(0):new BigDecimal(a.trim());
            for (String s : b) {
                ba = ba.multiply(s == null?new BigDecimal(0):new BigDecimal(s.trim()));
            }
            return ba;
        }catch (Exception e){
            return new BigDecimal(0);
        }
    }

    public static String getString(String string){
        if (string==null) return "";
        else return string;
    }

    public static boolean isEmojiCharacter(char codePoint) {
        if((codePoint == 0xFF5E) || (codePoint == 0xFF0C) || (codePoint == 0xFF01)|| (codePoint == 0xFF1F) || (codePoint == 0xFF1A) || (codePoint == 0xFF05) || (codePoint == 0xFF1B)|| (codePoint == 0xFF08) || (codePoint == 0xFF09)|| (codePoint == 0xFFE5)) return false;
        return !((codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }
}
