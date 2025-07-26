package com.taxicaraubas.chamataxi.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.taxicaraubas.chamataxi.aplicacoes.usuarioFirebase;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UsuarioMotorista implements Serializable {

    String nome, cpf, telefone, email, placa, modeloCarro, cor, foto, senha, idMotorista, status, statusEncomenda, horaSaida, destino;
    String tipo = "motorista";
    int quantPassageiro, vagas;

    public static final String STATUS_DISPONIVEL = "disponivel";
    public static final String STATUS_INDISPONIVEL = "indisponivel";

   public void salvar(){
       DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarioMotorista")
                .child(this.idMotorista)
                .setValue(this);
    }

    public void atualizar(){

        String identificadorMot = usuarioFirebase.usuario().getUid();
        DatabaseReference dataBase = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = dataBase.child("usuarioMotorista")
                .child(identificadorMot);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuarioRef.updateChildren( valoresUsuario );
    }

//    public void deletar() {
//        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
//        firebase.child("usuarioMotorista")
//                .child(this.idMotorista)
//                .removeValue();
//    }

    public void deletar() {
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarioMotorista")
                .child(this.idMotorista)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Firebase", "Motorista excluído com sucesso.");
                        } else {
                            Log.d("Firebase", "Falha ao excluir motorista.");
                        }
                    }
                });
    }

    @Exclude
    public Map<String , Object> converterParaMap (){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        //não adicionar campos que não precisam de atualização.
        usuarioMap.put("nome", getNome());
        usuarioMap.put("telefone", getTelefone());
        usuarioMap.put("foto", getFoto());
        usuarioMap.put("cor", getCor());
        usuarioMap.put("placa", getPlaca());
        usuarioMap.put("quantPassageiro", getQuantPassageiro());
        usuarioMap.put("status", getStatus());
        usuarioMap.put("statusEncomenda" , getStatusEncomenda());
        usuarioMap.put("modeloCarro", getModeloCarro());
        usuarioMap.put("horaSaida", getHoraSaida())
        ;
        return usuarioMap;
    }

    public void atualizarVaga(){

        String identificadorMot = usuarioFirebase.usuario().getUid();
        DatabaseReference dataBase = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = dataBase.child("usuarioMotorista")
                .child(identificadorMot);

        Map<String, Object> valoresUsuario = converterParaMapVagas();

        usuarioRef.updateChildren( valoresUsuario );
    }


    @Exclude
    public Map<String , Object> converterParaMapVagas (){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        //não adicionar campos que não precisam de atualização.
        usuarioMap.put("vagas", getVagas());
        return usuarioMap;
    }

    public void alterarStatus(){

        String identificadorMot = usuarioFirebase.usuario().getUid();
        DatabaseReference dataBase = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = dataBase.child("usuarioMotorista")
                .child(identificadorMot);

        Map<String, Object> valoresUsuario = converterParaMapFicarOnline();

        usuarioRef.updateChildren( valoresUsuario );
    }


    @Exclude
    public Map<String , Object> converterParaMapFicarOnline (){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        //não adicionar campos que não precisam de atualização.

        usuarioMap.put("status", getStatus());
        usuarioMap.put("statusEncomenda", getStatusEncomenda());
        usuarioMap.put("horaSaida", getHoraSaida());
        usuarioMap.put("destino", getDestino());
        usuarioMap.put("vagas", getVagas());
        return usuarioMap;
    }

    public void motoristaOffLine (){

        setIdMotorista(usuarioFirebase.usuario().getUid());
        setStatus(UsuarioMotorista.STATUS_INDISPONIVEL);
        setStatusEncomenda(UsuarioMotorista.STATUS_INDISPONIVEL);
        setDestino("");
        setHoraSaida("");
        setVagas(0);

        alterarStatus();
    }


    public String getTipo() {
        return tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModeloCarro() {
        return modeloCarro;
    }

    public void setModeloCarro(String modeloCarro) {
        this.modeloCarro = modeloCarro;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public int getQuantPassageiro() {
        return quantPassageiro;
    }

    public void setQuantPassageiro(int quantPassageiro) {
        this.quantPassageiro = quantPassageiro;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(String horaSaida) {
        this.horaSaida = horaSaida;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdMotorista() {
        return idMotorista;
    }

    public void setIdMotorista(String idMotorista) {
        this.idMotorista = idMotorista;
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        this.vagas = vagas;
    }

    public String getStatusEncomenda() {
        return statusEncomenda;
    }

    public void setStatusEncomenda(String statusEncomenda) {
        this.statusEncomenda = statusEncomenda;
    }
}
