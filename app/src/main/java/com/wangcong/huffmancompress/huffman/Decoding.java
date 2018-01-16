package com.wangcong.huffmancompress.huffman;

import com.wangcong.huffmancompress.beans.ElementBean;
import com.wangcong.huffmancompress.beans.HuffmanTreeNode;
import com.wangcong.huffmancompress.utils.CodeConversion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 根据哈夫曼树进行解码
 */
public class Decoding {
    private HuffmanTree huffmanTree; // 哈夫曼树
    private Elements elements; // 字节列表

    public Decoding(HuffmanTree huffmanTree, Elements elements) {
        this.huffmanTree = huffmanTree;
        this.elements = elements;
    }

    /**
     * 进行哈夫曼解码
     *
     * @param srcPath  待解压文件的绝对路径
     * @param destPath 解压后的文件的绝对路径
     */
    public void doDecoding(String srcPath, String destPath) throws Exception {
        StringBuilder stringBuilder = new StringBuilder(); // 保存压缩文件的字符串形式
//        try {
        FileInputStream fis = new FileInputStream(srcPath);
        BufferedInputStream bis = new BufferedInputStream(fis);
        int value = bis.read();
        while (value != -1) {
            stringBuilder.append(CodeConversion.ByteToString(value)); // 将字节转换成字符串
            value = bis.read();
        }
        fis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String stringCodes = stringBuilder.toString();
        stringCodes = stringCodes.substring(0, stringCodes.length() - elements.getZeroAddedCount());
        char temp;
        List<ElementBean> validElementList = elements.getValidElementList();
        HuffmanTreeNode[] huffmanTreeNodes = huffmanTree.getHuffmanTree();
        HuffmanTreeNode now = huffmanTreeNodes[huffmanTree.getRoot()]; // 初始化为树根
        HuffmanTreeNode parent; // 父节点
//        try {
        File file = new File(destPath);
        if (!file.exists()) {
            if (file.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                for (int i = 0; i < stringCodes.length(); ++i) {// 从树根开始向下
                    temp = stringCodes.charAt(i);
                    parent = now;// 保存父结点
                    if (temp == '0')// 如果是0，进入左子树
                        now = huffmanTreeNodes[now.getLeftLink()];
                    else
                        now = huffmanTreeNodes[now.getRightLink()];// 如果是1，进入右子树
                    if (now.getLeftLink() == -1 && now.getRightLink() == -1) {// 到达叶子节点，找到对应字节
                        // 写入字节
                        if (temp == '0')
                            bos.write(validElementList.get(parent.getLeftLink()).getElement());
                        else
                            bos.write(validElementList.get(parent.getRightLink()).getElement());
                        now = huffmanTreeNodes[huffmanTree.getRoot()];// 找到一个原字符后，令为根结点，开始找下一个字节
                    }
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

}
