package com.taxicaraubas.chamataxi;

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
import android.os.Build;
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
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;
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
// Removed java.util.Objects import as we'll use null checks

import de.hdodenhof.circleimageview.CircleImageView;

public class configuracoesMotorista extends AppCompatActivity {

    private static final String TAG = "ConfigMotoristaActivity";
    private String[] permissoesNecessarias; // Nome original

    private static final int SELECAO_CAMERA = 100; // Nome original
    private static final int SELECAO_GALERIA = 200; // Nome original
    private static final int REQUEST_CODE_PERMISSOES = 1; // Nome original

    private CircleImageView circleImageViewPerfil; // Nome original
    private EditText editeConfgNomeMotorista, editeConfigTelefoneMotorista, // Nome original
            editeCarroCor, editeCarroPlaca, editeCarroModelo, editeQuantVagaMotorista; // Nome original
    private StorageReference storageReference;
    private UsuarioMotorista usuarioLogado; // Nome original
    private FirebaseAuth autenticacao; // Nome original

    private ProgressBar carregar; // Nome original

    // private String pegarHora, pegarStatus; // Removido, dados no usuarioLogado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_motorista);

        // --- INICIALIZAÇÃO DINÂMICA DAS PERMISSÕES ---
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissoesNecessarias = new String[]{
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.CAMERA
            };
        } else {
            permissoesNecessarias = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };
        }
        // --- FIM DA INICIALIZAÇÃO DINÂMICA DAS PERMISSÕES ---

        circleImageViewPerfil = findViewById(R.id.circleImageViewFotoPerfilMotorista);
        editeConfgNomeMotorista = findViewById(R.id.editTextConfigNomeMotorista);
        editeConfigTelefoneMotorista = findViewById(R.id.editTextConfigTelefoneMotorista);
        editeCarroCor = findViewById(R.id.editTextCarroCor);
        editeCarroModelo = findViewById(R.id.editTextCarroModelo);
        editeCarroPlaca = findViewById(R.id.editTextCarroPlaca);
        editeQuantVagaMotorista = findViewById(R.id.editTextQuantVagasMotorista);
        carregar = findViewById(R.id.configuracaoMotoristaCarrgar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        usuarioLogado = usuarioFirebase.getDadosUsuarioLogadoMotorista(); // Get initial logged in user data

        setTitle("Configurações");
        if (getSupportActionBar() != null) { // Safe check: Use original method and check for null
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // PERMISSION HANDLING: Permissions are requested here.
        // Data loading is handled in onRequestPermissionsResult.
        Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES);
        // Initial load for when permissions are already granted
        carregarDadosUsuarioMotorista();
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(TAG, "onSupportNavigateUp: Botão de voltar da ActionBar clicado. Navegando para Motorista.class.");
        Intent intent = new Intent(getApplicationContext(), motorista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        return true;
    }

    private void carregarDadosUsuarioMotorista() { // Nome original
        Log.d(TAG, "carregarDadosUsuarioMotorista: Tentando carregar dados do motorista.");
        FirebaseUser usuarioRecuperar = usuarioFirebase.usuario(); // Nome original

        if (usuarioRecuperar != null) {
            // Load user profile picture from Firebase Auth
            Uri url = usuarioRecuperar.getPhotoUrl();
            if (url != null) {
                carregar.setVisibility(View.VISIBLE);
                Glide.with(this) // Use 'this' for context
                        .load(url)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                carregar.setVisibility(View.GONE);
                                Log.e(TAG, "Glide: Erro ao carregar imagem de perfil do motorista", e);
                                circleImageViewPerfil.setImageResource(R.drawable.icone_pessoa); // Fallback image
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                carregar.setVisibility(View.GONE);
                                Log.d(TAG, "Glide: Imagem de perfil do motorista carregada com sucesso.");
                                return false;
                            }
                        })
                        .into(circleImageViewPerfil);
            } else {
                circleImageViewPerfil.setImageResource(R.drawable.icone_pessoa);
                carregar.setVisibility(View.GONE);
                Log.d(TAG, "carregarDadosUsuarioMotorista: URL da foto de perfil nula, usando ícone padrão.");
            }

            // Populate name from Firebase Auth display name (can be overridden by Realtime DB)
            editeConfgNomeMotorista.setText(usuarioRecuperar.getDisplayName());

            // Load driver specific data from Realtime Database
            String idUsuario = autenticacao.getUid();
            if (idUsuario == null || idUsuario.isEmpty()) { // Added isEmpty check
                Log.e(TAG, "carregarDadosUsuarioMotorista: ID do usuário autenticado é nulo ou vazio.");
                Toast.makeText(this, "Erro: ID de usuário não encontrado.", Toast.LENGTH_SHORT).show();
                return;
            }

            DatabaseReference usuarioMotoristaRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarioMotorista")
                    .child(idUsuario);

            usuarioMotoristaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UsuarioMotorista usuarioMotPes = dataSnapshot.getValue(UsuarioMotorista.class);
                    if (usuarioMotPes != null) {
                        // Update the usuarioLogado object with fresh data from DB
                        usuarioLogado = usuarioMotPes; // Ensure usuarioLogado is always the latest

                        editeConfgNomeMotorista.setText(usuarioLogado.getNome());
                        editeConfigTelefoneMotorista.setText(usuarioLogado.getTelefone());
                        editeCarroCor.setText(usuarioLogado.getCor());
                        editeCarroPlaca.setText((usuarioLogado.getPlaca()));
                        editeCarroModelo.setText(usuarioLogado.getModeloCarro());

                        String quantidadePassageiro = String.valueOf(usuarioLogado.getQuantPassageiro());
                        editeQuantVagaMotorista.setText(quantidadePassageiro);

                        // pegarHora and pegarStatus are now part of usuarioLogado
                        // (assuming they exist in the UsuarioMotorista model)
                        // No need for separate global vars.
                        Log.d(TAG, "carregarDadosUsuarioMotorista: Dados do motorista carregados do Realtime Database.");
                    } else {
                        Log.w(TAG, "carregarDadosUsuarioMotorista: UsuarioMotorista nulo no snapshot para ID: " + idUsuario);
                        Toast.makeText(configuracoesMotorista.this, "Dados de motorista não encontrados. Por favor, complete o cadastro.", Toast.LENGTH_LONG).show();
                        // If usuarioLogado starts as null and foundDriver is null,
                        // it might remain null.
                        // However, per instructions, no new methods/constructors are added.
                        // The user will need to fill the fields and save.
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "carregarDadosUsuarioMotorista: Erro ao carregar dados do motorista: " + databaseError.getMessage());
                    Toast.makeText(configuracoesMotorista.this, "Erro ao carregar dados do motorista: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "carregarDadosUsuarioMotorista: FirebaseUser é nulo, não foi possível carregar dados.");
            Toast.makeText(this, "Erro: Motorista não logado ou dados indisponíveis.", Toast.LENGTH_LONG).show();
            // Redirect to login or handle unauthenticated state
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: Recebido resultado de permissão para requestCode " + requestCode);

        if (requestCode == REQUEST_CODE_PERMISSOES) { // Nome original
            boolean todasPermitidas = true; // Nome original

            if (grantResults.length > 0) {
                for (int resultado : grantResults) { // Nome original
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
                alertaValidacaoPermissao(); // Nome original
            } else {
                Log.i(TAG, "onRequestPermissionsResult: Permissões concedidas. Carregando dados do motorista e habilitando funcionalidades.");
                carregarDadosUsuarioMotorista(); // Nome original
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Recebido resultado. RequestCode: " + requestCode + ", ResultCode: " + resultCode);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null; // Nome original

            try {
                if (requestCode == SELECAO_CAMERA) { // Nome original
                    if (data != null && data.getExtras() != null) { // More robust null check
                        imagem = (Bitmap) data.getExtras().get("data");
                        Log.d(TAG, "onActivityResult: Imagem capturada da câmera.");
                    } else {
                        Log.e(TAG, "onActivityResult: Dados da câmera nulos ou sem extras (pode ocorrer em alguns dispositivos).");
                        Toast.makeText(this, "Falha ao obter imagem da câmera.", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == SELECAO_GALERIA) { // Nome original
                    if (data != null && data.getData() != null) { // More robust null check
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        Log.d(TAG, "onActivityResult: Imagem selecionada da galeria.");
                    } else {
                        Log.e(TAG, "onActivityResult: Dados da galeria nulos ou URI nula.");
                        Toast.makeText(this, "Falha ao obter imagem da galeria.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (imagem != null) {
                    circleImageViewPerfil.setImageBitmap(imagem); // Nome original
                    Log.d(TAG, "onActivityResult: Imagem definida no CircleImageView. Iniciando upload.");

                    FirebaseUser currentUser = usuarioFirebase.usuario();
                    if (currentUser == null || currentUser.getUid() == null || currentUser.getUid().isEmpty()) {
                        Log.e(TAG, "onActivityResult: ID do usuário nulo/vazio ao tentar fazer upload da imagem.");
                        Toast.makeText(this, "Erro: ID de usuário não encontrado para upload.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String idUsuarioImg = currentUser.getUid();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("perfil_motorista")
                            .child(idUsuarioImg + ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    uploadTask.addOnFailureListener(e -> {
                                Log.e(TAG, "UploadTask: Erro ao fazer upload da imagem: " + e.getMessage(), e);
                                Toast.makeText(configuracoesMotorista.this,
                                        "Erro ao fazer upload da imagem.",
                                        Toast.LENGTH_LONG).show();
                            })
                            .addOnSuccessListener(taskSnapshot -> imagemRef.getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        Log.d(TAG, "UploadTask: Imagem enviada com sucesso. URL: " + uri.toString());
                                        Toast.makeText(configuracoesMotorista.this,
                                                "Sucesso ao fazer upload da imagem!",
                                                Toast.LENGTH_SHORT).show(); // Changed to LENGTH_SHORT
                                        atualizarFotoUsuario(uri); // Nome original
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "UploadTask: Erro ao obter URL de download: " + e.getMessage(), e);
                                        Toast.makeText(configuracoesMotorista.this,
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

    public void atualizarFotoUsuario(Uri url) { // Nome original
        boolean retorno = usuarioFirebase.atulizarFotousuario(url);
        if (retorno) {
            if (usuarioLogado != null) { // Ensure usuarioLogado is not null before updating
                usuarioLogado.setFoto(url.toString());
                usuarioLogado.atualizar();
            } else {
                Log.w(TAG, "atualizarFotoUsuario: usuarioLogado é nulo, não foi possível atualizar foto no Realtime DB.");
            }

            Log.d(TAG, "atualizarFotoUsuario: Foto do motorista atualizada no Firebase Auth e modelo local.");
            Toast.makeText(configuracoesMotorista.this,
                    "Sua foto foi alterada!",
                    Toast.LENGTH_SHORT).show(); // Changed to LENGTH_SHORT
        } else {
            Log.e(TAG, "atualizarFotoUsuario: Falha ao atualizar foto do motorista no Firebase (método interno usuarioFirebase).");
            Toast.makeText(configuracoesMotorista.this, "Erro ao atualizar foto de perfil do motorista.", Toast.LENGTH_SHORT).show();
        }
    }

    private void alertaValidacaoPermissao() { // Nome original
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar as funcionalidades de foto e câmera, é necessário aceitar as permissões de câmera e armazenamento.\n\nPor favor, vá para as configurações do aplicativo e conceda as permissões manualmente.");
        builder.setCancelable(false);
        builder.setPositiveButton("Configurações", (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    public void imagemCamera(View view) { // Nome original
        Log.d(TAG, "imagemCamera: Botão da câmera clicado.");
        if (Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES)) { // Nome original
            Log.d(TAG, "imagemCamera: Permissões de câmera/armazenamento OK. Iniciando Intent.");
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(i, SELECAO_CAMERA); // Nome original
            } else {
                Log.e(TAG, "imagemCamera: Nenhuma Activity encontrada para MediaStore.ACTION_IMAGE_CAPTURE.");
                Toast.makeText(this, "Nenhum aplicativo de câmera encontrado.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w(TAG, "imagemCamera: Permissões não concedidas. Solicitação de permissões será feita ou alerta exibido.");
        }
    }

    public void imagemGaleria(View view) { // Nome original
        Log.d(TAG, "imagemGaleria: Botão da galeria clicado.");
        if (Permissao.validarPermissoes(permissoesNecessarias, this, REQUEST_CODE_PERMISSOES)) { // Nome original
            Log.d(TAG, "imagemGaleria: Permissões de leitura/armazenamento OK. Iniciando Intent.");
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (i.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(i, SELECAO_GALERIA); // Nome original
            } else {
                Log.e(TAG, "imagemGaleria: Nenhuma Activity encontrada para Intent.ACTION_PICK.");
                Toast.makeText(this, "Nenhum aplicativo de galeria encontrado.", Toast.LENGTH_LONG).show();
            }
        } else {
            Log.w(TAG, "imagemGaleria: Permissões não concedidas. Solicitação de permissões será feita ou alerta exibido.");
        }
    }

    public void botaoSalvarAlteracaoMotorista(View view) { // Nome original
        salvarAlteracoesMotorista(); // Nome original
    }

    public void salvarAlteracoesMotorista() { // Nome original
        String nome = editeConfgNomeMotorista.getText().toString(); // Nome original
        String telefone = editeConfigTelefoneMotorista.getText().toString(); // Nome original
        String cor = editeCarroCor.getText().toString(); // Nome original
        String modelo = editeCarroModelo.getText().toString(); // Nome original
        String placa = editeCarroPlaca.getText().toString(); // Nome original
        String quantPasStr = editeQuantVagaMotorista.getText().toString(); // Nome original

        if (nome.isEmpty() || telefone.isEmpty() || cor.isEmpty() || modelo.isEmpty() || placa.isEmpty() || quantPasStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantVagas; // Nome original
        try {
            quantVagas = Integer.parseInt(quantPasStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Quantidade de vagas inválida. Insira um número.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "salvarAlteracoesMotorista: Erro de formato numérico para vagas: " + e.getMessage());
            return;
        }

        // It's crucial that usuarioLogado is correctly initialized and updated from DB.
        if (usuarioLogado == null) {
            Log.e(TAG, "salvarAlteracoesMotorista: usuarioLogado é nulo. Não foi possível salvar as alterações.");
            Toast.makeText(this, "Erro: Dados do motorista não carregados. Tente novamente.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean nomeAtualizadoAuth = usuarioFirebase.atualizarNomeUsuario(nome); // Nome original

        usuarioLogado.setNome(nome);
        usuarioLogado.setTelefone(telefone);
        usuarioLogado.setCor(cor);
        usuarioLogado.setPlaca(placa);
        usuarioLogado.setModeloCarro(modelo);
        usuarioLogado.setQuantPassageiro(quantVagas);
        // Ensure that 'horaSaida' and 'status' are preserved if not being edited on this screen.
        // They should already be set in the 'usuarioLogado' object when 'carregarDadosUsuarioMotorista' runs.
        // As per previous instruction, no new methods/properties should be added to UsuarioMotorista.
        // Assuming your UsuarioMotorista class already has getHoraSaida() and getStatus()
        // and these are loaded by carregarDadosUsuarioMotorista().
        // If not, and they are essential, you might need to re-evaluate the UsuarioMotorista model.
        usuarioLogado.setHoraSaida(usuarioLogado.getHoraSaida()); // Retains current value
        usuarioLogado.setStatus(usuarioLogado.getStatus());     // Retains current value

        usuarioLogado.atualizar(); // Nome original

        if (nomeAtualizadoAuth) {
            Log.d(TAG, "salvarAlteracoesMotorista: Dados do motorista (nome, telefone, carro) salvos com sucesso.");
            Toast.makeText(configuracoesMotorista.this,
                    "Dados alterados com sucesso",
                    Toast.LENGTH_SHORT).show(); // Changed to LENGTH_SHORT

            // Navigate back to motorista.class
            Intent intent = new Intent(getApplicationContext(), motorista.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        } else {
            Log.e(TAG, "salvarAlteracoesMotorista: Falha ao atualizar nome do usuário no Firebase Auth, mas dados do DB podem ter sido salvos.");
            Toast.makeText(configuracoesMotorista.this,
                    "Não foi possível alterar o nome de usuário no Firebase Auth, mas outros dados foram salvos.",
                    Toast.LENGTH_LONG).show();
        }
    }
}