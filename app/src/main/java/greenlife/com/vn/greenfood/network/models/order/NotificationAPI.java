package greenlife.com.vn.greenfood.network.models.order;

import com.google.gson.annotations.SerializedName;


public class NotificationAPI {
    @SerializedName("body")
    private Order order;

    public NotificationAPI(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}

