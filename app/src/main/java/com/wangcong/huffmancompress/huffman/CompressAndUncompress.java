package com.wangcong.huffmancompress.huffman;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 压缩与解压接口
 */
public class CompressAndUncompress {
    private ProgressBar progressbar;
    private TextView text_info;

    public CompressAndUncompress(ProgressBar progressbar, TextView text_info) {
        this.progressbar = progressbar;
        this.text_info = text_info;
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath           待压缩文件的绝对路径
     * @param destFilePath          压缩后的文件的绝对路径
     * @param frequencyDestFilePath 保存字节频率文件的绝对路径
     */
    public void compress(String srcFilePath, String destFilePath, String frequencyDestFilePath) {
        Elements elements = new Elements();
        ReadingTool readingTool = new ReadingTool(elements);

        if (progressbar.getVisibility() == View.INVISIBLE)
            progressbar.setVisibility(View.VISIBLE);
        String info = text_info.getText() + "读取待压缩文件：" + srcFilePath + "\n";
        text_info.setText(info);
        int progress = progressbar.getProgress();
        progress += 20;
        progressbar.setProgress(progress);

        try {
            readingTool.readRawFile(srcFilePath); // 读原始文件获取字节频率信息
        } catch (Exception e) {
//            System.out.println(e.getMessage())
            info = text_info.getText() + "读取待压缩文件失败：" + e.getMessage() + "\n";
            text_info.setText(info);
        }
//        elements.printRaw();
//        System.out.println();
//        System.out.println(elements.getValidElementCount());
//        System.out.println();
//        elements.getValidElementList();
//        elements.printValid();

        info = text_info.getText() + "构建哈夫曼树...\n";
        text_info.setText(info);
        progress = progressbar.getProgress();
        progress += 20;
        progressbar.setProgress(progress);

        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 构建哈夫曼树
//        huffmanTree.print();

        info = text_info.getText() + "进行哈夫曼编码...\n";
        text_info.setText(info);
        progress = progressbar.getProgress();
        progress += 20;
        progressbar.setProgress(progress);

        Coding coding = new Coding(huffmanTree, elements);
        coding.doCoding(); // 进行哈夫曼编码
//        elements.printValid();
//        System.out.println();
//        elements.printRaw();
//        System.out.println(CodeConversion.stringToByte("11111111"));

//        elements.writeCompressedFile("E:\\test.txt", "E:\\coded.compressed");
//        elements.writeFrequencyFile("E:\\tree.cfg");
        WritingTool writingTool = new WritingTool(elements);
        try {
            info = text_info.getText() + "写入压缩文件：" + destFilePath + "\n";
            text_info.setText(info);
            progress = progressbar.getProgress();
            progress += 20;
            progressbar.setProgress(progress);
            writingTool.writeCompressedFile(srcFilePath, destFilePath); // 写入编码

            info = text_info.getText() + "保存字节频率文件：" + frequencyDestFilePath + "\n";
            text_info.setText(info);
            progress = progressbar.getProgress();
            progress += 20;
            progressbar.setProgress(progress);
            writingTool.writeFrequencyFile(frequencyDestFilePath); // 保存字节频率信息
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info = text_info.getText() + "写入文件失败：" + e.getMessage() + "\n";
            text_info.setText(info);
        }
        info = text_info.getText() + "压缩完成(＾ω＾)\n";
        text_info.setText(info);
        progressbar.setVisibility(View.INVISIBLE);
    }

    /**
     * 解压文件
     *
     * @param srcFilePath          待解压文件的绝对路径
     * @param destFilePath         解压后的文件的绝对路径
     * @param frequencySrcFilePath 字节频率文件的绝对路径
     */
    public void uncompress(String srcFilePath, String destFilePath, String frequencySrcFilePath) {
        Elements elements = new Elements();
        ReadingTool readingTool = new ReadingTool(elements);

        if (progressbar.getVisibility() == View.INVISIBLE)
            progressbar.setVisibility(View.VISIBLE);
        String info = text_info.getText() + "读取字节频率文件：" + frequencySrcFilePath + "\n";
        text_info.setText(info);
        int progress = progressbar.getProgress();
        progress += 33;
        progressbar.setProgress(progress);

        try {
            readingTool.loadFromFrequencyFile(frequencySrcFilePath); // 从字节频率文件中加载字节列表
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info = text_info.getText() + "读取频率文件失败：" + e.getMessage() + "\n";
            text_info.setText(info);
        }
//        System.out.println(elements.getZeroAddedCount());
//        elements.printValid();
//        System.out.println();
//        elements.printRaw();

        info = text_info.getText() + "重建哈夫曼树...\n";
        text_info.setText(info);
        progress = progressbar.getProgress();
        progress += 33;
        progressbar.setProgress(progress);

        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 重建哈夫曼树
//        huffmanTree.print();

        info = text_info.getText() + "进行解压操作...\n解压文件：" + srcFilePath + "\n到：" + destFilePath + "\n";
        text_info.setText(info);
        progress = progressbar.getProgress();
        progress += 34;
        progressbar.setProgress(progress);

        Decoding decoding = new Decoding(huffmanTree, elements);
        try {
            decoding.doDecoding(srcFilePath, destFilePath); // 进行解压操作
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info = text_info.getText() + "解压文件失败：" + e.getMessage() + "\n";
            text_info.setText(info);
        }

        info = text_info.getText() + "解压完成(＾ω＾)\n";
        text_info.setText(info);
        progressbar.setVisibility(View.INVISIBLE);
    }

//    public static void main(String[] args) {
//        CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
//        compressAndUncompress.compress("E:\\test\\test.jpg", "E:\\test\\test.compressed", "E:\\test\\test.frequency");
//        compressAndUncompress.uncompress("E:\\test\\test.compressed", "E:\\test\\test.uncompressed", "E:\\test\\test.frequency");
//    }
}
