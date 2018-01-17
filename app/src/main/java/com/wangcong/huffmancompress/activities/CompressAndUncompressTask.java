package com.wangcong.huffmancompress.activities;

import android.os.AsyncTask;

import com.wangcong.huffmancompress.huffman.Coding;
import com.wangcong.huffmancompress.huffman.Decoding;
import com.wangcong.huffmancompress.huffman.Elements;
import com.wangcong.huffmancompress.huffman.HuffmanTree;
import com.wangcong.huffmancompress.huffman.ReadingTool;
import com.wangcong.huffmancompress.huffman.WritingTool;
import com.wangcong.huffmancompress.listeners.UpdateUIListener;

import java.io.File;

/**
 * Created by 13307 on 2018/1/16.
 */

public class CompressAndUncompressTask extends AsyncTask<Void, String, Void> {
    private boolean isCompressMode; // 当前模式
    private String edit_path; // 文件绝对路径
    private UpdateUIListener updateUIListener; // UI操作监听器

    public CompressAndUncompressTask(String edit_path, boolean isCompressMode, UpdateUIListener updateUIListener) {
        this.edit_path = edit_path;
        this.isCompressMode = isCompressMode;
        this.updateUIListener = updateUIListener;
    }

    @Override
    protected void onPreExecute() {
        updateUIListener.onStart();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        updateUIListener.onUpdate(values[0]);
    }


    @Override
    protected Void doInBackground(Void... voids) {
        String path = edit_path.trim();
        File file = new File(path);
        if (file.exists() && file.isFile() && file.canRead()) {
            if (isCompressMode) {
                doCompress(path);
            } else {
                doUncompress(path);
            }
        } else {
            publishProgress("文件不存在或不是文件或文件不可读>_<");
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void s) {
        updateUIListener.onFinish();
    }

    /**
     * 开始执行压缩操作
     *
     * @param path 待压缩文件绝对路径
     */
    private void doCompress(String path) {
        String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String destDir = dirPath + fileName + ".huffman";
        File dir = new File(destDir);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                publishProgress("保存压缩文件的文件夹创建失败！！！");
                return;
            }
        }
        compressing(path, destDir + "/" + fileName + ".compressed", destDir + "/" + fileName + ".frequency");
    }

    /**
     * 执行压缩操作
     *
     * @param srcFilePath           待压缩文件绝对路径
     * @param destFilePath          压缩文件的绝对路径
     * @param frequencyDestFilePath 保存字节频率文件的绝对路径
     */
    private void compressing(String srcFilePath, String destFilePath, String frequencyDestFilePath) {
        Elements elements = new Elements();

        ReadingTool readingTool = new ReadingTool(elements);
        publishProgress("读取待压缩文件：" + srcFilePath);
        try {
            readingTool.readRawFile(srcFilePath); // 读原始文件获取字节频率信息
        } catch (Exception e) {
            publishProgress("读取待压缩文件失败：" + e.getMessage());
        }

        publishProgress("构建哈夫曼树...");
        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 构建哈夫曼树

        publishProgress("进行哈夫曼编码...");
        Coding coding = new Coding(huffmanTree, elements);
        coding.doCoding(); // 进行哈夫曼编码

        WritingTool writingTool = new WritingTool(elements);
        try {
            publishProgress("写入压缩文件：" + destFilePath);
            writingTool.writeCompressedFile(srcFilePath, destFilePath); // 写入编码

            publishProgress("保存字节频率文件：" + frequencyDestFilePath);
            writingTool.writeFrequencyFile(frequencyDestFilePath); // 保存字节频率信息
        } catch (Exception e) {
            publishProgress("写入文件失败：" + e.getMessage());
        }
        publishProgress("压缩完成(＾ω＾)\n");
    }

    /**
     * 开始执行解压操作
     *
     * @param path 待解压文件的绝对路径
     */
    private void doUncompress(String path) {
        if (path.endsWith(".compressed")) {
            String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
            String fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf("compressed") - 1);
            String freqFile = dirPath + fileName + ".frequency";
            File file = new File(freqFile);
            if (!file.exists()) {
                publishProgress("字节频率文件丢失或命名错误！！！");
                return;
            }
            uncompressing(path, dirPath + fileName, freqFile);
        } else {
            publishProgress("文件不是本软件产生的压缩文件或命名错误！！！");
        }
    }

    /**
     * 执行解压操作
     *
     * @param srcFilePath          待解压文件的绝对路径
     * @param destFilePath         解压后文件的绝对路径
     * @param frequencySrcFilePath 字节频率文件的绝对路径
     */
    private void uncompressing(String srcFilePath, String destFilePath, String frequencySrcFilePath) {
        Elements elements = new Elements();
        ReadingTool readingTool = new ReadingTool(elements);

        publishProgress("读取字节频率文件：" + frequencySrcFilePath);
        try {
            readingTool.loadFromFrequencyFile(frequencySrcFilePath); // 从字节频率文件中加载字节列表
        } catch (Exception e) {
            publishProgress("读取频率文件失败：" + e.getMessage());
        }

        publishProgress("重建哈夫曼树...");
        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 重建哈夫曼树

        publishProgress("进行解压操作...");
        publishProgress("解压文件：" + srcFilePath);
        publishProgress("到：" + destFilePath);
        Decoding decoding = new Decoding(huffmanTree, elements);
        try {
            decoding.doDecoding(srcFilePath, destFilePath); // 进行解压操作
        } catch (Exception e) {
            publishProgress("解压文件失败：" + e.getMessage());
        }

        publishProgress("解压完成(＾ω＾)\n");
    }
}
