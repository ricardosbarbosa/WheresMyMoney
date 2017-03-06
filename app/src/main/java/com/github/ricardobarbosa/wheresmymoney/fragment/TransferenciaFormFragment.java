package com.github.ricardobarbosa.wheresmymoney.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormFragment;
import com.github.dkharrat.nexusdialog.controllers.DatePickerController;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;
import com.github.ricardobarbosa.wheresmymoney.data.MovieDbHelper;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.CategoriaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.ContaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.MovimentacaoEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.TransferenciaEntry;
import com.github.ricardobarbosa.wheresmymoney.model.Conta;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TransferenciaFormFragment extends FormFragment {

    public static final String TAG = "TransferenciaFormFragment";

    public static final String CONTA = "conta";
    public static final String CONTA_DESTINO = "conta_destino";
    public static final String CATEGORIA = "categoria";
    public static final String DESCRICAO = "descricao";
    public static final String TIPO = "tipo";
    public static final String VALOR = "valor";
    public static final String DATA = "data";

    public boolean validate() {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {
            insertMovimentacaoTransaction();
        } else {
            getFormController().showValidationErrors();
            return false;
        }
        return true;
    }

    private Long insertMovimentacao(SQLiteDatabase db) {
        Long movimentacaoID;

        ContentValues values = new ContentValues();
        values.put(MovimentacaoEntry.COLUMN_TIPO, EnumMovimentacaoTipo.TRANSFERENCIA.name());

        values.put(MovimentacaoEntry.COLUMN_DESCRICAO, (String) getModel().getValue(DESCRICAO));
        values.put(MovimentacaoEntry.COLUMN_VALOR, Double.valueOf(getModel().getValue(VALOR).toString()));

        Date date = (Date) getModel().getValue(DATA);
        values.put(MovimentacaoEntry.COLUMN_DATA, date.getTime());

        values.put(MovimentacaoEntry.COLUMN_CATEGORIA_KEY, (Integer) getModel().getValue(CATEGORIA));
        values.put(MovimentacaoEntry.COLUMN_CONTA_KEY, (Integer)  getModel().getValue(CONTA));

//        Uri insertedUri = getContext().getContentResolver().insert(MovimentacaoEntry.CONTENT_URI, values);
//        contaID = ContentUris.parseId(insertedUri);

        movimentacaoID = db.insert(MovimentacaoEntry.TABLE_NAME, null,  values);

        return movimentacaoID;
    }

    private Integer updateContas(SQLiteDatabase db) {
        Integer rowsOrigem = updateContaDeOrigem(db);
        Integer rowsDestino = updateContaDeDestino(db);
        return rowsOrigem + rowsDestino;
    }

    private Integer updateContaDeOrigem(SQLiteDatabase db) {
        //Remove valor a conta de destino
        Integer contaId = (Integer) getModel().getValue(CONTA);
        Double valor = Double.valueOf(getModel().getValue(VALOR).toString());
        return updateConta(db, contaId, -valor);
    }

    private Integer updateContaDeDestino(SQLiteDatabase db) {
        //Acrescenta valor a conta de destino
        Integer contaId = (Integer) getModel().getValue(CONTA_DESTINO);
        Double valor = Double.valueOf(getModel().getValue(VALOR).toString());
        return updateConta(db, contaId, +valor);
    }

    private Integer updateConta(SQLiteDatabase db, Integer contaId, Double valor) {
        String where = ContaEntry._ID + " = ? ";
        String[] selectionArgs = new String[]{contaId.toString()};

        Cursor cursor = db.query(ContaEntry.TABLE_NAME, null, where, selectionArgs, null, null, null);

        cursor.moveToFirst();
        Conta conta = new Conta(cursor);

        Double saldo = conta.addValor(valor);

        ContentValues values = new ContentValues();
        values.put(ContaEntry.COLUMN_SALDO, saldo);

        Integer rows = db.update(ContaEntry.TABLE_NAME, values, where, selectionArgs);

        return rows;
    }

    private Long insertTransferencia(SQLiteDatabase db, Long movimentacaoID) {
        Long transferenciaId;

        ContentValues values = new ContentValues();
        values.put(TransferenciaEntry.COLUMN_MOVIMENTACAO_KEY, movimentacaoID);
        values.put(TransferenciaEntry.COLUMN_CONTA_DESTINO_KEY, (Integer) getModel().getValue(CONTA_DESTINO));

        transferenciaId = db.insert(MovimentacaoEntry.TABLE_NAME, null,  values);

        return transferenciaId;
    }

    @SuppressLint("LongLogTag")
    private void insertMovimentacaoTransaction() {
        MovieDbHelper mOpenHelper = new MovieDbHelper(getContext());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();;
        db.beginTransaction();
        try {

            updateContas(db);
            Long movimentacaoID = insertMovimentacao(db);
            insertTransferencia(db, movimentacaoID);

            db.setTransactionSuccessful();
        } catch (Exception e){
            //Error in between database transaction
            Log.d(TAG, "Nao foi possível salvar ", e.fillInStackTrace());
        } finally {
            db.endTransaction();
        }
    }


    @Override
    public void initForm(FormController controller) {
        Context ctxt = getContext();

        FormSectionController section = new FormSectionController(ctxt, "");

        section.addElement(new EditTextController(ctxt, VALOR, "Valor", "1000.00", true, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));

        final String sortContaOrder = ContaEntry.COLUMN_NOME + " ASC " ;
        Cursor cursor = getContext().getContentResolver().query(ContaEntry.CONTENT_URI, null, null, null, sortContaOrder);

        List<String> contas = new ArrayList<>();
        List<Integer> contasIds = new ArrayList<>();
        while (!cursor.isLast()){
            cursor.moveToNext();
            contas.add(cursor.getString(cursor.getColumnIndex(ContaEntry.COLUMN_NOME)));
            contasIds.add(cursor.getInt(cursor.getColumnIndex(ContaEntry._ID)));
        }
        section.addElement(new SelectionController(ctxt, CONTA, "Conta Origem", true, "Select", contas, contasIds ));
        section.addElement(new SelectionController(ctxt, CONTA_DESTINO, "Conta Destino", true, "Select", contas, contasIds ));

        final String sortCategoriaOrder = CategoriaEntry.COLUMN_NOME + " ASC " ;
        Cursor cursorCategoria = getContext().getContentResolver().query(CategoriaEntry.CONTENT_URI, null, null, null, sortCategoriaOrder);

        List<String> categoriasNomes = new ArrayList<>();
        List<Integer> categoriasIds = new ArrayList<>();
        while (!cursorCategoria.isLast()){
            cursorCategoria.moveToNext();
            categoriasNomes.add(cursorCategoria.getString(cursorCategoria.getColumnIndex(CategoriaEntry.COLUMN_NOME)));
            categoriasIds.add(cursorCategoria.getInt(cursorCategoria.getColumnIndex(CategoriaEntry._ID)));
        }
        section.addElement(new SelectionController(ctxt, CATEGORIA, "Categoria", true, "Select", categoriasNomes, categoriasIds ));


//        section.addElement(new SelectionController(ctxt, TIPO, "Tipo", true, "Select", Arrays.asList(EnumContaTipo.names()), true));

        section.addElement(new EditTextController(ctxt, DESCRICAO, "Descrição", "Restaurante", true, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        section.addElement(new DatePickerController(ctxt, DATA, "Data", true, simpleDateFormat));

        controller.addSection(section);
    }
}