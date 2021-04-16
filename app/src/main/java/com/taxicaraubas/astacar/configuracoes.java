package com.taxicaraubas.astacar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.aplicacoes.Permissao;
import com.taxicaraubas.astacar.aplicacoes.usuarioFirebase;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.taxicaraubas.astacar.model.UsuarioPassageiro;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class configuracoes extends AppCompatActivity {

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    private CircleImageView circleImageViewPerfil;
    private EditText editeConfgNome, editeConfigTelefone;
    private StorageReference storageReference;
    private UsuarioPassageiro usuarioLogado;
    private ProgressBar carregando;
    FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        ImageButton camera = findViewById(R.id.camera);
        ImageButton galeria = findViewById(R.id.galeria);
        circleImageViewPerfil  = findViewById(R.id.circleImageViewFotoPerfilMotorista);
        editeConfgNome = findViewById(R.id.editTextConfigNomeMotorista);
        editeConfigTelefone = findViewById(R.id.editTextConfigTelefoneMotorista);
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = usuarioFirebase.getDadosUsuarioLogado();
        carregando = findViewById(R.id.configuracoesCarregando);

        //adcionar nome na toobar.
        setTitle("Configurações");

        //botão voltar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //recuperar dados usuario -----------------------
        FirebaseUser usuarioRecuperar = usuarioFirebase.usuario();

        //Carregar foto do perfil do firebase ou foto padrão.
        Uri url = usuarioRecuperar.getPhotoUrl();

        if (url != null){

            carregando.setVisibility(View.VISIBLE);

            Glide.with(configuracoes.this)
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
                    .into(circleImageViewPerfil);
        }else {
            circleImageViewPerfil.setImageResource(R.drawable.icone_pessoa);
            carregando.setVisibility(View.GONE);
        }

        Permissao.validarPermissoes(permissoesNecessarias, this, 1);

        //carregar nome usuario
        editeConfgNome.setText(usuarioRecuperar.getDisplayName());

        //Carregar telefone

        String idUsuario = autenticacao.getUid().toString();
        DatabaseReference usuarioPassageiro = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarioPassageiro")
                .child(idUsuario);

        usuarioPassageiro.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UsuarioPassageiro usuario = dataSnapshot.getValue(UsuarioPassageiro.class);

                editeConfigTelefone.setText(usuario.getTelefone());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //FIM recuperar dados do usuario. -------------------
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){

                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;

                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                if (imagem != null){
                    circleImageViewPerfil.setImageBitmap(imagem);

                    String idUsuarioImg = usuarioFirebase.usuario().getUid();

                    //recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    //salvar imagem no firebase
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuarioImg + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem);

                    //Verifica erro
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(configuracoes.this,
                                    "Erro ao fazezr upload da imagem.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //pegar caminho da imagem URI.
                            imagemRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Toast.makeText(configuracoes.this,
                                            "SUCESSO ao fazer upload da imagem",
                                            Toast.LENGTH_LONG).show();

                                            atualizarFotoUsuario(uri);

                                }
                            });

                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void atualizarFotoUsuario(Uri url){

        boolean retorno = usuarioFirebase.atulizarFotousuario(url);
        if (retorno){
            String telefone = editeConfigTelefone.getText().toString();
            usuarioLogado.setFoto(url.toString());



            Toast.makeText(configuracoes.this,
                    "Sua foto foi alterada!",
                    Toast.LENGTH_LONG);
        }

    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pemissôes negadas");
        builder.setMessage("Para utilizar o app, é necessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void imagemCamera(View view){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, SELECAO_CAMERA);
        }
    }

    public void imagemGaleria(View view){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, SELECAO_GALERIA);
        }
    }

    public void botaoSalvarAlteracao(View view){

        salvarAlteracoes();

    }

    public void salvarAlteracoes (){
        String nome = editeConfgNome.getText().toString();
        boolean retorno = usuarioFirebase.atualizarNomeUsuario( nome );

        if (retorno){
            String telefone = editeConfigTelefone.getText().toString();


            usuarioLogado.setNome(nome);
            usuarioLogado.setTelefone(telefone);
            usuarioLogado.atualizar();

            Toast.makeText(configuracoes.this,
                    "Dados alterados com sucesso",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), pedirCarro.class);
            startActivity(intent);
        }
    }

}
