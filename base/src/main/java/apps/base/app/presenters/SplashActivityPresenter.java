package apps.base.app.presenters;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerNetworkComponent;
import apps.base.app.models.PortProtocol;
import apps.base.app.models.PortProtocolMap;
import apps.base.app.models.Server;
import apps.base.app.models.ServerResponse;
import apps.base.app.models.User;
import apps.base.app.rest.RetrofitRepository;
import apps.base.app.utils.ConfigManager;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.SplashActivity;
import apps.base.app.dagger.components.NetworkComponent;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

import static apps.base.app.utils.ConfigManager.SAFE_JUMPER;
import static apps.base.app.utils.ConfigManager.SHIELDTRA;
import static io.realm.ImportFlag.CHECK_SAME_VALUES_BEFORE_SET;

public class SplashActivityPresenter extends BasePresenter<SplashActivity> {

    @Inject Realm realm;
    @Inject SharedPrefs sharedPrefs;
    @Inject RetrofitRepository repository;

    private FetchedDataListener fetchedDataListener;
    private int RESPONSES_REQUIRED = 1;

    @Override public void onViewReady(SplashActivity activity) {
        super.onViewReady(activity);

        ApplicationComponent applicationComponent = ((BaseAppApplication) activity.getApplication()).getComponent();
        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .applicationComponent(applicationComponent)
                .build();
        networkComponent.inject(this);

        initFetchDataListener();
        if (ConfigManager.<String>getField("hubsUri").length() > 0) {
            RESPONSES_REQUIRED = 2;
            fetchMainHubs();
        }
        fetchServerList();
    }

