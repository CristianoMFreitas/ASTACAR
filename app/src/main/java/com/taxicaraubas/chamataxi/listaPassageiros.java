package com.taxicaraubas.chamataxi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.taxicaraubas.chamataxi.R;

public class listaPassageiros extends AppCompatActivity {

    /*instanciando uma variável do tipo RecyclerView que se chama
    igualmente RecycleView */

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_passageiros);
        setTitle("Selecionar passageiro");

        /*Dando uma referência para a variável criada,
        nessse caso esta referenciando o componente
        com id recyclerView, que esta no layout da tela.*/

        recyclerView = findViewById(R.id.recyclerView);

        //Configurar Adapter.
       /*
        Adapter adapter = new Adapter();


        //Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager); //entregando o objeto criado a cima chamado layoutManager ao recyclerView.
        recyclerView.setHasFixedSize(true); //otmizando o layout do recyclerview para ficar sempre do mesmo tamanho.
        recyclerView.setAdapter( adapter ); //esse adaptardor é o mesmo criado logo a cima em 'CONFIGURAR ADAPETER'

        */

    }
    public void botaoEscolherPassageiro (View view){
        Intent intent = new Intent(getApplicationContext(), passageiro.class);
        startActivity(intent);
    }
}
