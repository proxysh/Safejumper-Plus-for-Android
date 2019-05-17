package apps.base.app.models;

import java.util.Objects;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PortProtocolMap extends RealmObject {

    @PrimaryKey private String key;
    private RealmList<PortProtocol> value;

    public PortProtocolMap() {}

    public PortProtocolMap(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
    public RealmList<PortProtocol> getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setValue(RealmList<PortProtocol> value) {
        this.value = value;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortProtocolMap that = (PortProtocolMap) o;
        return Objects.equals(key, that.key);
    }

    @Override public int hashCode() {
        return Objects.hash(key);
    }

    @Override public String toString() {
        return "PortProtocolMap{" +
                "key='" + key + '\'' +
                ", value=" + value +
                '}';
    }
}
