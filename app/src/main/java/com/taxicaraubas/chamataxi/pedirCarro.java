package com.taxicaraubas.chamataxi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.aplicacoes.usuarioFirebase;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.Requisicao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.taxicaraubas.chamataxi.model.UsuarioPassageiro;

public class pedirCarro extends AppCompatActivity{

    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef;
    private RadioButton opcaoMossoro, opcaoCaraubas;
    private EditText rua;
    private EditText bairro;
    private EditText numero;
    private EditText proximo;
    private String destino;
    private String verRua = "Não informado";
    private String verBairro = "Não informado";
    private String verNum = "S/N";
    private String verProximo;
    private Button pedirCarro;
    private Requisicao novaRequisicao = new Requisicao();
    private UsuarioPassageiro passageiroAtual;
    private String idPassageiro, telefone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_carro);
        setTitle("Pedir carro");

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        opcaoMossoro = findViewById(R.id.radioButtonMossoro);
        opcaoCaraubas = findViewById(R.id.radioButtonCaraubaas);
//        rua = findViewById(R.id.editTextRua);
//        bairro = findViewById(R.id.editTextBairro);
//        numero = findViewById(R.id.editTextNum);
        proximo = findViewById(R.id.editTextProximo);
        pedirCarro = findViewById(R.id.botaoChamarCarro);

        pedirCarro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarLocalizacao();
            }
        });
        idPassageiro = autenticacao.getUid().toString();
        pegarTelefone();

    }

    //menus superior sair
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
            finish();
        } else if (id == R.id.menuConfiguracoes) {
            startActivity(new Intent(this, configuracoes.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void enviarLocalizacao (){
        verRua = "Não informado";
        verBairro = "Não informado";
        verNum = "S/N";
        verProximo = "Não informado";
//        if (!rua.getText().toString().equals("")){
//            verRua = rua.getText().toString();
//        }
//        else{
//            verRua = "Não informado";
//        }
//
//        if (!bairro.getText().toString().equals("")){
//            verBairro = bairro.getText().toString();
//        }
//        else{
//            verBairro = "Não informado";
//        }
//
//        if (!numero.getText().toString().equals("")){
//            verNum = numero.getText().toString();
//        }
//        else{
//            verNum = "S/N";
//        }
//
//        if (!proximo.getText().toString().equals("")){
//            verProximo = proximo.getText().toString();
//        }
//        else{
//            verProximo = "Não informado";
//        }


        if ((opcaoMossoro.isChecked())||(opcaoCaraubas.isChecked())){
            if (opcaoMossoro.isChecked()){
                destino = "Mossoró";
            }else if (opcaoCaraubas.isChecked()){
                destino = "Caraúbas";
            }

            salvarRequisicao();
//                if (!verProximo.isEmpty()){
//
//                                    StringBuilder menssagem = new StringBuilder();
//                                    menssagem.append("Estou indo para " + destino);
//                                    menssagem.append("\nRua: "+ verRua + ", Nº " + verNum);
//                                    menssagem.append("\nBairro: " + verBairro);
//                                    menssagem.append("\nPróximo: " + verProximo);
//
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                                            .setTitle("Confirme seu endereço!")
//                                            .setMessage(menssagem)
//                                            .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                    salvarRequisicao();
//
//                                                }
//                                            }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//
//                                                }
//                                            });
//                                    AlertDialog dialog = builder.create();
//                                    dialog.show();
//
//                }else {
//                    msgErro("Próximo!");
//                }

            }else {
                msgErro("Estou indo para:");
            }
    }

    public void msgErro (String var){
        Toast.makeText(pedirCarro.this, "Preencha o campo "+var, Toast.LENGTH_LONG).show();
    }

    public void salvarRequisicao () {
        setDadosPassageiroLogado();

        novaRequisicao.setBairro(verBairro);
        novaRequisicao.setDestino(destino);
        novaRequisicao.setNumero(verNum);
        novaRequisicao.setRua(verRua);
        novaRequisicao.setProximo(verProximo);
        novaRequisicao.setTelefonePassageiro(telefone);
        novaRequisicao.setIdPassageiro(idPassageiro);
        novaRequisicao.setStatus(Requisicao.STATUS_AGUARDANDO);

        novaRequisicao.salvar();

        String idRequisicao = novaRequisicao.getIdRequisicao();

        Toast.makeText(getApplicationContext(), "Dados enviados com sucesso! ", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(getApplicationContext(), listaMotorista.class);

        intent.putExtra("requisicaoEnvia", novaRequisicao);
        intent.putExtra("destinoOp", destino);

        startActivity(intent);
        finish();
    }

    public void setDadosPassageiroLogado (){
        //recuperar dados usuario -----------------------
        FirebaseUser usuarioRecuperarPassageiro = usuarioFirebase.usuario();

        //Carregar foto do perfil do firebase ou foto padrão.
        String nomePassageiro = usuarioRecuperarPassageiro.getDisplayName();

        novaRequisicao.setNomePassageiro(nomePassageiro);

        if (usuarioRecuperarPassageiro.getPhotoUrl() != null){

            Uri url = usuarioRecuperarPassageiro.getPhotoUrl();
            String urlFoto;
            urlFoto = url.toString();
            novaRequisicao.setFotoPassageiro(urlFoto);

        }


    }

    public void pegarTelefone (){
        DatabaseReference passageiro = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarioPassageiro").child(idPassageiro);

        passageiro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                passageiroAtual = dataSnapshot.getValue(UsuarioPassageiro.class);
                telefone = passageiroAtual.getTelefone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
