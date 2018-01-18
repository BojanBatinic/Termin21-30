package com.example.androiddevelopment.glumcilegende.async;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.androiddevelopment.glumcilegende.tools.ReviewerTools;

/**
 * Created by BBLOJB on 17.1.2018..
 */

public class SimpleService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    /**
     * Metoda koja se poziva prilikom izvrsavanja zadatka servisa
     * Koristeci Intent mozemo prilikom startovanja servisa proslediti
     * odredjene parametre.
     * */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        /**
         * Provericemo trenutnu povezanost sa mrezom.
         * Za ovo koristimo dostupne pozive android operativnog sistema
         * */
       // int status = ReviewerTools.getConStatus(getApplicationContext());
        int status = intent.getExtras().getInt("STATUS");
        /**
         * Primer poziva asinhronog zadatka ako ima veze ka mrezi
         * npr. sinhronizacija mail-ova, fotografija, muzike, dokumenata i sl.
         * */
        if (status == ReviewerTools.TYPE_WIFI){
            new SimpleSyncTask(getApplicationContext()).execute(status);
        }
        /**
         * Zaustaviti servis nakon obavljenog pokretanja asinhronog zadatka.
         * Ovu metodu nije potrebno pozvati ako zelimo da nasa aplikacija
         * konstantno osluskuje na neke izmene (npr. novi email, viber poruka i sl)
         * */
        stopSelf();

        /**
         * Ako iz nekog razloga operativni sistem ubije servis
         * ne kreirati novi.
         * */
        return START_NOT_STICKY;
    }
}
