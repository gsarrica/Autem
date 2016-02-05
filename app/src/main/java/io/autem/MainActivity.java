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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private EditText mTokenEditText;
    private EditText mChromeTokenEditText;
    private EditText mApiKeyEditText;
    private EditText mProjectNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
        mTokenEditText = (EditText) findViewById(R.id.tokenEditText);
        mChromeTokenEditText = (EditText) findViewById(R.id.chromeTokenEditText);
        mApiKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);
        mProjectNumberEditText = (EditText) findViewById(R.id.projectNumberEditText);

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


        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);


        String token = sharedPreferences.getString(AutemPreferences.REGISTRATION_TOKEN, "");
        mTokenEditText.setText(token);

        String apiKey = sharedPreferences.getString(AutemPreferences.API_KEY, "");
        mApiKeyEditText.setText(apiKey);

        String chromeToken = sharedPreferences.getString(AutemPreferences.CHROME_TOKEN, "");
        mChromeTokenEditText.setText(chromeToken);

        String projectNumber = sharedPreferences.getString(AutemPreferences.PROJECT_NUMBER, "");
        mProjectNumberEditText.setText(projectNumber);

        if (checkPlayServices()) {
            final Intent intent = new Intent(this, RegistrationIntentService.class);
            final Button registerDeviceButton = (Button) findViewById(R.id.registerDeviceButton);
            registerDeviceButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Start IntentService to register this application with GCM.
                    mProjectNumberEditText = (EditText) findViewById(R.id.projectNumberEditText);
                    if(!mProjectNumberEditText.getText().toString().isEmpty()) {
                        mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
                        mInformationTextView.setVisibility(View.VISIBLE);
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                        sharedPreferences.edit().putString(AutemPreferences.PROJECT_NUMBER, mProjectNumberEditText.getText().toString()).commit();
                        startService(intent);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.project_number_required), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        final Button testMessageButton = (Button) findViewById(R.id.testMessageButton);
        testMessageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mChromeTokenEditText = (EditText) findViewById(R.id.chromeTokenEditText);
                mApiKeyEditText = (EditText) findViewById(R.id.apiKeyEditText);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
                sharedPreferences.edit().putString(AutemPreferences.API_KEY, mApiKeyEditText.getText().toString()).commit();
                sharedPreferences.edit().putString(AutemPreferences.CHROME_TOKEN, mChromeTokenEditText.getText().toString()).commit();
                sendTestMessage(mChromeTokenEditText.getText().toString(), mApiKeyEditText.getText().toString());
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

    protected void sendTestMessage(final String chromeToken, final String apiKey) {
        Thread t = new Thread() {

            public void run() {
                Log.i(TAG, "Preparing message");
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                SendMessageService sendMessageService = new SendMessageService();
                sendMessageService.sendMessage(apiKey, chromeToken, "Message Tester", "This is a test.", "Autem Test Contact");
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }


}
