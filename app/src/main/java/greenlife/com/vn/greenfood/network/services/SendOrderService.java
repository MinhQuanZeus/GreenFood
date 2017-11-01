package greenlife.com.vn.greenfood.network.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import greenlife.com.vn.greenfood.network.models.order.SendOrderRequest;
import greenlife.com.vn.greenfood.network.models.order.SendOrderResponse;

/**
 * Created by EDGY on 6/30/2017.
 */

public interface SendOrderService {
    @POST("send")
    Call<SendOrderResponse> sendOrder(
            @HeaderMap Map<String, String> headers,
            @Body SendOrderRequest sendOrderRequest
    );
}

