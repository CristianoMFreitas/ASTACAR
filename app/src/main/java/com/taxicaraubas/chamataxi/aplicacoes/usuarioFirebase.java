package com.taxicaraubas.chamataxi.aplicacoes;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.taxicaraubas.chamataxi.config.ConfiguracaoFirebase;
import com.taxicaraubas.chamataxi.model.UsuarioMotorista;
import com.taxicaraubas.chamataxi.model.UsuarioPassageiro;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class usuarioFirebase {

    public static FirebaseUser usuario(){
        FirebaseAuth user = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return user.getCurrentUser();
    }

    public static boolean atualizarNomeUsuario (String nome){
        try {
            FirebaseUser userAtual = usuario();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(nome)
                    .build();
            userAtual.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){

                    }
                }
            });

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean atulizarFotousuario (Uri url){

    try {
        FirebaseUser user = usuario();
        UserProfileChangeRequest profile = new UserProfileChangeRequest
                .Builder()
                .setPhotoUri(url)
                .build();
        user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){

                }
            }
        });
        return true;
    }catch (Exception e){
        e.printStackTrace();
        return false;
    }
    }

    public static UsuarioPassageiro getDadosUsuarioLogado(){
        FirebaseUser firebaseUser = usuario();
        UsuarioPassageiro usuario = new UsuarioPassageiro();

        UsuarioPassageiro usuarioAux = new UsuarioPassageiro();

        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome( firebaseUser.getDisplayName());


        if (firebaseUser.getPhotoUrl() == null){
            usuario.setFoto("");
        }else {
            usuario.setFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuario;
    }

    public static UsuarioMotorista getDadosUsuarioLogadoMotorista(){
        FirebaseUser firebaseUser = usuario();
        UsuarioMotorista usuarioMot = new UsuarioMotorista();

        usuarioMot.setEmail(firebaseUser.getEmail());
        usuarioMot.setNome( firebaseUser.getDisplayName());

        if (firebaseUser.getPhotoUrl() == null){
            usuarioMot.setFoto("");
        }else {
            usuarioMot.setFoto(firebaseUser.getPhotoUrl().toString());
        }
        return usuarioMot;
    }

    public static void excluirMotorista(String motoristaId, final OnCompleteListener<Void> onCompleteListener) {
        DatabaseReference motoristasRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarioMotorista").child(motoristaId);
        motoristasRef.removeValue().addOnCompleteListener(onCompleteListener);
    }
}


