package com.taxicaraubas.chamataxi.aplicacoes;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {
    public static boolean validarPermissoes(String[] permissoes, Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> listaPermissoes = new ArrayList<>();
            for (String permissao : permissoes) {
                boolean temPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if (!temPermissao) listaPermissoes.add(permissao);
            }

            if (!listaPermissoes.isEmpty()) {
                String[] novasPermissoes = new String[listaPermissoes.size()];
                listaPermissoes.toArray(novasPermissoes);
                ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
                return false; // Ainda não tem todas permissões, pediu para usuário
            }
        }
        return true; // Tem todas permissões
    }

}
