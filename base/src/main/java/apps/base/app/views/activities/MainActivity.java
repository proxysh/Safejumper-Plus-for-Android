package apps.base.app.views.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.VpnService;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import apps.base.app.BaseAppApplication;
import apps.base.app.R;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.dagger.components.DaggerPresentersComponent;
import apps.base.app.models.DefaultDNS;
import apps.base.app.models.Logs;
import apps.base.app.models.PortProtocol;
import apps.base.app.presenters.MainActivityPresenter;
import apps.base.app.views.dialogs.BaseDialogListener;
import apps.base.app.views.dialogs.ContactUsDialogFragment;
import apps.base.app.views.dialogs.DialogResultListener;
import apps.base.app.views.dialogs.DisconnectDialogFragment;
import apps.base.app.views.dialogs.InAppOfferDialogFragment;
import apps.base.app.views.dialogs.LogsDialogFragment;
import apps.base.app.views.dialogs.ModalDNSDialogFragment;
import apps.base.app.views.dialogs.ModalEncryptionDialogFragment;
import apps.base.app.views.dialogs.ModalPortNoDialogFragment;
import apps.base.app.views.dialogs.OnBoardingDialogFragment;
import apps.base.app.views.fragment.BaseFragment;
import apps.base.app.views.fragment.MainFragment;
import apps.base.app.views.fragment.SettingsFragment;
import apps.base.app.views.fragment.SignInFragment;
import apps.base.app.views.fragment.SignUpFragment;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.api.IOpenVPNAPIService;
import de.blinkt.openvpn.api.IOpenVPNStatusCallback;
import de.blinkt.openvpn.core.Connection;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.LogItem;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import de.blinkt.openvpn.core.VpnStatus;

import static apps.base.app.views.activities.MainActivity.ServiceSwitcherStatus.WORKING;
import static apps.base.app.views.activities.MainActivity.VpnStatusLocal.Disconnected;
import static apps.base.app.views.dialogs.OnBoardingDialogFragment.LOGIN;
import static apps.base.app.views.fragment.MainFragment.CONNECTED;
import static apps.base.app.views.fragment.MainFragment.CONNECTING;
import static apps.base.app.views.fragment.MainFragment.NOT_CONNECTED;

public class MainActivity extends BaseVPNActivity implements VpnStatus.StateListener, VpnStatus.LogListener {

    private ServiceSwitcher mServiceSwitcher;

    public static int connectionState = NOT_CONNECTED;
    public static String activeConnectionCountry = "";
    private VpnStatusLocal lastState;

    private Logs logs = new Logs();

    private boolean cmFixed = false;
    private static boolean wasDestroyed = false;
    public static boolean shouldFinishOnDisconnect = false;

    @Inject MainActivityPresenter presenter;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApplicationComponent appComponent = ((BaseAppApplication) getApplication()).getComponent();
        DaggerPresentersComponent.builder()
                .applicationComponent(appComponent)
                .build()
                .inject(this);
        presenter.onViewReady(this);


