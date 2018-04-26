package com.fpliu.newton.http;

import com.fpliu.newton.http.cookie.MemoryCookieJar;
import com.fpliu.newton.http.interceptor.LogInterceptor;
import okhttp3.*;
import okhttp3.internal.platform.Platform;

import java.io.*;
import java.net.URLEncoder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 基于OKHttp的网络请求接口
 *
 * @author 792793182@qq.com 2016-06-11
 */
public final class OKHttpRequest {

    private static final String TAG = OKHttpRequest.class.getSimpleName();

    private static OkHttpClient okHttpClient = new DefaultConfig().createOkHttpClientBuilder().build();

    private static Logger logger = (message, throwable) -> Platform.get().log(Platform.INFO, message, throwable);

    private OKHttpRequest() {
    }

    public static void setConfig(Config config) {
        okHttpClient = config.createOkHttpClientBuilder().build();
    }

    public static void setLogger(Logger logger) {
        OKHttpRequest.logger = logger;
    }

    /**
     * 异步GET请求
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        URL的参数
     * @param callback      请求的回调
     */
    public static void asyncGet(String url, String authorization, Map<String, String> params, Callback callback) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(makeUrl(url, params)).get().tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步GET请求
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        URL的参数
     * @param callback      请求的回调
     */
    public static void asyncGet(String url, String authorization, Callback callback, String... params) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(makeUrl(url, params)).get().tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步POST请求，请求体是表单
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        请求参数
     * @param callback      请求的回调
     */
    public static void asyncPostForm(String url, String authorization, Map<String, String> params, Callback callback) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(getFormBody(params)).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步POST请求，请求体是表单
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        请求参数
     * @param callback      请求的回调
     */
    public static void asyncPostForm(String url, String authorization, Callback callback, String... params) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(getFormBody(params)).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步POST请求，请求体是JSON
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param json          JSON字符串，使用String类型可以支持GSON、fastJson、json-lib等库的转化，而不局限于一种
     * @param callback      请求的回调
     */
    public static void asyncPostJson(String url, String authorization, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(requestBody).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步POST请求，请求体是XML
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param xml           XML字符串
     * @param callback      请求的回调
     */
    public static void asyncPostXml(String url, String authorization, String xml, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml;charset=UTF-8"), xml);
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(requestBody).build());
        call.enqueue(callback);
    }

