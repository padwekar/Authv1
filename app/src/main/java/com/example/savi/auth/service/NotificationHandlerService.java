package com.example.savi.auth.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.savi.auth.R;
import com.example.savi.auth.activity.HomeActivity;
import com.example.savi.auth.constant.IntentConstant;
import com.example.savi.auth.utils.AuthApplication;
import com.example.savi.auth.utils.AuthPreferences;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class NotificationHandlerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        boolean isUserLoggedIn = AuthPreferences.getInstance().getLoginStatus();
        String loggedUserUid = AuthPreferences.getInstance().getUserUid();

        String forUser = remoteMessage.getData().get("for");

        if(!isUserLoggedIn || !loggedUserUid.equals(forUser)){
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(IntentConstant.INTENT_KEY_FRIEND_REQUEST,true);

        PendingIntent pendingIntent = PendingIntent.getActivity(AuthApplication.getInstance(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(getApplicationContext());
        builder.setContentTitle("Authv");
        builder.setContentText(remoteMessage.getData().get("body"));
        builder.setSmallIcon(R.drawable.logo_lifesavi);
        builder.setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(remoteMessage.hashCode(),builder.build());

    }



}
