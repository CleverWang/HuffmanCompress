package com.wangcong.huffmancompress.activities;

import android.os.AsyncTask;

import com.wangcong.huffmancompress.huffman.CompressAndUncompress;
import com.wangcong.huffmancompress.listeners.UpdateUIListener;

import java.io.File;

/**
 * Created by 13307 on 2018/1/16.
 */

public class CompressAndUncompressTask extends AsyncTask<Void, String, Void> {
    //    private ProgressBar progressbar;
//    private TextView text_info;
//    private ScrollView scrollview;
    private boolean isCompressMode;
    private String edit_path;
    private UpdateUIListener updateUIListener;

//    public CompressAndUncompressTask(String edit_path, boolean isCompressMode, ProgressBar progressbar, TextView text_info, ScrollView scrollview) {
//        this.edit_path = edit_path;
//        this.isCompressMode = isCompressMode;
//        this.progressbar = progressbar;
//        this.text_info = text_info;
//        this.scrollview = scrollview;
//    }

    public CompressAndUncompressTask(String edit_path, boolean isCompressMode, UpdateUIListener updateUIListener) {
        this.edit_path = edit_path;
        this.isCompressMode = isCompressMode;
        this.updateUIListener = updateUIListener;
    }

    @Override
    protected void onPreExecute() {
//        progressbar.setVisibility(View.VISIBLE);
        updateUIListener.onStart();
    }

    @Override
    protected void onProgressUpdate(String... values) {
//        String info = text_info.getText() + values[0];
//        text_info.setText(info);
//        scrollview.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
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
            publishProgress("文件不存在或不是文件或文件不可读>_<\n");
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void s) {
//        progressbar.setVisibility(View.INVISIBLE);
        updateUIListener.onFinish();
    }

    private void doCompress(String path) {
        String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String destDir = dirPath + fileName + ".huffman";
        File dir = new File(destDir);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                publishProgress("保存压缩文件的文件夹创建失败！！！\n");
                return;
            }
        }
        CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
        String returnInfo = compressAndUncompress.compress(path, destDir + "/" + fileName + ".compressed", destDir + "/" + fileName + ".frequency");
        publishProgress(returnInfo);
    }

    private void doUncompress(String path) {
        if (path.endsWith(".compressed")) {
            String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
            String fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf("compressed") - 1);
            String freqFile = dirPath + fileName + ".frequency";
            File file = new File(freqFile);
            if (!file.exists()) {
                publishProgress("字节频率文件丢失或命名错误！！！\n");
                return;
            }
            CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
            String returnInfo = compressAndUncompress.uncompress(path, dirPath + fileName, freqFile);
            publishProgress(returnInfo);
        } else {
            publishProgress("文件不是本软件产生的压缩文件或命名错误！！！\n");
        }
    }
}
