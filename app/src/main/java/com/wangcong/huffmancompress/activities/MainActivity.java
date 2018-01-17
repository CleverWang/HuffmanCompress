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
    private ToggleButton btn_mode_choose; // 模式选择（OFF是压缩，ON是解压）
    private ProgressBar progressbar; // 进度环
    private EditText edit_path; // 路径输入框
    private Button btn_select_path; // 文件选择按钮
    private TextView text_info; // 解压缩时状态信息显示区域
    private Button btn_start; // 开始执行解压缩操作
    private Button btn_about; // 关于按钮
    private Button btn_exit; // 退出按钮
    private ScrollView scrollview; // 滚动控件

    private boolean isCompressMode = true; // 是否是压缩模式标志位

    private UpdateUIListener updateUIListener = new UpdateUIListener() { // UI操作监听器实例
        @Override
        public void onStart() {
            progressbar.setVisibility(View.VISIBLE); // 显示进度环
        }

        @Override
        public void onUpdate(String info) {
            updateInfo(info); // 更新信息显示区
        }

        @Override
        public void onFinish() {
            progressbar.setVisibility(View.INVISIBLE); // 关闭进度环
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

    /**
     * 绑定视图
     */
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

    /**
     * 初始化视图或数据
     */
    private void initData() {
        btn_mode_choose.setChecked(false); // 初始是压缩模式
        progressbar.setVisibility(View.INVISIBLE);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { // 初始置入外存根目录
            String path = Environment.getExternalStorageDirectory().getPath();
            edit_path.setText(path);
            edit_path.setSelection(path.length()
            );
        }
        updateInfo("欢迎使用！！！");
        updateInfo("当前模式：压缩模式\n");
    }

    /**
     * 设置视图监听器
     */
    private void initEvent() {
        // 模式选择按钮监听器
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

        // 开始执行按钮监听器
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 运行时权限获取
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
//                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, progressbar, text_info, scrollview).execute();
                    // 执行后台解压缩任务
                    new CompressAndUncompressTask(edit_path.getText().toString(), isCompressMode, updateUIListener).execute();
//                    doCompressAndUncompress();
                }
            }
        });

        // 选择文件按钮监听器
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

        // 关于按钮监听器
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAbout();
            }
        });

        // 退出按钮监听器
        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 请求系统内容提供器，通过选择文件来获取其路径
     */
    private void openResource() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        startActivityForResult(intent, 1);
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

    /**
     * 4.4及以上系统使用这个方法处理返回的文件路径信息
     *
     * @param data 文件路径信息
     */
    @TargetApi(19)
    private void handleOnKitKat(Intent data) {
        String path = null;
        Uri uri = data.getData();
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

    /**
     * 4.4以下系统使用这个方法处理返回的文件路径信息
     *
     * @param data 文件路径信息
     */
    private void handleBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String path = getFilePath(uri, null);
        setPath(path);
    }

    /**
     * 通过URI获取文件绝对路径
     *
     * @param uri
     * @param selection
     * @return
     */
    private String getFilePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 把选择好的路径设置到输入框
     *
     * @param path 文件路径
     */
    private void setPath(String path) {
        edit_path.setText(path);
        edit_path.setSelection(path.length());
    }

    /**
     * 更新信息显示区
     *
     * @param info 需要显示的信息
     */
    private void updateInfo(String info) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String updatedInfo = text_info.getText() + simpleDateFormat.format(date) + "> " + info + "\n";
        text_info.setText(updatedInfo);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    /**
     * 展示关于对话框
     */
    private void showAbout() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("关于");
        dialog.setMessage("一款基于哈夫曼编码的简单压缩与解压软件。（作者：王聪  联系方式：1330792337@qq.com）");
        dialog.setIcon(R.drawable.ic_info_black_48dp);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    /**
     * 运行时权限申请
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
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
