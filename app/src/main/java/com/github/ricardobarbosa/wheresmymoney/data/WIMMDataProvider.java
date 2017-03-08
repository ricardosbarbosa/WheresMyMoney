package com.github.ricardobarbosa.wheresmymoney.data;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import static com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.*;

/**
 * Created by ricardobarbosa on 31/01/17.
 */
public class WIMMDataProvider extends ContentProvider {

    private static final String TAG = "WIMMDataProvider";
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private WIMMDbHelper mOpenHelper;


    static final int CONTA = 100;
    static final int CONTA_BY = 101;

    static final int CATEGORIA = 200;

    static final int MOVIMENTACAO = 300;
    static final int MOVIMENTACAO_COM_UMA_CONTA = 301;
    static final int MOVIMENTACAO_COM_UMA_CATEGORIA = 302;

    static final int TRANSFERENCIA = 400;
    static final int TRANSFERENCIA_COM_UMA_CONTA = 401;
    static final int TRANSFERENCIA_COM_UMA_CATEGORIA = 402;


    private static final SQLiteQueryBuilder sMovimentacoesByContaIdQueryBuilder;

    static{
        sMovimentacoesByContaIdQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sMovimentacoesByContaIdQueryBuilder.setTables(
                ContaEntry.TABLE_NAME + " INNER JOIN " +
                        MovimentacaoEntry.TABLE_NAME +
                        " ON "
                        + ContaEntry.TABLE_NAME + "." + ContaEntry._ID +
                        " = "
                        + MovimentacaoEntry.TABLE_NAME + "." + MovimentacaoEntry.COLUMN_CONTA_KEY);
    }

    //movie.favorite = ? 
//    private static final String sFavoriteSelection =
//            WIMMContract.MovimentacaoEntry.TABLE_NAME +
//                    "." + WIMMContract.MovimentacaoEntry.COLUMN_FAVORITE + " = ? ";

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_CONTA, CONTA);
        matcher.addURI(authority, PATH_CONTA+ "/#" , CONTA_BY);

        matcher.addURI(authority, PATH_CATEGORIA, CATEGORIA);
        matcher.addURI(authority, PATH_MOVIMENTACAO, MOVIMENTACAO);
        matcher.addURI(authority, PATH_TRANSFERENCIA, TRANSFERENCIA);

        // For each type of URI you want to add, create a corresponding code.
