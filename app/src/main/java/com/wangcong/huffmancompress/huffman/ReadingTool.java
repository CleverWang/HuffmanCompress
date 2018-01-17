package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.List;

/**
 * 读文件工具
 */
public class ReadingTool {
    private Elements elements;

    public ReadingTool(Elements elements) {
        this.elements = elements;
    }

    /**
     * 读取源文件，统计字节频率，构建字节列表
     *
     * @param path 源文件的绝对路径
     */
    public void readRawFile(String path) throws Exception {
        ElementBean rawElementList[] = elements.getRawElementList();
        List<ElementBean> validElementList = elements.getValidElementList();
//        try {
        //构造文件输入流
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //读取文件
        int value = bis.read();
        while (value != -1) {
            rawElementList[value].setElement(value); // 设置字节
            rawElementList[value].setFrequency(rawElementList[value].getFrequency() + 1); // 频率加一
            rawElementList[value].setValid(true); // 有效字节
//                System.out.println(rawElementList[value]);
            value = bis.read();
//                System.out.println(value);
        }
        //关闭流
        fis.close();
        bis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (ElementBean bean : rawElementList) { // 获取有效字节列表
            if (bean.isValid()) {
                validElementList.add(bean);
                elements.setValidElementCount(elements.getValidElementCount() + 1);
            }
        }
    }

    /**
     * 从保存的字节频率文件中加载字节列表
     *
     * @param path 字节频率文件的绝对路径
     */
    public void loadFromFrequencyFile(String path) throws Exception {
        ElementBean rawElementList[] = elements.getRawElementList();
        List<ElementBean> validElementList = elements.getValidElementList();
        StringBuilder stringBuilder = new StringBuilder();
//        try {
        //构造文件输入流
        FileInputStream fis = new FileInputStream(path);
        BufferedInputStream bis = new BufferedInputStream(fis);
        //读取文件
        int value = bis.read();
        while (value != -1) {
            stringBuilder.append((char) value);
            value = bis.read();
        }
        //关闭流
        fis.close();
        bis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String[] items = stringBuilder.toString().split("\n"); // 各个条目以回车隔开
        for (String item : items) {
            String[] one = item.split(" "); // 字节 频率
            int element = Integer.parseInt(one[0]);
            if (element != -1) {
                long frequency = Long.parseLong(one[1]);
                rawElementList[element].setElement(element);
                rawElementList[element].setFrequency(frequency);
                rawElementList[element].setValid(true);
            } else {
                elements.setZeroAddedCount(Integer.parseInt(one[1]));
            }
        }
        for (ElementBean bean : rawElementList) { // 获取有效字节列表
            if (bean.isValid()) {
                validElementList.add(bean);
                elements.setValidElementCount(elements.getValidElementCount() + 1);
            }
        }
    }
}
