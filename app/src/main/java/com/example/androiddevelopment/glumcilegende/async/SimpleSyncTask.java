package com.example.androiddevelopment.glumcilegende.async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


/**
 * Created by BBLOJB on 24.11.2017.
 * AsyncTask klasa prima tri parametra prilikom specijalizacije
 * Korisnici sami definisu tip u zavisnosti od posla koji zele da obave.
 *
 * Prvi argument predstavlja ulazne parametre, ono sto zelimo
 * da posaljemo zadatku. Recimo ime fajla koji zelimo da skinemo
 *
 * Drugi argument je indikator kako ce se meriti progres. Koliko je posla
 * zavrseno i koliko je posla ostalo.
 *
 * Treci parametar je povratna vrednost, tj sta ce metoda doInBackground
 * vratiti kao povratnu vrednost metodi onPostExecute
 */

public class SimpleSyncTask extends AsyncTask<Integer, Void, Integer>{

    private Context context;

    public SimpleSyncTask(Context context){
        this.context = context;
    }
    /**
     * Metoda se poziva pre samog starta pozadinskog zadatka
     * Sve pripreme odraditi u ovoj metodi, ako ih ima.
     */
    @Override
    protected void onPreExecute(){

    }
    /**
     * Posao koji se odvija u pozadini, ne blokira glavnu nit aplikacije.
     * Sav posao koji dugo traje izvrsavati unutar ove metode.
     */
    @Override
    protected Integer doInBackground(Integer... params) {
        try {
            //simulacija posla koji se obavlja u pozadini i traje duze vreme
            Thread.sleep(500);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return params[0];
    }
    /**
     * Kada se posao koji se odvija u pozadini zavrsi, poziva se ova metoda
     * Ako je potrebno osloboditi resurse ili obrisati elemente koji vise ne trebaju.
     */
    @Override
    protected void onPostExecute(Integer type){
       // Toast.makeText(context, "Sync done", Toast.LENGTH_SHORT).show();
        /**
         * Da bi poslali poruku BroadcastReceiver-u poterbno je da definisiemo Intent sa sadrzajem.
         * Definisemo intent i sa njim nasu akciju SYNC_DATA. Ovo radimo da bi BroadcastReceiver
         * znao kako da reaguje kada dobije poruku tog tipa.
         * Uz poruku mozemo vezati i neki sadrazaj RESULT_CODE u ovom slucaju.
         * Jedan BroadcastReceiver moze da prima vise poruka iz aplikacije i iz tog razloga definisanje
         * akcije je bitna stvar.
         *
         * Voditi racuna o tome da se naziv akcije kada korisnik salje Intent mora poklapati sa
         * nazivom akcije kada akciju proveravamo unutar BroadcastReceiver-a. Isto vazi i za podatke.
         * Dobra praksa je da se ovi nazivi izdvoje unutar neke staticke promenljive.
         * */
        Intent ints = new Intent("SYNC_DATA");
        ints.putExtra("RESULT_CODE", type);
        context.sendBroadcast(ints);
    }

}
