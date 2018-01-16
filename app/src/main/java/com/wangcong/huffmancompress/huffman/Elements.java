package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装字节列表
 */
public class Elements {
    private ElementBean rawElementList[]; // 256个字节的原始列表
    private List<ElementBean> validElementList; // 可以参与哈夫曼树的构建的字节列表，即有效字节列表
    private int validElementCount; // 可以参与哈夫曼树的构建的字节数
    private int zeroAddedCount; // 写入压缩文件不足8位时补0的个数

    public Elements() {
        rawElementList = new ElementBean[256];
        for (int i = 0; i < 256; ++i) {
            rawElementList[i] = new ElementBean();
        }
        validElementList = new ArrayList<>();
        validElementCount = 0;
        zeroAddedCount = 0;
    }

    public ElementBean[] getRawElementList() {
        return rawElementList;
    }

    public List<ElementBean> getValidElementList() {
        return validElementList;
    }

    public int getValidElementCount() {
        return validElementCount;
    }

    public void setValidElementCount(int validElementCount) {
        this.validElementCount = validElementCount;
    }

    public int getZeroAddedCount() {
        return zeroAddedCount;
    }

    public void setZeroAddedCount(int zeroAddedCount) {
        this.zeroAddedCount = zeroAddedCount;
    }

    public void printValid() {
        for (ElementBean bean : validElementList) {
            System.out.println(bean);
        }
    }

    public void printRaw() {
        for (ElementBean bean : rawElementList) {
            System.out.println(bean);
        }
    }
}
