package com.taxicaraubas.chamataxi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.Requisicao;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;
import com.google.firebase.auth.FirebaseAuth;


import java.util.List;

public class Adapter_carro_lotacao extends RecyclerView.Adapter<Adapter_carro_lotacao.MyViewHolder> {

    private List<Requisicao> requisicao;
    private Context contextAdapter;
    private int cont = 0;
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private int vagas;
    private UsuarioMotorista motorista = new UsuarioMotorista();

    public Adapter_carro_lotacao(List<Requisicao> requisicao, Context contextAdapter, int vagasReceber) {
        this.requisicao = requisicao;
        this.contextAdapter = contextAdapter;
        this.vagas = vagasReceber;
    }

    @NonNull
    @Override //Método chamado para criar as visualizações. o nome do método corresponde a classe criada abaixo.
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Converter o xml em uma view
        View passageiros = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_meus_passageiros, parent, false
        );
        return new MyViewHolder(passageiros);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Requisicao requisicaoAdapter = requisicao.get(position);
        Requisicao.tocarNotificacao = false;

        holder.nomePassageiro.setText(requisicaoAdapter.getNomePassageiro());
        holder.endRua.setText(requisicaoAdapter.getRua());
        holder.endBairro.setText(requisicaoAdapter.getBairro());
        holder.endNum.setText(requisicaoAdapter.getNumero());
        holder.endDestino.setText(requisicaoAdapter.getDestino());
        holder.proximo.setText(requisicaoAdapter.getProximo());

        if (requisicaoAdapter.getStatus().equals(Requisicao.STATUS_MOTORISTA_ACEITOU)){
            holder.endRua.setTextColor(Color.BLUE);
            holder.endBairro.setTextColor(Color.BLUE);
            holder.endNum.setTextColor(Color.BLUE);
            holder.endDestino.setTextColor(Color.BLUE);
            holder.proximo.setTextColor(Color.BLUE);

            cont++;

            if (cont >= vagas){

                motorista.motoristaOffLine();

            }else {

                motorista.setVagas(vagas - 1);
                motorista.atualizarVaga();
            }

        }

        if (requisicaoAdapter.getStatus().equals(Requisicao.STATUS_ESPERANDO_RESPOSTA)){
            holder.nomePassageiro.setTextColor(Color.RED);
            holder.endRua.setTextColor(Color.RED);
            holder.endBairro.setTextColor(Color.RED);
            holder.endNum.setTextColor(Color.RED);
            holder.endDestino.setTextColor(Color.RED);
            holder.proximo.setTextColor(Color.RED);
            //gerarNotificacao();
            Requisicao.tocarNotificacao = true;
        }




    }

    @Override
    public int getItemCount() {
        return requisicao.size();

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //criando os campos que serão usados para exibir meus dados no xml.

        TextView nomePassageiro, endRua, endBairro, endNum, endDestino, telefone, proximo;
        ImageView botao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomePassageiro = itemView.findViewById(R.id.lotacaoNomePassageiro);
            endRua = itemView.findViewById(R.id.lotacaoEnderecoRua);
            endBairro = itemView.findViewById(R.id.lotacaoEnderecoBairro);
            endNum = itemView.findViewById(R.id.lotacaoeEnderecoNum);
            endDestino = itemView.findViewById(R.id.lotacaoEnderecoCidade);
            proximo = itemView.findViewById(R.id.lotacaoEndereçoProximo);

        }
    }

   /* public void gerarNotificacao(){

        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone toque = RingtoneManager.getRingtone(contextAdapter, som);
        toque.play();

    }*/

}
