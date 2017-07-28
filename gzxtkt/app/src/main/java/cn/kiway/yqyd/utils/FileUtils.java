package cn.kiway.yqyd.utils;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

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
                "gzxtkt");
        if (!file.exists())
            file.mkdirs();
        return file.getAbsolutePath();
    }

    //解压文件
    public static void unZipFileWithProgress(String zipFilePath, final String filePath, final Handler handler,
                                             final boolean isDeleteZip) {
        final File zipFile = new File(zipFilePath);
        ZipFile zFile = null;
        try {
            zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset("GBK");
            if (!zFile.isValidZipFile()) { //
                throw new ZipException("exception!");
            }
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
                Bundle bundle = null;
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
                    handler.sendEmptyMessage(CompressStatus.COMPLETED);
                } catch (InterruptedException e) {
                    handler.sendEmptyMessage(CompressStatus.ERROR);
                    e.printStackTrace();
                } finally {
                    if (isDeleteZip) {
                        zipFile.delete();//将原压缩文件删除
                    }
                }
            }
        });
        thread.start();
        zFile.setRunInThread(true); //true 在子线程中进行解压 , false主线程中解压
        try {
            zFile.extractAll(filePath); //将压缩文件解压到filePath中...
        } catch (ZipException e) {
            e.printStackTrace();
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


