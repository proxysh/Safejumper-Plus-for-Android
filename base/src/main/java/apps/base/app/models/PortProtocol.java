package apps.base.app.models;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PortProtocol extends RealmObject {

    @PrimaryKey private int id;
    private String protocol;
    int port;

    public PortProtocol() {}

    public PortProtocol(int id, String protocol, int port) {
        this.id = id;
        this.protocol = protocol;
        this.port = port;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public int getPort() {
        return port;
    }
    public void setPort(byte port) {
        this.port = port;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortProtocol that = (PortProtocol) o;
        return port == that.port &&
                Objects.equals(protocol, that.protocol);
    }

    @Override public int hashCode() {
        return Objects.hash(protocol, port);
    }

    @Override public String toString() {
        return "PortProtocol{" +
                "protocol='" + protocol + '\'' +
                ", port=" + port +
                '}';
    }
}
