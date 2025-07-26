package com.taxicaraubas.chamataxi.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.model.Requisicao;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<UsuarioMotorista> motoristaAdapter;
    private Context contextAdapter;
    private Requisicao requisicaoAdapter;
    private String idRequisicao;

    public Adapter(List<UsuarioMotorista> motoristaAdapter, Context contextAdapter, Requisicao requisicaoAdapter) {
        this.motoristaAdapter = motoristaAdapter;
        this.contextAdapter = contextAdapter;
        this.idRequisicao = idRequisicao;
    }

    @NonNull
    @Override //Método chamado para criar as visualizações. o nome do método corresponde a classe criada abaixo.
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TextView nome, hora;

        //Converter o xml em uma view
        View passageiros = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_lista, parent, false
        );
        return new MyViewHolder(passageiros);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UsuarioMotorista motorista = motoristaAdapter.get(position);

        holder.nome.setText(motorista.getNome());
        holder.hora.setText(motorista.getHoraSaida());
        holder.vagas.setText(Integer.toString(motorista.getVagas()));
    }

    @Override
    public int getItemCount() {

        return motoristaAdapter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //criando os campos que serão usados para exibir meus dados no xml.
        TextView nome, hora,vagas;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textViewNomeMotoristaLista);
            hora = itemView.findViewById(R.id.textViewHoraSaidaLista);
            vagas = itemView.findViewById(R.id.textViewVagasLista);
        }
    }
}
