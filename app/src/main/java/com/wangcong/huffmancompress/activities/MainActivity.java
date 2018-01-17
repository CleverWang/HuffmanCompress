package com.wangcong.huffmancompress.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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
import com.wangcong.huffmancompress.listeners.UpdateUIListener;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    private UpdateUIListener updateUIListener = new UpdateUIListener() {
        @Override
        public void onStart() {
            progressbar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onUpdate(String info) {
//            String info = text_info.getText() + updateInfo;
//            text_info.setText(info);
//            scrollview.post(new Runnable() {
//                @Override
//                public void run() {
//                    scrollview.fullScroll(ScrollView.FOCUS_DOWN);
//                }
//            });
            updateInfo(info);
        }

        @Override
        public void onFinish() {
            progressbar.setVisibility(View.INVISIBLE);
        }
    };

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
        btn_mode_choose.setChecked(false);
        progressbar.setVisibility(View.INVISIBLE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory().getPath();
            edit_path.setText(path);
            edit_path.setSelection(path.length()
            );
        }
        updateInfo("欢迎使用！！！");
        updateInfo("当前模式：压缩模式\n");
    }

    private void initEvent() {
        btn_mode_choose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    isCompressMode = true;
                    updateInfo("当前模式：压缩模式\n");
                } else {
                    isCompressMode = false;
                    updateInfo("当前模式：解压模式\n");
                }
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
//                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, progressbar, text_info, scrollview).execute();
                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, updateUIListener).execute();
//                    doCompressAndUncompress();
                }
            }
        });

        btn_select_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openResource();
                }
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

    private void openResource() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        startActivityForResult(intent, 1); // 打开文件管理器
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理
                        handleOnKitKat(data);
                    } else {
                        // 4.4以下系统使用这个方法处理
                        handleBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleOnKitKat(Intent data) {
        String path = null;
        Uri uri = data.getData();
//        Log.d("TAG", "handleOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                path = getFilePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                path = getFilePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            path = getFilePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            path = uri.getPath();
        }
        setPath(path); // 设置路径
    }

    private void handleBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String path = getFilePath(uri, null);
        setPath(path);
    }

    private String getFilePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void setPath(String path) {
        edit_path.setText(path);
        edit_path.setSelection(path.length());
    }

    private void updateInfo(String info) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
        String updatedInfo = text_info.getText() + simpleDateFormat.format(date) + "> " + info + "\n";
        text_info.setText(updatedInfo);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, progressbar, text_info, scrollview).execute();
                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, updateUIListener).execute();
//                    doCompressAndUncompress();
                } else {
                    Toast.makeText(this, "你拒绝了文件读写权限！！！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openResource();
                } else {
                    Toast.makeText(this, "你拒绝了文件读写权限！！！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

}
