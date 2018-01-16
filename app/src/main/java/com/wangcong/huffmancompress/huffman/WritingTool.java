package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.utils.CodeConversion;

import java.io.*;
import java.util.List;

/**
 * 写文件工具
 */
public class WritingTool {
    private Elements elements; // 字节列表

    public WritingTool(Elements elements) {
        this.elements = elements;
    }

    /**
     * 压缩文件
     *
     * @param srcPath  待压缩文件的绝对路径
     * @param destPath 压缩后的文件的路径
     */
    public void writeCompressedFile(String srcPath, String destPath) throws Exception {
        ElementBean rawElementList[] = elements.getRawElementList();
        StringBuilder stringBuilder = new StringBuilder(); // 保存压缩文件的字符串形式
//        try {
        //构造文件输入流
        FileInputStream fis = new FileInputStream(srcPath);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //读取文件
        int value = bis.read();
        while (value != -1) {
            stringBuilder.append(rawElementList[value].getCode()); // 获取该字节对应哈夫曼编码字符串
            value = bis.read();
        }
        //关闭流
        fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        int codeLength = stringBuilder.length() / 8; // 写入的字节数
        int left = stringBuilder.length() % 8; // 最后一个字节的有效位数
        if (left != 0) { // 位数不足8位，需要添加0
            elements.setZeroAddedCount(8 - left);
            for (int i = 0; i < 8 - left; ++i) {
                stringBuilder.append('0');
            }
            codeLength += 1;
        }
        String allCodes = stringBuilder.toString();
//        try {
        File file = new File(destPath);
        if (!file.exists()) { // 判断文件是否存在，不存在就创建
            if (file.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                char[] oneByte = new char[8]; // 保存一个字节的字符数组
                for (int i = 0; i < codeLength; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        oneByte[j] = allCodes.charAt(i * 8 + j);
                    }
                    bos.write(CodeConversion.charArrayToByte(oneByte)); // 把该字节字符数组转换成字节
                }
                bos.flush();
                bos.close();
                fos.close();
            }
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 保存字节频率
     *
     * @param path 字节频率文件的绝对路径
     */
    public void writeFrequencyFile(String path) throws Exception {
        List<ElementBean> validElementList = elements.getValidElementList();
//        try {
        File file = new File(path);
        if (!file.exists()) {// 判断文件是否存在，不存在就创建
            if (file.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                for (ElementBean bean : validElementList) { // 写入字节频率
                    bos.write((bean.getElement() + " " + bean.getFrequency() + "\n").getBytes());
                }
                bos.write(("-1 " + elements.getZeroAddedCount()).getBytes()); // 写入补0的个数
                bos.flush();
                bos.close();
                fos.close();
            }
        }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
