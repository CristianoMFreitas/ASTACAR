package com.taxicaraubas.astacar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.taxicaraubas.astacar.R;

public class passageiro extends AppCompatActivity {

    TextView telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);
        setTitle("Passageiro");

        telefone = findViewById(R.id.textTelefone);
    }

    public void ligarTelefone(View view){
        String numero = telefone.getText().toString();
        Uri uri = Uri.parse("tel:" + numero);

        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(passageiro.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }
        }
        startActivity(intent);
    }

    public void botaoRecusar (View view){
        Intent intent = new Intent(getApplicationContext(), listaPassageiros.class);
        startActivity(intent);
    }

    public void botaoAceitar (View view){
        Intent intent = new Intent(getApplicationContext(), lotacaoCarro.class);
        startActivity(intent);
    }
}
