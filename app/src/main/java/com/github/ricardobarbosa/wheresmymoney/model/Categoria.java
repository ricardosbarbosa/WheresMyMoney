package com.github.ricardobarbosa.wheresmymoney.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;

/**
 * Created by ricardobarbosa on 19/02/17.
 */

public class Categoria implements Parcelable{
    public static final String PARCELABLE_KEY = "categoria";

    private String nome;

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    public Categoria(Cursor cursor) {
        this.nome = cursor.getString(cursor.getColumnIndex(WIMMContract.CategoriaEntry.COLUMN_NOME));
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    protected Categoria(Parcel in) {
        nome = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Categoria> CREATOR = new Parcelable.Creator<Categoria>() {
        @Override
        public Categoria createFromParcel(Parcel in) {
            return new Categoria(in);
        }

        @Override
        public Categoria[] newArray(int size) {
            return new Categoria[size];
        }
    };
}
