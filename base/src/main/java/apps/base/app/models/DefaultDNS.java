package apps.base.app.models;

public class DefaultDNS {

    private String primaryDNS;
    private String secondaryDNS;

    public String getPrimaryDNS() {
        return primaryDNS;
    }
    public void setPrimaryDNS(String primaryDNS) {
        this.primaryDNS = primaryDNS;
    }
    public String getSecondaryDNS() {
        return secondaryDNS;
    }
    public void setSecondaryDNS(String secondaryDNS) {
        this.secondaryDNS = secondaryDNS;
    }

    @Override public String toString() {
        return "DefaultDNS{" +
                "primaryDNS='" + primaryDNS + '\'' +
                ", secondaryDNS='" + secondaryDNS + '\'' +
                '}';
    }
}
