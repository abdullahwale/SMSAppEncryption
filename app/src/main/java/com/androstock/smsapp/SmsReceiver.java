/**
 * Copyright(C) 2013 Brightechno Inc.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.androstock.smsapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    private int mNotificationId = 101;
    @Override
    public void onReceive(Context context, Intent intent) {
       // Toast.makeText(context, "new sms heheh", Toast.LENGTH_SHORT).show();
        Bundle extras = intent.getExtras();

        if (extras == null) {
            return;
        }

        Object[] smsExtras = (Object[]) extras.get(SmsConstant.PDUS);

        ContentResolver contentResolver = context.getContentResolver();
        Uri smsUri = Uri.parse(SmsConstant.SMS_URI);

        for (Object smsExtra : smsExtras) {
            byte[] smsBytes = (byte[]) smsExtra;

            SmsMessage smsMessage = SmsMessage.createFromPdu(smsBytes);

            String body = smsMessage.getMessageBody();

            String address = smsMessage.getOriginatingAddress();
         //   Toast.makeText(context, "Sms aya hai", Toast.LENGTH_SHORT).show();
            String senderName = smsMessage.getDisplayOriginatingAddress();
            String senderNo = smsMessage.getOriginatingAddress();
            String message = smsMessage.getDisplayMessageBody();
            ContentValues values = new ContentValues();
            values.put(SmsConstant.COLUMN_ADDRESS, address);
            values.put(SmsConstant.COLUMN_BODY, body);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForeground2(context,senderName, senderNo, message);
            }
            else {


                issueNotification(context,senderName, senderNo, message);
            }

            Uri uri = contentResolver.insert(smsUri, values);

            // TODO: implement notification
        }
    }

    private void issueNotification(Context context, String senderName ,String senderNo, String message) {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
       // int notifyID = 1;
       // String CHANNEL_ID = "my_channel_01";// The id of the channel.
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setLargeIcon(icon)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(senderNo)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                        .setAutoCancel(true)
                        .setSound(soundUri) //This sets the sound to play
                        .setContentText(message);

        Intent resultIntent = new Intent(context, MainActivity.class);
     //   resultIntent.putExtra("name", senderName);
      //  resultIntent.putExtra("address",senderNo);
      //  resultIntent.putExtra("thread_id", senderNo);




        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }


    public void startForeground2(Context context, String senderName ,String senderNo, String message)
    {
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // int notifyID = 1;
        // String CHANNEL_ID = "my_channel_01";// The id of the channel.
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager)
               context. getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);
        int notificationId = 1;
        String channelId = "channel-01";
        String channelName = "Channel Name";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setLargeIcon(icon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(senderNo)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(soundUri) //This sets the sound to play
                .setContentText(message);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        //  notificationManager.notify(notificationId, mBuilder.build());
     //   startForeground(1337, mBuilder.build());
        notificationManager.notify(mNotificationId, mBuilder.build());










    }
}
