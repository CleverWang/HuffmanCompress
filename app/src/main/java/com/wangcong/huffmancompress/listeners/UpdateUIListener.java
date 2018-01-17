package com.wangcong.huffmancompress.listeners;

/**
 * Created by 13307 on 2018/1/17.
 */

/**
 * 执行后台解压缩任务时，用于更新主界面的监听器
 */
public interface UpdateUIListener {
    /**
     * 开始执行后台解压缩任务时主界面UI操作
     */
    void onStart();

    /**
     * 执行后台解压缩任务中更新主界面UI操作
     *
     * @param info 后台解压缩任务传过来的信息
     */
    void onUpdate(String info);

    /**
     * 后台解压缩任务执行完毕主界面UI操作
     */
    void onFinish();
}
