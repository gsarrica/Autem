package io.autem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gsarrica on 12/1/15.
 */
public class AutemSmsReceiver extends BroadcastReceiver {
    public static String TAG = "AutemSmsReceiver";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.i(TAG, "SMS received.~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            Thread t = new Thread() {

                public void run() {
                    Log.i(TAG, "Preparing message");
                    Looper.prepare(); //For Preparing Message Pool for the child Thread
                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        String originatingAddress = smsMessage.getOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();
                        String displayName = originatingAddress;
                        String contactName = originatingAddress;
                        try {
                            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(originatingAddress));
                            Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME}, null, null, null);

                            c.moveToFirst();
                            displayName = c.getString(0);
                            contactName = displayName;
                        } catch (Exception e) {
                            Log.d(TAG, "Contact not found for: " + originatingAddress);
                        }

                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(context);
                        String apiKey = sharedPreferences.getString(AutemPreferences.API_KEY, "no-key");
                        String chromeToken = sharedPreferences.getString(AutemPreferences.CHROME_TOKEN, "no-key");
                        Log.d(TAG, apiKey);
                        Log.d(TAG, chromeToken);
                        Log.d(TAG, contactName);
                        Log.d(TAG, originatingAddress);
                        Log.d(TAG, messageBody);
                        SendMessageService sendMessageService = new SendMessageService();
                        sendMessageService.sendMessage(apiKey, chromeToken, originatingAddress, messageBody, contactName);
                    }
                    Looper.loop(); //Loop in the message queue
                }
            };
            t.start();

        }
    }
}
