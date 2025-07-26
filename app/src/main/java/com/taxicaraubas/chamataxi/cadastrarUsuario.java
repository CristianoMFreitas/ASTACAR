package com.taxicaraubas.chamataxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.santalu.maskedittext.MaskEditText;
import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.aplicacoes.usuarioFirebase;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioPassageiro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class cadastrarUsuario extends AppCompatActivity {

    private EditText cadastrarNome, cadastrarCPF, cadastrarEmail, cadastrarSenha1, cadastrarSenha2;
    private MaskEditText cadastrarTelefone;
    private FirebaseAuth autenticacao;
    private UsuarioPassageiro usuario = new UsuarioPassageiro();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);
        setTitle("Cadastrar novo usuário");

        cadastrarNome = findViewById(R.id.editTextCadastrarNome);
        //cadastrarCPF = findViewById(R.id.editTextCadastrarCPF);
        cadastrarTelefone = findViewById(R.id.editTextCadastrarTelefone);
        cadastrarEmail = findViewById(R.id.editTextCadastrarEmail);
        cadastrarSenha1 = findViewById(R.id.editTextCadastrarSenha1);
        cadastrarSenha2 = findViewById(R.id.editTextCadastrarSenha2);
    }

    public void botaoCadastrar (View view){
        String nome = cadastrarNome.getText().toString();
        //String cpf = cadastrarCPF.getText().toString();
        String telefone = cadastrarTelefone.getRawText();
        String email = cadastrarEmail.getText().toString();
        String senha1 = cadastrarSenha1.getText().toString();
        String senha2 = cadastrarSenha2.getText().toString();

        //VALIDAR SE OS CAMPOS ESTÃO VAZIOS.
        if (!nome.isEmpty()){
                if (!telefone.isEmpty()){
                    if (!email.isEmpty()){
                        if (!senha1.isEmpty()){
                            if (!senha2.isEmpty()){
                                if (senha1.equals(senha2)) {

                                    usuario.setNome(nome);
                                    //usuario.setCpf(cpf);
                                    usuario.setTelefone(telefone);
                                    usuario.setEmail(email);
                                    usuario.setSenha(senha1);

                                    cadastrarNovoUsuario();

                                }else {
                                    msgErro("senha e repetir senha com a mesma informação");
                                }


                            }else {
                                msgErro("repetir Senha");
                            }
                        }else {
                            msgErro("Senha");
                        }
                    }else {
                        msgErro("E-Mail");
                    }
                }else {
                    msgErro("telefone");
                }
        }else {
            msgErro("nome");
        }
    }

    public void msgErro (String var){
            Toast.makeText(cadastrarUsuario.this, "Preencha o campo "+var, Toast.LENGTH_LONG).show();
    }

    public void cadastrarNovoUsuario (){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()

                //tratar os erros possíveis na hora do cadastro:
                // senha fraca, e-mail invalido, e-mail já cadastrado, ou outros erros enviados pelo sistema.
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String idUsuario = task.getResult().getUser().getUid();
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();


                    //Adicionar o nome e o telefone no usuario do firebase, servirá para consultas mais
                    //rapidas sem a necessidade de uma query.
                    usuarioFirebase.atualizarNomeUsuario(usuario.getNome());


                    Toast.makeText(cadastrarUsuario.this, "Sucesso ao cadastrar usuário! ", Toast.LENGTH_LONG).show();

                    //direcionar o usuário para fazer o login
                    //baseado no tipo de usuario

                        Intent intent = new Intent(getApplicationContext(), login.class);
                        startActivity(intent);
                        finish();

                } else {

                    String excecao = "testando excecao";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException e){
                        excecao = "Digite uma senha melhor com no mínimo 6 caracteres";
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        excecao = "Digite um E-MAIL válido!";
                    }catch (FirebaseAuthUserCollisionException e){
                        excecao = "Esta conta de e-mail já foi cadastrada, tente um e-mail diferente!";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(cadastrarUsuario.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
