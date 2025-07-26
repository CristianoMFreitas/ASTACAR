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
import com.taxicaraubas.chamataxi.aplicacoes.validacoes;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class novoMotorista extends AppCompatActivity {

    private EditText nome, cpf, email, senha, senhaConfirmar, carroModelo, carroPlaca, carroCor;
    private MaskEditText telefone;
    private String idMotorista;
    private UsuarioMotorista usuario = new UsuarioMotorista();
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_motorista);
        setTitle("Adicionar motorista");

        nome = findViewById(R.id.editTextMotoristaNome);
        cpf = findViewById(R.id.editTextMotoristaCpf);
        telefone = findViewById(R.id.editTextMotoristaTelefone);
        email = findViewById(R.id.editTextMotoristaEmail);
        senha = findViewById(R.id.editTextMotoristaSenha);
        senhaConfirmar = findViewById(R.id.editTextMotoristaSenhaConfirmar);
        carroModelo = findViewById(R.id.editTextMotoristaCarroModelo);
        carroPlaca = findViewById(R.id.editTextMotoristaCarroPlaca);
        carroCor = findViewById(R.id.editTextMotoristaCarroCor);

    }

    public void botaoCadastrar (View view){
        String nomeVerifica = nome.getText().toString();
        String cpfVerifica = cpf.getText().toString();
        String telefoneVerifica = telefone.getRawText();
        String emailVerifica = email.getText().toString();
        String senhaVerifica = senha.getText().toString();
        String senhaConfirmarVerifica = senhaConfirmar.getText().toString();
        String carroModeloVerifica = carroModelo.getText().toString();
        String carroPlacaVerifica = carroPlaca.getText().toString();
        String carroCorVerifica = carroCor.getText().toString();

        //VALIDAR SE OS CAMPOS ESTÃO VAZIOS
        if (!nomeVerifica.isEmpty()){
            if (!cpfVerifica.isEmpty()){
                if (validacoes.isCPF(cpfVerifica)){
                    if (!telefoneVerifica.isEmpty()){
                        if (!emailVerifica.isEmpty()){
                            if (!senhaVerifica.isEmpty()){
                                if (!senhaConfirmarVerifica.isEmpty()){
                                    if (senhaVerifica.equals(senhaConfirmarVerifica)){
                                        if (!carroModeloVerifica.isEmpty()){
                                            if (!carroPlacaVerifica.isEmpty()){
                                                if (!carroCorVerifica.isEmpty()){

                                                    usuario.setNome(nomeVerifica);
                                                    usuario.setCpf(cpfVerifica);
                                                    usuario.setTelefone(telefoneVerifica);
                                                    usuario.setEmail(emailVerifica);
                                                    usuario.setSenha(senhaVerifica);
                                                    usuario.setModeloCarro(carroModeloVerifica);
                                                    usuario.setPlaca(carroPlacaVerifica);
                                                    usuario.setCor(carroCorVerifica);

                                                    cadastrarNovoMotorista();


                                                }else{
                                                    msgErro("Cor");
                                                }

                                            }else{
                                                msgErro("Placa");
                                            }
                                        }else{
                                            msgErro("Modelo");
                                        }
                                    }else {
                                        Toast.makeText(novoMotorista.this, "O campo SENHA e VERIFICAR senha precisam ter o mesmo valor", Toast.LENGTH_LONG).show();
                                    }

                                }else {
                                    msgErro("Verificar Senha");
                                }
                            }else {
                                msgErro("Senha");
                            }
                        }else {
                            msgErro("E-Mail");
                        }
                    }else {
                        msgErro("Telefone");
                    }
                }else {
                    msgErro("CPF - CPF INVÁLIDO!");
                }
            }else {
                msgErro("CPF");
            }
        }else {
            msgErro("Nome");
        }

        //VALIDAR SE OS CAMPOS ESTÃO VAZIOS --- FIM ---
    }

    public void msgErro (String var){
        Toast.makeText(novoMotorista.this, "Preencha o campo "+var, Toast.LENGTH_LONG).show();
    }

    public void cadastrarNovoMotorista (){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()

                //tratar os erros possíveis na hora do cadastro:
                // senha fraca, e-mail invalido, e-mail já cadastrado, ou outros erros enviados pelo sistema.
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    idMotorista = task.getResult().getUser().getUid();
                    usuario.setIdMotorista(idMotorista);
                    usuario.salvar();


                    //Adicionar o nome e o telefone no usuario do firebase, servirá para consultas mais
                    //rapidas sem a necessidade de uma query.
                    //usuarioFirebase.atualizarNomeUsuario(usuario.getNome());


                    Toast.makeText(novoMotorista.this, "Sucesso ao cadastrar usuário! ", Toast.LENGTH_LONG).show();

                    //direcionar o usuário para fazer o login
                    //baseado no tipo de usuario

                    Intent intent = new Intent(getApplicationContext(), administrador.class);
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
                        excecao = "Erro ao cadastrar Motorista: "+e.getMessage();
                        e.printStackTrace();
                    }

                    Toast.makeText(novoMotorista.this, excecao, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
