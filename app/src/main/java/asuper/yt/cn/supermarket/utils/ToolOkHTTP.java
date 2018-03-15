package asuper.yt.cn.supermarket.utils;

import android.os.Handler;
import android.os.Looper;

import com.yatang.xc.oles.web.util.SecurityUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import asuper.yt.cn.supermarket.base.ApiConfig;
import asuper.yt.cn.supermarket.base.BaseActivity;
import asuper.yt.cn.supermarket.base.Config;
import asuper.yt.cn.supermarket.base.YTApplication;
import asuper.yt.cn.supermarket.modules.login.LoginActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Sink;
import okio.Source;
import supermarket.cn.yt.asuper.ytlibrary.utils.ActivityManager;
import supermarket.cn.yt.asuper.ytlibrary.utils.ToastUtil;

/**
 * Created by liaoqinsen on 2017/4/26 0026.
 */

public class ToolOkHTTP {

    public static String session = null;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            })
            .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .writeTimeout(10000, TimeUnit.MILLISECONDS)
            .build();

    public static void post(String url, Map<String, Object> map, final OKHttpCallBack callback) {
        if(!ToolNetwork.getInstance().isConnected()){
            handler.post(() -> ToastUtil.error("网络连接错误，请检查网络"));
            if(callback != null) callback.onFailure();
            return;
        }
        if(map == null) map = new HashMap<>();
        map.put("timestamp", new Date().getTime());
        Map<String, ?> finalMap = gainSign(map);
        Set<String> keys = finalMap.keySet();
        logMap = finalMap;
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : keys) {
            Object object = finalMap.get(key);
            if (object == null) continue;
            builder.add(key, object.toString());
        }
        FormBody body = builder.build();
        try {
            final Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
            client.newCall(requestBuilder.build()).enqueue(new CommonCallBack(callback,url));
        }catch (final Exception e){
            handler.post(() -> ToastUtil.error(e.getMessage()));
            if(callback != null) {
                callback.onFailure();
            }
        }
    }

    private static Map<String,?>  logMap;

    public static void upload(HashMap<String, Object> map, final OKHttpCallBack callback) {
        upload(map,callback,null);
    }
    public static void upload(HashMap<String, Object> map, final OKHttpCallBack callback,final ProgressRequestListener progress) {
        if(!ToolNetwork.getInstance().isConnected()){
            ToastUtil.error("网络连接错误，请检查网络");
            if(callback != null) callback.onFailure();
            return;
        }
        map.put("timestamp", new Date().getTime());
        Map<String, ?> finalMap = gainSign(map);
        logMap = finalMap;
        Set<String> keys = finalMap.keySet();
        MultipartBody.Builder mb = new MultipartBody.Builder().setType(MultipartBody.FORM);
        final long[] totalAndWritten = new long[]{0,0};
        for (String key : keys) {
            Object object = finalMap.get(key);
            if(object == null) continue;
            if (object instanceof File) {
                File file = (File) object;
                RequestBody fileBody;
                if(progress != null) {
                    ProgressRequestListener progressRequestListener = new ProgressRequestListener() {
                        @Override
                        public void onRequestProgress(long byteCount, long bytesWritten, long contentLength, boolean done) {
                            totalAndWritten[1] += byteCount;
                            progress.onRequestProgress(byteCount, totalAndWritten[1], totalAndWritten[0], totalAndWritten[1] >= totalAndWritten[0]);
                        }
                    };
                    fileBody = new ProgressRequestBody(RequestBody.create(MediaType.parse("media/type"), file),progressRequestListener);
                }else{
                    fileBody = RequestBody.create(MediaType.parse("media/type"), file);
                }
                try {
                    totalAndWritten[0] += fileBody.contentLength();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mb.addFormDataPart(key, file.getName(), fileBody);
            } else {
                mb.addFormDataPart(key, object.toString());
            }
        }

        MultipartBody mBody = mb.build();

        Request.Builder request = new Request.Builder().url(Config.HOST + "fileupload/multi.htm").post(mBody);
        if (session != null) request.addHeader("cookie", session);
        client.newCall(request.build()).enqueue(new CommonCallBack(callback,Config.HOST + "fileupload/multi.htm"));
    }

    public static Map<String, ?> gainSign(Map<String, Object> map) {
        Map<String, Object> parmsMap = new HashMap<>();
        for (String key : map.keySet()) {
            if (map.get(key) != null && !map.get(key).toString().isEmpty() && !(map.get(key) instanceof File)) {
                parmsMap.put(key, map.get(key));
            }
        }
        parmsMap.put("key", Config.SIGN_KEY);
        String mySign = SecurityUtil.buildRequestMysign(parmsMap);
        map.put("sign", mySign);
        return map;
    }

    public interface OKHttpCallBack {
        void onSuccess(JSONObject response);
        void onFailure();
    }

    public static class CommonCallBack implements Callback {

        public OKHttpCallBack callBack;
        private String headerUrl ;

        public CommonCallBack(OKHttpCallBack callBack,String url) {
            this.callBack = callBack;
            this.headerUrl = url;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            e.printStackTrace();
            ToolLog.e("错误：" + e.getMessage());
            error(e.getMessage());
            if (callBack != null) handler.post(() -> callBack.onFailure());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String resString = response.body().string();
            ToolLog.i(call.request().url().toString()+"="+logMap.toString());
            ToolLog.json(resString);
            JSONObject res = null;
            if (callBack == null) return;
            try {
                res = new JSONObject(resString);
            } catch (Exception e) {
                if(response.code()==404){
                    error("404服务器暂不提供该服务！");
                }else {
                    error("服务器返回信息异常:"+response.code()+"!");
                }
                handler.post(() -> callBack.onFailure());
                return;
            }
            boolean success = true;
            try {
                success = res.getBoolean("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final JSONObject finalRes = res;
            if (!success) {
                if (ApiConfig.apis.contains(headerUrl)) {  // 在白名单内的接口处理回调
                    handler.post(() -> callBack.onSuccess(finalRes));
                    return;
                }
                String err = res.optString("errMsg","");
                String err2 = res.optString("errorMessage","");
                String err3 = err+err2;
                error(err3);
                if (res.optString("code").equals("session_timeout")) {
                    if (ActivityManager.activityTop() instanceof LoginActivity) return;
                    else YTApplication.getOperation().forward(LoginActivity.class);
                }
                handler.post(() -> callBack.onFailure());
                return;
            }
            handler.post(() -> callBack.onSuccess(finalRes));
        }
    }

    private static void error(final String msg){
        handler.post(() -> ToastUtil.error(msg));
    }

    public interface ProgressRequestListener {
        void onRequestProgress(long byteCount,long bytesWritten, long contentLength, boolean done);
    }
    public static   class ProgressRequestBody extends RequestBody {
        //实际的待包装请求体
        private final RequestBody requestBody;
        //进度回调接口
        private final ProgressRequestListener progressListener;
        //包装完成的BufferedSink
        private BufferedSink bufferedSink;

        /**
         * 构造函数，赋值
         * @param requestBody 待包装的请求体
         * @param progressListener 回调接口
         */
        public ProgressRequestBody(RequestBody requestBody, ProgressRequestListener progressListener) {
            this.requestBody = requestBody;
            this.progressListener = progressListener;
        }

        /**
         * 重写调用实际的响应体的contentType
         * @return MediaType
         */
        @Override
        public MediaType contentType() {
            return requestBody.contentType();
        }

        /**
         * 重写调用实际的响应体的contentLength
         * @return contentLength
         * @throws IOException 异常
         */
        @Override
        public long contentLength() throws IOException {
            return requestBody.contentLength();
        }

        /**
         * 重写进行写入
         * @param sink BufferedSink
         * @throws IOException 异常
         */
        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            if (bufferedSink == null) {
                //包装
                bufferedSink = Okio.buffer(sink(sink));
            }
            //写入
            requestBody.writeTo(bufferedSink);
            //必须调用flush，否则最后一部分数据可能不会被写入
            bufferedSink.flush();

        }

        /**
         * 写入，回调进度接口
         * @param sink Sink
         * @return Sink
         */
        private Sink sink(Sink sink) {
            return new ForwardingSink(sink) {
                //当前写入字节数
                long bytesWritten = 0L;
                //总字节长度，避免多次调用contentLength()方法
                long contentLength = 0L;

                @Override
                public void write(Buffer source, long byteCount) throws IOException {
                    super.write(source, byteCount);
                    if (contentLength == 0) {
                        //获得contentLength的值，后续不再调用
                        contentLength = contentLength();
                    }
                    //增加当前写入的字节数
                    bytesWritten += byteCount;
                    //回调
                    progressListener.onRequestProgress(byteCount,bytesWritten, contentLength, bytesWritten >= contentLength);
                }
            };
        }
    }
}
