package com.github.ricardobarbosa.wheresmymoney.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.CategoriaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.MovimentacaoEntry;

import java.util.Date;

/**
 * Created by ricardobarbosa on 19/02/17.
 */

public class Movimentacao implements Parcelable{

    public static final String PARCELABLE_KEY = "movimentacao";

    private Integer id;

    private Conta conta;
    private Categoria categoria;

    private Double valor;
    private Date data;
    private String descricao;
    private EnumMovimentacaoTipo tipo;


    public Movimentacao() {}

    public Movimentacao(Conta conta, Double valor, Date data, String descricao, EnumMovimentacaoTipo tipo, Categoria categoria) {
        this.conta = conta;
        this.valor = valor;
        this.data = data;
        this.descricao = descricao;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    public Movimentacao(Cursor cursor){
        this.id = cursor.getInt(cursor.getColumnIndex(WIMMContract.ContaEntry._ID));
        this.descricao = cursor.getString(cursor.getColumnIndex(MovimentacaoEntry.COLUMN_DESCRICAO));
        this.tipo = EnumMovimentacaoTipo.valueOf(cursor.getString(cursor.getColumnIndex(MovimentacaoEntry.COLUMN_TIPO)));
        this.valor = cursor.getDouble(cursor.getColumnIndex(MovimentacaoEntry.COLUMN_VALOR));
        long longDate = cursor.getLong(cursor.getColumnIndex(MovimentacaoEntry.COLUMN_DATA));
        this.data = new Date(longDate);
        this.categoria = new Categoria(cursor);
        this.conta = new Conta(cursor);
    }
    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EnumMovimentacaoTipo getTipo() {
        return tipo;
    }

    public void setTipo(EnumMovimentacaoTipo tipo) {
        this.tipo = tipo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    protected Movimentacao(Parcel in) {
        conta = (Conta) in.readValue(Conta.class.getClassLoader());
        valor = in.readByte() == 0x00 ? null : in.readDouble();
        long tmpData = in.readLong();
        data = tmpData != -1 ? new Date(tmpData) : null;
        descricao = in.readString();
        tipo = (EnumMovimentacaoTipo) in.readValue(EnumMovimentacaoTipo.class.getClassLoader());
        categoria = (Categoria) in.readValue(Categoria.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(conta);
        if (valor == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(valor);
        }
        dest.writeLong(data != null ? data.getTime() : -1L);
        dest.writeString(descricao);
        dest.writeValue(tipo);
        dest.writeValue(categoria);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movimentacao> CREATOR = new Parcelable.Creator<Movimentacao>() {
        @Override
        public Movimentacao createFromParcel(Parcel in) {
            return new Movimentacao(in);
        }

        @Override
        public Movimentacao[] newArray(int size) {
            return new Movimentacao[size];
        }
    };
}
