package com.game.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import com.game.utils.IDownloadlister;

import java.io.File;


/**
 * Function: 文件下载工具类
 */
public  class DownLoadUtils {


    //测试url，下载链接
    private String url = "https://imtt.dd.qq.com/16891/apk/581B4C52F17C5F3A512C2CFBC88EB69F.apk";
    //保存目录
    private String FILE_URI = Environment.getExternalStorageDirectory()+"/downFile/";
    private IDownloadlister lister = null;
    //文件名
    private String fileName = "test.apk";
    //Context
    private Context context;

    static DownLoadUtils m_instance = null;

    public void download() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        //创建下载任务，url即任务链接
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径及文件名
//        request.setDestinationInExternalPublicDir(FILE_URI, fileName);
        String SavePath = FILE_URI+fileName;
        if(new File(SavePath).exists())//下载前判断是否有了 先删除
        {
            new File(SavePath).delete();
        }
        File saveFile = new File(SavePath);
        request.setDestinationUri(Uri.fromFile(saveFile));
        //获取下载管理器
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //一些配置
        //允许移动网络与WIFI下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //是否在通知栏显示下载进度
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 允许被媒体库扫描到；
        request.allowScanningByMediaScanner();
        //设置可见及可管理
        /*注意，Android Q之后不推荐使用*/
        request.setVisibleInDownloadsUi(true);

        request.setTitle("下载中...");
//        request.setDescription(desc);
        // 开始下载；
        if (lister != null) {
            lister.onDownloadStart();
        }
        //将任务加入下载队列
        assert downloadManager != null;
        final long id = downloadManager.enqueue(request);

        // 监听下载进度；
        listenDownloadState(downloadManager, id);
    }


    private void listenDownloadState(final DownloadManager manager, final long loadId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 实例化一个查询对象；
                DownloadManager.Query query = new DownloadManager.Query();
                //  通过downloadId 确定查询对象；
                query.setFilterById(loadId);

                Cursor cursor = null;
                boolean listen = true;
                while (listen) {
                    // 查询；
                    cursor = manager.query(query);
                    // 确定是否有查询对象；
                    if (cursor != null && cursor.moveToFirst()) {
                        switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            case DownloadManager.STATUS_PENDING:
                                // 在等待下载；
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                // 下载过程中被暂停了
                                if (lister != null) {
                                    lister.onDownloadPause();
                                }
                                break;
                            case DownloadManager.STATUS_RUNNING:
                                // 下载状态中；
                                if (lister != null) {
                                    // 需要下载的比特数；
                                    double totleSize = (double) cursor.getLong(cursor.getColumnIndex
                                            (DownloadManager
                                                    .COLUMN_TOTAL_SIZE_BYTES));
                                    // 已经下载的比特数；
                                    double currentSize = (double) cursor.getLong(cursor.getColumnIndex
                                            (DownloadManager
                                                    .COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    //  占的百分比；
                                    int progress = (int) ((currentSize / totleSize) * 100);
                                    //  将百分比数据回调给调用者；
                                    lister.onDownloadRunning(progress);
                                }
                                break;
                            case DownloadManager.STATUS_SUCCESSFUL:
                                // 下载成功；
                                listen = false;
                                if (lister != null) {
                                    Uri uri = manager.getUriForDownloadedFile(loadId);
                                    String path = FILE_URI+fileName;
                                    lister.onSuccess(path);
                                }
                                break;
                            case DownloadManager.STATUS_FAILED:
                                listen = false;
                                // 下载失败；
                                if (lister != null) {
                                    lister.onFailed();
                                }
                                break;
                        }

                    }
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }

            }
        }).start();


    }

    public static DownLoadUtils builder() {
        if (m_instance==null)
        {
            m_instance = new DownLoadUtils();
        }
        return m_instance;
    }

    public DownLoadUtils setUrl(String url) {
        this.url = url;
        return this;
    }

    public DownLoadUtils setSavePath(String path) {
        this.FILE_URI = path;
        return this;
    }

    public DownLoadUtils setLister(IDownloadlister lister) {
        this.lister = lister;
        return this;
    }


    public DownLoadUtils setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public DownLoadUtils setContext(Context context) {
        this.context = context;
        return this;
    }
    //安装apk
    public DownLoadUtils installAPK( String filepath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Android 7.0以上要使用FileProvider
        File file = new File(filepath);
        if (Build.VERSION.SDK_INT >= 24) {
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(context, "com.android.file.provider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {

            intent.setDataAndType(
                    Uri.fromFile(
                            file
                    ), "application/vnd.android.package-archive"
            );
        }
        context.startActivity(intent);
        return this;
    }

}