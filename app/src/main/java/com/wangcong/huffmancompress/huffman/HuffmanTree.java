package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.beans.HuffmanTreeNode;

import java.util.List;

/**
 * 哈夫曼树
 */
public class HuffmanTree {
    private int leafCount; // 哈夫曼树叶子结点个数
    private int root; // 哈夫曼树的根结点下标
    private HuffmanTreeNode huffmanTree[]; // 哈夫曼数组
    private List<ElementBean> validElementList; // 有效字节列表

    public HuffmanTree(Elements elements) {
        validElementList = elements.getValidElementList();
        leafCount = elements.getValidElementCount();
        root = 2 * leafCount - 2;
        huffmanTree = new HuffmanTreeNode[2 * leafCount - 1];
        for (int i = 0; i < 2 * leafCount - 1; ++i) {
            huffmanTree[i] = new HuffmanTreeNode();
        }
    }

    /**
     * 构建哈夫曼树
     */
    public void buildHuffmanTree() {
        int i, j, x1, x2;
        long m1, m2;
        for (i = 0; i < 2 * leafCount - 1; ++i) { // 置哈夫曼数组初始状态
            if (i < leafCount)
                this.huffmanTree[i].setWeight(validElementList.get(i).getFrequency()); // 权值为源文件中字节出现的频率
        }
        for (i = 0; i < leafCount - 1; ++i) { // 每循环一次构造一个内部结点
            m1 = m2 = Long.MAX_VALUE; // ∞
            x1 = x2 = -1;
            for (j = 0; j < leafCount + i; ++j)
                // 找两个最小权的无父结点的结点下标
                if (huffmanTree[j].getWeight() < m1 && huffmanTree[j].getParent() == -1) {
                    m2 = m1;
                    x2 = x1;
                    m1 = huffmanTree[j].getWeight();
                    x1 = j; // x1只存放最小权的无父结点的结点下标
                } else if (huffmanTree[j].getWeight() < m2 && huffmanTree[j].getParent() == -1) {
                    m2 = huffmanTree[j].getWeight();
                    x2 = j; // x2只存放次小权的无父结点的结点下标
                }
            huffmanTree[x1].setParent(leafCount + i);
            huffmanTree[x2].setParent(leafCount + i);
            huffmanTree[leafCount + i].setWeight(m1 + m2);
            huffmanTree[leafCount + i].setLeftLink(x1);
            huffmanTree[leafCount + i].setRightLink(x2);// 构造内部结点
        }
    }

    public int getRoot() {
        return root;
    }

    public int getLeafCount() {
        return leafCount;
    }

    public HuffmanTreeNode[] getHuffmanTree() {
        return huffmanTree;
    }

    public void print() {
        for (HuffmanTreeNode node : huffmanTree) {
            System.out.println(node);
        }
    }
}