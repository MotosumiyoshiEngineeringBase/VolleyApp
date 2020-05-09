package base.engineering.motosumiyoshi.volleymanageapp.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import base.engineering.motosumiyoshi.volleymanageapp.MainActivity;
import base.engineering.motosumiyoshi.volleymanageapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final static String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        // 端末＋アプリを一意に識別するためのトークンを取得
        Log.i("FIREBASE", "[SERVICE] Token = " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage != null) {
            // 通知メッセージ
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            if (notification != null) {
                // 通知メッセージを処理
            }

            // データメッセージ
            /////////
            String from = remoteMessage.getFrom();
            Log.d(TAG, "from:" + from);

            String msg = "hogemoge";
            Map<String, String> data = remoteMessage.getData();
            if (data != null) {
                if (data.get("data") != null) {
                    msg = data.get("data").toString();
                }
            }
            Log.d(TAG, "data:" + data.toString());

            sendNotification(msg);
        }
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 , intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
               .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Push通知のタイトル")
                .setSubText("Push通知のサブタイトル")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
    }
}
