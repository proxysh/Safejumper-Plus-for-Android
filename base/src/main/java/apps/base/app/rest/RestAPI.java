package apps.base.app.rest;

import com.google.gson.JsonElement;

import java.util.Map;

import apps.base.app.utils.ConfigManager;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RestAPI {

    @GET("{path}")
    Observable<JsonElement> getServerList(@Path(value = "path", encoded = true) String path);

    @GET("{path}")
    Observable<JsonElement> getMainHubsList(@Path(value = "path", encoded = true) String path);

    @POST("{path}")
    @FormUrlEncoded
    Observable<JsonElement> login(
            @Path(value = "path", encoded = true) String path,
            @FieldMap Map<String, String> loginFields
    );

    @POST("v1/user/add")
    @FormUrlEncoded
    Observable<JsonElement> signUp(
            @Field("email") String email,
            @Field("password") String password,
            @Field("type") String type,
            @Field("status") String status,
            @Field("expiration_date") String expirationDate,
            @Header("Authorization") String basicAuth
    );

    @GET("{path}")
    Single<ResponseBody> getServerConfigsGet(
            @Path(value = "path", encoded = true) String path,
            @QueryMap Map<String, String> serverParams
    );


    @POST("{path}")
    @FormUrlEncoded
    Single<ResponseBody> getServerConfigsPost(
            @Path(value = "path", encoded = true) String path,
            @QueryMap Map<String, String> serverParams,
            @Field("os") String osType,
            @Field("location") String location,
            @Field("port") String port
    );

    @POST("{path}")
    @FormUrlEncoded
    Observable<JsonElement> getServerListByUser(
            @Path(value = "path", encoded = true) String path,
            @FieldMap Map<String, String> loginFields
    );

}
