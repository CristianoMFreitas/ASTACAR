package com.taxicaraubas.astacar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.UsuarioMotorista;
import com.taxicaraubas.astacar.model.UsuarioPassageiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;
    private FloatingActionButton addMotorista;

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("ATENÇÃO")
                .setMessage("Deseja sair da aplicativo")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    finish();

                    }
                }).setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Fazer Login");

        setButtonBackVisible(false);
        setButtonNextVisible(false);


        addSlide(new FragmentSlide.Builder()
                    .background(android.R.color.white)
                    .fragment(R.layout.intro1)
                    .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro2)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro3)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro4)
                .build()
        );
        addSlide(new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro5)
                .canGoForward(false)
                .build()
        );
    }

   protected void onStart(){
        super.onStart();
        verificarUsuarioLogado();
    }

    public void botaoCadastrar (View view){
        Intent intent = new Intent(getApplicationContext(), cadastrarUsuario .class);
        startActivity(intent);
        finish();
    }


    public void login (View view){
        Intent intent = new Intent(getApplicationContext(), login.class);
        startActivity(intent);
        finish();
    }

    //verificar se o usuário esta logado, caso sim, pula os slides e vai direto para a tela
    //QUE PERTENCE A CATEGORIA DO USUÁRIO.
    public void verificarUsuarioLogado(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
//        addMotorista.setVisibility(View.GONE);

        if (autenticacao.getCurrentUser() != null){
            setContentView(R.layout.activity_main);
            String idUsuario = autenticacao.getUid();

             DatabaseReference usuarioPassageiro = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarioPassageiro")
                    .child(idUsuario);

            usuarioPassageiro.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsuarioPassageiro passageiroVer = dataSnapshot.getValue(UsuarioPassageiro.class);
                    if (passageiroVer != null){
                        if (passageiroVer.getTipo().equals("passageiro")){
                            motorista.administrador = false;

                            Intent intent = new Intent(getApplicationContext() , telaInicial.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference usuarioMotorista = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarioMotorista")
                    .child(idUsuario);

            usuarioMotorista.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsuarioMotorista motoristaVer = dataSnapshot.getValue(UsuarioMotorista.class);
                    if (motoristaVer != null){
                        if (motoristaVer.getTipo().equals("motorista")){
                            motorista.administrador = false;
                            Intent intent = new Intent(getApplicationContext() , motorista.class);
                            startActivity(intent);
                            finish();
                        }else if (motoristaVer.getTipo().equals("administrador")){
                            motorista.administrador = true;
                            Intent intent = new Intent(getApplicationContext() , motorista.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {

        }
    }

    public void textoBotaoPular (View view){
        Intent intent = new Intent(getApplicationContext() , login.class);
        startActivity(intent);
        finish();
    }
}
