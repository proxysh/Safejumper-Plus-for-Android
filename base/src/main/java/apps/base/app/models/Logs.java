package apps.base.app.models;

public class Logs {

    private String appLogs;
    private String vpnLogs;

    public String getAppLogs() {
        return appLogs != null ? appLogs : "";
    }
    public void setAppLogs(String appLogs) {
        this.appLogs = appLogs;
    }
    public String getVpnLogs() {
        return vpnLogs != null ? vpnLogs : "";
    }
    public void setVpnLogs(String vpnLogs) {
        this.vpnLogs = vpnLogs;
    }

    @Override public String toString() {
        return "Logs{" +
                "appLogs='" + appLogs + '\'' +
                ", vpnLogs='" + vpnLogs + '\'' +
                '}';
    }
}
