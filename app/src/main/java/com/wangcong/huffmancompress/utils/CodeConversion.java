package com.wangcong.huffmancompress.utils;

/**
 * 编码转换工具
 */
public class CodeConversion {
    /**
     * 把8位字符串形式的二进制字节转换成对应十进制字节
     *
     * @param stringCode 8位字符串形式的二进制字节
     * @return 十进制字节
     */
    public static int stringToByte(String stringCode) {
        int v1 = (stringCode.charAt(0) - '0') * 128;
        int v2 = (stringCode.charAt(1) - '0') * 64;
        int v3 = (stringCode.charAt(2) - '0') * 32;
        int v4 = (stringCode.charAt(3) - '0') * 16;
        int v5 = (stringCode.charAt(4) - '0') * 8;
        int v6 = (stringCode.charAt(5) - '0') * 4;
        int v7 = (stringCode.charAt(6) - '0') * 2;
        int v8 = (stringCode.charAt(7) - '0');
        return v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8;
    }

    /**
     * 把8位字符数组形式的二进制字节转换成对应十进制字节
     *
     * @param charCode 8位字符数组形式的二进制字节
     * @return 十进制字节
     */
    public static int charArrayToByte(char[] charCode) {
        int v1 = (charCode[0] - '0') * 128;
        int v2 = (charCode[1] - '0') * 64;
        int v3 = (charCode[2] - '0') * 32;
        int v4 = (charCode[3] - '0') * 16;
        int v5 = (charCode[4] - '0') * 8;
        int v6 = (charCode[5] - '0') * 4;
        int v7 = (charCode[6] - '0') * 2;
        int v8 = (charCode[7] - '0');
        return v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8;
    }

    /**
     * 把十进制字节转换成8位字符串形式二进制字节
     *
     * @param byteCode 十进制字节
     * @return 8位字符串形式二进制字节
     */
    public static String ByteToString(int byteCode) {
        StringBuilder stringBuilder = new StringBuilder();
        while (byteCode / 2 != 0) {// “除基取余”法求二进制
            stringBuilder.append(byteCode % 2);
            byteCode = byteCode / 2;
        }
        stringBuilder.append(byteCode % 2);
        int length = stringBuilder.length();
        if (length != 8) { // 不足8位补0
            for (int i = 1; i <= 8 - length; ++i)
                stringBuilder.append('0');
        }
        return stringBuilder.reverse().toString();// 需要转置
    }
}
