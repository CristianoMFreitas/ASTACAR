package com.taxicaraubas.astacar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.taxicaraubas.astacar.adapter.Adapter_carro_lotacao;
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

public class lotacaoCarro extends AppCompatActivity {

    RecyclerView recyclerViewlistaRequisicao;
    private List<Requisicao> listaRequisicao = new ArrayList<>();
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Adapter_carro_lotacao adapter_lotacao;
    private TextView msg;
    private Requisicao requisicaoDataSnapshot;
    private Boolean aceitarPassageiro = false;
    private Bundle extras;
    private String destino, hora;
    private Button botaoDiponivelIndisponivel;
    private Boolean desistir = false;
    private Boolean finalizar = false;
    private int cont= 0;
    private int quantPassageiro;
    public Button botaoDesistir;
    public Button botaoFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lotacao_carro);
        setTitle("Meus Passageiros");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        botaoDesistir = findViewById(R.id.botaoDesistir);
        botaoFinalizar = findViewById(R.id.botaoFinalizar);

              /*Dando uma referência para a variável criada,
        nessse caso esta referenciando o componente
        com id recyclerView, que esta no layout da tela.*/

        Requisicao.tocarNotificacao = true;
        recyclerViewlistaRequisicao = findViewById(R.id.recyclerViewLotacao);
        msg = findViewById(R.id.textViewMsgNenhumPassageiro);
        botaoDiponivelIndisponivel = findViewById(R.id.botaodisponivelIndisponivel);

        extras = getIntent().getExtras();
        quantPassageiro = extras.getInt("quantidadePassageiro");
        destino = extras.getString("enviarDestino");
        hora = extras.getString("hora");

        recuperarListaRequisicaoPassageiro();

        //Configurar Adapter.
        adapter_lotacao = new Adapter_carro_lotacao(listaRequisicao, getApplicationContext(), quantPassageiro);

        //Configurar recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewlistaRequisicao.setLayoutManager(layoutManager); //entregando o objeto criado a cima chamado layoutManager ao recyclerView.
        recyclerViewlistaRequisicao.setHasFixedSize(true); //otmizando o layout do recyclerview para ficar sempre do mesmo tamanho.
        recyclerViewlistaRequisicao.setAdapter( adapter_lotacao ); //esse adaptardor é o mesmo criado logo a cima em 'CONFIGURAR ADAPETER'

        //ADICIONAR CLICK AOS INTENS DO RECYCLER VIEW
        recyclerViewlistaRequisicao.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewlistaRequisicao,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, final int position) {
                                Requisicao.tocarNotificacao = false;
                                Requisicao r = listaRequisicao.get(position);

                                Intent intent = new Intent(getApplicationContext(), verPassageiro.class);
                                intent.putExtra("requisicao", r);
                                intent.putExtra("quantPassageiro", quantPassageiro );
                                startActivity(intent);

                            }

                            @Override
                            public void onLongItemClick(View view, final int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                ));
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ATENÇÃO")
                .setMessage("Tem certeza que deseja sair e cancelar todos os passageiros?")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        desistir = true;
                        recuperarListaRequisicaoPassageiro();

                        Intent intent = new Intent(getApplicationContext(), motorista.class);
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

    public void botaoDesistir (View view){
        desistir = true;
        recuperarListaRequisicaoPassageiro();
        Requisicao.tocarNotificacao = false;


        Intent intent = new Intent(getApplicationContext(), motorista.class);
        startActivity(intent);
        finish();
    }

    public void botaoFinalizarViajem (View view){
        finalizar = true;
        Requisicao.tocarNotificacao = false;
        recuperarListaRequisicaoPassageiro();


        Intent intent = new Intent(getApplicationContext(), motorista.class);
        startActivity(intent);
        finish();
    }

    private void recuperarListaRequisicaoPassageiro(){
        msg.setVisibility(View.VISIBLE);
        recyclerViewlistaRequisicao.setVisibility(View.GONE);

        destino = extras.getString("enviarDestino");
        String idMotoristaAtual =  autenticacao.getUid().toString();

        DatabaseReference dataBaserequisicoes = firebaseRef.child("requisicoes");

        final Query listaRequisicaoBD = dataBaserequisicoes.orderByChild("idMotorista")
                .equalTo(idMotoristaAtual);
        Log.i("testelocal" ,"inicio antes addValueEvent");


        listaRequisicaoBD.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("testelocal" ,"2 dentro do onDataChange");
               listaRequisicao.clear();


                if (dataSnapshot.getChildrenCount() > 0 ){
                    msg.setVisibility(View.GONE);
                    recyclerViewlistaRequisicao.setVisibility(View.VISIBLE);

                }else{
                    msg.setVisibility(View.VISIBLE);
                    recyclerViewlistaRequisicao.setVisibility(View.GONE);
                }

                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    Log.i("testelocal" ,"3 dentro do FOR");
                    requisicaoDataSnapshot= ds.getValue(Requisicao.class);


                    if (destino.equals(requisicaoDataSnapshot.getDestino())){

                        if (finalizar){
                            requisicaoDataSnapshot.setStatus(Requisicao.STATUS_FINALIZADA);
                            requisicaoDataSnapshot.setIdMotorista(requisicaoDataSnapshot.getIdMotorista());
                            requisicaoDataSnapshot.atualizar(requisicaoDataSnapshot.getIdRequisicao());
                        }

                        if (desistir){ //MOTORISTA DESISTIU

                            requisicaoDataSnapshot.setStatus(Requisicao.STATUS_ESPERANDO_RESPOSTA);
                            requisicaoDataSnapshot.setIdMotorista("");
                            requisicaoDataSnapshot.atualizar(requisicaoDataSnapshot.getIdRequisicao());

                        }else { //MOTORISTA CONTINUA
                                    listaRequisicao.add(requisicaoDataSnapshot);
                        }
                    }
                }


                Log.i("contando", String.valueOf(cont));
                adapter_lotacao.notifyDataSetChanged();

                //TOCAR NOTIFICACAO
                if ((Requisicao.tocarNotificacao != null) && (cont != 0)) {
                    if (Requisicao.tocarNotificacao) {
                        notificacao();
                    }
                }
                cont++;
                desistir = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }



    public void botaoDisponivelIndisponivel (View view){
        UsuarioMotorista motorista = new UsuarioMotorista();
        motorista.setIdMotorista(autenticacao.getUid());
        motorista.setVagas(quantPassageiro);
        motorista.setHoraSaida(hora);
        motorista.setDestino(destino);

        if (botaoDiponivelIndisponivel.getText().toString().equals("FICAR DISPONÍVEL")){

            botaoDiponivelIndisponivel.setText("FICAR INDISPONÍVEL");
            botaoDiponivelIndisponivel.setBackgroundColor(getResources().getColor(R.color.cinza_escuro));
            motorista.setStatus(UsuarioMotorista.STATUS_DISPONIVEL);
            motorista.setStatusEncomenda(UsuarioMotorista.STATUS_DISPONIVEL);

        }else {

            botaoDiponivelIndisponivel.setText("FICAR DISPONÍVEL");
            botaoDiponivelIndisponivel.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            motorista.setStatus(UsuarioMotorista.STATUS_INDISPONIVEL);
            motorista.setStatusEncomenda(UsuarioMotorista.STATUS_INDISPONIVEL);

        }

        motorista.alterarStatus();
    }

   public void notificacao(){

        //Configurações para notificação
        String canal = "com.example.notificacaoteste";
        Uri uriSOm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone toque = RingtoneManager.getRingtone(getApplicationContext(), uriSOm);
        toque.play();

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle("ASTACAR")
                .setContentText("Você tem um novo passageiro")
                .setSmallIcon(R.drawable.icone_pessoa);

        //Recuperando NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //verifica versão do android para criar ou não um canal de notificação.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(canal ,"canal",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //enviando notificacao
        notificationManager.notify(0,notificacao.build());
    }

}
