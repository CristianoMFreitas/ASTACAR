package com.taxicaraubas.astacar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.model.Requisicao;

public class verPassageiro extends AppCompatActivity {

    private Bundle extras;
    private Requisicao requisicao = new Requisicao();
    private ImageView img;
    private TextView nome;
    private ProgressBar carregandoFoto;
    private int quantidadePassageiro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_passageiro);

        img = findViewById(R.id.verPassageiroImg);
        nome = findViewById(R.id.verPassageiroNome);
        carregandoFoto = findViewById(R.id.verPassageiroCarregando);

        extras = getIntent().getExtras();
        requisicao = (Requisicao) extras.getSerializable("requisicao");
        quantidadePassageiro = extras.getInt("quantPassageiro");

        nome.setText(requisicao.getNomePassageiro());

        if (requisicao.getFotoPassageiro() != null){

            carregandoFoto.setVisibility(View.VISIBLE);

            Uri url = Uri.parse(requisicao.getFotoPassageiro());
            Glide.with(verPassageiro.this)
                    .load(url)
                    .listener(new RequestListener<Uri, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {

                            carregandoFoto.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(img);


        }else {
            img.setImageResource(R.drawable.icone_pessoa);
        }
    }

    public void botaoIncluirPassageiro (View view){
        requisicao.setStatus(Requisicao.STATUS_MOTORISTA_ACEITOU);
        requisicao.atualizar(requisicao.getIdRequisicao());

        Intent intent = new Intent(getApplicationContext(), lotacaoCarro.class);
        intent.putExtra("enviarDestino",requisicao.getDestino());
        intent.putExtra("quantidadePassageiro",quantidadePassageiro);
        startActivity(intent);
        finish();
    }

    public void botaoExcluirPassageiro (View view){
        Requisicao.tocarNotificacao = false;

        requisicao.setStatus(Requisicao.STATUS_ESPERANDO_RESPOSTA);
        requisicao.setIdMotorista("");
        requisicao.atualizar(requisicao.getIdRequisicao());

        Intent intent = new Intent(getApplicationContext(), lotacaoCarro.class);
        intent.putExtra("enviarDestino",requisicao.getDestino());
        startActivity(intent);
    }

    public void ligarPassageiro (View view){
        Uri uri = Uri.parse("tel:" + requisicao.getTelefonePassageiro().toString());

        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(verPassageiro.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return;
            }
        }
        startActivity(intent);
    }
}
