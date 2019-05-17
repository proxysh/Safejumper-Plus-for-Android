package apps.base.app.presenters;

import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerNetworkComponent;
import apps.base.app.dagger.components.NetworkComponent;
import apps.base.app.models.PortProtocol;
import apps.base.app.models.PortProtocolMap;
import apps.base.app.models.Server;
import apps.base.app.models.User;
import apps.base.app.presenters.opts.IMainFragmentPresenterOpts;
import apps.base.app.service.IPChecker;
import apps.base.app.utils.ConfigManager;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.fragment.MainFragment;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;
import io.realm.RealmList;

import static apps.base.app.utils.Utils.getFormattedServerName;
import static apps.base.app.utils.Utils.pingObservable;
import static apps.base.app.views.activities.MainActivity.connectionState;
import static apps.base.app.views.activities.MainActivity.shouldFinishOnDisconnect;
import static apps.base.app.views.dialogs.InAppOfferDialogFragment.SHOP_NOW;
import static apps.base.app.views.fragment.MainFragment.CONNECTED;
import static apps.base.app.views.fragment.MainFragment.CONNECTING;
import static apps.base.app.views.fragment.MainFragment.NOT_CONNECTED;

public class MainFragmentPresenter extends BaseFragmentPresenter<MainActivity, MainFragment> implements IMainFragmentPresenterOpts {

    private List<Server> serverList = new ArrayList<>(0);
    private int currentServerIndex;
    private boolean shouldConnectAfterLogin;
    private Server serverToConnectAfterLogin;

    @Inject Gson gson;
    @Inject SharedPrefs sharedPrefs;
    @Inject Realm realm;

    @Override public void onFragmentReady(MainFragment fragment) {
        super.onFragmentReady(fragment);

        ApplicationComponent component = ((BaseAppApplication) activity.getApplication()).getComponent();
        NetworkComponent networkComponent = DaggerNetworkComponent.builder()
                .applicationComponent(component)
                .build();
        networkComponent.inject(this);

        provideData();

        new Handler().postDelayed(
                () -> {
                    if (connectionState == NOT_CONNECTED && isAutoConnectEnabled()) {
                        onConnectDisconnectAction(serverList.get(currentServerIndex));
                    }
                },
                1500
        );
    }

    private void provideData() {
        provideServerList();
        provideUserData();
    }

    public void provideServerList() {

        realm.executeTransaction(realm -> {

            boolean showAllServers = sharedPrefs.getSwitches().isDisplayAllServers();

            List<Server> serverList;
            if(showAllServers) {
                serverList = realm.where(Server.class).findAll();
            } else {
                serverList = realm.where(Server.class).equalTo("isInHubs", true).findAll();
            }
            if (serverList.isEmpty()) {
                return;
            }
            this.serverList = serverList;
            fragment.updateServerList(serverList);
        });
    }

    private void provideUserData() {
        User user = sharedPrefs.getUser();
        if (user.getEmail().length() > 0) {
            fragment.updateUserData(user);
            ConfigManager.activeUserName = user.getUsername();
            ConfigManager.activePasswdOfUser = sharedPrefs.getUserPassword();
        }
    }

    @Override public void onEncryptionAction(SimpleModelListener<Server> simpleServerListener, Server server) {
        activity.showEncryptionDialog(
                chosenEncryptionType -> {
                    realm.executeTransaction(realm -> {
                        server.setEncryptionType(chosenEncryptionType);
                        PortProtocolMap key = server.getPorts().where().equalTo("key", chosenEncryptionType).findFirst();
                        PortProtocol portProtocol = key.getValue().get(0);
                        server.setPort(portProtocol.getProtocol() + " " + portProtocol.getPort());
                        simpleServerListener.onModelUpdated(server);
                    });
                },
                server.getEncryptionTypes(),
                server.getEncryptionType()
        );
    }

    @Override public void onPortNoAction(SimpleModelListener<Server> simpleServerListener, Server server) {
        PortProtocolMap key = server.getPorts().where().equalTo("key", server.getEncryptionType()).findFirst();
        RealmList<PortProtocol> value = key.getValue();
        activity.showPortNoDialog(
                chosenPortNo -> {
                    realm.executeTransaction(realm -> {
                        server.setPort(chosenPortNo);
                        simpleServerListener.onModelUpdated(server);
                    });
                },
                server.getPort(),
                value
        );
    }

    @Override public void onLoginLogoutAction() {
        if (sharedPrefs.getUser().getEmail().length() > 0) {
            sharedPrefs.clear();
            fragment.loggedOut();
            onDisconnectFromVPN();
        }

        activity.startSignInFragment();
    }

    @Override public void onShopAction() {
        activity.startShopFragment(clickType -> {
            switch (clickType) {
                case SHOP_NOW:
                    activity.startBrowserUrl(ConfigManager.getField("showNow"));
                    break;
                default:
                    break;
            }
        });
    }

