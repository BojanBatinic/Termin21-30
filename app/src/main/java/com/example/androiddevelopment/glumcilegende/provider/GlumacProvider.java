package com.example.androiddevelopment.glumcilegende.provider;

import com.example.androiddevelopment.glumcilegende.model.Glumac;
import com.example.androiddevelopment.glumcilegende.model.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BBLOJB on 20.11.2017..
 */
/**
 * Da bi mogli da koristimo ORMLight maper, ne mozemo da koristimo
 * Podrazumevani ContentProvider. Za to ce nam pomoci Android-OrmLiteContentProvider
 * bibliteka
 *
 * ContentProvider mora da nasledi OrmLiteSimpleContentProvider i da se parametrizuje sa
 * nasom bazom
 *
 * U metodi onCreate potrebno je da kazemo koji to URI selektuje sve elemente iz tabele
 * odnosno po odredjenom id-u
 *
 * content://rs.aleph.android.example26/products selektuje sve
 * content://rs.aleph.android.example26/products/1 selektuje product sa id-om 1
 * */
public class GlumacProvider {

   public static List<Glumac> getGlumci() {

        Film partizanski = new Film(0, "Partizanski");
        Film mangupski = new Film(1, "Mangupski");
        Film komedija = new Film(2, "Komedija" );

        List<Glumac> glumci = new ArrayList<>();
        glumci.add(new Glumac(0, "velimir.jpg", "Velimir Bata Živojinović", "Legenda jugoslovenskog i srpskog glumišta, uvek partizan...", 5.0f, partizanski));
        glumci.add(new Glumac(1, "dragan.jpg", "Dragan Gaga Nikolić", "Večni mangup jugoslovesnkog i srpskog glumišta, čime postaje legenda", 5.0f, mangupski));
        glumci.add(new Glumac(2, "zoran.jpg", "Zoran Radmilović", "Zauvek Radovan III, retko duhovit i dosetljiv, legenda među legendama", 5.0f, komedija));
        return glumci;
    }

    public static Glumac getGlumacById(int id){

        Film partizanski = new Film(0, "Partizanski");
        Film mangupski = new Film(1, "Mangupski");
        Film komedija = new Film(2, "Komedija" );

        switch (id){
            case 0:
                return new Glumac(0, "velimir.jpg", "Velimir Bata Živojinović", "Legenda jugoslovenskog i srpskog glumišta, uvek partizan...", 5.0f, partizanski);
            case 1:
                return new Glumac(1, "dragan.jpg", "Dragan Gaga Nikolić", "Večni mangup jugoslovesnkog i srpskog glumišta, čime postaje legenda", 5.0f, mangupski);
            case 2:
                return new Glumac(2, "zoran.jpg", "Zoran Radmilović", "Zauvek Radovan III, retko duhovit i dosetljiv, legenda među legendama", 5.0f, komedija);
                default:
                    return null;
        }
    }
}
