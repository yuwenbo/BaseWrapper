package usage.ywb.wrapper.net;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author yuwenbo
 * @version [ V.2.7.1  2019/12/12 ]
 */
public class OkHttpManager {

    private static OkHttpClient okHttpClient;

    private static String rootPath = "";
    
    public static <T> T createApi(Class<T> clazz){
        Retrofit retrofit = getRetrofit(rootPath);
        return retrofit.create(clazz);
    }

    public static <T> T createApi(Class<T> clazz, String baseUrl){
        Retrofit retrofit = getRetrofit(baseUrl);
        return retrofit.create(clazz);
    }

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(20000, TimeUnit.MILLISECONDS);
            builder.callTimeout(25000, TimeUnit.MILLISECONDS);
            builder.readTimeout(15000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(25000, TimeUnit.MILLISECONDS);

            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT);
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.addNetworkInterceptor(logInterceptor);
            builder.addInterceptor(new GlobalInterceptor());
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private static Gson getGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(new ReflectMethodAdapterFactory());
//        builder.registerTypeAdapterFactory(TypeAdapters.JSON_ELEMENT_FACTORY);
        return builder.create();
    }

    public static Retrofit getRetrofit(String baseUrl){
        Retrofit.Builder builder = new Retrofit.Builder();
        return builder.baseUrl(baseUrl)
                .client(OkHttpManager.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

}
