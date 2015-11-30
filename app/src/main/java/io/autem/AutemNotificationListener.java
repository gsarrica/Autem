package io.autem;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

/**
 * Created by gsarrica on 11/29/15.
 */
public class AutemNotificationListener extends NotificationListenerService {

    public static String TAG = "AutemNotificationListener";


    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "Inside on create");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG,"**********  onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText + "\t" + sbn.getPackageName());
        if(sbn != null && "com.google.android.apps.messaging".equalsIgnoreCase(sbn.getPackageName())) {

            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String apiKey = sharedPreferences.getString(AutemPreferences.API_KEY, "no-key");
            String chromeToken = sharedPreferences.getString(AutemPreferences.CHROME_TOKEN, "no-key");


            String[] split = sbn.getNotification().tickerText.toString().split(":");
            if(split != null && split.length > 0) {
                SendMessageService sendMessageService = new SendMessageService();
                sendMessageService.sendMessage(apiKey, chromeToken, split[0], split[1]);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
    }
}
