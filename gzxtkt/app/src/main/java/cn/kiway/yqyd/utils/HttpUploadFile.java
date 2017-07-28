package cn.kiway.yqyd.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by Administrator on 2017/5/10.
 */

public class HttpUploadFile {


    public static String BaseUrl = "http://trtxkt.qgjydd.com/jxt/servlet/";//基础地址
    public static String loginUrl = BaseUrl + "login?utype={type}&uaccount={userName}&upassword=";//登录
    public static String uploadUserPicUrl = BaseUrl + "v1/app/upload";//上传图像
    public static String checkVersion = BaseUrl + "static/version/zip_ls.json";//检查更新

    public static RequestBody createCustomRequestBody(final MediaType contentType, final File file) {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return file.length();
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                Source source;
                try {
                    source = Okio.source(file);
                    Buffer buf = new Buffer();
                    for (long readCount; (readCount = source.read(buf, 2048)) != -1; ) {
                        sink.write(buf, readCount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public static Request returnUploadImgRequser(File fileName, String fileType) throws UnsupportedEncodingException {
        Logger.log("图片地址：：：" + fileName.getPath());
        Logger.log("图片地址：：：：" + fileName.getName());
        Logger.log("图片地址：：：" + fileName.length() / 1024 + "K");
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file", fileName.getName(), createCustomRequestBody(MediaType.parse
                        (fileType),
                fileName));
        MultipartBody requestBody = builder.build();
        return new Request.Builder()
                .url(uploadUserPicUrl)//地址
                .post(requestBody)//添加请求体
                .build();
    }

    public class FileType {
        public static final String Image = "image/png";
        public static final String Mp3 = "audio/mp3";
        public static final String Mp4 = "vidoe/mp4";
    }
}
