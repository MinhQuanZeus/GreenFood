package greenlife.com.vn.greenfood.network.models.order;

import com.google.gson.annotations.SerializedName;



public class SendOrderRequest {
    @SerializedName("to")
    private String tokenID;
    @SerializedName("notification")
    private NotificationAPI notificationAPI;

    public SendOrderRequest(String tokenID, NotificationAPI notificationAPI) {
        this.tokenID = tokenID;
        this.notificationAPI = notificationAPI;
    }

    public String getTokenID() {
        return tokenID;
    }

    public NotificationAPI getNotificationAPI() {
        return notificationAPI;
    }
}