//        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
//        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);

        matcher.addURI(authority, PATH_MOVIMENTACAO + "/categoria/#" , MOVIMENTACAO_COM_UMA_CONTA);
        matcher.addURI(authority, PATH_MOVIMENTACAO + "/categoria/#" , MOVIMENTACAO_COM_UMA_CATEGORIA);
        matcher.addURI(authority, PATH_TRANSFERENCIA + "/categoria/#" , TRANSFERENCIA_COM_UMA_CONTA);
        matcher.addURI(authority, PATH_TRANSFERENCIA + "/categoria/#" , TRANSFERENCIA_COM_UMA_CATEGORIA);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WIMMDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case CONTA:
                return ContaEntry.CONTENT_TYPE;
            case CONTA_BY:
                return ContaEntry.CONTENT_ITEM_TYPE;

            case CATEGORIA:
                return CategoriaEntry.CONTENT_TYPE;

            case MOVIMENTACAO:
                return MovimentacaoEntry.CONTENT_TYPE;
            case MOVIMENTACAO_COM_UMA_CONTA:
                return MovimentacaoEntry.CONTENT_TYPE;
            case MOVIMENTACAO_COM_UMA_CATEGORIA:
                return MovimentacaoEntry.CONTENT_TYPE;

            case TRANSFERENCIA:
                return TransferenciaEntry.CONTENT_TYPE;
            case TRANSFERENCIA_COM_UMA_CONTA:
                return TransferenciaEntry.CONTENT_TYPE;
            case TRANSFERENCIA_COM_UMA_CATEGORIA:
                return TransferenciaEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d(TAG, "******query()");

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "conta"
            case CONTA:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(ContaEntry.TABLE_NAME,  projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "conta by"
            case CONTA_BY:
            {
                retCursor = getContaId(uri, projection, sortOrder);
                break;
            }
            // "categoria"
            case CATEGORIA:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(CategoriaEntry.TABLE_NAME,  projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "movimentacao"
            case MOVIMENTACAO:
            {
                retCursor = sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
//                retCursor = mOpenHelper.getReadableDatabase().query(MovimentacaoEntry.TABLE_NAME,  projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "transferencia"
            case TRANSFERENCIA:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(TransferenciaEntry.TABLE_NAME,  projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }
            // "movimentacao/categoria/#"
            case MOVIMENTACAO_COM_UMA_CONTA: {
                retCursor = getMovimentacoesByContaId(uri, projection, sortOrder);
                break;
            }
            // "movimentacao/categoria/#"
            case MOVIMENTACAO_COM_UMA_CATEGORIA: {
                retCursor = getMovimentacoesByCategoriaId(uri, projection, sortOrder);
                break;
            }
            // "trasnferencia/categoria/#"
            case TRANSFERENCIA_COM_UMA_CONTA: {
                retCursor = getTransferenciasByContaId(uri, projection, sortOrder);
                break;
            }
            // "trasnferencia/categoria/#"
            case TRANSFERENCIA_COM_UMA_CATEGORIA: {
                retCursor = getTransferenciasByCategoriaId(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    //location.location_setting = ?
    private static final String sContaIdSelection =
            MovimentacaoEntry.TABLE_NAME + "." + MovimentacaoEntry.COLUMN_CONTA_KEY + " = ? ";

    private Cursor getAllMovimentacoes(Uri uri, String[] projection, String sortOrder) {
        return sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, null, null, null, null, sortOrder);
    }
    private Cursor getContaId(Uri uri, String[] projection, String sortOrder) {
        Integer contaId = ContaEntry.getContaIdFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(contaId)};
        String selection = ContaEntry.TABLE_NAME + "." + ContaEntry._ID + " = ? ";

        return  mOpenHelper.getReadableDatabase().query(ContaEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }
    private Cursor getMovimentacoesByContaId(Uri uri, String[] projection, String sortOrder) {
        Integer contaId = MovimentacaoEntry.getContaIdFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(contaId)};
        String selection = sContaIdSelection;

        return sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getMovimentacoesByCategoriaId(Uri uri, String[] projection, String sortOrder) {
        Integer categoriaId = MovimentacaoEntry.getCategoriaIdFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(categoriaId)};
        String selection = sContaIdSelection;

        return sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getTransferenciasByContaId(Uri uri, String[] projection, String sortOrder) {
        Integer contaId = TransferenciaEntry.getContaIdFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(contaId)};
        String selection = sContaIdSelection;

        return sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    private Cursor getTransferenciasByCategoriaId(Uri uri, String[] projection, String sortOrder) {
        Integer categoriaId = TransferenciaEntry.getCategoriaIdFromUri(uri);

        String[] selectionArgs = new String[]{Integer.toString(categoriaId)};
        String selection = sContaIdSelection;

        return sMovimentacoesByContaIdQueryBuilder.query(mOpenHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
    }



    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CONTA: {
                long _id = db.insert(ContaEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ContaEntry.buildContaUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORIA: {
                long _id = db.insert(CategoriaEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CategoriaEntry.buildCategoriaUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIMENTACAO: {
                long _id = db.insert(MovimentacaoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovimentacaoEntry.buildMovimentacaoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRANSFERENCIA: {
                long _id = db.insert(TransferenciaEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TransferenciaEntry.buildTransferenciaUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CONTA:
                rowsDeleted = db.delete(
                        ContaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORIA:
                rowsDeleted = db.delete(
                        CategoriaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIMENTACAO:
                rowsDeleted = db.delete(
                        MovimentacaoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRANSFERENCIA:
                rowsDeleted = db.delete(
                        TransferenciaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CONTA:
                rowsUpdated = db.update(ContaEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CATEGORIA:
                rowsUpdated = db.update(CategoriaEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIMENTACAO:
                rowsUpdated = db.update(MovimentacaoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRANSFERENCIA:
                rowsUpdated = db.update(TransferenciaEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
