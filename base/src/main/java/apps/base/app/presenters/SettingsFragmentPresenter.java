package apps.base.app.presenters;

import apps.base.app.BaseAppApplication;
import apps.base.app.dagger.components.ApplicationComponent;
import apps.base.app.models.SettingsSwitches;
import apps.base.app.presenters.opts.ISettingsFragmentPresenterOpts;
import apps.base.app.utils.ConfigManager;
import apps.base.app.utils.SharedPrefs;
import apps.base.app.views.activities.MainActivity;
import apps.base.app.views.fragment.MainFragment;
import apps.base.app.views.fragment.SettingsFragment;

import static apps.base.app.views.activities.MainActivity.shouldFinishOnDisconnect;
import static apps.base.app.views.dialogs.ContactUsDialogFragment.CONTACT_US;
import static apps.base.app.views.fragment.MainFragment.NOT_CONNECTED;
import static apps.base.app.views.fragment.SettingsFragment.AUTO_CONNECT_LAUNCH;
import static apps.base.app.views.fragment.SettingsFragment.AUTO_CONNECT_WIFI;
import static apps.base.app.views.fragment.SettingsFragment.DISPLAY_ALL_SERVERS;
import static apps.base.app.views.fragment.SettingsFragment.KILL_INTERNET;

public class SettingsFragmentPresenter extends BaseFragmentPresenter<MainActivity, SettingsFragment> implements ISettingsFragmentPresenterOpts {

    private SharedPrefs sharedPrefs;

    @Override public void onFragmentReady(SettingsFragment fragment) {
        super.onFragmentReady(fragment);

        ApplicationComponent component = ((BaseAppApplication) activity.getApplication()).getComponent();
        sharedPrefs = component.sharedPreferences();

        fragment.updateUserData(sharedPrefs.getUser());
        fragment.updateDefaultDNS(sharedPrefs.getDefaultDNS());
        fragment.updateSwitches(sharedPrefs.getSwitches());
    }

    @Override public void onInfoAction() {
        activity.showContactUsDialog(
                clickType -> {
                    switch (clickType) {
                        case CONTACT_US:
                            activity.startBrowserUrl(ConfigManager.getField("contactUs"));
                            break;
                        default:
                            break;
                    }
                }
        );
    }

    @Override public void onLogoutAction() {
        activity.popBackStack();
        sharedPrefs.clear();
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if (fragment != null) {
            fragment.loggedOut();
        }
        onDisconnectFromVPN();
        activity.startSignInFragment();
    }

    private void onDisconnectFromVPN() {
        activity.stopVpn();
        MainActivity.connectionState = NOT_CONNECTED;
        if (shouldFinishOnDisconnect) {
            shouldFinishOnDisconnect = false;
//            activity.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    @Override public void onEditEncryptionTypeAction() {
        activity.showEncryptionDialog(
                choseEncryptionType -> {
                    sharedPrefs.updateDefaultEncryptionType(choseEncryptionType);
                    updateServerListEncryption();
                },
                ConfigManager.getEncryptionType(), sharedPrefs.getDefaultEncryptionType()
        );
    }

    private void updateServerListEncryption() {
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if(fragment != null) {
            fragment.updateAllServersEncryptionType();
        }
    }

    private void updateServerList() {
        MainFragment fragment = (MainFragment) activity.getSupportFragmentManager().findFragmentByTag(MainFragment.TAG);
        if(fragment != null) {
            fragment.onNewServerList();
        }
    }

    @Override public void onEditDefaultDNSAction() {
        activity.showModalDNSDialog(
                defaultDNS -> {
                    sharedPrefs.updateDefaultDNS(defaultDNS);
                    fragment.updateDefaultDNS(defaultDNS);
                },
                sharedPrefs.getDefaultDNS()
        );
    }

    @Override public void onManageAccountAction() {
        activity.startBrowserUrl(ConfigManager.getField("manageAccount"));
    }

    @Override public void onSwitchChanged(int type, boolean isChecked) {
        SettingsSwitches switches = sharedPrefs.getSwitches();
        switch (type) {
            case KILL_INTERNET:
                switches.setKillInternet(isChecked);
                break;
            case AUTO_CONNECT_WIFI:
                switches.setAutoConnectOnPublicWiFi(isChecked);
                break;
            case AUTO_CONNECT_LAUNCH:
                switches.setAutoConnectOnLaunch(isChecked);
                break;
            case DISPLAY_ALL_SERVERS:
                switches.setDisplayAllServers(isChecked);

                updateServerList();

                break;
        }
        sharedPrefs.updateSwitches(switches);
    }
}
