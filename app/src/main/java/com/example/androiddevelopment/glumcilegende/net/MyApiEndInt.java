package com.example.androiddevelopment.glumcilegende.net;

/**
 * Created by BBLOJB on 22.1.2018..
 */

import com.example.androiddevelopment.glumcilegende.net.model.Event;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Klasa koja opisuje tj mapira putanju servisa
 * opisuje koji metod koristimo ali i sta ocekujemo kao rezultat
 * */

public interface MyApiEndInt {

    @GET("artists/{artist}/events")
    Call<List<Event>> getArtistByName(@Path("artist") String artist, @QueryMap Map<String, String> options);
}
