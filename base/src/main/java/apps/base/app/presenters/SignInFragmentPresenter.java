package apps.base.app.presenters;

import android.app.ProgressDialog;
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
import apps.base.app.dagger.components.DaggerNetworkComponent;
import apps.base.app.dagger.components.NetworkComponent;
import apps.base.app.models.PortProtocol;
import apps.base.app.models.PortProtocolMap;
import apps.base.app.models.Server;
import apps.base.app.models.User;
import apps.base.app.presenters.opts.ISignInFragmentPresenterOpts;
import apps.base.app.rest.RetrofitRepository;
import apps.base.app.utils.ConfigManager;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.fragment.MainFragment;
import apps.base.app.views.fragment.SignInFragment;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

import static apps.base.app.utils.ConfigManager.SAFE_JUMPER;
import static apps.base.app.utils.ConfigManager.SHIELDTRA;
import static io.realm.ImportFlag.CHECK_SAME_VALUES_BEFORE_SET;

public class SignInFragmentPresenter extends BaseFragmentPresenter<MainActivity, SignInFragment> implements ISignInFragmentPresenterOpts {

    @Inject SharedPrefs sharedPrefs;
    @Inject RetrofitRepository repository;
    @Inject Gson gson;
    @Inject Realm realm;

    @Override public void onFragmentReady(SignInFragment fragment) {
        super.onFragmentReady(fragment);

        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .applicationComponent(((BaseAppApplication) activity.getApplication()).getComponent())
                .build();
        networkComponent.inject(this);
    }

    @Override public void onDestroyView() {
        cancelRequests();
        activity.hideKeyboard();
    }

    @Override public void onForgotPasswordAction() {

    }

    @Override public void onSignUpAction() {
        activity.startSignUpFragment();
    }

