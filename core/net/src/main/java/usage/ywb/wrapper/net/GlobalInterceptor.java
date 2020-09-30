package usage.ywb.wrapper.net;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/16 ]
 */
public class GlobalInterceptor implements Interceptor {


    private String token="";
    
    private static final String TAG = "LogInterceptor";

    @NonNull
    @Override
    public okhttp3.Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        if (token == null) {
            Request request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Accept-Language", "zh")
                    .addHeader("content-type", "application/x-www-form-urlencoded")
                    .build();
            return chain.proceed(request);
        }

        Request oldRequest = chain.request();
        Request.Builder newRequestBuild;
        String method = oldRequest.method();
        StringBuilder postBodyString = new StringBuilder();
        if ("POST".equals(method)) {
            RequestBody oldBody = oldRequest.body();
            if (oldBody instanceof FormBody) {
                newRequestBuild = oldRequest.newBuilder();
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("REP_SESSION_TOKEN", token);
                FormBody formBody = (FormBody) oldBody;
                for (int i = 0, len = formBody.size(); i < len; i++) {
                    formBodyBuilder.add(formBody.encodedName(i), formBody.encodedValue(i));
                }
                newRequestBuild.post(formBodyBuilder.build());
            } else if (oldBody instanceof MultipartBody) {
                MultipartBody oldBodyMultipart = (MultipartBody) oldBody;
                List<MultipartBody.Part> oldPartList = oldBodyMultipart.parts();
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"), token);
                for (MultipartBody.Part part : oldPartList) {
                    builder.addPart(part);
                    postBodyString.append(bodyToString(part.body())).append("\n");
                }
                postBodyString.append(bodyToString(requestBody1)).append("\n");
//              builder.addPart(oldBody);  //不能用这个方法，因为不知道oldBody的类型，可能是PartMap过来的，也可能是多个Part过来的，所以需要重新逐个加载进去
                builder.addPart(requestBody1);
                newRequestBuild = oldRequest.newBuilder();
                newRequestBuild.post(builder.build());
                Log.e(TAG, "MultipartBody," + oldRequest.url());
            } else {
                newRequestBuild = oldRequest.newBuilder();
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                formBodyBuilder.add("REP_SESSION_TOKEN", token);
                newRequestBuild.post(formBodyBuilder.build());
            }
        } else {
            // 添加新的参数
            HttpUrl.Builder commonParamsUrlBuilder = oldRequest.url()
                    .newBuilder()
                    .scheme(oldRequest.url().scheme())
                    .host(oldRequest.url().host())
                    .addQueryParameter("REP_SESSION_TOKEN", token);
            newRequestBuild = oldRequest.newBuilder()
                    .method(oldRequest.method(), oldRequest.body())
                    .url(commonParamsUrlBuilder.build());
        }
        Request newRequest = newRequestBuild
                .addHeader("Accept", "application/json")
                .addHeader("Accept-Language", "zh")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        long startTime = System.currentTimeMillis();
        okhttp3.Response response = chain.proceed(newRequest);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        ResponseBody body = response.body();
        if (body != null) {
            okhttp3.MediaType mediaType = body.contentType();
            String content = body.string();
            int httpStatus = response.code();
            String logSB = "-------start:" + method + "|" +
                    newRequest.toString() + "\n|" +
                    (method.equalsIgnoreCase("POST") ? "post参数{" + postBodyString + "}\n|" : "") +
                    "httpCode=" + httpStatus + ";Response:" + content + "\n|" +
                    "----------End:" + duration + "毫秒----------";
            Log.d(TAG, logSB);
            return response.newBuilder()
                    .body(okhttp3.ResponseBody.create(mediaType, content))
                    .build();
        }
        return response;
    }

    private static String bodyToString(final RequestBody request) {
        try {
            final Buffer buffer = new Buffer();
            if (request != null)
                request.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
