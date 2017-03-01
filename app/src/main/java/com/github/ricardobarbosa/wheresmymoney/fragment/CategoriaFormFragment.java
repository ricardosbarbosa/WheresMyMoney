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

public class CategoriaFormFragment extends FormFragment {
    public static final String NOME = "nome";

    public boolean validate() {
        getFormController().resetValidationErrors();
        if (getFormController().isValidInput()) {
            Object firstName = getModel().getValue(NOME);

            String msg = "First name: " + firstName + "\n";
            MessageUtil.showAlertMessage("Field Values", msg, getActivity());

            insertCategoria();
        } else {
            getFormController().showValidationErrors();
            return false;
        }
        return true;
    }

    private long insertCategoria() {
        long contaID;
        ContentValues values = new ContentValues();

        values.put(WIMMContract.CategoriaEntry.COLUMN_NOME, (String) getModel().getValue(NOME));

        Uri insertedUri = getContext().getContentResolver().insert(WIMMContract.CategoriaEntry.CONTENT_URI, values);

        contaID = ContentUris.parseId(insertedUri);

        return contaID;
    }

    @Override
    public void initForm(FormController controller) {
        Context ctxt = getContext();

        FormSectionController section = new FormSectionController(ctxt, "");

        section.addElement(new EditTextController(ctxt, NOME, "Name", "Conta Corrente", true, InputType.TYPE_CLASS_TEXT));

        controller.addSection(section);
    }
}