package com.taxicaraubas.chamataxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;
import com.taxicaraubas.chamataxi.model.UsuarioPassageiro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private EditText loginEmail, loginSenha;
    private UsuarioPassageiro usuario = new UsuarioPassageiro();
    private UsuarioMotorista motoristaLogin = new UsuarioMotorista();
    private Switch tipo;
    private FirebaseAuth autenticacao;
    final String TIPO_MOTORISTA = "usuarioMotorista";
    final String TIPO_PASSAGEIRO = "usuarioPassageiro";
    private ProgressBar carregando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.editTextLoginEmail);
        loginSenha = findViewById(R.id.editTextLoginSenha);
        tipo = findViewById(R.id.switchPassageiroOuMotorista);
        carregando = findViewById(R.id.progressBar);

        carregando.setVisibility(View.GONE);
    }

    public void botaoLoginEntrar (View view){

        carregando.setVisibility(View.VISIBLE);

        String varLoginEmail = loginEmail.getText().toString();
        String varLoginSenha = loginSenha.getText().toString();

         if (!varLoginEmail.isEmpty()){
            if (!varLoginSenha.isEmpty()){
                if (tipo.isChecked()){ //motorista

                    motoristaLogin.setEmail(varLoginEmail);
                    motoristaLogin.setSenha(varLoginSenha);
                    validarLoginMotoristaAdmin();

                }else{  // passageiro

                    usuario.setEmail(varLoginEmail);
                    usuario.setSenha(varLoginSenha);
                    validarLoginPassageiro();

                }

            }else {
                Toast.makeText(login.this, "Preencha o campo E-Mail", Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(login.this, "Preencha o campo Senha", Toast.LENGTH_LONG).show();
        }
    }

    public void validarLoginMotoristaAdmin (){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                motoristaLogin.getEmail(),
                motoristaLogin.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String idUsuario = autenticacao.getUid().toString();

                    DatabaseReference motoristaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child(TIPO_MOTORISTA)
                            .child(idUsuario);

                    motoristaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UsuarioMotorista motoristaUser = dataSnapshot.getValue(UsuarioMotorista.class);

                            if (motoristaUser != null) {
                                String tipoMotorista = motoristaUser.getTipo();

                                if (tipoMotorista.equals("motorista")) { // Entrar como motorista

                                    motorista.administrador = false;

                                    Toast.makeText(login.this,
                                            "MOTORISTA: Login realizado com sucesso!",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), motorista.class);
                                    startActivity(intent);
                                    finish();

                                } else if (tipoMotorista.equals("administrador")) { //Entrar como administrador

                                    motorista.administrador = true;

                                    Toast.makeText(login.this,
                                            "ADMINISTRADOR - login realizado com sucesso!",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), administrador.class);
                                    startActivity(intent);
                                    finish();

                                } else { // sair
                                    Toast.makeText(login.this,
                                            "Verifique se você ativou a opção MOTORISTA",
                                            Toast.LENGTH_LONG)
                                            .show();
                                    carregando.setVisibility(View.GONE);
                                }
                            }else {
                                Toast.makeText(login.this,
                                        "Verifique se você ativou a opção MOTORISTA",
                                        Toast.LENGTH_LONG).show();
                                carregando.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                   }else {
                    String excecao = "testando excecao";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-MAIL ou senha não corresponde a um usuário cadastrado!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta de e-mail já foi cadastrada, tente um e-mail diferente!";
                    }catch (Exception e){
                        excecao = "Erro ao fazer login: "+e.getMessage();
                        e.printStackTrace();
                    }
                    carregando.setVisibility(View.GONE);
                    Toast.makeText(login.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void validarLoginPassageiro (){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()

        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String idUsuario = autenticacao.getUid();

                    DatabaseReference motoristaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child(TIPO_PASSAGEIRO)
                            .child(idUsuario);

                    motoristaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            UsuarioPassageiro passageiro = dataSnapshot.getValue(UsuarioPassageiro.class);

                            if (passageiro !=null) {
                                String tipoPassageiro = passageiro.getTipo();

                                if (tipoPassageiro.equals("passageiro")) { // Entrar como passageiro

                                    motorista.administrador = false;
                                    Toast.makeText(login.this,
                                            "PASSAGEIRO: Login realizado com sucesso!",
                                            Toast.LENGTH_LONG).show();

                                    Intent intent = new Intent(getApplicationContext(), pedirCarro.class);
                                    startActivity(intent);
                                    finish();

                                } else { // sair
                                    Toast.makeText(login.this,
                                            "Verifique se você ativou a opção MOTORISTA",
                                            Toast.LENGTH_LONG).show();
                                    carregando.setVisibility(View.GONE);
                                }
                            }else {
                                Toast.makeText(login.this,
                                        "Verifique se você ativou a opção MOTORISTA",
                                        Toast.LENGTH_LONG).show();
                                carregando.setVisibility(View.GONE);

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else {
                    String excecao = "testando excecao";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e){
                        excecao = "Usuário não cadastrado!";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "E-MAIL ou senha não corresponde a um usuário cadastrado!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta de e-mail já foi cadastrada, tente um e-mail diferente!";
                    }catch (Exception e){
                        excecao = "Erro ao fazer login: "+e.getMessage();
                        e.printStackTrace();
                    }
                    carregando.setVisibility(View.GONE);
                    Toast.makeText(login.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void CadastrarNovo (View view){
        Intent intent = new Intent(getApplicationContext(), cadastrarUsuario.class);
        startActivity(intent);
    }

    public void politicaDePrivacidade (View view){
        Intent intent = new Intent(getApplicationContext(), politicaDePrivacidade.class);
        startActivity(intent);
    }
}
