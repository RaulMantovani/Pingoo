package br.pingoo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String titulo = intent.getStringExtra("titulo");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String canalId = "canal_pingoo";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, "Pingoo Notificações", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(canal);
        }

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(context, canalId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // substitua pelo seu ícone
                .setContentTitle("Lembrete de Estudo 📚")
                .setContentText(titulo)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), notificacao.build());
    }
}
