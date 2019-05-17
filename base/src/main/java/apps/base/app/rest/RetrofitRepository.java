package apps.base.app.rest;

import java.util.Map;

import apps.base.app.models.ServerResponse;
import apps.base.app.utils.ConfigManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class RetrofitRepository implements ObservablesRepository {

    public static final String TAG = "RetrofitRepository";

    private final RestAPI restAPI;

    public RetrofitRepository(Retrofit retrofit) {
        this.restAPI = retrofit.create(RestAPI.class);
    }

    @Override public Observable<ServerResponse<String>> getServerList() {
        return restAPI.getServerList(ConfigManager.getField("publicServerList"))
                .subscribeOn(Schedulers.io())
                .map(jsonElement -> {
                    ServerResponse<String> response = new ServerResponse<>(true, jsonElement.toString());
                    response.setData(jsonElement.toString());
                    return response;
                })
                .onErrorReturn(throwable -> new ServerResponse<>(false, throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override public Observable<ServerResponse<String>> getMainHubs() {
        return restAPI.getMainHubsList(ConfigManager.getField("hubsUri"))
                .subscribeOn(Schedulers.io())
                .map(jsonElement -> {
                    ServerResponse<String> response = new ServerResponse<>(true, jsonElement.toString());
                    response.setData(jsonElement.toString());
                    return response;
                })
                .onErrorReturn(throwable -> new ServerResponse<>(false, throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override public Observable<ServerResponse<String>> login(Map<String, String> loginFields) {
        return restAPI.login(ConfigManager.getField("login"), loginFields)
                .subscribeOn(Schedulers.io())
                .map(jsonElement -> {
                    ServerResponse<String> response = new ServerResponse<>(true, jsonElement.toString());
                    response.setData(jsonElement.toString());
                    return response;
                })
                .onErrorReturn(throwable -> new ServerResponse<>(false, throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override public Observable<ServerResponse<String>> signUp(String email, String password, String type, String status, String expirationDate, String token) {
        return restAPI.signUp(email, password, type, status, expirationDate, token)
                .subscribeOn(Schedulers.io())
                .map(jsonElement -> {
                    ServerResponse<String> response = new ServerResponse<>(true, jsonElement.toString());
                    response.setData(jsonElement.toString());
                    return response;
                })
                .onErrorReturn(throwable -> new ServerResponse<>(false, throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override public Observable<ServerResponse<String>> getServerListByUser(String path, Map<String, String> loginFields) {
        return restAPI.getServerListByUser(path, loginFields)
                .subscribeOn(Schedulers.io())
                .map(jsonElement -> {
                    ServerResponse<String> response = new ServerResponse<>(true, jsonElement.toString());
                    response.setData(jsonElement.toString());
                    return response;
                })
                .onErrorReturn(throwable -> new ServerResponse<>(false, throwable.getMessage()))
                .observeOn(AndroidSchedulers.mainThread());
    }


}
