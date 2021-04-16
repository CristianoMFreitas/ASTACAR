package com.taxicaraubas.astacar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.adapter.Adapter;
import com.taxicaraubas.astacar.aplicacoes.RecyclerItemClickListener;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.Requisicao;
import com.taxicaraubas.astacar.model.UsuarioMotorista;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listaMotorista extends AppCompatActivity {

    private RecyclerView recyclerViewMotorista;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private TextView nenhumMotorista;
    private List<UsuarioMotorista> listaMotoristaArray = new ArrayList<>();
    private Requisicao requisicaoAdapter = new Requisicao();
    private Bundle extras;
    private Adapter adapter;
    private String destino;
    private Button cancelarViajem, telaInicial;
    private Boolean escolherTela = false;
    private int vagas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_motorista);
        setTitle("Escolher motorista");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        nenhumMotorista = findViewById(R.id.textViewAvitoNenhumMotorista);
        cancelarViajem = findViewById(R.id.botaoListarMotoristaCancelarViajem);
        telaInicial = findViewById(R.id.botaoListaMotoristaTelaInicial);

        extras = getIntent().getExtras();


            requisicaoAdapter = (Requisicao) extras.getSerializable("requisicaoEnvia");
            destino = extras.getString("destinoOp");
            escolherTela = extras.getBoolean("tela");

            if (escolherTela){
                listaParaEncomenda();
                cancelarViajem.setVisibility(View.GONE);
                telaInicial.setVisibility(View.VISIBLE);

            } else {
                recuperarListaMotorista();
                telaInicial.setVisibility(View.GONE);
            }


         /*Dando uma referência para a variável criada,
        nessse caso esta referenciando o componente
        com id recyclerView, que esta no layout da tela.*/

        recyclerViewMotorista = findViewById(R.id.recyclerViewMotoristaLista);

        //Configurar Adapter.
        adapter = new Adapter(listaMotoristaArray, getBaseContext(), requisicaoAdapter);

        //Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMotorista.setLayoutManager(layoutManager); //entregando o objeto criado a cima chamado layoutManager ao recyclerView.
        recyclerViewMotorista.setHasFixedSize(true); //otmizando o layout do recyclerview para ficar sempre do mesmo tamanho.
        recyclerViewMotorista.setAdapter( adapter ); //esse adaptardor é o mesmo criado logo a cima em 'CONFIGURAR ADAPETER'

        //adicionar evento de click ao recycleview
        recyclerViewMotorista.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewMotorista,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                UsuarioMotorista motoristaClick = new UsuarioMotorista();
                                motoristaClick = listaMotoristaArray.get(position);

                                Intent intent = new Intent(getApplicationContext(), motoristaEscolher.class);
                                intent.putExtra("motoristaEscolhido", motoristaClick);
                                intent.putExtra("requisicao", requisicaoAdapter);
                                intent.putExtra("tela", escolherTela);
                                intent.putExtra("destinoOp", destino);
                                startActivity(intent);
                                finish();

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ATENÇÃO")
                .setMessage("Você tem certeza que deseja desistir da viajem?")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getApplicationContext(), telaInicial.class);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        //this.moveTaskToBack(true);
    }

    private void recuperarListaMotorista(){
        DatabaseReference motoristas = firebaseRef.child("usuarioMotorista");

        final Query listaMotorista = motoristas.orderByChild("status")
                .equalTo(UsuarioMotorista.STATUS_DISPONIVEL);


        listaMotorista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaMotoristaArray.clear();

                if (dataSnapshot.getChildrenCount() > 0){
                    nenhumMotorista.setVisibility(View.GONE);
                    recyclerViewMotorista.setVisibility(View.VISIBLE);

                }else{
                    nenhumMotorista.setVisibility(View.VISIBLE);
                    recyclerViewMotorista.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    UsuarioMotorista motoristaLista = ds.getValue(UsuarioMotorista.class);
                    if (destino.equals(motoristaLista.getDestino())) {

                        listaMotoristaArray.add(motoristaLista);
                    }

                }

                adapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listaParaEncomenda(){
        DatabaseReference motoristas = firebaseRef.child("usuarioMotorista");

        final Query listaMotorista = motoristas.orderByChild("statusEncomenda")
                .equalTo(UsuarioMotorista.STATUS_DISPONIVEL);

        listaMotorista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaMotoristaArray.clear();

                if (dataSnapshot.getChildrenCount() > 0){
                    nenhumMotorista.setVisibility(View.GONE);
                    recyclerViewMotorista.setVisibility(View.VISIBLE);

                }else{
                    nenhumMotorista.setVisibility(View.VISIBLE);
                    recyclerViewMotorista.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    UsuarioMotorista motoristaLista = ds.getValue(UsuarioMotorista.class);
                    if (destino.equals(motoristaLista.getDestino())) {
                        listaMotoristaArray.add(motoristaLista);
                    }

                }

                adapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void botaoCancelarViajem (View view){

        requisicaoAdapter.setStatus(Requisicao.STATUS_CANCELADA);
        requisicaoAdapter.atualizar(requisicaoAdapter.getIdRequisicao());

        Intent intent = new Intent(getApplicationContext(), telaInicial.class);
        startActivity(intent);
        finish();
    }

    public void botaoTelaInicial (View view){
        Intent intent = new Intent(getApplicationContext(), telaInicial.class);
        startActivity(intent);
        finish();
    }



}
