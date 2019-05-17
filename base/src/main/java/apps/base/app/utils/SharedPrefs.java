package apps.base.app.utils;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import apps.base.app.models.DefaultDNS;
import apps.base.app.models.SettingsSwitches;
import apps.base.app.models.User;

public class SharedPrefs {

    private static final String SHARED_USER_EMAIL = "sharedUserEmail";
    private static final String SHARED_USER_TYPE = "sharedUserType";
    private static final String SHARED_USER_STATUS = "sharedUserStatus";
    private static final String SHARED_USER_PASSWORD = "sharedUserPassword";
    private static final String SHARED_USER_EXPIRATION_DATE = "sharedExpirationDate";
    private static final String SHARED_ENCRYPTION_TYPE = "sharedDefaultEncryptionType";
    private static final String SHARED_PRIMARY_DNS = "sharedPrimaryDNS";
    private static final String SHARED_SECONDARY_DNS = "sharedSecondaryDNS";
    private static final String SHARED_KILL_INTERNET = "sharedKillInternet";
    private static final String SHARED_AUTO_CONNECT_WIFI = "sharedAutoConnectOnPublicWiFi";
    private static final String SHARED_AUTO_CONNECT_LAUNCH = "sharedAutoConnectOnLaunch";
    private static final String SHARED_DISPLAY_ALL_SERVERS = "sharedDisplayAllServers";
    private static final String SHARED_TEMP_USER = "sharedTempsUser";
    private static final String SHARED_USER_USERNAME = "sharedUsername";

    private static SharedPrefs sharedPrefs;
    private SharedPreferences sharedPreferences;
    private User user;
    private String userPassword;
    private String encryptionType;
    private DefaultDNS defaultDNS;
    private SettingsSwitches settingsSwitches;

    private SharedPrefs(Application application) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(application);
    }

    public static SharedPrefs getSharedPrefs(Application application) {
        if (sharedPrefs == null) {
            sharedPrefs = new SharedPrefs(application);
        }
        return sharedPrefs;
    }

    public void updateDefaultEncryptionType(String encryptionType) {
        boolean success = sharedPreferences.edit()
                .putString(SHARED_ENCRYPTION_TYPE, encryptionType)
                .commit();
        if (success) {
            this.encryptionType = encryptionType;
        }
    }

    public String getDefaultEncryptionType() {
        if (this.encryptionType == null) {
            this.encryptionType = sharedPreferences.getString(SHARED_ENCRYPTION_TYPE, "");
        }
        return this.encryptionType;
    }

    public void updateUserPassword(String userPassword) {
        boolean success = sharedPreferences.edit()
                .putString(SHARED_USER_PASSWORD, userPassword)
                .commit();
        if (success) {
            this.userPassword = userPassword;
        }
    }

    public String getUserPassword() {
        if (this.userPassword == null) {
            this.userPassword = sharedPreferences.getString(SHARED_USER_PASSWORD, "");
        }
        return this.userPassword;
    }

    public void updateUserData(User user) {
        boolean success = sharedPreferences.edit()
                .putString(SHARED_USER_EMAIL, user.getEmail())
                .putString(SHARED_USER_USERNAME, user.getUsername())
                .putString(SHARED_USER_TYPE, user.getType())
                .putString(SHARED_USER_STATUS, user.getStatus())
                .putString(SHARED_USER_EXPIRATION_DATE, user.getExpirationDate())
                .commit();
        if (success) {
            this.user = user;
        }
    }

    public User getUser() {
        if (user == null) {
            user = new User();
            user.setEmail(sharedPreferences.getString(SHARED_USER_EMAIL, ""));
            user.setUsername(sharedPreferences.getString(SHARED_USER_USERNAME, ""));
            user.setType(sharedPreferences.getString(SHARED_USER_TYPE, ""));
            user.setStatus(sharedPreferences.getString(SHARED_USER_STATUS, ""));
            user.setExpirationDate(sharedPreferences.getString(SHARED_USER_EXPIRATION_DATE, ""));
        }
        return user;
    }

    public void updateDefaultDNS(DefaultDNS defaultDNS) {
        boolean success = sharedPreferences.edit()
                .putString(SHARED_PRIMARY_DNS, defaultDNS.getPrimaryDNS())
                .putString(SHARED_SECONDARY_DNS, defaultDNS.getSecondaryDNS())
                .commit();
        if (success) {
            this.defaultDNS = defaultDNS;
        }
    }

    public DefaultDNS getDefaultDNS() {
        if (defaultDNS == null) {
            defaultDNS = new DefaultDNS();
            defaultDNS.setPrimaryDNS(sharedPreferences.getString(SHARED_PRIMARY_DNS, "8.8.8.8"));
            defaultDNS.setSecondaryDNS(sharedPreferences.getString(SHARED_SECONDARY_DNS, "8.8.4.4"));
        }
        return defaultDNS;
    }

    public void updateSwitches(SettingsSwitches settingsSwitches) {
        boolean success = sharedPreferences.edit()
                .putBoolean(SHARED_KILL_INTERNET, settingsSwitches.isKillInternet())
                .putBoolean(SHARED_AUTO_CONNECT_WIFI, settingsSwitches.isAutoConnectOnPublicWiFi())
                .putBoolean(SHARED_AUTO_CONNECT_LAUNCH, settingsSwitches.isAutoConnectOnLaunch())
                .putBoolean(SHARED_DISPLAY_ALL_SERVERS, settingsSwitches.isDisplayAllServers())
                .commit();
        if (success) {
            this.settingsSwitches = settingsSwitches;
        }
    }

    public SettingsSwitches getSwitches() {
        if (settingsSwitches == null) {
            settingsSwitches = new SettingsSwitches();
            settingsSwitches.setKillInternet(sharedPreferences.getBoolean(SHARED_KILL_INTERNET, false));
            settingsSwitches.setAutoConnectOnPublicWiFi(sharedPreferences.getBoolean(SHARED_AUTO_CONNECT_WIFI, false));
            settingsSwitches.setAutoConnectOnLaunch(sharedPreferences.getBoolean(SHARED_AUTO_CONNECT_LAUNCH, false));
            settingsSwitches.setDisplayAllServers(sharedPreferences.getBoolean(SHARED_DISPLAY_ALL_SERVERS, true));
        }
        return settingsSwitches;
    }

    public void setTempUser(boolean isTempUser) {
        boolean success = sharedPreferences.edit()
                .putBoolean(SHARED_TEMP_USER, isTempUser)
                .commit();
        if (success) {}
    }

    public boolean isTempUser() {
        return sharedPreferences.getBoolean(SHARED_TEMP_USER, true);
    }


    public boolean clear() {
        return  sharedPreferences.edit().clear().commit();
    }
}
