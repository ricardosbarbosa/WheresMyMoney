package com.github.ricardobarbosa.wheresmymoney.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;

import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.CategoriaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.*;

/**
 * Created by ricardobarbosa on 31/01/17.
 */
public class WIMMDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 10;

    public static final String DATABASE_NAME = "whereismymoney.db";

    public WIMMDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_CATEGORIA_TABLE = "CREATE TABLE " + CategoriaEntry.TABLE_NAME + " (" +
                CategoriaEntry._ID + " INTEGER PRIMARY KEY," +
                CategoriaEntry.COLUMN_NOME + " TEXT UNIQUE NOT NULL " +
                " );";

        final String SQL_CREATE_CONTA_TABLE = "CREATE TABLE " + ContaEntry.TABLE_NAME + " (" +
                ContaEntry._ID + " INTEGER PRIMARY KEY," +
                ContaEntry.COLUMN_NOME + " TEXT UNIQUE NOT NULL , " +
                ContaEntry.COLUMN_TIPO + " TEXT NOT NULL , " +
                ContaEntry.COLUMN_SALDO + " REAL NOT NULL  " +
                " );";

        final String SQL_CREATE_MOVIMENTACAO_TABLE = "CREATE TABLE " + MovimentacaoEntry.TABLE_NAME + " (" +
                MovimentacaoEntry._ID + " INTEGER PRIMARY KEY," +
                //chaves estrangeiras
                MovimentacaoEntry.COLUMN_CATEGORIA_KEY + " INTEGER NOT NULL , " +
                MovimentacaoEntry.COLUMN_CONTA_KEY + " INTEGER NOT NULL , " +
                //demias colunas
                MovimentacaoEntry.COLUMN_DATA + " INTEGER NOT NULL , " +
                MovimentacaoEntry.COLUMN_DESCRICAO + " TEXT NOT NULL , " +
                MovimentacaoEntry.COLUMN_TIPO + " TEXT NOT NULL , " +
                MovimentacaoEntry.COLUMN_VALOR + " REAL NOT NULL  " +
                " );";

        final String SQL_CREATE_TRANSFERENCIA_TABLE = "CREATE TABLE " + TransferenciaEntry.TABLE_NAME + " (" +
                TransferenciaEntry._ID + " INTEGER PRIMARY KEY," +
                //chaves estrangeiras
                TransferenciaEntry.COLUMN_MOVIMENTACAO_KEY + " INTEGER NOT NULL , " +
                TransferenciaEntry.COLUMN_CONTA_DESTINO_KEY + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_CATEGORIA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CONTA_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIMENTACAO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRANSFERENCIA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoriaEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ContaEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovimentacaoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransferenciaEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    // To make it easy to query for the exact date, we normalize all dates that go into
    // the database to the start of the the Julian day at UTC.
    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }
}
