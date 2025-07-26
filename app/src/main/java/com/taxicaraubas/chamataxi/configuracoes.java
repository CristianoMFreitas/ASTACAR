package com.taxicaraubas.chamataxi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest; // Importação adicionada para Manifest.permission
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build; // Importação adicionada para Build.VERSION
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.taxicaraubas.chamataxi.aplicacoes.Permissao;
import com.taxicaraubas.chamataxi.aplicacoes.usuarioFirebase;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioPassageiro;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class configuracoes extends AppCompatActivity {

    private static final String TAG = "ConfiguracoesActivity";

    // A lista de permissões será inicializada dinamicamente no onCreate
    private String[] permissoesNecessarias;

    // Códigos de requisição para intents
    private static final int SELECAO_CAMERA = 100;
    private static final int SELECAO_GALERIA = 200;
    // Código de requisição para as permissões
    private static final int REQUEST_CODE_PERMISSOES = 1;

    // Componentes da UI
    private CircleImageView circleImageViewPerfil;
    private EditText editeConfgNome, editeConfigTelefone;
    private ProgressBar carregando;

    // Referências Firebase
    private StorageReference storageReference;
    private UsuarioPassageiro usuarioLogado;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // --- INICIALIZAÇÃO DINÂMICA DAS PERMISSÕES ---
        // Define as permissões necessárias com base na versão do Android em tempo de execução
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33 (Android 13) ou superior
            permissoesNecessarias = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES, // Nova permissão para imagens no Android 13+
                    Manifest.permission.CAMERA
            };
        } else { // APIs anteriores ao Android 13 (inclusive)
            permissoesNecessarias = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, // Permissão legada
                    Manifest.permission.CAMERA
            };
        }
        // --- FIM DA INICIALIZAÇÃO DINÂMICA DAS PERMISSÕES ---


        // Inicialização dos componentes da UI
        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfilMotorista);
        editeConfgNome = findViewById(R.id.editTextConfigNomeMotorista);
        editeConfigTelefone = findViewById(R.id.editTextConfigTelefoneMotorista);
        carregando = findViewById(R.id.configuracoesCarregando);

        // Inicialização das referências Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = usuarioFirebase.getDadosUsuarioLogado();

        // Configurações da ActionBar (barra superior)
        setTitle("Configurações");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Validação de permissões no onCreate para carregar dados iniciais.
        // As permissões para câmera/galeria serão verificadas novamente nos cliques dos botões.
        if (Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES)) {
            Log.d(TAG, "onCreate: Permissões já concedidas. Carregando dados do usuário.");
            carregarDadosUsuario();
        } else {
            Log.d(TAG, "onCreate: Solicitando permissões...");
            // O carregarDadosUsuario() será chamado em onRequestPermissionsResult após a concessão.
        }
    }

    /**
     * Carrega os dados do usuário (nome, telefone, foto de perfil) do Firebase.
     */
    private void carregarDadosUsuario() {
        Log.d(TAG, "carregarDadosUsuario: Tentando carregar dados do usuário.");
        FirebaseUser usuarioRecuperar = usuarioFirebase.usuario();

        if (usuarioRecuperar != null) {
            Uri url = usuarioRecuperar.getPhotoUrl();
            if (url != null) {
                carregando.setVisibility(View.VISIBLE);

                Glide.with(configuracoes.this)
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                carregando.setVisibility(View.GONE);
                                Log.e(TAG, "Glide: Erro ao carregar imagem de perfil", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                carregando.setVisibility(View.GONE);
                                Log.d(TAG, "Glide: Imagem de perfil carregada com sucesso.");
                                return false;
                            }
                        })
                        .into(circleImageViewPerfil);
            } else {
                circleImageViewPerfil.setImageResource(R.drawable.icone_pessoa);
                carregando.setVisibility(View.GONE);
                Log.d(TAG, "carregarDadosUsuario: URL da foto de perfil nula, usando ícone padrão.");
            }

            editeConfgNome.setText(usuarioRecuperar.getDisplayName());

            String idUsuario = autenticacao.getUid();
            DatabaseReference usuarioPassageiroRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarioPassageiro")
                    .child(idUsuario);

            usuarioPassageiroRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsuarioPassageiro usuario = snapshot.getValue(UsuarioPassageiro.class);
                    if (usuario != null) {
                        editeConfigTelefone.setText(usuario.getTelefone());
                        Log.d(TAG, "carregarDadosUsuario: Telefone do usuário carregado.");
                    } else {
                        Log.w(TAG, "carregarDadosUsuario: UsuárioPassageiro nulo no snapshot.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "carregarDadosUsuario: Erro ao carregar telefone: " + error.getMessage());
                    Toast.makeText(configuracoes.this, "Erro ao carregar telefone", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "carregarDadosUsuario: FirebaseUser é nulo, não foi possível carregar dados.");
            Toast.makeText(this, "Erro: Usuário não logado ou dados indisponíveis.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Callback para o resultado da solicitação de permissões.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Recebido resultado de permissão para requestCode " + requestCode);

        if (requestCode == REQUEST_CODE_PERMISSOES) {
            boolean todasPermitidas = true;

            if (grantResults.length > 0) {
                for (int resultado : grantResults) {
                    if (resultado == PackageManager.PERMISSION_DENIED) {
                        todasPermitidas = false;
                        break;
                    }
                }
            } else {
                todasPermitidas = false;
            }

            if (!todasPermitidas) {
                Log.w(TAG, "onRequestPermissionsResult: Permissões negadas.");
                alertaValidacaoPermissao();
            } else {
                Log.i(TAG, "onRequestPermissionsResult: Permissões concedidas. Carregando dados do usuário e habilitando funcionalidades.");
                carregarDadosUsuario();
            }
        }
    }

    /**
     * Exibe um AlertDialog quando as permissões são negadas.
     */
    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar as funcionalidades de foto, é necessário aceitar as permissões de câmera e armazenamento.\n\nPor favor, vá para as configurações do aplicativo e conceda as permissões manualmente.");
        builder.setCancelable(false);
        builder.setPositiveButton("Configurações", (dialog, which) -> {
            dialog.dismiss();
            // Abre as configurações do aplicativo
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
            // Opcional: Ações caso o usuário não queira ir para as configurações
        });
        builder.show();
    }

    /**
     * Callback para o resultado de Intents (câmera, galeria).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Recebido resultado. RequestCode: " + requestCode + ", ResultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                if (requestCode == SELECAO_CAMERA) {
                    if (data != null && data.getExtras() != null) {
                        imagem = (Bitmap) data.getExtras().get("data");
                        Log.d(TAG, "onActivityResult: Imagem capturada da câmera.");
                    } else {
                        Log.e(TAG, "onActivityResult: Dados da câmera nulos ou sem extras (pode ocorrer em alguns dispositivos).");
                        Toast.makeText(this, "Falha ao obter imagem da câmera.", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == SELECAO_GALERIA) {
                    if (data != null) {
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        Log.d(TAG, "onActivityResult: Imagem selecionada da galeria.");
                    } else {
                        Log.e(TAG, "onActivityResult: Dados da galeria nulos.");
                        Toast.makeText(this, "Falha ao obter imagem da galeria.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (imagem != null) {
                    circleImageViewPerfil.setImageBitmap(imagem);
                    Log.d(TAG, "onActivityResult: Imagem definida no CircleImageView. Iniciando upload.");

                    String idUsuarioImg = usuarioFirebase.usuario().getUid();

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil")
                            .child(idUsuarioImg + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(e -> {
                                Log.e(TAG, "UploadTask: Erro ao fazer upload da imagem: " + e.getMessage(), e);
                                Toast.makeText(configuracoes.this,
                                        "Erro ao fazer upload da imagem.",
                                        Toast.LENGTH_LONG).show();
                            })
                            .addOnSuccessListener(taskSnapshot -> imagemRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Log.d(TAG, "UploadTask: Imagem enviada com sucesso. URL: " + uri.toString());
                                        Toast.makeText(configuracoes.this,
                                                "Sucesso ao fazer upload da imagem!",
                                                Toast.LENGTH_LONG).show();
                                        atualizarFotoUsuario(uri);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "UploadTask: Erro ao obter URL de download: " + e.getMessage(), e);
                                        Toast.makeText(configuracoes.this,
                                                "Erro ao obter URL da imagem.",
                                                Toast.LENGTH_LONG).show();
                                    }));
                } else {
                    Log.w(TAG, "onActivityResult: Imagem nula após seleção/captura. Nenhuma imagem para processar.");
                }
            } catch (Exception e) {
                Log.e(TAG, "onActivityResult: Erro ao processar imagem: " + e.getMessage(), e);
                Toast.makeText(this, "Erro ao processar a imagem", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Log.d(TAG, "onActivityResult: Seleção/captura de imagem cancelada pelo usuário.");
            Toast.makeText(this, "Operação cancelada.", Toast.LENGTH_SHORT).show();
        } else {
            Log.w(TAG, "onActivityResult: Resultado inesperado. ResultCode: " + resultCode);
        }
    }

    /**
     * Atualiza a URL da foto de perfil do usuário no Firebase Authentication e no modelo local.
     * @param url Uri da nova foto.
     */
    private void atualizarFotoUsuario(Uri url) {
        boolean retorno = usuarioFirebase.atulizarFotousuario(url);
        if (retorno) {
            usuarioLogado.setFoto(url.toString());
            Log.d(TAG, "atualizarFotoUsuario: Foto do usuário atualizada no Firebase Auth e modelo local.");
            Toast.makeText(configuracoes.this,
                    "Sua foto foi alterada!",
                    Toast.LENGTH_LONG).show();
        } else {
            Log.e(TAG, "atualizarFotoUsuario: Falha ao atualizar foto do usuário no Firebase (método interno usuarioFirebase).");
            Toast.makeText(configuracoes.this, "Erro ao atualizar foto de perfil.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método onClick para o botão da câmera.
     * Verifica permissões e abre a câmera.
     */
    public void imagemCamera(View view) {
        Log.d(TAG, "imagemCamera: Botão da câmera clicado.");
        if (Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES)) {
            Log.d(TAG, "imagemCamera: Permissões de câmera/armazenamento OK. Iniciando Intent.");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, SELECAO_CAMERA);
            } else {
                Log.e(TAG, "imagemCamera: Nenhuma Activity encontrada para MediaStore.ACTION_IMAGE_CAPTURE.");
                Toast.makeText(this, "Nenhum aplicativo de câmera encontrado.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "imagemCamera: Permissões não concedidas. Solicitação de permissões será feita ou alerta exibido.");
        }
    }

    /**
     * Método onClick para o botão da galeria.
     * Verifica permissões e abre a galeria.
     */
    public void imagemGaleria(View view) {
        Log.d(TAG, "imagemGaleria: Botão da galeria clicado.");
        if (Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES)) {
            Log.d(TAG, "imagemGaleria: Permissões de leitura/armazenamento OK. Iniciando Intent.");
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, SELECAO_GALERIA);
            } else {
                Log.e(TAG, "imagemGaleria: Nenhuma Activity encontrada para Intent.ACTION_PICK.");
                Toast.makeText(this, "Nenhum aplicativo de galeria encontrado.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.w(TAG, "imagemGaleria: Permissões não concedidas. Solicitação de permissões será feita ou alerta exibido.");
        }
    }

    /**
     * Método onClick para o botão "Salvar alterações".
     */
    public void botaoSalvarAlteracao(View view) {
        salvarAlteracoes();
    }

    /**
     * Salva as alterações de nome e telefone do usuário no Firebase.
     */
    private void salvarAlteracoes() {
        String nome = editeConfgNome.getText().toString();
        boolean retorno = usuarioFirebase.atualizarNomeUsuario(nome);

        if (retorno) {
            String telefone = editeConfigTelefone.getText().toString();

            usuarioLogado.setNome(nome);
            usuarioLogado.setTelefone(telefone);
            usuarioLogado.atualizar();
            Log.d(TAG, "salvarAlteracoes: Dados do usuário (nome, telefone) salvos com sucesso.");

            Toast.makeText(configuracoes.this,
                    "Dados alterados com sucesso",
                    Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(), pedirCarro.class);
            startActivity(intent);
        } else {
            Log.e(TAG, "salvarAlteracoes: Falha ao atualizar nome do usuário.");
            Toast.makeText(configuracoes.this, "Erro ao salvar nome.", Toast.LENGTH_SHORT).show();
        }
    }
}