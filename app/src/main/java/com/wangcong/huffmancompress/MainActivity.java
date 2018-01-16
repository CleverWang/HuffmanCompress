package com.wangcong.huffmancompress;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton btn_mode_chose;
    private ProgressBar progressbar;
    private EditText edit_path;
    private Button btn_select_path;
    private TextView text_info;
    private Button btn_start;
    private Button btn_about;
    private Button btn_exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindView();
        initData();
        initEvent();
    }

    private void bindView() {
        btn_mode_chose = findViewById(R.id.btn_mode_choose);
        progressbar = findViewById(R.id.progressbar);
        edit_path = findViewById(R.id.edit_path);
        btn_select_path = findViewById(R.id.btn_select_path);
        text_info = findViewById(R.id.text_info);
        btn_start = findViewById(R.id.btn_start);
        btn_about = findViewById(R.id.btn_about);
        btn_exit = findViewById(R.id.btn_exit);
    }

    private void initData() {
        progressbar.setVisibility(View.GONE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath();
            edit_path.setText(path);
            edit_path.setSelection(path.length()
            );
        }

    }

    private void initEvent() {


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
}