    @Override public void onConnectDisconnectAction(Server server) {
        if (sharedPrefs.getUser().getEmail().length() > 0) {
            if (!serverList.isEmpty()) {
                switch (connectionState) {
                    case NOT_CONNECTED:

                        if (server == null) {
                            server = serverList.get(new Random().nextInt(serverList.size()));
                        }
                        HashMap<String, String> serverInfo = new HashMap<>();
                        serverInfo.put("ip", server.getIp());
                        serverInfo.put("protocol", server.getPort().contains("UDP".toLowerCase()) ? "udp" : "tcp");
                        serverInfo.put("port", server.getPort().replaceAll("\\D+", ""));

                        activity.stopVpn();

                        MainActivity.activeConnectionCountry = server.getName();

                        IPChecker.getInstance(activity).downloadOvpnTemplate(serverInfo);

                        connectionState = CONNECTING;

                        fragment.collapseSlidingLayout();
                        fragment.updateState();
                        fragment.updateUIWithServer(server);
                        break;
                    case CONNECTED:
                        activity.showDisconnectDialog(
                                clickType -> {
                                    onDisconnectFromVPN();
                                }
                        );
                        break;
                    case CONNECTING:
                        break;
                }
            }
            return;
        }

        if (!serverList.isEmpty()) {
            if (server == null) {
                server = serverList.get(new Random().nextInt(serverList.size()));
            }
            serverToConnectAfterLogin = server;
            shouldConnectAfterLogin = true;
        }

        activity.startSignInFragment();

    }

    private void onDisconnectFromVPN() {
        activity.stopVpn();
        connectionState = NOT_CONNECTED;
        fragment.collapseSlidingLayout();
        fragment.updateState();

        if (shouldFinishOnDisconnect) {
            shouldFinishOnDisconnect = false;
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override public void onPreviousCountryAction() {
        if (!serverList.isEmpty() && currentServerIndex != 0) {
            currentServerIndex--;
            fragment.updateFavoriteContainer(serverList, currentServerIndex);
        }
    }

    @Override public void onNextCountryAction() {
        if (!serverList.isEmpty() && currentServerIndex != serverList.size() - 1) {
            currentServerIndex++;
            fragment.updateFavoriteContainer(serverList, currentServerIndex);
        }
    }

    @Override public void onSettingsAction() {
        if (sharedPrefs.getUser().getEmail().length() > 0) {
            activity.startSettingsFragment();
            return;
        }
        activity.startSignInFragment();
    }

    @Override public void onShowLogsAction() {
        activity.showLogsDialog();
    }

    private boolean isAutoConnectEnabled() {
        return sharedPrefs.getSwitches().isAutoConnectOnLaunch();
    }

    @Override public void updateFavouriteServerList(Server server) {

        realm.executeTransaction(realm -> {
            server.setFavourite(!server.isFavourite());
        });

        if (!server.isFavourite()) {
            if (currentServerIndex != 0) {
                currentServerIndex--;
            }
        }
        fragment.updateLikeIcon(server);
        fragment.updateAllServersState();
        fragment.updateFavoriteContainer(serverList, currentServerIndex);

    }

    @Override public void onServerPingAction(Server server) {
        if (server == null) {
            return;
        }
        Observable<Long> longObservable = pingObservable(server.getIp(), server.getPort().replaceAll("\\D+", ""), activity);
        Disposable disposable = longObservable.subscribe(aLong -> {
            realm.executeTransaction(realm -> {
                server.setServerPing(aLong);
                fragment.updateServerPing();
            });
        });
    }

    @Override public void onServerListDataUpdated() {
        if (shouldConnectAfterLogin) {
            shouldConnectAfterLogin = false;
            if (serverList.contains(serverToConnectAfterLogin)) {
                onConnectDisconnectAction(serverToConnectAfterLogin);
            }
        }
    }

    @Override public void onFavoriteServerClick() {
        fragment.updateSlidingServerContainer(serverList, currentServerIndex);
    }

    public void updateServersEncryptionType() {
        realm.executeTransaction(realm -> {
            for (Server nextServer : serverList) {
                String defaultEncryptionType = sharedPrefs.getDefaultEncryptionType().length() > 0 ? sharedPrefs.getDefaultEncryptionType() : nextServer.getEncryptionTypes().get(0);

                PortProtocolMap key = nextServer.getPorts().where().equalTo("key", defaultEncryptionType).findFirst();
                PortProtocol portProtocol = key.getValue().get(0);

                nextServer.setEncryptionType(defaultEncryptionType);
                nextServer.setPort(portProtocol.getProtocol() + " " + portProtocol.getPort());
                fragment.updateAllServersState();
            }
        });
    }
}