        mServiceSwitcher = new ServiceSwitcher();
        VpnStatus.addLogListener(MainActivity.this);
        lastState = Disconnected;

    }

    @Override protected void onDestroy() {
        if (connectionState == CONNECTED) {
            mServiceSwitcher.onPause();
        }
        wasDestroyed = true;
        super.onDestroy();
    }

    @Override protected void onResume() {
        super.onResume();
        if (wasDestroyed && connectionState == CONNECTED) {
            mServiceSwitcher.onResume();
            wasDestroyed = false;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wasDestroyed && connectionState == CONNECTED) {
            mServiceSwitcher.onPause();
        }

    }

    @Override public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0 && connectionState == NOT_CONNECTED) {
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mServiceSwitcher.onActivityResult(requestCode, resultCode, data);
    }

    public void startMainFragment() {
        BaseFragment fragment = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainContainer, fragment, MainFragment.TAG)
                .commit();
    }

    public void startOnBoardingFragment() {
        OnBoardingDialogFragment onBoardingDialogFragment = new OnBoardingDialogFragment();
        onBoardingDialogFragment.setListener(clickType -> {
            switch (clickType) {
                case LOGIN:
                    startSignInFragment();
                    break;
                default:
                    break;

            }
        });
        onBoardingDialogFragment.show(getSupportFragmentManager(), OnBoardingDialogFragment.TAG);
    }

    public void startSignInFragment() {
        BaseFragment fragment = new SignInFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .add(R.id.mainContainer, fragment, SignInFragment.TAG)
                .addToBackStack(SignInFragment.TAG)
                .commit();
    }

    public void startSignUpFragment() {
        BaseFragment fragment = new SignUpFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .add(R.id.mainContainer, fragment, SignUpFragment.TAG)
                .addToBackStack(SignUpFragment.TAG)
                .commit();
    }

    public void startSettingsFragment() {
        BaseFragment fragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.enter_from_right,
                        R.anim.exit_to_left,
                        R.anim.enter_from_left,
                        R.anim.exit_to_right
                )
                .add(R.id.mainContainer, fragment, SettingsFragment.TAG)
                .addToBackStack(SettingsFragment.TAG)
                .commit();
    }

    public void startShopFragment(BaseDialogListener baseDialogListener) {
        showInAppOfferDialog(baseDialogListener);
    }

    public void showEncryptionDialog(DialogResultListener<String> baseDialogListener, List<String> availableEncryptionTypes, String encryptionType) {
        ModalEncryptionDialogFragment modalEncryptionDialogFragment = new ModalEncryptionDialogFragment();
        modalEncryptionDialogFragment.setEncryptionType(encryptionType, availableEncryptionTypes);
        modalEncryptionDialogFragment.setListener(baseDialogListener);
        modalEncryptionDialogFragment.show(getSupportFragmentManager(), ModalEncryptionDialogFragment.TAG);
    }

    public void showPortNoDialog(DialogResultListener<String> baseDialogListener, String portNo, List<PortProtocol> availablePortNos) {
        ModalPortNoDialogFragment modalPortNoDialogFragment = new ModalPortNoDialogFragment();
        modalPortNoDialogFragment.setPortNo(portNo, availablePortNos);
        modalPortNoDialogFragment.setListener(baseDialogListener);
        modalPortNoDialogFragment.show(getSupportFragmentManager(), ModalPortNoDialogFragment.TAG);
    }

    public void showModalDNSDialog(DialogResultListener<DefaultDNS> dialogResultListener, DefaultDNS defaultDNSIps) {
        ModalDNSDialogFragment modalDNSDialogFragment = new ModalDNSDialogFragment();
        modalDNSDialogFragment.setDNSIps(defaultDNSIps);
        modalDNSDialogFragment.setListener(dialogResultListener);
        modalDNSDialogFragment.show(getSupportFragmentManager(), ModalDNSDialogFragment.TAG);
    }

    public void showInAppOfferDialog(BaseDialogListener baseDialogListener) {
        InAppOfferDialogFragment inAppOfferDialog = new InAppOfferDialogFragment();
        inAppOfferDialog.setListener(baseDialogListener);
        inAppOfferDialog.show(getSupportFragmentManager(), InAppOfferDialogFragment.TAG);
    }

    public void showDisconnectDialog(BaseDialogListener baseDialogListener) {
        DisconnectDialogFragment disconnectDialogFragment = new DisconnectDialogFragment();
        disconnectDialogFragment.setListener(baseDialogListener);
        disconnectDialogFragment.show(getSupportFragmentManager(), DisconnectDialogFragment.TAG);
    }

    public void showLogsDialog() {
        LogsDialogFragment logsDialogFragment = new LogsDialogFragment();
        logsDialogFragment.setLogs(logs);
        logsDialogFragment.show(getSupportFragmentManager(), LogsDialogFragment.TAG);
    }

    public void showContactUsDialog(BaseDialogListener baseDialogListener) {
        ContactUsDialogFragment contactUsDialogFragment = new ContactUsDialogFragment();
        contactUsDialogFragment.setListener(baseDialogListener);
        contactUsDialogFragment.show(getSupportFragmentManager(), ContactUsDialogFragment.TAG);
    }

    public void startBrowserUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    public void popBackStack() {
        getSupportFragmentManager().popBackStack(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override public void appLog(String appLogs) {
        runOnUiThread(() -> logs.setAppLogs(logs.getAppLogs().concat(currentTime() + ":" + appLogs + "\n")));
    }

    @Override public void newLog(LogItem logItem) {
        runOnUiThread(() -> {
                    logs.setVpnLogs(logs.getVpnLogs().concat(logItem.getString(MainActivity.this)) + "\n");
                    if(logItem.getLogLevel() == VpnStatus.LogLevel.ERROR) {

                        connectionState = NOT_CONNECTED;

                        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                        if (mainFragment != null) {
                            mainFragment.updateState();
                        }
                    }
                }
        );
    }

    private String currentTime() {
        return DateFormat.getDateFormat(MainActivity.this).format(new Date());
    }

    @Override public void startVpn(VpnProfile profile) {
        mServiceSwitcher.startVPN(profile);
    }

    public void stopVpn() {
        mServiceSwitcher.stopVpn(false);
    }

    enum ServiceSwitcherStatus {
        NOT_CONNECTED_TO_SERVICE,
        LAUNCHING,
        WORKING
    }

    enum VpnStatusLocal {
        Connecting,
        Connected,
        Disconnected,
        Unknown
    }

    @Override public void updateState(String state, String logmessage, int localizedResId, final ConnectionStatus level) {

//        if (!ConfigManager.isLoginned)
//            return;

        VpnStatusLocal currentStatus;

        switch (level) {
            case LEVEL_CONNECTED:
                currentStatus = VpnStatusLocal.Connected;
                break;
            case LEVEL_CONNECTING_SERVER_REPLIED:
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
                currentStatus = VpnStatusLocal.Connecting;
                break;
            case LEVEL_WAITING_FOR_USER_INPUT:
            case LEVEL_AUTH_FAILED:
            case LEVEL_NONETWORK:
            case LEVEL_VPNPAUSED:
            case LEVEL_NOTCONNECTED:
            case UNKNOWN_LEVEL:
            default:
                currentStatus = Disconnected;
                break;
        }

        if (lastState == currentStatus)
            return;


//        final String location;
//        final String ip;
//        final String proto;
//        final String load;
//        final int noteResourceId;

        MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        this.runOnUiThread(() -> {

            switch (currentStatus) {
                case Connected:
                    connectionState = CONNECTED;
                    break;
                case Connecting:
                    connectionState = CONNECTING;
                    break;
                default:
                case Disconnected:
                    connectionState = NOT_CONNECTED;
                    break;
            }

            if (mainFragment != null) {
                mainFragment.updateState();
            }
        });

//
//        switch (currentStatus) {
//            case Connected:
//                noteResourceId = R.string.connected_note;
//                break;
//            case Connecting:
//                noteResourceId = R.string.connecting_note;
//                break;
//            default:
//            case Disconnected:
//                noteResourceId = R.string.disconnected_note;
//                break;
//        }
//
//        if (currentStatus == VpnStatusLocal.Connected || currentStatus == VpnStatusLocal.Connecting) {
//            VpnProfile profile = defaultProfile();
//            final boolean isUseUdp = profile.mUseUdp;
//            final String port = profile.mServerPort;
//            ip = profile.mServerName;
//            ServerResponse serverInfo = IPChecker.getInstance(this).serverForVpnByIp(ip);
//            location = (String) serverInfo.getIsoCode();
//            load = serverInfo.getServerLoad();
//            proto = (isUseUdp ? "UDP" : "TCP") + " " + port;
//            //update listview and connect/disconnect button on ui
//            this.runOnUiThread(new Runnable() {
//                public void run() {
//                    actServers.markActiveLocation(location);
//                    actPorts.markActiveService(isUseUdp ? "UDP" : "TCP", port);
//                    setConnectableButtonBar(false);
//                    switchForAction(ActionType.SettingAction);
//                    actSettings.setConnectionStatistics(0, 0, 1, mServiceSwitcher.mUseAidlService);
//                }
//            });
//            if (currentStatus == VpnStatusLocal.Connected) {
//                //save params
//                ConfigManager.isConnected = true;
//                ConfigManager.getInstance(this).setPrefBool(ConfigManager.PK_LAST_SUCCESS, true);
//                ConfigManager.getInstance(this).setPrefString(ConfigManager.PK_LAST_VPNSERVER, ip);
//                ConfigManager.getInstance(this).setPrefString(ConfigManager.PK_LAST_VPNPORT, port);
//                ConfigManager.getInstance(this).setPrefString(ConfigManager.PK_LAST_PROTO, isUseUdp ? "UDP" : "TCP");
//            } else {
//                ConfigManager.isConnected = false;
//            }
//        } else {
//            Log.i("tt", "----------------------enter disconnect");
//            ConfigManager.isConnected = false;
//            location = "Unknown";
//            load = "";
//            ip = "";
//            proto = "";
//            this.runOnUiThread(() -> {
//                actServers.clearActiveLocation();
//                actPorts.clearActiveService();
//                //show connect button and jump to Setting
//                setConnectableButtonBar(true);
//                switchForAction(ActionType.SettingAction);
//                actSettings.setConnectionStatistics(0, 0, 1, mServiceSwitcher.mUseAidlService);
//            });
//        }
//
//        if (requestState == RequestStatus.WantShieldtra) {
//
//            if (currentStatus != VpnStatusLocal.Disconnected) {
//                runOnUiThread(() -> {
//                    actSettings.setConnectionInfo(location, ip, proto, load);
//                    actSettings.setConnectionState(noteResourceId);
//                });
//
//            }
//        } else {
//            runOnUiThread(() -> {
//                actSettings.setConnectionInfo(location, ip, proto, load);
//                actSettings.setConnectionState(noteResourceId);
//            });
//        }
//
        if (/*(*/lastState == VpnStatusLocal.Connected/* || lastState == VpnStatus.Connecting)*/ && currentStatus == VpnStatusLocal.Disconnected) {
//            if (requestState != RequestStatus.WantDisconnect && ConfigManager.getInstance(this).prefBoolForKey(ConfigManager.PK_DROP_RECONNECT)) {
//                startVpn(defaultProfile());
//            } else
            if (presenter.shouldKillInternet()) {
                killInternet();
            }
        }
        lastState = currentStatus;
    }

    protected void killInternet() {
        //Disable wifi
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(false);
        //Disable mobile data
        try {
            final ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(iConnectivityManager, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override public void setConnectedVPN(String uuid) {

    }


    class ServiceSwitcher {

        private static final int START_PROFILE_EMBEDDED = 2;
        private static final int ICS_OPENVPN_PERMISSION = 7;
        private static final int START_VPN_CMD = 102;
        private static final String TAG = "ServiceSwitcher";

        private boolean mUseAidlService;
        private Handler mHandler;
        private IOpenVPNAPIService mAidlService = null;
        private VpnProfile mActiveVpnProfile = null;

        private ServiceSwitcherStatus state = ServiceSwitcherStatus.NOT_CONNECTED_TO_SERVICE;

        ServiceSwitcher() {

        }

        private void bindToService() {
            if (mUseAidlService) {
                Intent icsopenvpnService = new Intent(IOpenVPNAPIService.class.getName());
                icsopenvpnService.setPackage("de.blinkt.openvpn");
                bindService(icsopenvpnService, mAidlConnection, Context.BIND_AUTO_CREATE);
            } else {
                Intent intent = new Intent(MainActivity.this, OpenVPNService.class);
                intent.setAction(OpenVPNService.START_SERVICE);

                bindService(intent, vpnServiceConn, Context.BIND_AUTO_CREATE);
            }
        }

        private void prepareStartProfile(int requestCode) throws RemoteException {
            Intent requestpermission = mAidlService.prepareVPNService();
            if (requestpermission == null) {
                onActivityResult(requestCode, Activity.RESULT_OK, null);
            } else {
                // Have to call an external Activity since services cannot used onActivityResult
                startActivityForResult(requestpermission, requestCode);
            }
        }

        /**
         * Class for interacting with the main interface of the service.
         */
        private ServiceConnection mAidlConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // This is called when the connection with the service has been
                // established, giving us the service object we can use to
                // interact with the service.  We are communicating with our
                // service through an IDL interface, so get a client-side
                // representation of that from the raw service object.

                mAidlService = IOpenVPNAPIService.Stub.asInterface(service);
                onServiceBind();

//                try {
//                    // Request permission to use the API
//                    Intent i = mAidlService.prepare(getPackageName());
//                    if (i!=null) {
//                        startActivityForResult(i, ICS_OPENVPN_PERMISSION);
//                    } else {
//                        onActivityResult(ICS_OPENVPN_PERMISSION, Activity.RESULT_OK,null);
//                    }
//
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }

            public void onServiceDisconnected(ComponentName className) {
                // This is called when the connection with the service has been
                // unexpectedly disconnected -- that is, its process crashed.
                mAidlService = null;
            }
        };

        private IOpenVPNServiceInternal vpnService;
        private ServiceConnection vpnServiceConn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
//                LocalBinder binder =  IOpenVPNServiceInternal.Stub.asInterface(service);
                vpnService = IOpenVPNServiceInternal.Stub.asInterface(service);
//                 binder.getService();
                onServiceBind();

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                vpnService = null;
            }

        };

        private void onServiceBind() {
//            startStateListener();
            if (state == ServiceSwitcherStatus.LAUNCHING) {
                startVPN1();
            }

            //TODO uncomment
            if (!mUseAidlService) {
//				vpnService.setConfigurationIntent(getPendingIntent());
//				vpnService.setNotificationIntent(getPendingIntent());
            }

        }

        PendingIntent getPendingIntent() {
            Intent intent;
            // Let the configure Button show the Log
            intent = new Intent(MainActivity.this.getBaseContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent startLW = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            return startLW;
        }

        private void launchVPN() {

            if (mActiveVpnProfile == null)
                return;
            moveOptionsToConnection(mActiveVpnProfile);
            int vpnok = mActiveVpnProfile.checkProfile(MainActivity.this);
            if (vpnok != R.string.no_error_found) {
                Log.e(TAG, "checkProfile failed: " + getString(vpnok));
                return;
            }

            Intent intent = VpnService.prepare(MainActivity.this);

            // Check if we want to fix /dev/tun
            boolean usecm9fix = false;
            boolean loadTunModule = false;

            if (loadTunModule)
                execeuteSUcmd("insmod /system/lib/modules/tun.ko");

            if (usecm9fix && !cmFixed) {
                execeuteSUcmd("chown system /dev/tun");
            }

            if (intent != null) {
                VpnStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
                        ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
                //start query
                try {
                    startActivityForResult(intent, START_VPN_CMD);
                } catch (ActivityNotFoundException ane) {
                    VpnStatus.logError(R.string.no_vpn_support_image);
                }
            } else {
                onActivityResult(START_VPN_CMD, Activity.RESULT_OK, null);
            }
        }

        private void execeuteSUcmd(String command) {
            ProcessBuilder pb = new ProcessBuilder("su", "-c", command);
            try {
                Process p = pb.start();
                int ret = p.waitFor();
                if (ret == 0)
                    cmFixed = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void stopVpn(boolean onDestroy) {
            if (state == ServiceSwitcherStatus.NOT_CONNECTED_TO_SERVICE) {
                return;
            }
            state = ServiceSwitcherStatus.NOT_CONNECTED_TO_SERVICE;
//            if (mActiveVpnProfile == null)
//                return;
            if (mUseAidlService) {
                try {
                    // do not stop external VPN in onDestroy()
                    if (mAidlService != null && !onDestroy)
                        mAidlService.disconnect();
                } catch (RemoteException e) {
                    Log.e(TAG, "stopVpn()", e);
                }
            } else {
                if (vpnService != null) {
                    try {
//                        vpnService.stopVPN(true);
                        vpnService.stopVPN(false);
                    } catch (RemoteException e) {
                        VpnStatus.logException(e);
                    }
                }
                stopService(new Intent(MainActivity.this, OpenVPNService.class));
                cancelNotification();
            }
            mServiceSwitcher.unbindFromService();
        }

        public void unbindFromService() {
            if (mUseAidlService) {
                unbindService(mAidlConnection);
            } else {
                unbindService(vpnServiceConn);
            }
        }

        private IOpenVPNStatusCallback.Stub statusCallbackAidl = new IOpenVPNStatusCallback.Stub() {
            /**
             * This is called by the remote service regularly to tell us about
             * new values.  Note that IPC calls are dispatched through a thread
             * pool running in each process, so the code executing here will
             * NOT be running in our main thread like most other things -- so,
             * to update the UI, we need to use a Handler to hop over there.
             */
            @Override
            public void newStatus(String uuid, String state, String message, String level) throws RemoteException {
                MainActivity.this.updateState(state, message, 0, parseConnectionStatus(level));
            }
        };

        public void startStateListener() {
            if (mUseAidlService) {
                try {
                    mAidlService.registerStatusCallback(statusCallbackAidl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                VpnStatus.addStateListener(MainActivity.this);
            }
        }

        public void removeStateListener() {
            if (mUseAidlService) {
                try {
                    mAidlService.unregisterStatusCallback(statusCallbackAidl);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            } else {
                VpnStatus.removeStateListener(MainActivity.this);
            }
        }

        private void startVPN1() {
            if (mUseAidlService) {
                try {
                    // Request permission to use the API
                    Intent i = mAidlService.prepare(getPackageName());
                    if (i != null) {
                        startActivityForResult(i, ICS_OPENVPN_PERMISSION);
                    } else {
                        onActivityResult(ICS_OPENVPN_PERMISSION, Activity.RESULT_OK, null);
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
//                try {
//                    prepareStartProfile(START_PROFILE_EMBEDDED);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            } else {
                launchVPN();
            }
        }

        public void startVPN(VpnProfile profile) {
            mActiveVpnProfile = profile;
//            int externalVpn = ConfigManager.getInstance(MainActivity.this).prefIntForKey(ConfigManager.PK_ICS_OPENVPN);
            boolean isIcsOpenVpnPresent;
            boolean isIcsOpenVpnPresentNewer = false;
            try {
                PackageInfo pinfo = getPackageManager().getPackageInfo("de.blinkt.openvpn", 0);
                if (pinfo != null) {
                    isIcsOpenVpnPresentNewer = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 < pinfo.versionCode;
                }
                isIcsOpenVpnPresent = true;
            } catch (PackageManager.NameNotFoundException e) {
                Log.i(TAG, "ics-openvpn not found");
                isIcsOpenVpnPresent = false;
            }
//            switch (externalVpn) {
//                case ConfigManager.ICS_OPENVPN_EXTERNAL:
//                    mUseAidlService = isIcsOpenVpnPresent;
//                    break;
//                case ConfigManager.ICS_OPENVPN_BUILTIN:
//                    mUseAidlService = false;
//                    break;
//                case ConfigManager.ICS_OPENVPN_AUTO:
//                    mUseAidlService = isIcsOpenVpnPresentNewer;
//                    break;
//            }
            state = ServiceSwitcherStatus.LAUNCHING;
            bindToService();
        }

        public void onResume() {
            Intent intent = new Intent(MainActivity.this, OpenVPNService.class);
            intent.setAction(OpenVPNService.START_SERVICE);
            bindService(intent, vpnServiceConn, Context.BIND_AUTO_CREATE);
            state = WORKING;
            shouldFinishOnDisconnect = true;
        }

        public void onPause() {
            unbindService(vpnServiceConn);
        }

//        public void addCurrentProfile() {
//            if (!mUseAidlService) {
//                Log.e(TAG, "addCurrentProfile() cannot be called for builtin OpenVPN");
//                return;
//            }
//            moveOptionsToConnection(mActiveVpnProfile);
//            int vpnok = mActiveVpnProfile.checkProfile(MainActivity.this);
//            if (vpnok != R.string.no_error_found) {
//                Log.e(TAG, "checkProfile failed: " + getString(vpnok));
//                return;
//            }
//            final String profileStr = mActiveVpnProfile.getConfigFile(MainActivity.this, false);
//
//            ServerResponse serverInfo = IPChecker.getInstance(MainActivity.this).serverForVpnByIp(mActiveVpnProfile.mServerName);
//            final String location = (String) serverInfo.getIsoCode();
//            final String proto = (mActiveVpnProfile.mUseUdp ? "UDP" : "TCP") + " " + mActiveVpnProfile.mServerPort;
//            String profileName = location + ": " + proto;
//
//            LayoutInflater li = LayoutInflater.from(MainActivity.this);
//            View promptsView = li.inflate(R.layout.add_profile_dialog, null);
//
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
//            alertDialogBuilder.setView(promptsView);
//
//            final EditText userInput = (EditText) promptsView.findViewById(R.id.editProfileName);
//            userInput.setText(profileName);
//
//            alertDialogBuilder
//                    .setCancelable(false)
//                    .setPositiveButton("OK",
//                            (dialog, id) -> {
//                                try {
//                                    boolean ok = mAidlService.addVPNProfile(userInput.getText().toString(), profileStr);
//                                    Toast.makeText(MainActivity.this, ok ? "Profile added to OpenVPN" : "Can't export profile", Toast.LENGTH_LONG).show();
//                                } catch (RemoteException e) {
//                                    e.printStackTrace();
//                                }
//                            })
//                    .setNegativeButton("Cancel",
//                            (dialog, id) -> dialog.cancel());
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
//
//        }

        private class startOpenVpnThread extends Thread {

            @Override
            public void run() {
                VPNLaunchHelper.startOpenVpn(mActiveVpnProfile, getBaseContext());
            }
        }


        /**
         * from VPN permission dialog
         */
        void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (!mUseAidlService) {
                if (requestCode == START_VPN_CMD) {
                    if (resultCode == Activity.RESULT_OK) {
                        //TODO uncomment
//                        int needpw = mActiveVpnProfile.needUserPWInput(false);
                        int needpw = 0;
                        if (needpw != 0) {
                            VpnStatus.updateStateString("USER_VPN_PASSWORD", "", R.string.state_user_vpn_password,
                                    ConnectionStatus.LEVEL_AUTH_FAILED);
                        } else {
                            Log.i("ProfileManager", "Package Name: " + MainActivity.this.getPackageName());
//                                TODO ACTIVE VPN PROFILE NOTE
//                            ProfileManager.updateLRU(MainActivity.this, activeVpnProfile);
                            Collection<VpnProfile> vpnProfiles = ProfileManager.getInstance(MainActivity.this).getProfiles();
                            Log.i("MainActivity", "VPN Profiles: " + vpnProfiles.size());

                            new startOpenVpnThread().start();
                            startStateListener();
                        }
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        // User does not want us to start, so we just vanish
                        VpnStatus.updateStateString("USER_VPN_PERMISSION_CANCELLED", "", R.string.state_user_vpn_permission_cancelled,
                                ConnectionStatus.LEVEL_NOTCONNECTED);


                        runOnUiThread(() -> {
                            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
                            connectionState = NOT_CONNECTED;
                            if (mainFragment != null) {
                                mainFragment.updateState();
                            }
                        });

                    }
                }
            } else {
                if (requestCode == ICS_OPENVPN_PERMISSION && resultCode == Activity.RESULT_OK) {
                    startEmbeddedProfile(mActiveVpnProfile);
                    startStateListener();
                }
            }
        }

        public void cancelNotification() {
            // remove status bar notifications for local service only
            if (!mUseAidlService && vpnService != null) {
                //TODO uncomment
//				vpnService.setConfigurationIntent(null);
//				vpnService.setNotificationIntent(null);
//                vpnService.cancelNotification();
            }
        }

        private void startEmbeddedProfile(VpnProfile profile) {
            try {
                moveOptionsToConnection(profile);
                int vpnok = profile.checkProfile(MainActivity.this);
                if (vpnok != R.string.no_error_found) {
                    Log.e(TAG, "checkProfile failed: " + getString(vpnok));
                    return;
                }
                String ss = profile.getConfigFile(MainActivity.this, false);
                mAidlService.startVPN(ss);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private ConnectionStatus parseConnectionStatus(String statusName) {
            for (ConnectionStatus status : ConnectionStatus.values()) {
                if (status.name().equals(statusName)) {
                    Log.d(TAG, "parseConnectionStatus: " + statusName + "=" + status.toString());
                    return status;
                }
            }
            Log.d(TAG, "parseConnectionStatus: " + statusName + "=" + ConnectionStatus.UNKNOWN_LEVEL.toString());
            return ConnectionStatus.UNKNOWN_LEVEL;
        }

    }

    private void moveOptionsToConnection(VpnProfile profile) {
        profile.mConnections = new Connection[1];
        Connection conn = new Connection();

        conn.mServerName = profile.mServerName;
        conn.mServerPort = profile.mServerPort;
        conn.mUseUdp = profile.mUseUdp;
        conn.mCustomConfiguration = "";

        profile.mConnections[0] = conn;

    }
}
