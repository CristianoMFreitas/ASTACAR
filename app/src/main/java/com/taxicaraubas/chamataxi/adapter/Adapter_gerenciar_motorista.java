package com.taxicaraubas.chamataxi.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.taxicaraubas.chamataxi.R;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;

import java.util.List;

public class Adapter_gerenciar_motorista extends RecyclerView.Adapter<Adapter_gerenciar_motorista.MyViewHolder> {

    private List<UsuarioMotorista> motoristaAdapter;
    private Context contextAdapter;

    public Adapter_gerenciar_motorista(List<UsuarioMotorista> motoristaAdapter, Context contextAdapter) {
        this.motoristaAdapter = motoristaAdapter;
        this.contextAdapter = contextAdapter;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View passageiros = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_lista_admin, parent, false
        );
        return new MyViewHolder(passageiros);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        UsuarioMotorista motorista = motoristaAdapter.get(position);

        if (motorista.getStatus() != null) {
            if (motorista.getStatus().equals(UsuarioMotorista.STATUS_INDISPONIVEL)) {
                holder.nomeMot.setTextColor(Color.RED);
                holder.status.setTextColor(Color.RED);
                holder.telefone.setTextColor(Color.RED);
            }
        }

        holder.nomeMot.setText(motorista.getNome());
        holder.status.setText(motorista.getStatus());
        holder.telefone.setText(motorista.getTelefone());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                confirmarExclusaoMotorista(holder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return motoristaAdapter.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nomeMot, telefone, status;
        ImageView foto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeMot = itemView.findViewById(R.id.adpterAdminNomeMotorista);
            telefone = itemView.findViewById(R.id.adpterAdminTelefone);
            status = itemView.findViewById(R.id.adapterListaAdminStatusMotorista);
        }
    }

    private void confirmarExclusaoMotorista(final int position) {
        if (contextAdapter instanceof Activity) {
            Activity activity = (Activity) contextAdapter;
            if (!activity.isFinishing() && !activity.isDestroyed()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Confirmar Exclusão");
                builder.setMessage("Você tem certeza que deseja excluir este motorista?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        excluirMotorista(position);
                    }
                });
                builder.setNegativeButton("Não", null);
                builder.show();
            }
        }
    }




    private void excluirMotorista(int position) {
        UsuarioMotorista motorista = motoristaAdapter.get(position);
        motorista.deletar();
        motoristaAdapter.remove(position);
        notifyItemRemoved(position);
        Toast.makeText(contextAdapter, "Motorista excluído com sucesso!", Toast.LENGTH_LONG).show();
    }
}
