package com.taxicaraubas.chamataxi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.model.Requisicao;

public class SalaDeEspera extends AppCompatActivity {

    private TextView endereco;

    private String rua;
    private String num;
    private String bairro;
    private String proximo;
    private String destino;
    private String idRequisicao;
    private Requisicao requisicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sala_de_espera);

        endereco = findViewById(R.id.textViewEnderecoRequisicao);

        Bundle extras = getIntent().getExtras();
        requisicao = (Requisicao) extras.getSerializable("requisicaoEnvia");

        if (extras != null) {
            rua = requisicao.getRua();
            num = requisicao.getNumero();
            bairro = requisicao.getBairro();
            proximo = requisicao.getProximo();
            destino = requisicao.getDestino();
            idRequisicao = requisicao.getIdRequisicao();

            String textoEndereco = "Você esta indo para "+ destino + "\nRua: " + rua + ", N.º" + num + "\nBairro: " + bairro + "\nPróximo: "
                    + proximo;
            endereco.setText(textoEndereco);
        }
    }

    public void botaoEscolherMotorista (View view){
        Intent intent = new Intent(getApplicationContext(), listaMotorista.class);
        intent.putExtra("requisicaoEnvia", requisicao);
        startActivity(intent);
    }

    public void botaoCancelarViajem (View view){

            requisicao.setStatus(Requisicao.STATUS_FINALIZADA);
            requisicao.atualizar(idRequisicao);

            Intent intent = new Intent(getApplicationContext(), pedirCarro.class);
            startActivity(intent);
    }
}
