package apps.base.app.service;


import java.util.concurrent.TimeUnit;

import apps.base.app.BuildConfig;
import apps.base.app.rest.RestAPI;
import apps.base.app.utils.ConfigManager;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {

    private static ApiService instance = null;
    private static RestAPI apiService = null;
    private static final String BASE_URL = ConfigManager.getField("ENDPOINT");


    private ApiService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient().newBuilder()
                        .addInterceptor(chain -> {
                            Request originalRequest = chain.request();
                            final Request.Builder requestBuilder = originalRequest.newBuilder();
                            return chain.proceed(requestBuilder.build());
                        })
                        .addNetworkInterceptor(interceptor)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
                .create(RestAPI.class);
    }

    public static ApiService getInstance(){
        if (instance == null){
            instance = new ApiService();
        }
        return instance;
    }

    public RestAPI getApiService() {
        return apiService;
    }
}
