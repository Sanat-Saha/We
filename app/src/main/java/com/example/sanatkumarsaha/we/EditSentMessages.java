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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;
import java.net.URLEncoder;


public class EditSentMessages extends ActionBarActivity {

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog2.dismiss();
            Toast.makeText(EditSentMessages.this, "Data Saved Successfully.", Toast.LENGTH_LONG).show();
            startActivity(intent);
        }
    };

    EditText message;
    String username,password,accdata,name;
    Intent intent;
    ProgressDialog progressDialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_sent_messages);
        if (!isConnected()){
            Toast.makeText(EditSentMessages.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return;
        }
        message = (EditText)findViewById(R.id.message);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        name = bundle.getString("name");
        accdata = bundle.getString("data");
        message.setText(accdata);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_sent_messages, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isConnected()){
            Toast.makeText(EditSentMessages.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }

        switch (item.getItemId()){

            case R.id.save:
                accdata = message.getText().toString();
                intent = new Intent(this,SentMessages.class);
                intent.putExtra("username",username);
                intent.putExtra("password",password);
                intent.putExtra("name", name);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                progressDialog2 = ProgressDialog.show(this,"","Saving...");
                Runnable k = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/EditSentMessages.php?username="+username+"&key=12051996&password="+password+"&data="+ URLEncoder.encode(accdata, "utf-8"));
                            httpClient.execute(httpPost);
                        }catch (IOException e){
                            Toast.makeText(EditSentMessages.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                            return;
                        }handler2.sendEmptyMessage(0);
                    }
                };

                Thread thread1 = new Thread(k);
                thread1.start();
                return true;
            default:return super.onOptionsItemSelected(item);
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
