package apps.base.app.models;

public class User {

    private String email;
    private String username;
    private String type;
    private String status;
    private String expirationDate;

    public String getEmail() {
        return email;
    }
    public String getType() {
        return type;
    }
    public String getStatus() {
        return status;
    }
    public String getExpirationDate() {
        return expirationDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    @Override public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                '}';
    }
}
