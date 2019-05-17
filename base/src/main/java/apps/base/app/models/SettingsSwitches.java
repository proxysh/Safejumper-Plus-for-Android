package apps.base.app.models;

public class SettingsSwitches {

    private boolean killInternet;
    private boolean autoConnectOnPublicWiFi;
    private boolean autoConnectOnLaunch;
    private boolean displayAllServers;

    public boolean isKillInternet() {
        return killInternet;
    }
    public void setKillInternet(boolean killInternet) {
        this.killInternet = killInternet;
    }
    public boolean isAutoConnectOnPublicWiFi() {
        return autoConnectOnPublicWiFi;
    }
    public void setAutoConnectOnPublicWiFi(boolean autoConnectOnPublicWiFi) {
        this.autoConnectOnPublicWiFi = autoConnectOnPublicWiFi;
    }
    public boolean isAutoConnectOnLaunch() {
        return autoConnectOnLaunch;
    }
    public void setAutoConnectOnLaunch(boolean autoConnectOnLaunch) {
        this.autoConnectOnLaunch = autoConnectOnLaunch;
    }
    public boolean isDisplayAllServers() {
        return displayAllServers;
    }
    public void setDisplayAllServers(boolean displayAllServers) {
        this.displayAllServers = displayAllServers;
    }

    @Override public String toString() {
        return "SettingsSwitches{" +
                "killInternet=" + killInternet +
                ", autoConnectOnPublicWiFi=" + autoConnectOnPublicWiFi +
                ", autoConnectOnLaunch=" + autoConnectOnLaunch +
                ", displayAllServers=" + displayAllServers +
                '}';
    }
}
