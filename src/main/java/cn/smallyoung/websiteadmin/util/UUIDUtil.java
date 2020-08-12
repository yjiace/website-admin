package cn.smallyoung.websiteadmin.util;

import java.util.UUID;

/**
 * @author yjc
 * @create 2020-07-10
 */
public class UUIDUtil {

    private final static String[] CHARS = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    /**
     * 生成8位UUID
     * @return 生成的uuid  8位
     */
    public static String getId(){
        return generateShortUuid(8);
    }

    /**
     * 生成uuid
     * @return  生成的uuid  32位
     */
    public static String getUuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成固定位数的UUID
     * @param length 字符串长度
     * @return 固定位数的UUID
     */
    public static String generateShortUuid(int length) {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = getUuid();
        for (int i = 0; i < length; i++) {
            //每组4位
            String str = uuid.substring(i * 4, i * 4 + 4);
            //输出str在16进制下的表示
            int x = Integer.parseInt(str, 16);
            //用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
            shortBuffer.append(CHARS[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

}
