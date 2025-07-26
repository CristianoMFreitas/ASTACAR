package com.taxicaraubas.chamataxi.model;

import android.util.Log;

import com.taxicaraubas.chamataxi.aplicacoes.usuarioFirebase;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class UsuarioPassageiro implements Serializable {

    private String idUsuario, nome, cpf, telefone, email, senha;
    String foto;
    String tipo = "passageiro";
    FirebaseAuth autenticacao;
    private UsuarioPassageiro usuarioLogado;
    DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();

    public UsuarioPassageiro() {

    }

    public void salvar(){
        firebase.child("usuarioPassageiro")
                .child(this.idUsuario)
                .setValue(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FIREBASE", "Usuário salvo com sucesso!");
                    } else {
                        Log.e("FIREBASE", "Erro ao salvar usuário", task.getException());
                    }
                });
    }

    public void atualizar(){
        String identificadorUsuario = usuarioFirebase.usuario().getUid();
        DatabaseReference dataBase = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = dataBase.child("usuarioPassageiro")
                .child(identificadorUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        Task<Void> verificarAtualizar = usuarioRef.updateChildren( valoresUsuario );

    }

        //atualizar dados do ususario no firebase

    @Exclude
    public Map<String , Object> converterParaMap (){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        //não adicionar campos que não precisam de atualização.
        usuarioMap.put("nome", getNome());
        usuarioMap.put("telefone", getTelefone());
        usuarioMap.put("foto", getFoto())
        ;
        return usuarioMap;
    }
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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

    @Exclude
    public String getSenha() {

        return senha;
    }

    public void setSenha(String senha) {

        this.senha = senha;
    }



}
