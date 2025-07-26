package com.taxicaraubas.chamataxi.aplicacoes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.taxicaraubas.chamataxi.R;

public class Notificacao extends AppCompatActivity {

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
    }
}
