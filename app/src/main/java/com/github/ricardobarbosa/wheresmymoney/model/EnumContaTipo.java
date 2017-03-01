package com.github.ricardobarbosa.wheresmymoney.model;

import java.util.Arrays;

/**
 * Created by ricardobarbosa on 19/02/17.
 */
public enum EnumContaTipo {
    DINHEIRO,CONTA_CORRENTE, POUPANCA, CARTAO_DE_CREDITO;

    EnumContaTipo() {
    }

    public static String[] names() {
        return Arrays.toString(EnumContaTipo.values()).replaceAll("^.|.$", "").split(", ");
    }
}
