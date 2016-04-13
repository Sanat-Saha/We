package com.example.sanatkumarsaha.we;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SentMessages extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            message.setText(webPage);
        }
    };

    TextView message;
    String username,password,name;
    String data = "";
    String webPage = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_messages);
        try{getSupportActionBar().setDisplayHomeAsUpEnabled(true);}
        catch (NullPointerException e){}
        if (!isConnected()){
            Toast.makeText(SentMessages.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        message = (TextView)findViewById(R.id.message);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        name = bundle.getString("name");
        progressDialog = ProgressDialog.show(this,"","Loading...");
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://sanat.byethost12.com/SentMessages.php?username="+username+"&key="+"12051996"+"&password="+password);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                    while ((data = buffer.readLine())!=null){
                        webPage += data + "\n";
                    }
                } catch (IOException e) {
                    return;
                }handler.sendEmptyMessage(0);
            }
        };
        Thread thread = new Thread(r);
        thread.start();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sent_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isConnected()){
            Toast.makeText(SentMessages.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()){

            case R.id.edit:
                Intent intent = new Intent(this,EditSentMessages.class);
                intent.putExtra("username",username);
                intent.putExtra("password",password);
                intent.putExtra("name", name);
                intent.putExtra("data",message.getText().toString());
                startActivity(intent);
                return true;
            case android.R.id.home:
                Intent intent1 = new Intent(this,AccountData.class);
                intent1.putExtra("username",username);
                intent1.putExtra("password",password);
                intent1.putExtra("name", name);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent1);
            default:return super.onOptionsItemSelected(item);
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,AccountData.class);
        intent.putExtra("username",username);
        intent.putExtra("password",password);
        intent.putExtra("name", name);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
    }
    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        else  return false;
    }

}
