package greenlife.com.vn.greenfood.network.services;

import greenlife.com.vn.greenfood.network.models.distance.MainObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GetDistanceService {
//    @GET("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins={current}&destinations={destinate}&key=AIzaSyCatOAS9MJMa5XODZ-DP5rLPFVGKkxQ7is")
    @GET("https://maps.googleapis.com/maps/api/distancematrix/json")
    Call<MainObject> getDistance(
            @Query("units") String units,
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("key") String key
    );
}
