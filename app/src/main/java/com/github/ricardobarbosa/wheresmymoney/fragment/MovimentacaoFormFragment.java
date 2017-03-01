package com.github.ricardobarbosa.wheresmymoney.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormFragment;
import com.github.dkharrat.nexusdialog.controllers.DatePickerController;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;
import com.github.dkharrat.nexusdialog.utils.MessageUtil;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.CategoriaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.ContaEntry;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract.MovimentacaoEntry;
import com.github.ricardobarbosa.wheresmymoney.model.EnumMovimentacaoTipo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovimentacaoFormFragment extends FormFragment {
    public static final String CONTA = "conta";
    public static final String CATEGORIA = "categoria";
    public static final String DESCRICAO = "descricao";
    public static final String TIPO = "tipo";
    public static final String VALOR = "valor";
    public static final String DATA = "data";

    public boolean validate() {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {
            Object conta = getModel().getValue(CONTA);
            Object categoria = getModel().getValue(CATEGORIA);
            Object descricao = getModel().getValue(DESCRICAO);
            Object tipo = getModel().getValue(TIPO);
            Object valor = getModel().getValue(VALOR);
            Object data = getModel().getValue(DATA);


            insertMovimentacao();
        } else {
            getFormController().showValidationErrors();
            return false;
        }
        return true;
    }

    private long insertMovimentacao() {
        long contaID;
        ContentValues values = new ContentValues();

        Bundle bundle = getArguments();
        String tipoStrg = getActivity().getIntent().getExtras().getString("tipo");
        if (bundle != null) {
            tipoStrg = bundle.getString("tipo", "TRANSFERENCIA");
        }

        values.put(MovimentacaoEntry.COLUMN_TIPO, tipoStrg);

        values.put(MovimentacaoEntry.COLUMN_DESCRICAO, (String) getModel().getValue(DESCRICAO));
        values.put(MovimentacaoEntry.COLUMN_VALOR, Double.valueOf(getModel().getValue(VALOR).toString()));

        Date date = (Date) getModel().getValue(DATA);
        values.put(MovimentacaoEntry.COLUMN_DATA, date.getTime());

        values.put(MovimentacaoEntry.COLUMN_CATEGORIA_KEY, (Integer) getModel().getValue(CATEGORIA));
        values.put(MovimentacaoEntry.COLUMN_CONTA_KEY, (Integer)  getModel().getValue(CONTA));

        Uri insertedUri = getContext().getContentResolver().insert(MovimentacaoEntry.CONTENT_URI, values);

        contaID = ContentUris.parseId(insertedUri);

        return contaID;
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