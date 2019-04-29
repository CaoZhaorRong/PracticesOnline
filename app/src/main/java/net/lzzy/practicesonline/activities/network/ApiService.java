package net.lzzy.practicesonline.activities.network;

import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ValueRange;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 * @author lzzy_gxy
 * @date 2019/4/19
 * Description:
 */
public class ApiService {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    public static String get(String address) throws IOException {
        //region

        //1.通过在 URL 上调用 openConnection 方法创建连接对象
        URL url = new URL(address);
        //此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类的子类HttpURLConnection,
        //故此处最好将其转化为HttpURLConnection类型的对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        //2.处理设置参数和一般请求属性
        //2.1设置参数
        //可以根据请求的需要设置参数
        try {
            connection.setRequestMethod("GET");
            //默认为GET 所以GET不设置也行
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            //请求超时时间
            connection.setRequestProperty("accept", "*/*");
            //2.2请求属性
            //设置通用的请求属性 更多的头字段信息可以查阅HTTP协议
            connection.setRequestProperty("connection", "keep-Alive");
            //3.使用 connect 方法建立到远程对象的实际连接。
            connection.connect();
            //4.远程对象变为可用。远程对象的头字段和内容变为可访问。
            //4.1获取HTTP 响应消息获取状态码

            BufferedReader reader;
            //4.3获取响应正文
            StringBuffer resultBuffer = new StringBuffer();
            String tempLine;
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine).append("\n");
            }
            reader.close();
            return resultBuffer.toString();

        } finally {
            connection.disconnect();
        }

        //endregion

    }

    public static void post(String address, JSONObject json) throws IOException {

        //1.通过在 URL 上调用 openConnection 方法创建连接对象
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //2.处理设置参数和一般请求属性
        //2.1设置参数
        //可以根据请求的需要设置参数
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        conn.setConnectTimeout(5000);
        //请求超时时间

        //2.2请求属性
        //设置通用的请求属性 消息报头 即设置头字段 更多的头字段信息可以查阅HTTP协议
        conn.setRequestProperty("Content-Type", "application/json");
        byte[] data = json.toString().getBytes(StandardCharsets.UTF_8);
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setUseCaches(false);

        //2.3设置请求正文 即要提交的数据
        try (OutputStream stream = conn.getOutputStream()) {
            stream.write(data);
            stream.flush();
        } finally {
            conn.disconnect();
        }
    }

    public static String okGet(String address) throws IOException {
        Request request = new Request.Builder()
                .url(address)
                .build();

        try (Response response = CLIENT.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("错误码：" + response.code());
            }
        }
    }

    public static String okget(String address, String args, HashMap<String,Object> hashMap) throws IOException {
        if (TextUtils.isEmpty(args)){
            address=address.concat("?").concat(args);
        }
        Request.Builder builder=new Request.Builder().url(address);
        if (hashMap!=null && hashMap.size()>0){
            for (Object o:hashMap.entrySet()){
                Map.Entry entry=(Map.Entry) o;
                Object val =entry.getValue();
                String key=entry.getKey().toString();
                if (val instanceof List){
                    builder=builder.header(key,val.toString());
                }else if(val instanceof  List) {
                    for (String v : (List<String>) val) {
                        builder = builder.addHeader(key, v);
                    }

                }
            }
        }
        Request request=builder.build();
        try(Response response=CLIENT.newCall(request).execute()){
            if (response.isSuccessful()){
                return response.body().string();
            }else {
                throw  new IOException("错误码："+response.code());
            }
        }
    }
    @SuppressWarnings("unchecked")
    private static <T> T cast(Object obj){
        return (T) obj;
    }

    public  static int okPost(String address,JSONObject json) throws IOException {
        RequestBody body=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
                return response.code();
        }
    }
    public static  String okRequest(String address,JSONObject json)throws IOException{
        RequestBody body=RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                json.toString()
        );
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return response.body().string();
        }
    }
}





