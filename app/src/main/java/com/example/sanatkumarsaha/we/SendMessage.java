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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
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
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class SendMessage extends ActionBarActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (webPage.equals("false")){
                progressDialog.dismiss();
                Toast.makeText(SendMessage.this,"Username Doesn't Exist",Toast.LENGTH_LONG).show();
                webPage = "";
                return;
            }else{
                Runnable k = new Runnable() {
                    @Override
                    public void run() {
                        try{
                            SharedPreferences share = getSharedPreferences("history",MODE_PRIVATE);
                            SharedPreferences.Editor editor = share.edit();
                            if (share.getString("SendMessage_username","").equals(""))
                                editor.putString("SendMessage_username",receiveruname);
                            else if (!Arrays.asList(array).contains(receiveruname)) editor.putString("SendMessage_username",share.getString("LogInScreen_username","")+","+receiveruname);
                            editor.apply();
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("http://sanat.byethost12.com/SendMessage.php?senderuname="+username+"&receiveruname="+receiveruname+"&key=12051996&password="+password+"&message_r="+ URLEncoder.encode(message_r, "utf-8")+"&message_s="+ URLEncoder.encode(message_s, "utf-8"));
                            httpClient.execute(httpPost);
                        }catch (IOException e){
                            Toast.makeText(SendMessage.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                            return;
                        }handler2.sendEmptyMessage(0);
                    }
                };

                Thread thread1 = new Thread(k);
                thread1.start();
            }
        }
    };

    Handler handler2 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            Toast.makeText(SendMessage.this,"Message Sent Successfully",Toast.LENGTH_LONG).show();
            startActivity(i);
        }
    };

    EditText Message1;
    AutoCompleteTextView Username1;
    String username,password;
    String message_r,message_s,receiveruname,sendername;
    String webPage = "";
    String data = "";
    Intent i;
    ProgressDialog progressDialog;
    String history;
    String[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Bundle bundle = getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");
        sendername = bundle.getString("name");
        Username1 = (AutoCompleteTextView) findViewById(R.id.Username);
        Username1.setOnClickListener(new AutoCompleteTextView.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (Username1.getText().toString().length() <1){
                    Username1.showDropDown();
                }
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("history",MODE_PRIVATE);
        history = sharedPreferences.getString("SendMessage_username","");
        if (!history.equals("")){
            array = history.split(",");
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,array);
            Username1.setAdapter(adapter);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isConnected()){
            Toast.makeText(SendMessage.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()){

            case R.id.send:
                Message1 = (EditText) findViewById(R.id.Message);
                receiveruname = Username1.getText().toString();
                String Message = Message1.getText().toString();
                if (receiveruname.equals("") || Message.equals("")){
                    Toast.makeText(SendMessage.this,"Invalid Input!",Toast.LENGTH_LONG).show();
                    return true;
                }
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss, dd-MM-yyyy");
                message_r = "Message : [From : "+sendername+"("+username+") At : "+dateFormat.format(new Date())+"] "+Message + "\n";
                message_s = "Message : [To : "+receiveruname+" At : "+dateFormat.format(new Date())+"] "+Message + "\n";
                progressDialog = ProgressDialog.show(this,"","Sending...");
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://sanat.byethost12.com/CreateAccountUsernameCheck.php?username="+receiveruname+"&key="+"12051996");
                            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                            conn.connect();
                            InputStream is = conn.getInputStream();
                            BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
                            while ((data = buffer.readLine())!=null){
                                webPage += data;
                            }
                        } catch (IOException e) {
                            Toast.makeText(SendMessage.this,"Could Not Connect To the Network",Toast.LENGTH_LONG).show();
                            return;
                        }handler.sendEmptyMessage(0);
                    }
                };
                Thread thread = new Thread(r);
                thread.start();
                i = new Intent(this,AccountData.class);
                i.putExtra("username",username);
                i.putExtra("password",password);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


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

