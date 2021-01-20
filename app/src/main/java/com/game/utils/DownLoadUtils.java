package com.game.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
        File saveFile = new File(FILE_URI+fileName);
        request.setDestinationUri(Uri.fromFile(saveFile));
        //获取下载管理器
        final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //一些配置
        //允许移动网络与WIFI下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        //是否在通知栏显示下载进度
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //设置可见及可管理
        /*注意，Android Q之后不推荐使用*/
        request.setVisibleInDownloadsUi(true);

        request.setTitle("下载中...");
//        request.setDescription(desc);

        //将任务加入下载队列
        assert downloadManager != null;
        final long id = downloadManager.enqueue(request);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //获取下载id
                long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (myDwonloadID == id) {
                    //获取下载uri
                    Uri uri = downloadManager.getUriForDownloadedFile(myDwonloadID);
                    lister.success(uri);
                }
            }
        };
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.registerReceiver(receiver, filter);
        }
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
    public DownLoadUtils installAPK( ) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //Android 7.0以上要使用FileProvider
        File file = new File(FILE_URI+fileName);
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