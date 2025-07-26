package com.taxicaraubas.chamataxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.adapter.Adapter_gerenciar_motorista;
import com.taxicaraubas.chamataxi.aplicacoes.RecyclerItemClickListener;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Adicionado para Objects.requireNonNull

public class gerenciarMotorista extends AppCompatActivity {

    private RecyclerView recyclerViewAdmin;
    private Adapter_gerenciar_motorista adapter;
    private List<UsuarioMotorista> listaMotoristaArray = new ArrayList<>();
    private TextView gerenciarMotoristaAviso;
    private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menuSair) {
            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
            autenticacao.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
        } else if (id == R.id.menuConfiguracoes) {
            startActivity(new Intent(this, configuracoesMotorista.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para lidar com o clique do botão de voltar na ActionBar
    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(getApplicationContext(), motorista.class);
        // Flags para limpar a pilha de atividades e garantir que Motorista.class fique no topo
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish(); // Finaliza esta Activity
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_motorista);

        // Configurar a ActionBar
        setTitle("Gerenciar Motoristas"); // Define o título da ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true); // Habilita o botão de voltar

        recyclerViewAdmin = findViewById(R.id.gerenciarMotoristaRecyclerView);
        gerenciarMotoristaAviso = findViewById(R.id.gerenciarMotoristaAviso);
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();

        listaMotorista();

        // Configurar Adapter
        adapter = new Adapter_gerenciar_motorista(listaMotoristaArray, getBaseContext());

        // Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewAdmin.setLayoutManager(layoutManager);
        recyclerViewAdmin.setHasFixedSize(true);
        recyclerViewAdmin.setAdapter(adapter);

        // Adicionar evento de clique ao RecyclerView
        recyclerViewAdmin.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewAdmin,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // Não fazer nada no clique simples
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                confirmarExclusaoMotorista(position);
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                // Não fazer nada no clique simples
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

    public void botaoNovoMotorista(View view){
        Intent intent = new Intent(getApplicationContext(), novoMotorista.class);
        startActivity(intent);
    }

    private void confirmarExclusaoMotorista(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(gerenciarMotorista.this);
        builder.setTitle("Confirmar Exclusão");
        builder.setMessage("Você tem certeza que deseja excluir este motorista?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                excluirMotorista(position);
            }
        });
        builder.setNegativeButton("Não", null);
        builder.show();
    }

    private void excluirMotorista(int position) {
        UsuarioMotorista motorista = listaMotoristaArray.get(position);
        motorista.deletar();
        listaMotoristaArray.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(gerenciarMotorista.this, "Motorista excluído com sucesso!", Toast.LENGTH_LONG).show();
    }
}