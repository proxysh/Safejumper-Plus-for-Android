package apps.base.app.dagger.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import apps.base.app.BaseAppApplication;
import apps.base.app.BuildConfig;
import apps.base.app.dagger.scope.PerActivity;
import apps.base.app.rest.RetrofitRepository;
import apps.base.app.utils.ConfigManager;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    @Provides @PerActivity OkHttpClient provideOkHttpClient() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }

        return builder
                .readTimeout(45, TimeUnit.SECONDS)
                .connectTimeout(45, TimeUnit.SECONDS)
                .build();
    }

    @Provides @PerActivity Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides @PerActivity Retrofit provideRetrofit(OkHttpClient client, Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(ConfigManager.<String>getField("ENDPOINT"))
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    @Provides @PerActivity RetrofitRepository provideRepository(Retrofit retrofit) {
        return new RetrofitRepository(retrofit);
    }

}
