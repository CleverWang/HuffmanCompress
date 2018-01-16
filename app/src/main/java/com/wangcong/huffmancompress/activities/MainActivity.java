package com.wangcong.huffmancompress.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.wangcong.huffmancompress.R;
import com.wangcong.huffmancompress.huffman.CompressAndUncompress;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ToggleButton btn_mode_choose;
    private ProgressBar progressbar;
    private EditText edit_path;
    private Button btn_select_path;
    private TextView text_info;
    private Button btn_start;
    private Button btn_about;
    private Button btn_exit;
    private ScrollView scrollview;

    private boolean isCompressMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        initData();
        initEvent();
    }

    private void bindView() {
        btn_mode_choose = findViewById(R.id.btn_mode_choose);
        progressbar = findViewById(R.id.progressbar);
        edit_path = findViewById(R.id.edit_path);
        btn_select_path = findViewById(R.id.btn_select_path);
        text_info = findViewById(R.id.text_info);
        btn_start = findViewById(R.id.btn_start);
        btn_about = findViewById(R.id.btn_about);
        btn_exit = findViewById(R.id.btn_exit);
        scrollview = findViewById(R.id.scrollview);
    }

    private void initData() {
        progressbar.setVisibility(View.INVISIBLE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/Download/test/test.jpg";
//            String path = Environment.getExternalStorageDirectory().getPath() + "/Download/test/test.txt.huffman/test.txt.compressed";
            edit_path.setText(path);
            edit_path.setSelection(path.length()
            );
        }

    }

    private void initEvent() {
        btn_mode_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    isCompressMode = true;
                    String info = text_info.getText() + "\n当前模式：压缩模式\n";
                    text_info.setText(info);
                } else {
                    isCompressMode = false;
                    String info = text_info.getText() + "\n当前模式：解压模式\n";
                    text_info.setText(info);
                }
                scrollview.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressbar.setVisibility(View.VISIBLE);
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    doCompressAndUncompress();
                }
//                progressbar.setVisibility(View.GONE);
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAbout();
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void doCompressAndUncompress() {
        String path = edit_path.getText().toString().trim();
        File file = new File(path);
        if (file.exists() && file.isFile() && file.canRead()) {
            if (isCompressMode) {
                doCompress(path);
            } else {
                doUncompress(path);
            }
        } else {
            String info = text_info.getText() + "文件不存在或不是文件或文件不可读>_<\n";
            text_info.setText(info);
        }
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    private void doCompress(String path) {
        String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String destDir = dirPath + fileName + ".huffman";
        File dir = new File(destDir);
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                String info = text_info.getText() + "保存压缩文件的文件夹创建失败！！！\n";
                text_info.setText(info);
                return;
            }
        }
        CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
        String returnInfo = compressAndUncompress.compress(path, destDir + "/" + fileName + ".compressed", destDir + "/" + fileName + ".frequency");
        String info = text_info.getText() + returnInfo;
        text_info.setText(info);
    }

    private void doUncompress(String path) {
        if (path.endsWith(".compressed")) {
            String dirPath = path.substring(0, path.lastIndexOf('/') + 1);
            String fileName = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf("compressed") - 1);
            String freqFile = dirPath + fileName + ".frequency";
            File file = new File(freqFile);
            if (!file.exists()) {
                String info = text_info.getText() + "字节频率文件丢失或命名错误！！！\n";
                text_info.setText(info);
                return;
            }
            CompressAndUncompress compressAndUncompress = new CompressAndUncompress();
            String returnInfo = compressAndUncompress.uncompress(path, dirPath + fileName, freqFile);
            String info = text_info.getText() + returnInfo;
            text_info.setText(info);
        } else {
            String info = text_info.getText() + "文件不是本软件产生的压缩文件或命名错误！！！\n";
            text_info.setText(info);
        }
    }

    private void showAbout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("关于");
        dialog.setMessage("基于哈夫曼编码的简单压缩与解压软件。");
        dialog.setIcon(R.drawable.ic_info_black_48dp);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doCompressAndUncompress();
                } else {
                    Toast.makeText(this, "你拒绝了文件读写权限！！！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
