
/*
 * Copyright 2016 Gregory Sarrica and Kyle Chaplin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    public void sendMessage(String apiKey, String chromeToken, String from, String textMessage, String contactName) {
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
            AutemConversation autemConversation = new AutemConversation();

            AutemContact autemContact = new AutemContact();
            autemContact.setName(contactName);
            autemContact.setPhoneNumber(from);

            autemConversation.setAutemContact(autemContact);

            AutemTextMessage autemTextMessage = new AutemTextMessage();
            autemTextMessage.setMessage(textMessage);
            autemTextMessage.setTimestamp(new LocalDateTime());
            autemTextMessage.setTo("me");
            autemTextMessage.setFrom(from);
            autemConversation.setAutemTextMessage(autemTextMessage);

            Gson gson = new Gson();
            String json = gson.toJson(autemConversation);
            message.put("message", json);
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("data", message);
            dataJSON.put("to", chromeToken);
            dataJSON.put("priority", "high");
            // dataJSON.put("time_to_live", 600); set this later. rather have this off for testing
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
