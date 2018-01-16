package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.beans.HuffmanTreeNode;

import java.util.List;

/**
 * 根据哈夫曼树构建各个字节的哈夫曼编码
 */
public class Coding {
    private HuffmanTree huffmanTree; // 哈夫曼树
    private Elements elements; // 字节列表

    public Coding(HuffmanTree huffmanTree, Elements elements) {
        this.huffmanTree = huffmanTree;
        this.elements = elements;
    }

    /**
     * 进行哈夫曼编码
     */
    public void doCoding() {
        List<ElementBean> validElementList = elements.getValidElementList();
        HuffmanTreeNode[] huffmanTreeNodes = huffmanTree.getHuffmanTree();
        for (int i = 0, count = huffmanTree.getLeafCount(); i < count; i++) {// 从叶子结点向父结点，自下而上，循环来确定各个字节的哈夫曼编码
            StringBuilder temp = new StringBuilder();// 用于暂存编码
            HuffmanTreeNode now = huffmanTreeNodes[i];// 保存当前结点
            HuffmanTreeNode parent = huffmanTreeNodes[now.getParent()];// 保存父结点
            int position = i;
            while (parent.getParent() != -1) {
                if (parent.getLeftLink() == position)// 是左儿子则往加入0
                    temp.append('0');
                else
                    temp.append('1');// 是右儿子则加入1
                position = now.getParent();// 保存当下结点的父结点在哈夫曼数组中的下标
                now = parent;
                parent = huffmanTreeNodes[parent.getParent()];// 向父结点靠近
            }
            if (parent.getLeftLink() == position)
                temp.append('0');
            else
                temp.append('1');
            temp = temp.reverse();// 由于是从下往上，所以需要转置才是最终的哈夫曼编码
            validElementList.get(i).setCode(temp.toString());
        }
    }
}
