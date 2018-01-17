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
    private final long ONEMB = 1024 * 1024; // 1M字节数量

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
        File srcFile = new File(srcPath);
        long fileLength = srcFile.length();
        if (fileLength <= ONEMB) {
            doSmallFileDecoding(srcPath, destPath);
        } else {
            doLargeFileDecoding(srcPath, destPath, fileLength);
        }
    }

    /**
     * 解压小文件（字节数不超过1M）
     *
     * @param srcPath  待解压文件的绝对路径
     * @param destPath 解压后的文件的绝对路径
     * @throws Exception
     */
    private void doSmallFileDecoding(String srcPath, String destPath) throws Exception {
//        Log.d("TEST", "doSmallFileDecoding: ");
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
        bis.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String stringCodes = stringBuilder.toString();
//        stringCodes = stringCodes.substring(0, stringCodes.length() - elements.getZeroAddedCount());
        stringBuilder.delete(stringBuilder.length() - elements.getZeroAddedCount(), stringBuilder.length());

        char temp;
        List<ElementBean> validElementList = elements.getValidElementList();
        HuffmanTreeNode[] huffmanTreeNodes = huffmanTree.getHuffmanTree();
        HuffmanTreeNode now = huffmanTreeNodes[huffmanTree.getRoot()]; // 初始化为树根
        HuffmanTreeNode parent; // 父节点
//        try {
        File file = new File(destPath);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        for (int i = 0; i < stringBuilder.length(); ++i) { // 从树根开始向下
            temp = stringBuilder.charAt(i);
            parent = now; // 保存父结点
            if (temp == '0') // 如果是0，进入左子树
                now = huffmanTreeNodes[now.getLeftLink()];
            else
                now = huffmanTreeNodes[now.getRightLink()]; // 如果是1，进入右子树
            if (now.getLeftLink() == -1 && now.getRightLink() == -1) { // 到达叶子节点，找到对应字节
                // 写入字节
                if (temp == '0')
                    bos.write(validElementList.get(parent.getLeftLink()).getElement());
                else
                    bos.write(validElementList.get(parent.getRightLink()).getElement());
                now = huffmanTreeNodes[huffmanTree.getRoot()]; // 找到一个原字符后，令为根结点，开始找下一个字节
            }
        }
        bos.flush();
        fos.close();
        bos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 解压大文件（字节数超过1M）
     *
     * @param srcPath    待解压文件的绝对路径
     * @param destPath   解压后的文件的绝对路径
     * @param fileLength 待解压文件的字节数
     * @throws Exception
     */
    private void doLargeFileDecoding(String srcPath, String destPath, long fileLength) throws Exception {
//        Log.d("TEST", "doLargeFileDecoding: ");
        FileInputStream fis = new FileInputStream(srcPath); // 输入流
        BufferedInputStream bis = new BufferedInputStream(fis);

        File file = new File(destPath); // 文件不存在则创建
        if (!file.exists()) {
            if (!file.createNewFile()) {
                return;
            }
        }
        FileOutputStream fos = new FileOutputStream(file); // 输出流
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        List<ElementBean> validElementList = elements.getValidElementList();
        HuffmanTreeNode[] huffmanTreeNodes = huffmanTree.getHuffmanTree();

        long countMB = fileLength / ONEMB; // 文件的MB数
        long count = 1;
        int value; // 暂存当前读取的字节
        HuffmanTreeNode now, parent; // 当前节点，父节点
        char temp; // 暂存当前处理的字符
        int leftStringIdx = 0; // 每次处理剩余的字符串的起始位置
        String leftString = ""; // 每次处理剩余的字符串

        while (count <= countMB) { // 每次处理1MB
            StringBuilder stringBuilder = new StringBuilder(leftString); // 保存压缩文件的字符串形式
            for (int i = 0; i < ONEMB; i++) { // 读取1MB
                value = bis.read();
                stringBuilder.append(CodeConversion.ByteToString(value)); // 将字节转换成字符串
            }
            now = huffmanTreeNodes[huffmanTree.getRoot()]; // 初始化为树根
            for (int i = 0; i < stringBuilder.length(); ++i) { // 从树根开始向下
                temp = stringBuilder.charAt(i);
                parent = now; // 保存父结点
                if (temp == '0') // 如果是0，进入左子树
                    now = huffmanTreeNodes[now.getLeftLink()];
                else
                    now = huffmanTreeNodes[now.getRightLink()]; // 如果是1，进入右子树
                if (now.getLeftLink() == -1 && now.getRightLink() == -1) { // 到达叶子节点，找到对应字节
                    // 写入字节
                    if (temp == '0')
                        bos.write(validElementList.get(parent.getLeftLink()).getElement());
                    else
                        bos.write(validElementList.get(parent.getRightLink()).getElement());
                    now = huffmanTreeNodes[huffmanTree.getRoot()]; // 找到一个原字符后，令为根结点，开始找下一个字节
                    leftStringIdx = i; // 保存剩余字符串位置信息
                }
            }
            bos.flush();
            leftString = stringBuilder.substring(leftStringIdx + 1); // 保存剩余字符串
            ++count;
        }

        // 处理剩余字节
        StringBuilder stringBuilder = new StringBuilder(leftString);
        value = bis.read();
        while (value != -1) {
            stringBuilder.append(CodeConversion.ByteToString(value)); // 将字节转换成字符串
            value = bis.read();
        }
        fis.close();
        bis.close();

        // 删除补的多余的0
        stringBuilder.delete(stringBuilder.length() - elements.getZeroAddedCount(), stringBuilder.length());

        now = huffmanTreeNodes[huffmanTree.getRoot()]; // 初始化为树根
        for (int i = 0; i < stringBuilder.length(); ++i) { // 从树根开始向下
            temp = stringBuilder.charAt(i);
            parent = now; // 保存父结点
            if (temp == '0') // 如果是0，进入左子树
                now = huffmanTreeNodes[now.getLeftLink()];
            else
                now = huffmanTreeNodes[now.getRightLink()]; // 如果是1，进入右子树
            if (now.getLeftLink() == -1 && now.getRightLink() == -1) { // 到达叶子节点，找到对应字节
                // 写入字节
                if (temp == '0')
                    bos.write(validElementList.get(parent.getLeftLink()).getElement());
                else
                    bos.write(validElementList.get(parent.getRightLink()).getElement());
                now = huffmanTreeNodes[huffmanTree.getRoot()]; // 找到一个原字符后，令为根结点，开始找下一个字节
            }
        }
        bos.flush();
        fos.close();
        bos.close();
    }

}
