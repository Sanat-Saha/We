package com.example.sanatkumarsaha.we;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DeleteAccount extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Toast.makeText(DeleteAccount.this,"Account Deleted Successfully.",Toast.LENGTH_LONG).show();
            startActivity(i);
        }
    };

    String username,password,name;
    Intent i;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        name = bundle.getString("name");
    }

    public void deleteClicked(View v){
        if (!isConnected()){
            Toast.makeText(DeleteAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        i = new Intent(this,LogInScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        progressDialog = ProgressDialog.show(this,"","Deleting Account...");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/DeleteAccount.php?username="+username+"&key="+"12051996"+"&password="+password);
                    httpClient.execute(httpPost);
                } catch (IOException e) {
                    Toast.makeText(DeleteAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                    return;
                }handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();

    }

    public void cancelClicked(View v){
        if (!isConnected()){
            Toast.makeText(DeleteAccount.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        Intent i = new Intent(this,AccountData.class);
        i.putExtra("username",username);
        i.putExtra("password",password);
        i.putExtra("name",name);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }

}





























