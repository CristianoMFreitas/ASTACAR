package com.taxicaraubas.astacar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.taxicaraubas.astacar.R;

public class administrador extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        getSupportActionBar().hide();


    }

    public void botaoMotorista (View view){
        motorista.administrador = true;
        Intent intent = new Intent(getApplicationContext(), motorista.class);
        startActivity(intent);
        finish();
    }

    public void botaoAdmin (View view){
        Intent intent = new Intent(getApplicationContext(), gerenciarMotorista.class);
        startActivity(intent);
        finish();
    }

}