    /**
     * 异步上传文件（二进制数据流）
     *
     * @param url             资源路径
     * @param filePath        本地文件路径
     * @param requestCallBack 请求回调
     */
    public static void asyncPostFile(String url, String authorization, String filePath, Callback requestCallBack) {
        if (isEmpty(filePath) && requestCallBack != null) {
            requestCallBack.onFailure(null, new FileNotFoundException("文件路径不能为空"));
            return;
        }

        File file = new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(requestBody).tag(TAG).build());
        call.enqueue(requestCallBack);
    }

    /**
     * 异步POST请求，请求体是Multipart
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param parts         每一部分的列表
     * @param callback      请求的回调
     */
    public static void asyncPostMultipart(String url, String authorization, List<MultipartBody.Part> parts, Callback callback) {
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (parts != null) {
            for (MultipartBody.Part part : parts) {
                multipartBodyBuilder.addPart(part);
            }
        }
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).post(multipartBodyBuilder.build()).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步PUT请求，请求体是表单
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        请求参数对，语法糖
     * @param callback      请求的回调
     */
    public static void asyncPutForm(String url, String authorization, Map<String, String> params, Callback callback) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).put(getFormBody(params)).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步PUT请求，请求体是表单
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        请求参数
     * @param callback      请求的回调
     */
    public static void asyncPutForm(String url, String authorization, Callback callback, String... params) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).put(getFormBody(params)).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步PUT请求，请求体是JSON
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param json          JSON字符串，使用String类型可以支持GSON、fastJson、json-lib等库的转化，而不局限于一种
     * @param callback      请求的回调
     */
    public static void asyncPutJson(String url, String authorization, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), json);
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).put(requestBody).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步PUT请求，请求体是XML
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param xml           XML字符串
     * @param callback      请求的回调
     */
    public static void asyncPutXml(String url, String authorization, String xml, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/xml;charset=UTF-8"), xml);
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(url).put(requestBody).tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步DELETE请求
     *
     * @param url           请求资源的路径
     * @param authorization 验证用户，不需要验证的，传入空，即可
     * @param params        URL的参数
     * @param callback      请求的回调
     */
    public static void asyncDelete(String url, String authorization, Map<String, String> params, Callback callback) {
        Call call = okHttpClient.newCall(getRequestBuilder(authorization).url(makeUrl(url, params)).delete().tag(TAG).build());
        call.enqueue(callback);
    }

    /**
     * 异步下载
     *
     * @param url              资源路径
     * @param desFilePath      本地路径
     * @param needContinue     是否断点续传
     * @param progressCallback 带有进度的回调
     */
    public static void asyncDownload(String url, String authorization, String desFilePath, boolean needContinue, final ProgressCallback progressCallback) {
        if (isEmpty(desFilePath)) {
            FileNotFoundException exception = new FileNotFoundException("文件路径不能为空");
            if (progressCallback != null) {
                progressCallback.onFailure(null, exception);
            }
            return;
        }

        Request.Builder requestBuilder = getRequestBuilder(authorization).url(url).get().tag(TAG);
        final File file = new File(desFilePath);
        if (file.exists()) {
            if (needContinue) {
                final long startByte = file.length();
                //断点续传要用到的，指示下载的区间
                requestBuilder.header("RANGE", "bytes=" + startByte + "-");
                Call call = okHttpClient.newCall(requestBuilder.build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (progressCallback != null) {
                            progressCallback.onFailure(call, e);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        saveFile(response, response.body(), file, startByte, progressCallback);
                    }
                });
            } else {
                file.delete();
            }
        }
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (progressCallback == null) {
                    progressCallback.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                saveFile(response, response.body(), file, 0, progressCallback);
            }
        });
    }

    public static void saveFile(Response response, ResponseBody body, File destFile, long startByte, ProgressCallback progressCallback) {
        final long contentLength = body.contentLength();
        InputStream inputStream = body.byteStream();
        FileChannel channelOut = null;
        // 随机访问文件，可以指定断点续传的起始位置
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(destFile, "rwd");
            channelOut = randomAccessFile.getChannel();
            // 内存映射，直接使用RandomAccessFile，是用其seek方法指定下载的起始位置，使用缓存下载，在这里指定下载位置。
            MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE, startByte, contentLength);
            byte[] buffer = new byte[1024];
            long currentLength = 0;
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                currentLength += length;
                mappedBuffer.put(buffer, 0, length);

                if (progressCallback == null) {
                    logger.log("asyncDownload() currentLength = " + currentLength + ", contentLength = " + contentLength, null);
                } else {
                    progressCallback.onProgress(currentLength, contentLength);
                }
            }
        } catch (IOException e) {
            progressCallback.onFailure(null, e);
        } finally {
            try {
                inputStream.close();
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                progressCallback.onFailure(null, e);
            }
        }
    }

    /**
     * 组装URL
     *
     * @param url
     * @param params
     * @return
     */
    private static String makeUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null) {
            urlBuilder.append("?");
            Set<String> keys = params.keySet();
            for (String key : keys) {
                String value = params.get(key);
                if (isEmpty(key)) {
                    continue;
                }

                try {
                    urlBuilder.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(value, "UTF-8"))
                        .append("&");
                } catch (UnsupportedEncodingException e) {
                    logger.log("makeUrl()", e);
                }
            }
        }

        return urlBuilder.toString();
    }

    /**
     * 组装URL
     *
     * @param url
     * @param params
     * @return
     */
    private static String makeUrl(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null) {
            urlBuilder.append("?");
            int length = params.length / 2;
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    int index = 2 * i;
                    String key = params[index];
                    String value = params[index + 1];
                    if (isEmpty(key)) {
                        continue;
                    }

                    try {
                        urlBuilder.append(URLEncoder.encode(key, "UTF-8"))
                            .append("=")
                            .append(URLEncoder.encode(value, "UTF-8"))
                            .append("&");
                    } catch (UnsupportedEncodingException e) {
                        logger.log("makeUrl()", e);
                    }
                }
            }
        }

        return urlBuilder.toString();
    }

    private static boolean isEmpty(String str) {
        return (null == str) || ("" == str);
    }

    public static Request.Builder getRequestBuilder(String authorization) {
        return new Request.Builder()
            .addHeader("Accept", "*/*")
            .addHeader("Connection", "Keep-Alive")
            .addHeader("User-Agent", "Android")
            .addHeader("Referer", "service://www.fpliu.com")
            .addHeader("Authorization", authorization);
    }

    public static FormBody getFormBody(Map<String, String> params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                if (isEmpty(key)) {
                    continue;
                }
                formBodyBuilder.add(key, params.get(key));
            }
        }
        return formBodyBuilder.build();
    }

    public static FormBody getFormBody(String... params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            int length = params.length / 2;
            for (int i = 0; i < length; i++) {
                int index = 2 * i;
                String key = params[index];
                String value = params[index + 1];
                if (isEmpty(key)) {
                    continue;
                }
                formBodyBuilder.add(key, value);
            }
        }
        return formBodyBuilder.build();
    }

    public interface ProgressCallback extends Callback {
        /**
         * 进度回掉
         *
         * @param currentByte 当前字节
         * @param total       总字节
         */
        void onProgress(long currentByte, long total);
    }

    public interface Config {
        OkHttpClient.Builder createOkHttpClientBuilder();
    }

    public static class DefaultConfig implements Config {
        @Override
        public OkHttpClient.Builder createOkHttpClientBuilder() {
            return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)//设置链接超时时间
                .readTimeout(10, TimeUnit.SECONDS)   //设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS)  //设置写入超时时间
                .retryOnConnectionFailure(true)      //设置失败后重试
                .addInterceptor(new LogInterceptor())
                .cookieJar(new MemoryCookieJar());
        }
    }

    public interface Logger {
        void log(String message, Throwable throwable);
    }
}