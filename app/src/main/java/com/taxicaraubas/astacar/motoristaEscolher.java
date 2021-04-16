package com.taxicaraubas.astacar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.Requisicao;
import com.taxicaraubas.astacar.model.UsuarioMotorista;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class motoristaEscolher extends AppCompatActivity {

    private TextView nomeMotorista,
                        carroPlaca,
                          carroCor,
                       carroModelo,
                         horaSaida,
                 motoristaTelefone,
                    msgConfirmacao;

    private ImageView fotoMotorista;
    private Bundle extras;
    private UsuarioMotorista motoristaEscolhido = new UsuarioMotorista();
    private Requisicao requisicao = new Requisicao();
    private CircleImageView telefoneLigar;
    private String telefone;
    private Button cancelar, outroMotorista, iniciarViajem, botaoVoltar;
    private Boolean tela = false;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private String destino;
    private ProgressBar carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motorista_escolher);
        setTitle("Motorista");

        nomeMotorista = findViewById(R.id.EscolherMotoristaNomeMotorista);
        carroModelo = findViewById(R.id.EscolherMotoristaModeloCarro);
        carroCor = findViewById(R.id.escolherMotoristaCor);
        carroPlaca = findViewById(R.id.escolherMotoristaPlaca);
        fotoMotorista = findViewById(R.id.EscolherMotoristaImgFotoMotorista);
        horaSaida = findViewById(R.id.EscolherMotoristaHoraSaida);
        carregando = findViewById(R.id.motoristaEscolherCarregar);

        telefoneLigar = findViewById(R.id.botaoTelefone);
        cancelar = findViewById(R.id.botaoCancelarViajem);
        outroMotorista = findViewById(R.id.botaoProcurarOutroMotorista);
        iniciarViajem = findViewById(R.id.botaoIniciarViajem);

        msgConfirmacao = findViewById(R.id.MsgEsperandoMotoristaConfirmar);


        cancelar.setVisibility(View.GONE);
        msgConfirmacao.setVisibility(View.GONE);

        extras = getIntent().getExtras();
        motoristaEscolhido = (UsuarioMotorista) extras.getSerializable("motoristaEscolhido");
        requisicao = (Requisicao) extras.getSerializable("requisicao");
        tela = extras.getBoolean("tela");
        destino = extras.getString("destinoOp");

        String telefoneModificado = "Saída às " + motoristaEscolhido.getHoraSaida();

        nomeMotorista.setText(motoristaEscolhido.getNome());
        carroModelo.setText(motoristaEscolhido.getModeloCarro());
        carroCor.setText(motoristaEscolhido.getCor());
        carroPlaca.setText(motoristaEscolhido.getPlaca());
        horaSaida.setText(telefoneModificado);

        telefone = motoristaEscolhido.getTelefone();
        carregando.setVisibility(View.GONE);

        if (motoristaEscolhido.getFoto() != null){
            carregando.setVisibility(View.VISIBLE);

            Uri url = Uri.parse(motoristaEscolhido.getFoto());
            Glide.with(motoristaEscolher.this)
                    .load(url)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            carregando.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(fotoMotorista);
        }else {
            fotoMotorista.setImageResource(R.drawable.motoristafundo);
        }

        if (tela){
            tela();
        }else {
            msgMuda ();
        }


    }

    public void ligarTelefone(View view){
        Uri uri = Uri.parse("tel:" + telefone);

        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(motoristaEscolher.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }
        }
        startActivity(intent);
    }

    public void botaoIniciarViajem (View view){
        msgConfirmacao.setVisibility(View.VISIBLE);
        cancelar.setVisibility(View.VISIBLE);
        outroMotorista.setVisibility(View.GONE);
        iniciarViajem.setVisibility(View.GONE);

        requisicao.setIdMotorista(motoristaEscolhido.getIdMotorista());
        requisicao.setStatus(Requisicao.STATUS_ESPERANDO_RESPOSTA);

        requisicao.atualizar(requisicao.getIdRequisicao());



    }

    public void botaoProcurarOutroMotorista(View view){
        Intent intent = new Intent(getApplicationContext(), listaMotorista.class);
        intent.putExtra("requisicaoEnvia", requisicao);
        intent.putExtra("tela", tela);
        intent.putExtra("destinoOp", destino);
        startActivity(intent);
        finish();

    }

    public void botaoCancelarViajem (View view){
        requisicao.setStatus(Requisicao.STATUS_CANCELADA);
        requisicao.setIdMotorista("");
        requisicao.atualizar(requisicao.getIdRequisicao());

        Intent intent = new Intent(getBaseContext(), telaInicial.class);
        intent.putExtra("destinoOp" , destino);
        startActivity(intent);
        finish();
    }

    public void tela(){
        iniciarViajem.setVisibility(View.GONE);
        outroMotorista.setVisibility(View.VISIBLE);


    }


    public void msgMuda (){
         DatabaseReference dataBaserequisicoes = firebaseRef.child("requisicoes");

        final Query requisicaoMudar = dataBaserequisicoes.orderByChild("idRequisicao")
                .equalTo(requisicao.getIdRequisicao());

        requisicaoMudar.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String verificaId = dataSnapshot.getValue(Requisicao.class).getIdMotorista();
                String verificaStatus = dataSnapshot.getValue(Requisicao.class).getStatus();

                if (verificaId.equals("")){
                    msgConfirmacao.setText("O motorista REJEITOU seu pedido!");
                    msgConfirmacao.setTextColor(getResources().getColor(R.color.vermelhoMsg));

                    AlertDialog.Builder builder = new AlertDialog.Builder(motoristaEscolher.this)
                            .setTitle("AVISO! ")
                            .setMessage("Não é possível viajar com o motorista selecionado")
                            .setPositiveButton("Escolher outro motorista", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(getApplicationContext(), listaMotorista.class);
                                    intent.putExtra("requisicaoEnvia", requisicao);
                                    intent.putExtra("destinoOp", destino);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                if (verificaStatus.equals(Requisicao.STATUS_MOTORISTA_ACEITOU)){
                    msgConfirmacao.setText("O motorista ACEITOU seu pedido \nAguarde no local indicado");
                    msgConfirmacao.setTextColor(getResources().getColor(R.color.verdeConfirmar));
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
