package com.wangcong.huffmancompress.huffman;

/**
 * 压缩与解压接口
 */
public class CompressAndUncompress {

    public CompressAndUncompress() {
    }

    /**
     * 压缩文件
     *
     * @param srcFilePath           待压缩文件的绝对路径
     * @param destFilePath          压缩后的文件的绝对路径
     * @param frequencyDestFilePath 保存字节频率文件的绝对路径
     * @return 压缩过程信息
     */
    public String compress(String srcFilePath, String destFilePath, String frequencyDestFilePath) {
        Elements elements = new Elements();
        ReadingTool readingTool = new ReadingTool(elements);
        String info = "读取待压缩文件：" + srcFilePath + "\n";
        try {
            readingTool.readRawFile(srcFilePath); // 读原始文件获取字节频率信息
        } catch (Exception e) {
//            System.out.println(e.getMessage())
            info += "读取待压缩文件失败：" + e.getMessage() + "\n";
        }
//        elements.printRaw();
//        System.out.println();
//        System.out.println(elements.getValidElementCount());
//        System.out.println();
//        elements.getValidElementList();
//        elements.printValid();

        info += "构建哈夫曼树...\n";

        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 构建哈夫曼树
//        huffmanTree.print();

        info += "进行哈夫曼编码...\n";

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
            info += "写入压缩文件：" + destFilePath + "\n";

            writingTool.writeCompressedFile(srcFilePath, destFilePath); // 写入编码

            info += "保存字节频率文件：" + frequencyDestFilePath + "\n";

            writingTool.writeFrequencyFile(frequencyDestFilePath); // 保存字节频率信息
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info += "写入文件失败：" + e.getMessage() + "\n";
        }
        info += "压缩完成(＾ω＾)\n";
        return info;
    }

    /**
     * 解压文件
     *
     * @param srcFilePath          待解压文件的绝对路径
     * @param destFilePath         解压后的文件的绝对路径
     * @param frequencySrcFilePath 字节频率文件的绝对路径
     * @return 解压过程信息
     */
    public String uncompress(String srcFilePath, String destFilePath, String frequencySrcFilePath) {
        Elements elements = new Elements();
        ReadingTool readingTool = new ReadingTool(elements);

        String info = "读取字节频率文件：" + frequencySrcFilePath + "\n";

        try {
            readingTool.loadFromFrequencyFile(frequencySrcFilePath); // 从字节频率文件中加载字节列表
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info += "读取频率文件失败：" + e.getMessage() + "\n";
        }
//        System.out.println(elements.getZeroAddedCount());
//        elements.printValid();
//        System.out.println();
//        elements.printRaw();

        info += "重建哈夫曼树...\n";

        HuffmanTree huffmanTree = new HuffmanTree(elements);
        huffmanTree.buildHuffmanTree(); // 重建哈夫曼树
//        huffmanTree.print();

        info += "进行解压操作...\n解压文件：" + srcFilePath + "\n到：" + destFilePath + "\n";

        Decoding decoding = new Decoding(huffmanTree, elements);
        try {
            decoding.doDecoding(srcFilePath, destFilePath); // 进行解压操作
        } catch (Exception e) {
//            System.out.println(e.getMessage());
            info += "解压文件失败：" + e.getMessage() + "\n";
        }

        info += "解压完成(＾ω＾)\n";
        return info;
    }

//    public static void main(String[] args) {
//        CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
//        compressAndUncompress.compress("E:\\test\\test.jpg", "E:\\test\\test.compressed", "E:\\test\\test.frequency");
//        compressAndUncompress.uncompress("E:\\test\\test.compressed", "E:\\test\\test.uncompressed", "E:\\test\\test.frequency");
//    }
}
