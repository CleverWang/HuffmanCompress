package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.utils.CodeConversion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
//            writeCompressedFileInProducerConsumerMode(srcPath, destPath);
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

    @Deprecated
    private final int QUEUESIZE = 1024 * 1024 * 8;
    @Deprecated
    private final Character ENDOFFILE = '2';

    /**
     * 利用生产者-消费者模式进行压缩操作（运行速度反而更慢，why？？？）
     *
     * @param srcPath
     * @param destPath
     * @throws InterruptedException
     */
    @Deprecated
    private void writeCompressedFileInProducerConsumerMode(final String srcPath, final String destPath) throws Exception {
//        Log.d("DEBUG", "writeCompressedFileInProducerConsumerMode: ");
        final ElementBean rawElementList[] = elements.getRawElementList();
        final BlockingQueue<Character> blockingDeque = new LinkedBlockingQueue<>(QUEUESIZE); // 阻塞队列

        // 生产者线程
        Thread producer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //构造文件输入流
                    FileInputStream fis = new FileInputStream(srcPath);
                    BufferedInputStream bis = new BufferedInputStream(fis);

                    //读取文件
                    String temp;
                    int value = bis.read();
                    while (value != -1) {
                        temp = rawElementList[value].getCode(); // 获取该字节对应哈夫曼编码字符串
                        for (int i = 0, length = temp.length(); i < length; i++) {
                            blockingDeque.put(temp.charAt(i)); // 不断放入字符
                        }
                        value = bis.read();
                    }
                    // 放入文件结束标志字符
                    blockingDeque.put(ENDOFFILE);
                    //关闭流
                    fis.close();
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        // 消费者线程
        Thread consumer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(destPath);
                    if (!file.exists()) { // 判断文件是否存在，不存在就创建
                        if (!file.createNewFile()) {
                            return;
                        }
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    StringBuilder stringBuilder = null;
                    boolean isOver = false; // 是否结束标志
                    char temp = blockingDeque.take();
//                    Log.d("DEBUG", "run: " + temp);
                    while (temp != ENDOFFILE) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(temp);
                        for (int i = 0; i < 7; i++) { // 提取8个字符
                            temp = blockingDeque.take();
//                            Log.d("DEBUG", "run: " + temp);
                            if (temp == ENDOFFILE) {
                                isOver = true;
                                break;
                            } else {
                                stringBuilder.append(temp);
                            }
                        }
                        if (isOver) {
                            break;
                        } else {
//                            Log.d("DEBUG", "run: " + stringBuilder.length());
                            bos.write(CodeConversion.stringToByte(stringBuilder.toString())); // 把该字节字符数组转换成字节
                            temp = blockingDeque.take();
                        }
                    }
                    int leftCodeLength = stringBuilder.length(); // 剩余字符串
//                    Log.d("DEBUG", "run: " + leftCodeLength);
                    if (leftCodeLength < 8) {
                        elements.setZeroAddedCount(8 - leftCodeLength);
                        for (int i = 0; i < 8 - leftCodeLength; i++) {
                            stringBuilder.append('0');
                        }
                        bos.write(CodeConversion.stringToByte(stringBuilder.toString()));
                    }
                    bos.flush();
                    fos.close();
                    bos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 启动线程
        producer.start();
        consumer.start();

        // 等待线程结束
        producer.join();
        consumer.join();
    }


}
