package com.example.sanatkumarsaha.we;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Background extends IntentService {

    public Background() {
        super("Background");
    }

    Bundle bundle;
    String username,password,name;
    String webPage = "";
    String data = "";

    @Override
    protected void onHandleIntent(Intent intent) {
        bundle = intent.getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        name = bundle.getString("name");

        while (true) {


                webPage = "";
            if (isConnected()) {
                try {
                    URL url = new URL("http://sanat.byethost12.com/getPending.php?username=" + username + "&key=" + "12051996");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is, "utf-8"));
                    while ((data = buffer.readLine()) != null) {
                        webPage += data + "\n";
                    }
                } catch (IOException e) {
                    return;
                }

                if (webPage.equals("Doesn't Exist" + "\n")) {
                    return;
                }

                if (!webPage.equals("")) {
                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/Notification.php?username=" + username + "&key=12051996&password=" + password);
                        httpClient.execute(httpPost);
                    } catch (IOException e) {
                        return;
                    }


                    Intent i = new Intent(this, Notification.class);
                    i.putExtra("username", username);
                    i.putExtra("password", password);
                    i.putExtra("name", name);
                    startService(i);
                }


            }

        }
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }


}
