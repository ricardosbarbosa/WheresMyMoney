package com.github.ricardobarbosa.wheresmymoney.fragment;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormFragment;
import com.github.dkharrat.nexusdialog.controllers.DatePickerController;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;
import com.github.ricardobarbosa.wheresmymoney.R;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMDbHelper;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.CategoriaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.ContaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.MovimentacaoEntry;
import com.github.ricardobarbosa.wheresmymoney.model.Categoria;
import com.github.ricardobarbosa.wheresmymoney.model.Conta;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimentacaoFormFragment extends FormFragment {

    public static final String TAG = "MovimentacaoFormFragment";

    public static final String CONTA = "conta";
    public static final String CATEGORIA = "categoria";
    public static final String DESCRICAO = "descricao";
    public static final String TIPO = "tipo";
    public static final String VALOR = "valor";
    public static final String DATA = "data";

    public boolean validate() {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {

            Bundle bundle = getArguments();
            String tipoStrg = getActivity().getIntent().getExtras().getString("tipo");

            if (bundle != null) {
                tipoStrg = bundle.getString("tipo", "TRANSFERENCIA");
            }

            insertMovimentacaoTransaction(EnumMovimentacaoTipo.valueOf(tipoStrg));
        } else {
            getFormController().showValidationErrors();
            return false;
        }
        return true;
    }

    private long insertMovimentacao(EnumMovimentacaoTipo enumMovimentacaoTipo, SQLiteDatabase db) {
        long contaID;

        ContentValues values = new ContentValues();
        values.put(MovimentacaoEntry.COLUMN_TIPO, enumMovimentacaoTipo.name());

        values.put(MovimentacaoEntry.COLUMN_DESCRICAO, (String) getModel().getValue(DESCRICAO));
        values.put(MovimentacaoEntry.COLUMN_VALOR, Double.valueOf(getModel().getValue(VALOR).toString()));

        Date date = (Date) getModel().getValue(DATA);
        values.put(MovimentacaoEntry.COLUMN_DATA, date.getTime());

        values.put(MovimentacaoEntry.COLUMN_CATEGORIA_KEY, (Integer) getModel().getValue(CATEGORIA));
        values.put(MovimentacaoEntry.COLUMN_CONTA_KEY, (Integer)  getModel().getValue(CONTA));

//        Uri insertedUri = getContext().getContentResolver().insert(MovimentacaoEntry.CONTENT_URI, values);
//        contaID = ContentUris.parseId(insertedUri);

        contaID = db.insert(MovimentacaoEntry.TABLE_NAME, null,  values);



        return contaID;
    }

    private Integer updateConta(EnumMovimentacaoTipo enumMovimentacaoTipo, SQLiteDatabase db) {
        Integer contaId = (Integer) getModel().getValue(CONTA);

        String where = ContaEntry._ID + " = ?";
        String[] selectionArgs = new String[]{contaId.toString()};

        Cursor cursor = db.query(ContaEntry.TABLE_NAME, null, where, selectionArgs, null, null, null);

        cursor.moveToFirst();
        Conta conta = new Conta(cursor);

        Double valor = Double.valueOf(getModel().getValue(VALOR).toString());
        Double saldo;
        if (enumMovimentacaoTipo.equals(EnumMovimentacaoTipo.DESPESA) ){
            saldo = conta.addValor(-valor);
        } else {
            saldo = conta.addValor(valor);
        }

        ContentValues values = new ContentValues();
        values.put(ContaEntry.COLUMN_SALDO, saldo);

        Integer rows = db.update(ContaEntry.TABLE_NAME, values, where, selectionArgs);

        return rows;
    }

    @SuppressLint("LongLogTag")
    private void insertMovimentacaoTransaction(EnumMovimentacaoTipo enumMovimentacaoTipo) {
        WIMMDbHelper mOpenHelper = new WIMMDbHelper(getContext());
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();;
        db.beginTransaction();
        try {

            updateConta(enumMovimentacaoTipo, db);
            insertMovimentacao(enumMovimentacaoTipo, db);

            db.setTransactionSuccessful();
        } catch (Exception e){
            //Error in between database transaction
            Log.d(TAG, "Nao foi possível salvar ", e.fillInStackTrace());
        } finally {
            db.endTransaction();
        }
    }

    private void configureCategorias() {
        Cursor cursor = getContext().getContentResolver().query(CategoriaEntry.CONTENT_URI, null, null, null, null);

        if (!cursor.moveToFirst()) {
            Resources resources = getResources();
            String[] receitas = resources.getStringArray(R.array.receitas);
            String[] despesas = resources.getStringArray(R.array.despesas);
            String[] lancamentos = resources.getStringArray(R.array.lancamentos);

            for (String[] array: new String[][]{receitas, despesas, lancamentos}) {
                for (String nome : array) {
                    ContentValues values = new ContentValues();
                    values.put(WIMMContract.CategoriaEntry.COLUMN_NOME, nome);
                    Uri insertedUri = getContext().getContentResolver().insert(WIMMContract.CategoriaEntry.CONTENT_URI, values);
                }
            }
        }

    }
    @Override
    public void initForm(FormController controller) {

        configureCategorias();

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
        section.addElement(new SelectionController(ctxt, CONTA, "Conta", true, "Select", contas, contasIds ));


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