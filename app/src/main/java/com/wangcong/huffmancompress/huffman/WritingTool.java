package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.utils.CodeConversion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 写文件工具
 */
public class WritingTool {
    private Elements elements; // 字节列表
    private final long ONEMB = 1024 * 1024; // 1M字节数量

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
        File srcFile = new File(srcPath);
        long fileLength = srcFile.length();
        if (fileLength <= ONEMB) {
            writeSmallCompressedFile(srcPath, destPath);
        } else {
            writeLargeCompressedFile(srcPath, destPath, fileLength);
        }
    }

    /**
     * 压缩小文件（字节数不超过1MB）
     *
     * @param srcPath  待压缩文件的绝对路径
     * @param destPath 压缩后的文件的路径
     * @throws Exception
     */
    private void writeSmallCompressedFile(String srcPath, String destPath) throws Exception {
//        Log.d("TEST", "writeSmallCompressedFile: ");
        ElementBean rawElementList[] = elements.getRawElementList();

        StringBuilder stringBuilder = new StringBuilder(); // 保存压缩文件的字符串形式

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
        bis.close();
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
//        String allCodes = stringBuilder.toString();
//        try {
        File file = new File(destPath);
        if (!file.exists()) { // 判断文件是否存在，不存在就创建
            if (!file.createNewFile()) {
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        char[] oneByte = new char[8]; // 保存一个字节的字符数组
        for (int i = 0; i < codeLength; ++i) {
            for (int j = 0; j < 8; ++j) {
                oneByte[j] = stringBuilder.charAt(i * 8 + j);
            }
            bos.write(CodeConversion.charArrayToByte(oneByte)); // 把该字节字符数组转换成字节
        }
        bos.flush();
        fos.close();
        bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 压缩大文件（字节数超过1MB）
     *
     * @param srcPath    待压缩文件的绝对路径
     * @param destPath   压缩后的文件的路径
     * @param fileLength 待压缩文件的字节数
     * @throws Exception
     */
    private void writeLargeCompressedFile(String srcPath, String destPath, long fileLength) throws Exception {
//        Log.d("TEST", "writeLargeCompressedFile: ");
        ElementBean rawElementList[] = elements.getRawElementList();

        //构造文件输入流
        FileInputStream fis = new FileInputStream(srcPath);
        BufferedInputStream bis = new BufferedInputStream(fis);

        //构建文件输出流
        File file = new File(destPath);
        if (!file.exists()) { // 判断文件是否存在，不存在就创建
            if (!file.createNewFile()) {
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        long countMB = fileLength / ONEMB; // 文件的MB数
        long count = 1;
        int value, codeLength; // 暂存当前读取的字节，写入的字节数
        String leftString = ""; // 每次处理剩余的字符串
        char[] oneByte = new char[8]; // 保存一个字节信息的字符数组

        while (count <= countMB) { // 每次处理1MB
            StringBuilder stringBuilder = new StringBuilder(leftString); // 保存压缩文件的字符串形式
            for (int i = 0; i < ONEMB; i++) { // 读取1MB
                value = bis.read();
                stringBuilder.append(rawElementList[value].getCode()); // 获取该字节对应哈夫曼编码字符串
            }
            codeLength = stringBuilder.length() / 8;
            leftString = stringBuilder.substring(codeLength * 8); // 保存每次处理剩余的字符串
            for (int i = 0; i < codeLength; ++i) {
                for (int j = 0; j < 8; ++j) {
                    oneByte[j] = stringBuilder.charAt(i * 8 + j);
                }
                bos.write(CodeConversion.charArrayToByte(oneByte)); // 把该字节字符数组转换成字节
            }
            bos.flush();
            ++count;
        }

        // 处理剩余字节
        StringBuilder stringBuilder = new StringBuilder(leftString);
        value = bis.read();
        while (value != -1) {
            stringBuilder.append(rawElementList[value].getCode()); // 获取该字节对应哈夫曼编码字符串
            value = bis.read();
        }
        //关闭流
        fis.close();
        bis.close();

        codeLength = stringBuilder.length() / 8; // 写入的字节数
        int left = stringBuilder.length() % 8; // 最后一个字节的有效位数
        if (left != 0) { // 位数不足8位，需要添加0
            elements.setZeroAddedCount(8 - left);
            for (int i = 0; i < 8 - left; ++i) {
                stringBuilder.append('0');
            }
            codeLength += 1;
        }
        for (int i = 0; i < codeLength; ++i) {
            for (int j = 0; j < 8; ++j) {
                oneByte[j] = stringBuilder.charAt(i * 8 + j);
            }
            bos.write(CodeConversion.charArrayToByte(oneByte)); // 把该字节字符数组转换成字节
        }
        bos.flush();
        fos.close();
        bos.close();
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
        if (!file.exists()) { // 判断文件是否存在，不存在就创建
            if (!file.createNewFile()) {
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        for (ElementBean bean : validElementList) { // 写入字节频率
            bos.write((bean.getElement() + " " + bean.getFrequency() + "\n").getBytes());
        }
        bos.write(("-1 " + elements.getZeroAddedCount()).getBytes()); // 写入补0的个数
        bos.flush();
        fos.close();
        bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
