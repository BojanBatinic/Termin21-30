package com.example.androiddevelopment.glumcilegende.net;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by BBLOJB on 22.1.2018..
 */

public class MyService {

    /**
     * Prvo je potrebno da definisemo retrofit instancu preko koje ce komunikacija ici
     * */
    public static Retrofit getRetrofitInstance(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MyServiceContract.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }
    /**
     * Definisemo konkretnu instancu servisa na intnerntu sa kojim
     * vrsimo komunikaciju
     * */
    public static MyApiEndInt apiInterface(){
        MyApiEndInt apiService = getRetrofitInstance().create(MyApiEndInt.class);

        return apiService;
    }

}
