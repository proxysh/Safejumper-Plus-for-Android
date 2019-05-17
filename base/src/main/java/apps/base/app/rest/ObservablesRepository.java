package apps.base.app.rest;

import java.util.Map;

import apps.base.app.models.ServerResponse;
import io.reactivex.Observable;

public interface ObservablesRepository {

    Observable<ServerResponse<String>> getServerList();
    Observable<ServerResponse<String>> getMainHubs();
    Observable<ServerResponse<String>> getServerListByUser(String path, Map<String, String> loginFields);
    Observable<ServerResponse<String>> login(Map<String, String> loginFields);
    Observable<ServerResponse<String>> signUp(String email, String password, String type, String status, String expirationDate, String token);
}
