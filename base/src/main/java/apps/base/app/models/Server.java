package apps.base.app.models;

import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Server extends RealmObject {

    @PrimaryKey
    private String ip;
    private String name;
    private String hostname;
    private String isoCode;
    private RealmList<String> encryptionTypes;
    private RealmList<PortProtocolMap> ports;
    private float serverLoad;
    private float serverPing;

    private boolean isFavourite;
    private boolean isInHubs;
    private boolean isMainServer;

    private String encryptionType;
    private String port;

    public Server() {}

    public String getIp() {
        return ip != null ? ip : "";
    }
    public String getName() {
        return name != null ? name : "";
    }
    public String getHostname() {
        return hostname != null ? hostname : "";
    }
    public String getIsoCode() {
        return isoCode != null ? isoCode : "";
    }
    public RealmList<PortProtocolMap> getPorts() {
        return ports != null ? ports : new RealmList<>();
    }
    public float getServerLoad() {
        return serverLoad;
    }
    public float getServerPing() {
        return serverPing;
    }
    public void setServerPing(float serverPing) {
        this.serverPing = serverPing;
    }

    public String getPort() {
        return port != null ? port : "";
    }
    public String getEncryptionType() {
        return encryptionType != null ? encryptionType : "";
    }

    public RealmList<String> getEncryptionTypes() {
        return encryptionTypes;
    }

    public void setEncryptionType(String encryptionType) {
        this.encryptionType = encryptionType;
    }
    public void setPort(String port) {
        this.port = port;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }
    public boolean isInHubs() {
        return isInHubs;
    }
    public void setInHubs(boolean inHubs) {
        isInHubs = inHubs;
    }

    public void setMainServer(boolean mainServer) {
        isMainServer = mainServer;
    }

    public boolean isMainServer() {
        return isMainServer;
    }

    public boolean isFavourite() {
        return isFavourite;
    }
    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(ip, server.isValid() ? server.ip : null) &&
                Objects.equals(name, server.isValid() ? server.name : null) &&
                Objects.equals(isoCode, server.isValid() ? server.isoCode: null);
    }

    @Override public int hashCode() {
        return Objects.hash(ip, name, isoCode);
    }

    @Override public String toString() {
        return "Server{" +
                "ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", hostname='" + hostname + '\'' +
                ", isoCode='" + isoCode + '\'' +
                ", encryptionTypes=" + encryptionTypes +
                ", ports=" + ports +
                ", serverLoad=" + serverLoad +
                ", serverPing=" + serverPing +
                ", isFavourite=" + isFavourite +
                ", isInHubs=" + isInHubs +
                ", isMainServer=" + isMainServer +
                ", encryptionType='" + encryptionType + '\'' +
                ", port='" + port + '\'' +
                '}';
    }
}
