package cn.kiway.yqyd.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.progress.ProgressMonitor;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Android on 2016/4/20.
 */
public class FileUtils {

    //更新解压成功后往sd卡写入这次的版本号
    public static void writeFileData(String message) {
        try {
            File file = new File(Environment.getExternalStorageDirectory() + "/yqyd/yqyd/version");
            if (!file.exists())
                file.mkdir();
            File file1 = new File(file.getPath(), "zip_ls.json");
            if (file1.exists())
                file1.delete();
            file1.createNewFile();
            FileOutputStream fos = new FileOutputStream(file1);
            fos.write(message.getBytes());
            fos.close();
            System.out.println("写入成功：");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建私有文件夹
    public static String createZipFloder() {
        File file = new File(Environment.getExternalStorageDirectory(),
                "yqyd");
        if (!file.exists())
            file.mkdirs();
        File file1 = new File(Environment.getExternalStorageDirectory(), ".nomedia");
        if (!file1.exists())
            file1.mkdirs();
        return file.getAbsolutePath();
    }

    //创建私有文件夹
    public static String createDocFloder() {
        File file1 = new File(createZipFloder(), "doc");
        if (!file1.exists())
            file1.mkdirs();
        return file1.getAbsolutePath();
    }

    //解压文件
    public static void unZipFileWithProgress(String zipFilePath, final String filePath, final Handler handler,
                                             boolean isDeleteZip) {
        final File zipFile = new File(zipFilePath);
        ZipFile zFile = null;
        try {
            zFile = new ZipFile(zipFile);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        File destDir = new File(filePath);
        if (destDir.isDirectory() && !destDir.exists()) {
            destDir.mkdir();
        }
        final ProgressMonitor progressMonitor = zFile.getProgressMonitor();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = null;
                try {
                    if (handler == null) {
                        return;
                    }
                    handler.sendEmptyMessage(CompressStatus.START);
                    while (true) {
                        // 每隔50ms,发送一个解压进度出去
                        Thread.sleep(50);
                        msg = new Message();
                        msg.what = CompressStatus.HANDLING;
                        msg.obj = "正在解压" + progressMonitor.getPercentDone();
                        handler.sendMessage(msg); //通过 Handler将进度扔出去
                        if (progressMonitor.getPercentDone() >= 100) {
                            break;
                        }
                    }
                    handler.sendEmptyMessageDelayed(CompressStatus.COMPLETED, 1500);
                } catch (InterruptedException e) {
                    handler.sendEmptyMessage(CompressStatus.ERROR);
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        zFile.setRunInThread(false); //true 在子线程中进行解压 , false主线程中解压
        try {
            zFile.extractAll(filePath); //将压缩文件解压到filePath中...
        } catch (ZipException e) {
            e.printStackTrace();
        } finally {
            if (isDeleteZip) {
                zipFile.delete();//将原压缩文件删除
            }
        }
    }

    public static void openFile(Context context, String filePath) {
        filePath = "file://" + filePath;
        String type = filePath.split("\\.")[1].toLowerCase();
        String typeOpenFile = "*";
        Logger.log("*****************" + filePath);
        Logger.log("*****************" + type);
        if (type.equals("pdf"))
            typeOpenFile = "application/pdf";
        else if (type.equals("ppt") || type.equals("pptx"))
            typeOpenFile = "application/vnd.ms-powerpoint";
        else if (type.equals("doc") || type.equals("docx") || type.equals("docm") || type.equals("dotx") || type
                .equals("dotm"))
            typeOpenFile = "application/msword";
        else if (type.equals("xlsx") || type.equals("xlsm") || type.equals("xltx"))
            typeOpenFile = "application/vnd.ms-excel";
        else if (type.equals("mp3") || type.equals("amr") || type.equals("ogg") || type.equals("wav")) {
            typeOpenFile = "audio/*";
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(Uri.parse(filePath), "audio/*");
//            context.startActivity(intent);
//            Intent intent = new Intent(context, VideoAcitivy.class);
//            intent.putExtra("url", filePath);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        } else if (type.equals("mp4") || type.equals("3gp") || type.equals("avi") || type.equals("rmvb") || type
                .equals("mpg") | type.equals("rm") || type.equals("flv")) {
            typeOpenFile = "video/*";
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Uri uri = Uri.parse(filePath);
//            intent.setDataAndType(uri, "video/*");
//            context.startActivity(intent);
//            Intent intent = new Intent(context, VideoAcitivy.class);
//            intent.putExtra("url", filePath);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
        }
        if (!typeOpenFile.equals("")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse(filePath), typeOpenFile);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                if (typeOpenFile.equals("video/*") || typeOpenFile.equals("audio/*"))
                    Toast.makeText(context, "手机没有安装相关的播放器，请下载安装", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "手机没有安装相关的办公软件，请下载安装", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 封装不同的解压状态
     **/
    public class CompressStatus {
        public final static int START = 10000;//开始解压
        public final static int HANDLING = 10001;//解压中
        public final static int COMPLETED = 10002;//解压成功
        public final static int ERROR = 10003;//解压失败

        public final static String PERCENT = "PERCENT";
        public final static String ERROR_COM = "ERROR";
    }
}


