package com.example.androiddevelopment.glumcilegende.provider.model;

import com.example.androiddevelopment.glumcilegende.provider.GlumacContract;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;

/**
 * Created by BBLOJB on 20.1.2018..
 */

//Kao i do sada moramo mapirati naziv tabele da bi znali kako da je smestimo u bazu
@DatabaseTable(tableName = GlumacContract.Glumac.TABLE_NAME)
//Moramo dodati i content URI da bi znali gde se tacno nasa tabela nalazi content://AUTHORITY/glumci
@AdditionalAnnotation.DefaultContentUri(authority = GlumacContract.AUTORITY, path = GlumacContract.Glumac.CONTENT_URI_PATH)
//Takodje moramo mapirati i MIMETYPE_NAME da bi znali na koje sve tipove nasa tabela moze da reaguje tekst, slike, nasa tabela itd
@AdditionalAnnotation.DefaultContentMimeTypeVnd(name = GlumacContract.Glumac.MIMETYPE_NAME, type = GlumacContract.Glumac.MIMETYPE_TYPE)

public class Glumac {

    @DatabaseField(columnName = GlumacContract.Glumac._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private int mId;

    @DatabaseField(columnName = GlumacContract.Glumac.FILED_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = GlumacContract.Glumac.FILED_NAME_BIOGRAFIJA)
    private String biografija;

    @DatabaseField(columnName = GlumacContract.Glumac.FILED_NAME_RATING)
    private float rating;

    @DatabaseField(columnName = GlumacContract.Glumac.FILED_NAME_IMAGE)
    private String image;

    //ORMLite zahteva prazan konstuktur u klasama koje opisuju tabele u bazi!
    public Glumac(){
    }

    /**getters & setters**/
    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getBiografija() {
        return biografija;
    }

    public void setBiografija(String biografija) {
        this.biografija = biografija;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return mName;
    }
}
