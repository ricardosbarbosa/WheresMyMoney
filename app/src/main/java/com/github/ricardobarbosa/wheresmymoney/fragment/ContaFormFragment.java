package com.github.ricardobarbosa.wheresmymoney.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.InputType;

import com.github.dkharrat.nexusdialog.FormController;
import com.github.dkharrat.nexusdialog.FormFragment;
import com.github.dkharrat.nexusdialog.controllers.EditTextController;
import com.github.dkharrat.nexusdialog.controllers.FormSectionController;
import com.github.dkharrat.nexusdialog.controllers.SelectionController;
import com.github.dkharrat.nexusdialog.utils.MessageUtil;
import com.github.ricardobarbosa.wheresmymoney.data.WIMMContract;
import com.github.ricardobarbosa.wheresmymoney.model.EnumContaTipo;

import java.util.Arrays;

public class ContaFormFragment extends FormFragment {
    public static final String NOME = "nome";
    public static final String TIPO = "tipo";
    public static final String SALDO = "saldo";

    public boolean validate() {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {
            Object firstName = getModel().getValue(NOME);
            Object lastName = getModel().getValue(TIPO);
            Object gender = getModel().getValue(SALDO);

            String msg = "First name: " + firstName + "\n"
                    + "Last name: " + lastName + "\n"
                    + "Gender: " + gender + "\n";
            MessageUtil.showAlertMessage("Field Values", msg, getActivity());

            insertConta();
        } else {
            getFormController().showValidationErrors();
            return false;
        }
        return true;
    }

    private long insertConta() {
        long contaID;
        ContentValues values = new ContentValues();

        values.put(WIMMContract.ContaEntry.COLUMN_NOME, (String) getModel().getValue(NOME));
        values.put(WIMMContract.ContaEntry.COLUMN_TIPO, (String)  getModel().getValue(TIPO));
        values.put(WIMMContract.ContaEntry.COLUMN_SALDO, Double.valueOf(getModel().getValue(SALDO).toString()));

        Uri insertedUri = getContext().getContentResolver().insert(WIMMContract.ContaEntry.CONTENT_URI, values);

        contaID = ContentUris.parseId(insertedUri);

        return contaID;
    }

    @Override
    public void initForm(FormController controller) {
        Context ctxt = getContext();

        FormSectionController section = new FormSectionController(ctxt, "");

        section.addElement(new EditTextController(ctxt, NOME, "Name", "Conta Corrente", true, InputType.TYPE_CLASS_TEXT));


        section.addElement(new SelectionController(ctxt, TIPO, "Tipo", true, "Select", Arrays.asList(EnumContaTipo.names()), true));
        section.addElement(new EditTextController(ctxt, SALDO, "Saldo", "1000.00", true, InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL));



        controller.addSection(section);
    }
}