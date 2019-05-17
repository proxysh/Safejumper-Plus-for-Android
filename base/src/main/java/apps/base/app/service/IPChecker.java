package apps.base.app.service;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import apps.base.app.utils.ConfigManager;
import apps.base.app.views.activities.BaseVPNActivity;
import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.fragment.MainFragment;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VpnStatus;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.ResponseBody;

import static apps.base.app.views.fragment.MainFragment.NOT_CONNECTED;

public class IPChecker {


    private BaseVPNActivity c;
    private static IPChecker instance = null;

    private static void initialize(BaseVPNActivity c) {
        if (instance == null)
            instance = new IPChecker(c);
    }

    public static IPChecker getInstance(BaseVPNActivity c) {
        initialize(c);
        return instance;
    }

    private IPChecker(BaseVPNActivity c) {
        this.c = c;
    }


    public void downloadOvpnTemplate(Map<String, String> serverParams) {
        String field = ConfigManager.getField("configUri");
        Single<ResponseBody> configUri;
        if (!field.contains("public")) {
            configUri = ApiService.getInstance().getApiService().getServerConfigsGet(
                    field,
                    serverParams
            );

        } else {
            configUri = ApiService.getInstance().getApiService().getServerConfigsPost(
                    field,
                    serverParams,
                    "Android",
                    String.format("Specific:%s", serverParams.get("ip")),
                    String.format("%s:%s", serverParams.get("port"), serverParams.get("protocol"))
            );
        }

        c.appLog("--------Downloading openvpn config template file servers----------");
        (new CompositeDisposable()).add(
                configUri
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                ConfigParser cp = new ConfigParser();
                                try {
                                    cp.parseConfig(responseBody.charStream());
                                    VpnProfile vp = cp.convertProfile();
                                    vp.mUsername = ConfigManager.activeUserName;
                                    vp.mPassword = ConfigManager.activePasswdOfUser;
                                    vp.mProfileCreator = "com.proxysh.shieldtra.service.IPChecker";
                                    // We don't want provisioned profiles to be editable
                                    vp.mUserEditable = false;

                                    vp.setUUID(UUID.randomUUID());
                                    vp.mServerName = serverParams.get("ip");

                                    if (c != null){
                                        ProfileManager pm = ProfileManager.getInstance(c);

                                        // The add method will replace any older profiles with the same UUID
                                        pm.addProfile(vp);
                                        pm.saveProfile(c, vp);
                                        pm.saveProfileList(c);
                                        c.startVpn(vp);
                                    }
                                } catch (ConfigParser.ConfigParseError | IOException | IllegalArgumentException e) {
                                    VpnStatus.logException("Error during import of managed profile", e);
                                    c.runOnUiThread(() -> {
                                        MainFragment mainFragment = (MainFragment) c.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                                        MainActivity.connectionState = NOT_CONNECTED;
                                        if (mainFragment != null) {
                                            mainFragment.updateState();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                c.runOnUiThread(() -> {
                                    MainFragment mainFragment = (MainFragment) c.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                                    MainActivity.connectionState = NOT_CONNECTED;
                                    if (mainFragment != null) {
                                        mainFragment.updateState();
                                    }
                                });
                                c.appLog("Cannot connect to server for downloading openvpn config template file. Please check your internet connection.");
                            }
                        })
        );

    }
}
