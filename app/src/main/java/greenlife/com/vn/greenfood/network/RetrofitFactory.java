package greenlife.com.vn.greenfood.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Quang Ly on 11/1/2017.
 */

public class RetrofitFactory {
    private static Retrofit retrofit;
    private static RetrofitFactory retrofitFactory;

    public static RetrofitFactory getInstance(String url){
        if(retrofitFactory == null){
            return new RetrofitFactory(url);
        }
        return null;
    }

    private RetrofitFactory(String url){
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static <ServiceClass> ServiceClass createService(Class<ServiceClass> serviceClass){
        return retrofit.create(serviceClass);
    }
}
