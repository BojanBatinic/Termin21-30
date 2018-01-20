package com.example.androiddevelopment.glumcilegende.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.androiddevelopment.glumcilegende.provider.GlumacContract;
import com.example.androiddevelopment.glumcilegende.provider.model.Glumac;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by BBLOJB on 19.1.2018..
 */

public class DbHelper extends OrmLiteSqliteOpenHelper {

    private Dao<Glumac, Integer> mGlumacDao = null;

    //potrebno je dodati konstruktor zbog pravilne inicijalizacije biblioteke
    public DbHelper(Context context){
        super(context, GlumacContract.DATABASE_NAME, null, GlumacContract.DATABASE_VERSION);
    }

    //Prilikom kreiranja baze potrebno je da pozovemo odgovarajuce metode biblioteke
    //prilikom kreiranja moramo pozvati TableUtils.createTable za svaku tabelu koju imamo
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTable(connectionSource, Glumac.class);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //kada zelimo da izmenimo tabele, moramo pozvati TableUtils.dropTable za sve
    // tabele koje imamo
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Glumac.class, true);
            onCreate(database, connectionSource);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    //jedan Dao objekat sa kojim komuniciramo. Ukoliko imamo vise tabela
    //potrebno je napraviti Dao objekat za svaku tabelu
    public Dao<Glumac, Integer> getGlumacDao() throws SQLException{
        if (mGlumacDao == null){
         mGlumacDao = getDao(Glumac.class);
        }
        return mGlumacDao;
    }
    //obavezno prilikom zatvarnaj rada sa bazom osloboditi resurse
    @Override
    public void close(){
        mGlumacDao = null;

        super.close();
    }

}