    @Override public void onLoginAction(String loginCredential, String password, boolean isTempUser) {

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        boolean useUsername = ConfigManager.getField("useUsername");
        Map<String, String> map = new HashMap<>();
        if (useUsername) {
            map.put("username", loginCredential);
        } else {
            map.put("email", loginCredential);
        }
        map.put("password", password);

        Disposable disposable = repository.login(map)
                .subscribe(response -> {
                    if (dialog.isShowing() && !useUsername) {
                        dialog.dismiss();
                    }
                    User user = provideUserData(response.getData());
                    if (user == null || !response.isSuccess()) {
                        Toast.makeText(activity, response.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        return;
                    }

                    ConfigManager.activeUserName = loginCredential;
                    ConfigManager.activePasswdOfUser = password;

                    sharedPrefs.clear();
                    sharedPrefs.updateUserPassword(password);
                    sharedPrefs.setTempUser(isTempUser);

                    user.setUsername(loginCredential);

                    if (!useUsername) {
                        user.setEmail(loginCredential);
                        sharedPrefs.updateUserData(user);
                        updateMainFragmentWithUser(user);

                        JsonArray jsonArray = provideServerListData(response.getData());
                        if (jsonArray != null) {
                            Gson gson = getGson();
                            JsonParser parser = new JsonParser();
                            if (jsonArray.size() > 0) {
                                Set<String> encryptionTypes = new HashSet<>();
                                Set<PortProtocolMap> portsProtocolMap = new HashSet<>();
                                for (JsonElement nextJsonObject : jsonArray) {

                                    JsonObject jsonObject = nextJsonObject.getAsJsonObject();

                                    String stringPorts = jsonObject.get("ports").getAsString();
                                    String stringXorPorts = jsonObject.get("ports_xor").getAsString();
                                    String[] portsArray = stringPorts.split(",");
                                    String[] portsXorArray = stringXorPorts.split(",");
                                    encryptionTypes.add("tlscrypt");
                                    encryptionTypes.add("tlscrypt+xor");
                                    RealmList<PortProtocol> portProtocolList = new RealmList<>();
                                    for (String nextPort : portsArray) {
                                        portProtocolList.add(new PortProtocol(("udp"+Integer.parseInt(nextPort.trim())).hashCode(), "udp", Integer.parseInt(nextPort.trim())));
                                        portProtocolList.add(new PortProtocol(("tcp"+Integer.parseInt(nextPort.trim())).hashCode(), "tcp", Integer.parseInt(nextPort.trim())));
                                    }

                                    PortProtocolMap protocolMap = new PortProtocolMap();
                                    protocolMap.setKey("tlscrypt");
                                    protocolMap.setValue(portProtocolList);
                                    portsProtocolMap.add(protocolMap);

                                    RealmList<PortProtocol> xorPortProtocolList = new RealmList<>();
                                    for (String nextXorPort : portsXorArray) {
                                        xorPortProtocolList.add(new PortProtocol(("udp"+Integer.parseInt(nextXorPort.trim())).hashCode(), "udp", Integer.parseInt(nextXorPort.trim())));
                                        xorPortProtocolList.add(new PortProtocol(("tcp"+Integer.parseInt(nextXorPort.trim())).hashCode(), "tcp", Integer.parseInt(nextXorPort.trim())));
                                    }
                                    PortProtocolMap xorProtocolMap = new PortProtocolMap();
                                    xorProtocolMap.setKey("tlscrypt+xor");
                                    xorProtocolMap.setValue(xorPortProtocolList);
                                    portsProtocolMap.add(xorProtocolMap);

                                    PortProtocol portProtocol = portProtocolList.get(0);
                                    String defaultPort = portProtocol.getProtocol() + " " + portProtocol.getPort();
                                    String enctyptionType = "tlscrypt";

                                    jsonObject.remove("ports_xor");

                                    jsonObject.remove("ports");
                                    jsonObject.add("encryption_types", parser.parse(gson.toJson(encryptionTypes)));
                                    jsonObject.add("encryption_type", parser.parse(gson.toJson(enctyptionType)));
                                    jsonObject.add("ports", parser.parse(gson.toJson(portsProtocolMap)));
                                    jsonObject.add("port", parser.parse(gson.toJson(defaultPort)));

                                }

                                ConfigManager.setEncryptionTypes(encryptionTypes);

                                realm.executeTransactionAsync(realm -> {
                                    realm.where(Server.class).findAll().deleteAllFromRealm();
                                    Type myType = new TypeToken<ArrayList<Server>>() {
                                    }.getType();
                                    List<Server> serverList = gson.fromJson(gson.toJson(jsonArray), myType);
                                    realm.copyToRealmOrUpdate(serverList);
                                    activity.runOnUiThread(() -> {
                                        updateServerList();
                                        onBackPressed();
                                    });
                                });
                            }
                        }
                        return;
                    }

                    sharedPrefs.updateUserData(user);
                    updateMainFragmentWithUser(user);
                    fetchUserServers(loginCredential, password, dialog);

                });
        activeRequests.add(disposable);
    }

    @NonNull private Gson getGson() throws IOException {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(new TypeToken<RealmList<String>>() {
                }.getType(), new TypeAdapter<RealmList<String>>() {

                    @Override
                    public void write(JsonWriter out, RealmList<String> value) {
                        // Ignore
                    }

                    @Override
                    public RealmList<String> read(JsonReader in) throws IOException {
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

    private void updateServerList() {
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (fragment != null) {
            fragment.onNewServerList();
        }
    }

    private JsonArray provideServerListData(String responseJson) {
        if (responseJson != null) {
            JsonObject jsonObject = gson.fromJson(responseJson, JsonObject.class);
            if (jsonObject != null) {
                JsonElement serverList = jsonObject.get("servers");
                if (serverList != null) {
                    return serverList.getAsJsonArray();
                }
            }
        }
        return null;
    }

    private void fetchUserServers(String username, String password, ProgressDialog dialog) {
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        Disposable disposable = repository.getServerListByUser(
                ConfigManager.getField("userServerList"),
                map
        )
                .subscribe(serverResponse -> {

                    if (!serverResponse.isSuccess()) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show();
                        Log.d(RetrofitRepository.TAG, "fetchServerList: " + serverResponse.getMessage());
                        onBackPressed();
                        return;
                    }
                    String stringJsonArray = serverResponse.getData();

                    Gson gson = getGson();

                    JsonParser parser = new JsonParser();
                    if (!parser.parse(stringJsonArray).isJsonArray()) {
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Toast.makeText(activity, "Error fetching data", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        return;
                    }
                    JsonArray jsonArray = parser.parse(stringJsonArray).getAsJsonArray();

                    realm.executeTransactionAsync(realm -> {

                        Set<String> encryptionTypes = new HashSet<>();
                        Set<PortProtocolMap> portsProtocolMap = new HashSet<>();
                        String unsupportedProtocols = "scramblesuit, stunnel, obfs2, obfs3";
                        for (JsonElement nextJsonObject : jsonArray) {

                            JsonObject jsonObject = nextJsonObject.getAsJsonObject();

                            JsonObject ports = jsonObject.get("ports").getAsJsonObject();
                            Set<Map.Entry<String, JsonElement>> protocolEntrySet = ports.entrySet();

                            String defaultPort = "";
                            String enctyptionType = "";

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
                                        if(!nextPortProtocol.contains(portProtocol1)) {
                                            nextPortProtocol.add(portProtocol1);
                                        }
                                    }
                                    PortProtocolMap portProtocolMap = new PortProtocolMap();
                                    portProtocolMap.setKey(encryptionKey);
                                    portProtocolMap.setValue(nextPortProtocol);
                                    portsProtocolMap.add(portProtocolMap);


                                    if (defaultPort.length() == 0) {
                                        PortProtocol portProtocol = nextPortProtocol.get(0);
                                        defaultPort = portProtocol.getProtocol() + " " + portProtocol.getPort();
                                        enctyptionType = encryptionKey;
                                    }
                                }
                            }


                            jsonObject.remove("protocols");


                            jsonObject.remove("ports");
                            jsonObject.add("encryption_types", parser.parse(gson.toJson(encryptionTypes)));
                            jsonObject.add("encryption_type", parser.parse(gson.toJson(enctyptionType)));
                            jsonObject.add("ports", parser.parse(gson.toJson(portsProtocolMap)));
                            jsonObject.add("port", parser.parse(gson.toJson(defaultPort)));
                        }
                        ConfigManager.setEncryptionTypes(encryptionTypes);
                        realm.where(Server.class)
                                .equalTo("isInHubs", false)
                                .equalTo("isMainServer", true)
                                .findAll().deleteAllFromRealm();
                        Type myType = new TypeToken<ArrayList<Server>>() {}.getType();
                        List<Server> serverList = gson.fromJson(gson.toJson(jsonArray), myType);
                        realm.copyToRealmOrUpdate(serverList, CHECK_SAME_VALUES_BEFORE_SET);
                        activity.runOnUiThread(() -> {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            updateServerList();
                            onBackPressed();
                        });
                    });
                });


        activeRequests.add(disposable);
    }

    private void updateMainFragmentWithUser(User user) {
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (fragment != null) {
            fragment.updateUserData(user);
        }
    }

    private User provideUserData(String responseJson) {
        if (responseJson == null) {
            return null;
        }
        JsonObject jsonObject = gson.fromJson(responseJson, JsonObject.class);
        if (jsonObject == null) {
            return null;
        }

        int serverListAPI = ConfigManager.getField("serverListAPI");
        switch (serverListAPI) {
            case SAFE_JUMPER:

                if (jsonObject.has("error")) {
                    return null;
                }

                jsonObject.add("expiration_date", jsonObject.get("expire_date"));
                jsonObject.remove("expire_date");
                jsonObject.add("type", jsonObject.get("package"));
                jsonObject.remove("package");

                break;
            case SHIELDTRA:
                JsonElement user = jsonObject.get("user");
                if (user == null) {
                    return null;
                }
                jsonObject = user.getAsJsonObject();

                break;
        }


        Type myType = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(jsonObject.toString(), myType);
    }


}
