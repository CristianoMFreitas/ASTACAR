package com.taxicaraubas.astacar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.adapter.Adapter_gerenciar_motorista;
import com.taxicaraubas.astacar.aplicacoes.RecyclerItemClickListener;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.UsuarioMotorista;


import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class gerenciarMotorista extends AppCompatActivity {

    private RecyclerView recyclerViewAdmin;
    private Adapter_gerenciar_motorista adapter;
    private List<UsuarioMotorista> listaMotoristaArray = new ArrayList<>();
    private TextView gerenciarMotoristaAviso;
    private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;
    private FirebaseUser user;

    //menus superior sair
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                autenticacao.signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            case R.id.menuConfiguracoes:
                startActivity(new Intent(this, configuracoesMotorista.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_motorista);

                 /*Dando uma referência para a variável criada,
        nessse caso esta referenciando o componente
        com id recyclerView, que esta no layout da tela.*/

        recyclerViewAdmin = findViewById(R.id.gerenciarMotoristaRecyclerView);
        gerenciarMotoristaAviso = findViewById(R.id.gerenciarMotoristaAviso);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        listaMotorista();

        //Configurar Adapter.
        adapter = new Adapter_gerenciar_motorista(listaMotoristaArray, getBaseContext());

        //Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAdmin.setLayoutManager(layoutManager); //entregando o objeto criado a cima chamado layoutManager ao recyclerView.
        recyclerViewAdmin.setHasFixedSize(true); //otmizando o layout do recyclerview para ficar sempre do mesmo tamanho.
        recyclerViewAdmin.setAdapter( adapter ); //esse adaptardor é o mesmo criado logo a cima em 'CONFIGURAR ADAPETER'

        //adicionar evento de click ao recycleview
        recyclerViewAdmin.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewAdmin,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {


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

    private void listaMotorista(){
        DatabaseReference motoristas = firebaseRef.child("usuarioMotorista");

        final Query listaMotorista = motoristas.orderByChild("nome");

        listaMotorista.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                listaMotoristaArray.clear();

                if (dataSnapshot.getChildrenCount() > 0){
                    gerenciarMotoristaAviso.setVisibility(View.GONE);
                    recyclerViewAdmin.setVisibility(View.VISIBLE);

                }else{
                    gerenciarMotoristaAviso.setVisibility(View.VISIBLE);
                    recyclerViewAdmin.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    UsuarioMotorista motoristaLista = ds.getValue(UsuarioMotorista.class);
                        listaMotoristaArray.add(motoristaLista);

                }

                adapter.notifyDataSetChanged();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void botaoNovoMotorista (View view){
        Intent intent = new Intent(getApplicationContext(), novoMotorista.class);
        startActivity(intent);
    }
}
