package com.wangcong.huffmancompress.beans;

/**
 * 保存256个字节中某个字节相关的信息
 */
public class ElementBean {
    private int element = -1; // 256个字节中该字节的下标，也就是它本身
    private long frequency = 0; // 该字节出现的频率
    private boolean isValid = false; // 出现的频率>0，表示需要参与哈夫曼树的构建，定义为有效字节
    private String code = ""; // 哈夫曼编码

    public ElementBean() {

    }

    @Override
    public String toString() {
        return "Element: " + String.valueOf(element) + " Frequency: " + String.valueOf(frequency) + " code: " + code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
