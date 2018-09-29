package com.example.suelliton.agita.utils;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.ImageView;

import com.example.suelliton.agita.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Classe utilitária para criar notificações.
 * @Author Lecheta
 * http://developer.android.com/guide/topics/ui/notifiers/notifications.html
 */
public class NotificationUtil {

    public static final String ACTION_VISUALIZAR = "br.com.ufrn.eaj.tads.notificationexample.ACTION_VISUALIZAR";

    private static PendingIntent getPendingIntent(Context context, Intent intent, int id) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(intent.getComponent());
        stackBuilder.addNextIntent(intent);
        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return p;
    }

    public static void create(Context context, Intent intent, String contentTitle, String contentText, int id) {
        PendingIntent p = getPendingIntent(context, intent, id);

        // Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); // Ativa configurações padrão
        b.setSmallIcon(R.drawable.ic_notification_icon); // Ícone
        b.setContentTitle(contentTitle); // Título
        b.setContentText(contentText); // Mensagem
        b.setContentIntent(p); // Intent que será chamada ao clicar na notificação.
        b.setAutoCancel(true); // Auto cancela a notificação ao clicar nela

        b.setColor(Color.GREEN);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, b.build());
    }

    public static void createBig(Context context, Intent intent, String contentTitle, String contentText,List<String> lines, int id) {
        PendingIntent p = getPendingIntent(context, intent, id);

        // Configura o estilo Inbox
        int size = lines.size();
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentTitle);
        for (String s: lines) {
            inboxStyle.addLine(s);
        }
        inboxStyle.setSummaryText(contentText);

        // Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); // Ativa configurações padrão
        b.setSmallIcon(R.drawable.ic_notification_icon); // Ícone
        b.setContentTitle(contentTitle); // Título
        b.setContentText(contentText); // Mensagem
        b.setContentIntent(p); // Intent que será chamada ao clicar na notificação.
        b.setAutoCancel(true); // Auto cancela a notificação ao clicar nela
        b.setNumber(size); // Número para aparecer na notificação
        b.setStyle(inboxStyle); // Estilo customizado

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, b.build());
    }

    public static void createWithAction(Context context, Intent intent, String contentTitle, String contentText, int id) {
        PendingIntent p = getPendingIntent(context, intent, id);

        // Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); // Ativa configurações padrão
        b.setSmallIcon(R.drawable.ic_notification_icon); // Ícone
        b.setContentTitle(contentTitle); // Título
        b.setContentText(contentText); // Mensagem
        b.setContentIntent(p); // Intent que será chamada ao clicar na notificação.
        b.setAutoCancel(true); // Auto cancela a notificação ao clicar nela

        // Ação customizada (deixei a mesma intent para os dois)
        PendingIntent actionIntent = PendingIntent.getBroadcast(
                context, 0, new Intent(ACTION_VISUALIZAR), 0);
        b.addAction(R.drawable.ic_action_like, "Pause", actionIntent);
        b.addAction(R.drawable.ic_action_like, "Play", actionIntent);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, b.build());
    }

    // Notification no Android 5.0 Lollipop (Cor vermelha e heads-up e tela de bloqueio)
    public static void createHeadsUpNotification(Context context, Intent intent, String contentTitle, String contentText, int id) {
        PendingIntent p = getPendingIntent(context, intent, id);

        // Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); // Ativa configurações padrão
        b.setSmallIcon(R.drawable.ic_notification_icon); // Ícone
        b.setContentTitle(contentTitle); // Título
        b.setContentText(contentText); // Mensagem
        b.setContentIntent(p); // Intent que será chamada ao clicar na notificação.
        b.setAutoCancel(true); // Auto cancela a notificação ao clicar nela
        // No Android 5.0
        b.setColor(Color.RED);
        // Heads-up notification
        b.setFullScreenIntent(p,false);
        // Privada se estiver na tela de bloqueio
        b.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);



        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, b.build());
    }

    // Notification no Android 5.0 Lollipop (tela de bloqueio)
    public static void createPrivateNotification(Context context, Intent intent, String contentTitle, String contentText, int id) {
        PendingIntent p = getPendingIntent(context, intent, id);

        // Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); // Ativa configurações padrão
        b.setSmallIcon(R.drawable.ic_notification_icon); // Ícone
        b.setContentTitle(contentTitle); // Título
        b.setContentText(contentText); // Mensagem
        b.setContentIntent(p); // Intent que será chamada ao clicar na notificação.
        b.setAutoCancel(true); // Auto cancela a notificação ao clicar nela

        // PUBLIC, PRIVATE, SECRET
        b.setVisibility(NotificationCompat.VISIBILITY_SECRET);

        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.notify(id, b.build());
    }

    public static void cancell(Context context, int id) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancel(id);
    }

    public static void cancellAll(Context context) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancelAll();
    }
}