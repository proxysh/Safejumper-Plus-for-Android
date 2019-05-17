package apps.base.app.models;


public class ServerResponse<DataType> {

    private boolean status;
    private String message;
    private DataType data;

    public ServerResponse(boolean status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public DataType getData() {
        return data;
    }

    public void setData(DataType data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return status;
    }

    @Override public String toString() {
        return "ServerResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}