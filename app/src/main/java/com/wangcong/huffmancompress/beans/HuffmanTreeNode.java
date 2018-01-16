package com.wangcong.huffmancompress.beans;

/**
 * 哈夫曼树的结点
 */
public class HuffmanTreeNode {
    private long weight = -1;// 结点的权值
    private int parent = -1, leftLink = -1, rightLink = -1;// 结点的父亲，左儿子，右儿子的下标

    public HuffmanTreeNode() {

    }

    @Override
    public String toString() {
        return "Weight: " + String.valueOf(weight) + " Parent: " + String.valueOf(parent) + " Left: " + String.valueOf(leftLink) + " Right: " + String.valueOf(rightLink);
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getLeftLink() {
        return leftLink;
    }

    public void setLeftLink(int leftLink) {
        this.leftLink = leftLink;
    }

    public int getRightLink() {
        return rightLink;
    }

    public void setRightLink(int rightLink) {
        this.rightLink = rightLink;
    }
}