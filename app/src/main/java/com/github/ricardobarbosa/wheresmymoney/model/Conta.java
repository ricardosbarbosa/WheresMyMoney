package com.github.ricardobarbosa.wheresmymoney.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.ContaEntry;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ricardobarbosa on 19/02/17.
 */

public class Conta implements Parcelable{

    public static final String PARCELABLE_KEY = "categoria";

    private String nome;
    private EnumContaTipo tipo;
    private Double saldo;

    public Conta() {}

    public Conta(String nome, EnumContaTipo tipo, Double saldo) {
        this.nome = nome;
        this.tipo = tipo;
        this.saldo = saldo;
    }

    public Conta(Cursor cursor ) {
//        this.id = cursor.getInt(cursor.getColumnIndex(ContaEntry._ID));
        this.nome = cursor.getString(cursor.getColumnIndex(ContaEntry.COLUMN_NOME));
        this.tipo = EnumContaTipo.valueOf(cursor.getString(cursor.getColumnIndex(ContaEntry.COLUMN_TIPO)));
        this.saldo = cursor.getDouble(cursor.getColumnIndex(ContaEntry.COLUMN_SALDO));
    }
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Conta conta = (Conta) o;

        return nome.equals(conta.nome);

    }

    @Override
    public int hashCode() {
        return nome.hashCode();
    }

    @Override
    public String toString() {
        return "Conta{" +
                "nome='" + nome + '\'' +
                '}';
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public EnumContaTipo getTipo() {
        return tipo;
    }

    public void setTipo(EnumContaTipo tipo) {
        this.tipo = tipo;
    }

    public boolean validate() {
        return !TextUtils.isEmpty(nome)
                && tipo != null;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("tipo", tipo);
        result.put("saldo", saldo);

        return result;
    }

    protected Conta(Parcel in) {
        nome = in.readString();
        tipo = (EnumContaTipo) in.readValue(EnumContaTipo.class.getClassLoader());
        saldo = in.readByte() == 0x00 ? null : in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nome);
        dest.writeValue(tipo);
        if (saldo == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(saldo);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Conta> CREATOR = new Parcelable.Creator<Conta>() {
        @Override
        public Conta createFromParcel(Parcel in) {
            return new Conta(in);
        }

        @Override
        public Conta[] newArray(int size) {
            return new Conta[size];
        }
    };

    public Double addValor(Double valor) {
        this.saldo += valor;
        return saldo;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();

        values.put(ContaEntry.COLUMN_NOME, this.nome);
        values.put(ContaEntry.COLUMN_SALDO, this.saldo);
        values.put(ContaEntry.COLUMN_TIPO, this.tipo.name());

        return values;
    }
}
