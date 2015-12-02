package io.autem;

import android.util.Log;

import com.google.gson.Gson;

import org.joda.time.LocalDateTime;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by gsarrica on 11/30/15.
 */
public class SendMessageService {

    private static final String TAG = "SendMessageService";

    public void sendMessage(String apiKey, String chromeToken, String from, String textMessage) {
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

            AutemTextMessage autemTextMessage = new AutemTextMessage();
            autemTextMessage.setFrom(from);
            autemTextMessage.setMessage(textMessage);
            autemTextMessage.setTimestamp(new LocalDateTime());
            Gson gson = new Gson();
            String json = gson.toJson(autemTextMessage);
            message.put("message", json);
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
    }

}
