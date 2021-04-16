package com.taxicaraubas.astacar.model;

import com.taxicaraubas.astacar.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

public class CarroLotacao extends Requisicao{
    private String idMotorista;
    private String idRequisicao;
    private String statusPassageiro;

    public CarroLotacao(String idMotorista, String idRequisicao) {
        this.idMotorista = idMotorista;
        this.idRequisicao = idRequisicao;
    }

    public String getIdMotorista() {
        return idMotorista;
    }

    public void setIdMotorista(String idMotorista) {
        this.idMotorista = idMotorista;
    }

    public String getIdRequisicao() {
        return idRequisicao;
    }

    public void setIdRequisicao(String idRequisicao) {
        this.idRequisicao = idRequisicao;
    }

    public String getStatusPassageiro() {
        return statusPassageiro;
    }

    public void setStatusPassageiro(String statusPassageiro) {
        this.statusPassageiro = statusPassageiro;
    }

    public static final String ESPERANDO_RESPOSTA = "aguardando";
    public static final String MOTORISTA_ACEITOU = "aceitou";
    public static final String MOTORISTA_REJEITOU = "aceitouNao";

    //metodos ------------
    public void addPassageiro(){
        DatabaseReference firebase = ConfiguracaoFirebase.getFirebaseDatabase();
        firebase.child("usuarioMotorista")
                .child(this.idMotorista)
                .child("carroLotacao")
                .child(idRequisicao)
                .setValue(ESPERANDO_RESPOSTA);

    }
}
