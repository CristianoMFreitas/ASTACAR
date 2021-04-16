package com.taxicaraubas.astacar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class politicaDePrivacidade extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_de_privacidade);
        setTitle("Política de Privacidade");
    }

    public void voltar (View view){
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
    }
}
