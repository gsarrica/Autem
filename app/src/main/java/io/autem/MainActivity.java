package io.autem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private EditText mTokenEditText;
    private EditText mChromeTokenEditText;
    private EditText mApiKeyEditText;
    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(AutemPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    mInformationTextView.setText(getString(R.string.token_error_message));
                }

                String token = sharedPreferences.getString(AutemPreferences.REGISTRATION_TOKEN, "");
                mTokenEditText.setText(token);

            }
        };
        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mTokenEditText = (EditText) findViewById(R.id.tokenEditText);

        mApiKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String apiKey = sharedPreferences.getString(AutemPreferences.API_KEY, "");
        mApiKeyEditText.setText(apiKey);

        mChromeTokenEditText = (EditText) findViewById(R.id.chromeTokenEditText);
        String chromeToken = sharedPreferences.getString(AutemPreferences.CHROME_TOKEN, "");
        mChromeTokenEditText.setText(chromeToken);

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("io.autem.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver,filter);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


        final Button button = (Button) findViewById(R.id.testMessageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mChromeTokenEditText = (EditText) findViewById(R.id.chromeTokenEditText);
                mApiKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                sharedPreferences.edit().putString(AutemPreferences.API_KEY, mApiKeyEditText.getText().toString()).commit();
                sharedPreferences.edit().putString(AutemPreferences.CHROME_TOKEN, mChromeTokenEditText.getText().toString()).commit();
                sendJson(mChromeTokenEditText.getText().toString(), mApiKeyEditText.getText().toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AutemPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    protected void sendJson(final String chromeToken, final String apiKey) {
        Thread t = new Thread() {

            public void run() {
                Log.i(TAG, "Preparing message");
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                try {
                    URL url;
                    HttpURLConnection urlConn;
                    DataOutputStream printout;
                    DataInputStream input;
                    url = new URL("https://android.googleapis.com/gcm/send");
                    urlConn = (HttpURLConnection)url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Authorization", "key=" + apiKey);
                    urlConn.setRequestProperty("Host", "android.googleapis.com");
                    urlConn.setRequestProperty("charset", "utf-8");
                    urlConn.connect();

                    // Send POST output.
                    printout = new DataOutputStream(urlConn.getOutputStream ());
                    JSONObject message = new JSONObject();
                    message.put("message", "Yes");
                    JSONObject dataJSON = new JSONObject();
                    dataJSON.put("data", message);
                    dataJSON.put("to", chromeToken);
                    Log.i(TAG, dataJSON.toString());

                    printout.write(dataJSON.toString().getBytes("UTF-8"));
                    printout.flush();
                    printout.close();
                    Log.i(TAG, "Sent Message");

                    int httpResult =urlConn.getResponseCode();

                    Log.i(TAG, "http code:" + httpResult);

                }catch (Exception e) {
                    Log.i(TAG, "Error sending message", e);
                }




                Looper.loop(); //Loop in the message queue
            }
        };

        t.start();
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event");
            Log.d(TAG,temp);
        }
    }

}
