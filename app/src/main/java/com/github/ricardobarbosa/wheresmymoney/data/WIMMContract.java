package com.github.ricardobarbosa.wheresmymoney.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ricardobarbosa on 31/01/17.
 */
public class WIMMContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    static final String CONTENT_AUTHORITY = "com.github.ricardosbarbosa.whereismymoney.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    static final String PATH_CONTA = "conta";
    static final String PATH_MOVIMENTACAO = "movimentacao";
    static final String PATH_CATEGORIA = "categoria";
    static final String PATH_TRANSFERENCIA = "trasnferencia";

    /* Inner class that defines the table contents of the location table */
    public static final class CategoriaEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORIA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORIA;

        public static final String TABLE_NAME = "categoria";

        public static final String COLUMN_NOME = "nome";

        public static Uri buildCategoriaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the location table */
    public static final class ContaEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTA).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CONTA;

        public static final String TABLE_NAME = "conta";

        public static final String COLUMN_NOME = "nome";
        public static final String COLUMN_TIPO = "conta_tipo";
        public static final String COLUMN_SALDO = "saldo";

        public static Uri buildContaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Integer getContaIdFromUri(Uri uri) {
            return Integer.parseInt((uri.getPathSegments().get(1)));
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class MovimentacaoEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIMENTACAO).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIMENTACAO;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIMENTACAO;

        public static final String TABLE_NAME = "movimentacao";

        // Column with the foreign key into the categoria table.
        public static final String COLUMN_CONTA_KEY = "conta_id";
        // Column with the foreign key into the categoria table.
        public static final String COLUMN_CATEGORIA_KEY = "categoria_id";

        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_VALOR = "valor";
        public static final String COLUMN_DESCRICAO = "descricao";
        public static final String COLUMN_TIPO = "mov_tipo";

        public static Uri buildMovimentacaoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Integer getContaIdFromUri(Uri uri) {
            return Integer.parseInt((uri.getPathSegments().get(2)));
        }

        public static Integer getCategoriaIdFromUri(Uri uri) {
            return Integer.parseInt((uri.getPathSegments().get(2)));
        }

    }

    public static final class TransferenciaEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSFERENCIA).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSFERENCIA;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSFERENCIA;

        public static final String TABLE_NAME = "transferencia";

        // Column with the foreign key into the categoria table.
        //representa os dados normais de uma movimentacao
        public static final String COLUMN_MOVIMENTACAO_KEY = "movimentacao_id";
        //transferencia eh uma movimentacao com categoria destino
        public static final String COLUMN_CONTA_DESTINO_KEY = "conta_destino_id";

        public static Uri buildTransferenciaUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Integer getContaIdFromUri(Uri uri) {
            return Integer.parseInt((uri.getPathSegments().get(2)));
        }

        public static Integer getCategoriaIdFromUri(Uri uri) {
            return Integer.parseInt((uri.getPathSegments().get(2)));
        }
    }
}
