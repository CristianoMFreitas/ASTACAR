package com.taxicaraubas.astacar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.model.Requisicao;
import com.taxicaraubas.astacar.model.UsuarioMotorista;

import java.util.List;

public class Adapter_gerenciar_motorista extends RecyclerView.Adapter<Adapter_gerenciar_motorista.MyViewHolder> {

    private List<UsuarioMotorista> motoristaAdapter;
    private Context contextAdapter;
    private Requisicao requisicaoAdapter;

    public Adapter_gerenciar_motorista(List<UsuarioMotorista> motoristaAdapter, Context contextAdapter) {
        this.motoristaAdapter = motoristaAdapter;
        this.contextAdapter = contextAdapter;
    }

    @NonNull
    @Override //Método chamado para criar as visualizações. o nome do método corresponde a classe criada abaixo.
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Converter o xml em uma view
        View passageiros = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_lista_admin, parent, false
        );
        return new MyViewHolder(passageiros);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UsuarioMotorista motorista = motoristaAdapter.get(position);
       if (motorista.getStatus() != null){
           if (motorista.getStatus().equals(UsuarioMotorista.STATUS_INDISPONIVEL)) {
               holder.nomeMot.setTextColor(Color.RED);
               holder.status.setTextColor(Color.RED);
               holder.telefone.setTextColor(Color.RED);
           }

       }

           holder.nomeMot.setText(motorista.getNome());
           holder.status.setText(motorista.getStatus());
           holder.telefone.setText(motorista.getTelefone());

    }

    @Override
    public int getItemCount() {

        return motoristaAdapter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        //criando os campos que serão usados para exibir meus dados no xml.
        TextView nomeMot, telefone,status;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMot = itemView.findViewById(R.id.adpterAdminNomeMotorista);
            telefone = itemView.findViewById(R.id.adpterAdminTelefone);
            status = itemView.findViewById(R.id.adapterListaAdminStatusMotorista);
        }
    }
}
