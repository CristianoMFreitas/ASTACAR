package com.taxicaraubas.astacar.model;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.taxicaraubas.astacar.R;
import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Requisicao implements Serializable {


    private String idRequisicao;
    private String idPassageiro;
    private String idMotorista;
    private String nomePassageiro;
    private String fotoPassageiro;
    private String telefonePassageiro;
    private String destino;
    private String rua;
    private String bairro;
    private String numero;
    private String proximo;
    private String status;
    public static Boolean tocarNotificacao;

    public static final String STATUS_AGUARDANDO = "aguardando";
    public static final String STATUS_ESPERANDO_RESPOSTA = "esperandoMotorista";
    public static final String STATUS_MOTORISTA_ACEITOU = "inicioViajem";
    public static final String STATUS_FINALIZADA = "finalizada";
    public static final String STATUS_CANCELADA = "cancelada";

    public Requisicao() {
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");

        String idRequisicao = requisicoes.push().getKey();
        setIdRequisicao(idRequisicao);

        requisicoes.child(getIdRequisicao()).setValue(this);

    }

    public void atualizar(String identificadorUsuario){
        DatabaseReference dataBase = ConfiguracaoFirebase.getFirebaseDatabase();

        DatabaseReference usuarioRef = dataBase.child("requisicoes")
                .child(identificadorUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();

        usuarioRef.updateChildren( valoresUsuario );
    }

    //atualizar dados do ususario no firebase

    @Exclude
    public Map<String , Object> converterParaMap (){
        HashMap<String, Object> usuarioMap = new HashMap<>();

        //não adicionar campos que não precisam de atualização.
        usuarioMap.put("status", getStatus());
        usuarioMap.put("idMotorista", getIdMotorista());

        return usuarioMap;
    }

    public void recuperarRequisicao (){

        final DatabaseReference requisicaoAtual = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("requisicoes")
                .child(getIdRequisicao());

        requisicaoAtual.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Requisicao requisicaoDados = dataSnapshot.getValue(Requisicao.class);

                setBairro(requisicaoDados.getBairro());
                setRua(requisicaoDados.getRua());
                setNumero(requisicaoDados.getNumero());
                setDestino(requisicaoDados.getDestino());
                setFotoPassageiro(requisicaoDados.getFotoPassageiro());
                setNomePassageiro(requisicaoDados.getNomePassageiro());
                setProximo(requisicaoDados.getProximo());
                setStatus(requisicaoDados.getStatus());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void cancelarRequisicao (){
        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        setIdRequisicao(autenticacao.getUid());

    }

    //INICIO GETS AND SETS

    public String getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(String idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public String getIdPassageiro() {
        return idPassageiro;
    }

    public void setIdPassageiro(String idPassageiro) {
        this.idPassageiro = idPassageiro;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getProximo() {
        return proximo;
    }

    public void setProximo(String proximo) {
        this.proximo = proximo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNomePassageiro() {
        return nomePassageiro;
    }

    public void setNomePassageiro(String nomePassageiro) {
        this.nomePassageiro = nomePassageiro;
    }

    public String getFotoPassageiro() {
        return fotoPassageiro;
    }

    public void setFotoPassageiro(String fotoPassageiro) {
        this.fotoPassageiro = fotoPassageiro;
    }

    public String getIdMotorista() {
        return idMotorista;
    }

    public void setIdMotorista(String idMotorista) {
        this.idMotorista = idMotorista;
    }

    public String getTelefonePassageiro() {
        return telefonePassageiro;
    }

    public void setTelefonePassageiro(String telefonePassageiro) {
        this.telefonePassageiro = telefonePassageiro;
    }

    public void notificacao(Context context){

        //Configurações para notificação
        String canal = "com.example.notificacaoteste";
        Uri uriSOm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone toque = RingtoneManager.getRingtone(context, uriSOm);
        toque.play();

        //Criar notificação
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(context, canal)
                .setContentTitle("ASTACAR")
                .setContentText("Você tem um novo passageiro")
                .setSmallIcon(R.drawable.icone_pessoa);

        //Recuperando NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //verifica versão do android para criar ou não um canal de notificação.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(canal ,"canal",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //enviando notificacao
        notificationManager.notify(0,notificacao.build());
        tocarNotificacao = false;
    }
}