    private void initFetchDataListener() {
        fetchedDataListener = new FetchedDataListener() {
            int successResponsesCount = 0;
            int failedResponsesCount = 0;

            @Override public void onDataFetched() {
                successResponsesCount++;
                if (successResponsesCount == RESPONSES_REQUIRED - failedResponsesCount) {
                    if (failedResponsesCount > 0) {
                        Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                    activity.startMainActivity();
                }
            }

            @Override public void onFailureFetchingData() {
                failedResponsesCount++;
                if (failedResponsesCount == RESPONSES_REQUIRED - successResponsesCount) {
                    Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show();
                    activity.startMainActivity();
                }
            }
        };
    }

    private void fetchMainHubs() {
        Observable<ServerResponse<String>> mainHubs = repository.getMainHubs();
        Disposable disposable = mainHubs.subscribe(serverResponse -> {
            if (!serverResponse.isSuccess()) {
                fetchedDataListener.onFailureFetchingData();
                Log.d(RetrofitRepository.TAG, "fetchServerList: " + serverResponse.getMessage());
                return;
            }

            realm.executeTransactionAsync(realm -> {
                String stringJsonArray = serverResponse.getData();
                processServerListResponse(realm, stringJsonArray, true, false);
                fetchedDataListener.onDataFetched();
            });
        });
        activeRequests.add(disposable);
    }

    private void fetchServerList() {
        User user = sharedPrefs.getUser();
        Observable<ServerResponse<String>> serverListObservable = repository.getServerList();
        if (user.getEmail().length() > 0) {
            Map<String, String> map = new HashMap<>();
            if (ConfigManager.getField("useUsername")) {
                map.put("username", user.getUsername());
            } else {
                map.put("email", user.getUsername());
            }
            map.put("password", sharedPrefs.getUserPassword());
            serverListObservable = repository.getServerListByUser(
                    ConfigManager.getField("userServerList"),
                    map
            );
        }
        Disposable disposable = serverListObservable.subscribe(serverResponse -> {
            if (!serverResponse.isSuccess()) {
                fetchedDataListener.onFailureFetchingData();
                Log.d(RetrofitRepository.TAG, "fetchServerList: " + serverResponse.getMessage());
                return;
            }

            realm.executeTransactionAsync(realm -> {
                String stringJsonArray = serverResponse.getData();
                processServerListResponse(realm, stringJsonArray, false, true);
                fetchedDataListener.onDataFetched();
            });

        });
        activeRequests.add(disposable);
    }

    private void processServerListResponse(Realm realm, String stringJsonArray, boolean isMainHubs, boolean isMainServer) {
        Gson gson = getGson();
        JsonParser parser = new JsonParser();
        if (!parser.parse(stringJsonArray).isJsonArray()) {
            Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show();
            activity.startMainActivity();
            return;
        }
        JsonArray jsonArray = parser.parse(stringJsonArray).getAsJsonArray();
        int serverListAPI = ConfigManager.getField("serverListAPI");
        Set<String> encryptionTypes = new HashSet<>();
        Set<PortProtocolMap> portsProtocolMap = new HashSet<>();
        String defaultPort = "";
        String encryptionType = "";
        for (JsonElement nextJsonObject : jsonArray) {
            JsonObject jsonObject = nextJsonObject.getAsJsonObject();
            switch (serverListAPI) {
                case SHIELDTRA:
                    String stringPorts = jsonObject.get("ports").getAsString();
                    String stringXorPorts = jsonObject.get("ports_xor").getAsString();
                    String[] portsArray = stringPorts.split(",");
                    String[] portsXorArray = stringXorPorts.split(",");
                    encryptionTypes.add("tlscrypt");
                    encryptionTypes.add("tlscrypt+xor");
                    RealmList<PortProtocol> portProtocolList = new RealmList<>();
                    for (String nextPort : portsArray) {
                        portProtocolList.add(new PortProtocol(("udp" + Integer.parseInt(nextPort.trim())).hashCode(), "udp", Integer.parseInt(nextPort.trim())));
                        portProtocolList.add(new PortProtocol(("tcp" + Integer.parseInt(nextPort.trim())).hashCode(), "tcp", Integer.parseInt(nextPort.trim())));
                    }
                    PortProtocolMap protocolMap = new PortProtocolMap();
                    protocolMap.setKey("tlscrypt");
                    protocolMap.setValue(portProtocolList);
                    portsProtocolMap.add(protocolMap);
                    RealmList<PortProtocol> xorPortProtocolList = new RealmList<>();
                    for (String nextXorPort : portsXorArray) {
                        xorPortProtocolList.add(new PortProtocol(("udp" + Integer.parseInt(nextXorPort.trim())).hashCode(), "udp", Integer.parseInt(nextXorPort.trim())));
                        xorPortProtocolList.add(new PortProtocol(("tcp" + Integer.parseInt(nextXorPort.trim())).hashCode(), "tcp", Integer.parseInt(nextXorPort.trim())));
                    }
                    PortProtocolMap xorProtocolMap = new PortProtocolMap();
                    xorProtocolMap.setKey("tlscrypt+xor");
                    xorProtocolMap.setValue(xorPortProtocolList);
                    portsProtocolMap.add(xorProtocolMap);
                    jsonObject.remove("ports_xor");

                    PortProtocol portProtocol = portProtocolList.get(0);
                    defaultPort = portProtocol.getProtocol() + " " + portProtocol.getPort();
                    encryptionType = "tlscrypt";
                    break;
                case SAFE_JUMPER:
                    String unsupportedProtocols = "scramblesuit, stunnel, obfs2, obfs3";
                    JsonObject ports = jsonObject.get("ports").getAsJsonObject();
                    Set<Map.Entry<String, JsonElement>> protocolEntrySet = ports.entrySet();
                    for (Map.Entry<String, JsonElement> nextProtocol : protocolEntrySet) {
                        String nextProtocolKey = nextProtocol.getKey();
                        JsonObject nextProtocolObject = nextProtocol.getValue().getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> encryptionEntries = nextProtocolObject.entrySet();
                        for (Map.Entry<String, JsonElement> encryptionEntry : encryptionEntries) {
                            String encryptionKey = encryptionEntry.getKey();
                            if (unsupportedProtocols.contains(encryptionKey)) {
                                continue;
                            }
                            encryptionTypes.add(encryptionKey);
                            RealmList<PortProtocol> nextPortProtocol = null;

                            PortProtocolMap mapEncryptionKey = new PortProtocolMap(encryptionKey);
                            if (portsProtocolMap.contains(mapEncryptionKey)) {
                                for (PortProtocolMap nextPortProtocolMap : portsProtocolMap) {
                                    if (nextPortProtocolMap.equals(mapEncryptionKey)) {
                                        nextPortProtocol = nextPortProtocolMap.getValue();
                                    }
                                }
                            } else {
                                nextPortProtocol = new RealmList<>();
                            }
                            JsonArray jsonElements = encryptionEntry.getValue().getAsJsonArray();
                            for (JsonElement nextElement : jsonElements) {
                                assert nextPortProtocol != null;
                                PortProtocol portProtocol1 = new PortProtocol((nextProtocolKey + nextElement.getAsInt()).hashCode(), nextProtocolKey, nextElement.getAsInt());
                                if (!nextPortProtocol.contains(portProtocol1)) {
                                    nextPortProtocol.add(portProtocol1);
                                }
                            }
                            PortProtocolMap portProtocolMap = new PortProtocolMap();
                            portProtocolMap.setKey(encryptionKey);
                            portProtocolMap.setValue(nextPortProtocol);
                            portsProtocolMap.add(portProtocolMap);
                            if (defaultPort.length() == 0) {
                                PortProtocol portProtocol2 = nextPortProtocol.get(0);
                                defaultPort = portProtocol2.getProtocol() + " " + portProtocol2.getPort();
                                encryptionType = encryptionKey;
                            }
                        }
                    }
                    jsonObject.remove("protocols");
                    break;
            }
            jsonObject.remove("ports");
            jsonObject.add("encryption_types", parser.parse(gson.toJson(encryptionTypes)));
            jsonObject.add("encryption_type", parser.parse(gson.toJson(encryptionType)));
            jsonObject.add("ports", parser.parse(gson.toJson(portsProtocolMap)));
            jsonObject.add("port", parser.parse(gson.toJson(defaultPort)));

            if (isMainHubs) {
                jsonObject.add("is_in_hubs", parser.parse(gson.toJson(true)));
            }
            if (isMainServer) {
                jsonObject.add("is_main_server", parser.parse(gson.toJson(true)));
            }

            ConfigManager.setEncryptionTypes(encryptionTypes);
        }

        Type myType = new TypeToken<ArrayList<Server>>() {}.getType();
        List<Server> serverList = gson.fromJson(gson.toJson(jsonArray), myType);
        for (Server nextServer : serverList) {
            Server first = realm.where(Server.class).equalTo("ip", nextServer.getIp()).findFirst();
            if (first == null) {
                realm.copyToRealmOrUpdate(nextServer, CHECK_SAME_VALUES_BEFORE_SET);
            }

            if(first != null) {
                if(isMainHubs) {
                    nextServer.setInHubs(true);
                }
                if(isMainServer) {
                    nextServer.setMainServer(true);
                }
            }
        }
//        activity.startMainActivity();
    }

    @NonNull private Gson getGson() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(new TypeToken<RealmList<String>>() {
                }.getType(), new TypeAdapter<RealmList<String>>() {

                    @Override public void write(JsonWriter out, RealmList<String> value) {
                        // Ignore
                    }

                    @Override public RealmList<String> read(JsonReader in) throws IOException {
                        RealmList<String> list = new RealmList<>();
                        in.beginArray();
                        while (in.hasNext()) {
                            list.add(in.nextString());
                        }
                        in.endArray();
                        return list;
                    }
                })
                .create();
    }

    @Override public void onDestroy() {
        cancelRequests();
    }

    @Override public void onResume() {
    }

    @Override public void onPause() {
    }

    private interface FetchedDataListener {
        void onDataFetched();

        void onFailureFetchingData();
    }
}
