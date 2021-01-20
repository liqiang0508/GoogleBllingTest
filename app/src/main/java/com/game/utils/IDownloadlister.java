package com.game.utils;

import android.net.Uri;

public interface IDownloadlister {
    // 开始下载
    void onDownloadStart();
    // 下载暂停
    void onDownloadPause();
    // 下载进行中（参数为下载的百分比）
    void onDownloadRunning(int current);
    // 下载成功
    void onSuccess(String filepath);
    // 下载失败
    void onFailed();
}

